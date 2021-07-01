package net.yura.domination.mapstore;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.yura.domination.engine.RiskUtil;
import net.yura.domination.mapstore.gen.XMLMapAccess;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.io.FileUtil;
import net.yura.mobile.io.HTTPClient;
import net.yura.mobile.io.ServiceLink.Task;
import net.yura.mobile.io.UTF8InputStreamReader;
import net.yura.mobile.util.SystemUtil;
import net.yura.mobile.util.Url;
import net.yura.social.GooglePlusOne;

/**
 * @author Yura Mamyrin
 */
public class MapServerClient extends HTTPClient {

    public static final Logger logger = Logger.getLogger(MapServerClient.class.getName());

    static final int REQUEST_TYPE_XML = 1;
    static final int REQUEST_TYPE_MAP = 2;
    static final int REQUEST_TYPE_IMG = 3;
    static final int REQUEST_TYPE_PLUS = 4;

    private static final String RATE_URL = "http://maps.yura.net/maps?mapfile=";

    // this stop imagees being fucked by operators
    // http://benvallack.com/notebook/how-to-fix-poor-image-quality-compression-when-using-tmobile-web-n-walk-on-a-mac/
    static final Hashtable headers = new Hashtable();
    static {
        headers.put("Cache-Control", "no-cache");
        headers.put("Pragma", "no-cache");
    }

    MapServerListener chooser;
    List downloads = new Vector();

    class ServerRequest extends Request {
	public int type;
    }
    
    public MapServerClient(MapServerListener aThis) {
        super(4);
        chooser = aThis;
    }

    public void kill() {
        chooser = null;
        if (downloads.isEmpty()) {
            super.kill();
        }
    }

    protected void onError(Request r, int responseCode, Hashtable headers, Exception ex) {
	ServerRequest request = (ServerRequest)r;
        MapServerListener ch = this.chooser;

        if (request.type == REQUEST_TYPE_MAP && ((MapDownload) request.id).ignoreErrorInDownload(request.url, responseCode)) {
            logger.info("skipped "+request);
            return;
        }

        Level level  = Level.WARNING;
        if (    ex instanceof UnknownHostException ||
                ex instanceof SocketTimeoutException ||
                ex instanceof ConnectException ||
               (ex instanceof EOFException && responseCode==0) || // end of stream during getResponseCode
               (ex instanceof SocketException &&
                   ("Connection timed out".equals(ex.getMessage()) ||
                    "Connection reset by peer".equals(ex.getMessage()) ||
                    "recvfrom failed: ETIMEDOUT (Connection timed out)".equals(ex.getMessage()) ||
                    "recvfrom failed: ECONNRESET (Connection reset by peer)".equals(ex.getMessage())) ) ) {
            level = Level.INFO;
        }
        // print error to console
        logger.log(level, "error: "+responseCode+" "+ex+" "+request+"\n"+headers, ex!=null?ex:new Exception());

        // show error dialog to the user
        if (ch!=null) {
            String error = "error:"+(responseCode!=0?" "+responseCode:"")+(ex!=null?" "+ex:"");
            if (request.type == REQUEST_TYPE_XML || request.type == REQUEST_TYPE_PLUS) {
                ch.onXMLError(error);
            }
            else if (request.type == REQUEST_TYPE_MAP) {
                ch.onDownloadError(error);
            }
            // for images do not pop-up error
        }
    }

    protected void onResult(Request r, int responseCode, Hashtable headers, InputStream is, long length) throws Exception {
	ServerRequest request = (ServerRequest)r;
        MapServerListener ch = this.chooser;

        if (request.type == REQUEST_TYPE_XML) {
            XMLMapAccess access = new XMLMapAccess();

            // on android BufferedInputStream makes it much fast, on desktop java does not really make a difference
            Task task = (Task)access.load( new UTF8InputStreamReader(new BufferedInputStream(is)) );

            // HACK!!! there is a massive bug in Android where if you dont do a extra read after reading all the data
            // HACK!!! your next http request will fail! http://code.google.com/p/android/issues/detail?id=7786
            // HACK!!! this bug is found on Android 1.6, it seems to be fixed on Android 2.3.3
            if (Midlet.getPlatform()==Midlet.PLATFORM_ANDROID) {
                is.read();
            }

//System.out.println("Got XML "+task);

            if (ch!=null) {
        	String method = task.getMethod();
        	Object param = task.getObject();
        	if ("categories".equals(method)) {
                    if (param instanceof java.util.List) {
                	ch.gotResultCategories( request.url, (java.util.List)param );
                    }
                }
                else if ("maps".equals(method)) {
                    if (param instanceof java.util.Map) {
                        java.util.Map info = (java.util.Map)param;
                        List<Map> list = (List)info.get("maps");

                        if (request.id instanceof Observer) {
                            Observer o = (Observer)request.id;
                            o.update(null, list.isEmpty() ? null : list.get(0));
                            return;
                        }
                        
                        // check if needs to be sorted by rating.
                        if (request.params != null && "TOP_RATINGS".equals(request.params.get("sort")) && list.size() > 0) {
                            List<String> urls = new ArrayList(list.size());
                            for (Map map : list) {
                                String fileUID = MapChooser.getFileUID( map.getMapUrl() );
                                urls.add(RATE_URL+Url.encode(fileUID));
                            }
                            ServerRequest request1 = new ServerRequest();
                            request1.id = new Object[] {request.url, list};
                            request1.url = GooglePlusOne.URL;
                            request1.type = REQUEST_TYPE_PLUS;
                            request1.post = true;
                            request1.postData = GooglePlusOne.getRequest(urls);
                            request1.headers = new Hashtable();
                            request1.headers.put("Content-Type", "application/json");
                            makeRequest(request1);
                        }
                        else {
                            //info.get("search");
                            //info.get("author");
                            //info.get("category");
                            //info.get("offset");
                            //info.get("total");
                            ch.gotResultMaps(request.url, list);
                        }
                    }
                }
            }
        }
        else if (request.type == REQUEST_TYPE_MAP) {
            ((MapDownload)request.id).gotRes(request.url, is );
        }
        else if (request.type == REQUEST_TYPE_IMG) {
            MapChooser.gotImgFromServer(request.id, request.url, SystemUtil.getData(is, (int)length), ch );
        }
        else if (request.type == REQUEST_TYPE_PLUS) {
            Object[] tmp = (Object[])request.id;
            String ratedlistUrl = (String)tmp[0];
            List<Map> ratedList = (List<Map>)tmp[1];
            java.util.Map<String,Integer> urlRatings = GooglePlusOne.getCount(is);
            final java.util.Map<String,Integer> ratings = new HashMap();
            for (java.util.Map.Entry<String,Integer> entry: urlRatings.entrySet()) {
                String fileUID = Url.decode(entry.getKey().substring(RATE_URL.length()));
                ratings.put(fileUID, entry.getValue());
            }
            Collections.sort(ratedList, new Comparator<Map>() {
                @Override
                public int compare(Map map0, Map map1) {
                    int rating0 = getRating(map0, ratings);
                    int rating1 = getRating(map1, ratings);
                    if (rating0 != rating1) {
                        return rating1 - rating0;
                    }
                    return Integer.parseInt(map0.getId()) - Integer.parseInt(map1.getId());
                }
            });
            if (ch!=null) {
                ch.gotResultMaps(ratedlistUrl, ratedList);
            }
        }
        else {
            logger.warning("[MapServerClient] unknown type "+request.type);
        }
    }

    private static int getRating(Map map, java.util.Map<String,Integer> ratings) {
        Integer rating = ratings.get(MapChooser.getFileUID(map.getMapUrl()));
        return rating == null ? 0 : rating;
    }

    private void makeRequest(String url,Hashtable params,int type, Object id) {
	    ServerRequest request = new ServerRequest();
	    request.type = type;
            request.url = url;
            request.params = params;
            request.id = id;
            request.headers = headers;
            logger.info("Make Request: "+request);
            // TODO, should be using RiskIO to do this get
            // as otherwise it will not work with lobby
            makeRequest(request);
    }

    public void makeRequestXML(String string, String key, String value) {
        Hashtable params = null;
        if (key != null && value != null) {
            params = new Hashtable();
            params.put(key, value);
        }
        makeRequest(string, params, REQUEST_TYPE_XML, null);
    }

    public void makeRequestMap(String url, String mapUid, Observer o) {
        Hashtable params = new Hashtable();
        params.put("mapfile", mapUid);
        makeRequest(url, params, REQUEST_TYPE_XML, o);
    }

    /**
     * you should use {@link MapChooser#getRemoteImage(java.lang.Object, java.lang.String, net.yura.domination.mapstore.MapServerClient)}
     * to make sure we check the disk cache, as the image may have already been downloaded.
     */
    void getImage(String url, Object key) {
	makeRequest(url, null, REQUEST_TYPE_IMG, key);
    }

    public void downloadMap(String fullMapUrl) {
        MapDownload download = new MapDownload(fullMapUrl);
        downloads.add(download); // TODO thread safe??
    }

    public boolean isDownloading(String mapUID) {
        // TODO this is NOT thread safe and does throw ArrayIndexOutOfBoundsException
        // TODO as items are removed in HttpClient thread, but this method is called from ui (paint) thread.
        for (int c=0;c<downloads.size();c++) {
            MapDownload download = (MapDownload)downloads.get(c);
            if ( download.mapUID.equals(mapUID) ) {
                return true;
            }
        }
        return false;
    }


    class MapDownload {

        String mapUID;
        String mapContext;
        final List urls = new Vector();
        final List fileNames = new Vector();
        boolean error = false;

        MapDownload(String url) {

            mapUID = MapChooser.getFileUID(url);

            mapContext = url.substring(0, url.length() - mapUID.length() );

            downloadFile( mapUID );
        }

        public String toString() {
            return mapUID;
        }

        final void downloadFile(String fileName) {
            // this does not support spaces in file names
            //String url = mapContext + fileName;

            fileNames.add(fileName);

            String url = getURL(mapContext, fileName);

            urls.add(url);

            makeRequest(url, null, MapServerClient.REQUEST_TYPE_MAP, this);
        }

        boolean hasUrl(String url) {
            return urls.contains(url);
        }

        /**
         * This method must be called for ALL responses to map file requests, normal AND error!
         */
        private void gotResponse(String url) {
            boolean empty;

            synchronized(urls) {
                urls.remove(url);
                empty = urls.isEmpty();
            }

            if (empty) {
                downloads.remove(this);

                try {
                    if (!error) {
                        // rename all .part to there normal names
                        // go backwards so we get to the .map file last
                        for (int c=fileNames.size()-1;c>=0;c--) {
                            String fileName = (String)fileNames.get(c);
                            RiskUtil.streamOpener.renameMapFile(fileName + ".part", fileName);
                        }

                        MapChooser.clearFromCache(mapUID);
                        MapUpdateService.getInstance().downloadFinished(mapUID);

                        MapServerListener ch = chooser; // avoid null pointers, take a copy
                        if (ch!=null) {
                            ch.downloadFinished(mapUID);
                        }
                    }
                }
                catch (Exception ex) {
                    logger.log(Level.WARNING, "rename error! map=" + mapUID + " context=" + mapContext + " url=" + url + " files=" + fileNames, ex);
                }

                if (chooser==null) {
                    kill();
                }
            }
        }

        private void gotRes(String url, InputStream is) throws Exception {
            // this does not support spaces in file names
            //String fileName = url.substring(mapContext.length());

            String fileName = getPath(mapContext, url);
            String saveToDiskName = fileName + ".part";

            OutputStream out = null;
            try {
                out = RiskUtil.streamOpener.saveMapFile(saveToDiskName);
                saveFile(is, out);
                if (fileName.endsWith(".map")) {
                    java.util.Map info = RiskUtil.loadInfo(saveToDiskName, false);

                    // {prv=ameroki.jpg, pic=ameroki_pic.png, name=Ameroki Map, crd=ameroki.cards, map=ameroki_map.gif, comment=map: ameroki.map blah... }

                    // files to download
                    String pic = (String)info.get("pic");
                    String crd = (String)info.get("crd");
                    String map = (String)info.get("map");
                    String prv = (String)info.get("prv");

                    if (pic==null || crd==null || map==null || "".equals(pic) || "".equals(crd) || "".equals(map)) {
                        throw new RuntimeException("info not found for map: "+mapUID+" in file: "+saveToDiskName+" info="+info);
                    }

                    downloadFile( pic );
                    downloadFile( crd );
                    downloadFile( map );
                    if (prv!=null) {
                        downloadFile( "preview/"+prv );
                    }
                }
            }
            catch (Exception ex) {
                error = true;
                throw ex; // throwing here will call ignoreErrorInDownload -> gotResponse
            }
            finally {
                FileUtil.close(is);
                FileUtil.close(out);
            }
            // only call gotResponse when everything is finished and went ok and we didnt throw any exceptions
            gotResponse(url);
        }

        private boolean ignoreErrorInDownload(String url, int responseCode) {
            //String fileName = url.substring(mapContext.length());
            String fileName = getPath(mapContext, url);

            // only ignore server 404 errors, this happens when maps link to bundled cards files.
            boolean ignoreError = responseCode == 404 && MapChooser.fileExists(fileName);

            if (ignoreError) {
                // we got a error, but we already have this file, so ignore the error
                fileNames.remove(fileName);
            }
            else {
                error = true;
            }

            gotResponse(url);

            return ignoreError;
        }
    }


    private static void saveFile(InputStream is, OutputStream out) throws IOException {
        int COPY_BLOCK_SIZE=1024;
        byte[] data = new byte[COPY_BLOCK_SIZE];
        int i = 0;
        while( ( i = is.read(data,0,COPY_BLOCK_SIZE ) ) != -1  ) {
            out.write(data,0,i);
        }
    }

    public static String getURL(String context, String path) {
        // as we downloading a file, we need to have the correct encoding! (Hello World -> Hello%20World)
        try {
            return new URI(context).resolve( new URI(null, null, path, null) ).toASCIIString();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getPath(String context, String url) {
        // we need to convert this url back into a normal path (Hello%20world -> Hello World)
        try {
            return new URI(context).relativize( new URI(url) ).getPath();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
