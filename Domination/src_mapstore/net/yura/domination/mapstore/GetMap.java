package net.yura.domination.mapstore;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import net.yura.domination.engine.RiskUtil;

/**
 * @author Yura Mamyrin
 */
public class GetMap extends Observable implements MapServerListener {

    MapServerClient client;
    String filename;

    /**
     * Should always use {@link #getMap(java.lang.String, java.util.Observer) }
     */
    private GetMap() { }

    public static void getMap(String filename, Observer gml) {
        GetMap get = new GetMap();
        get.filename = filename;
        get.addObserver(gml);
        get.client = new MapServerClient(get);
        get.client.start();
        get.client.makeRequestXML(MapChooser.MAP_PAGE, "mapfile", filename);
    }

    public void gotResultMaps(String url, List maps) {
        if (maps.size() == 1) {
            Map themap = (Map)maps.get(0);
            client.downloadMap(MapChooser.getURL(MapChooser.getContext(url), themap.mapUrl));
        }
        else {
            onError("wrong number of maps on server: " + maps.size() + " for map: " + filename);
        }
    }

    private void onError(String error) {
        System.err.println(error); // TODO we dont want to trigger the error catcher for network problems
        notifyListeners(RiskUtil.ERROR);
    }

    private void notifyListeners(Object arg) {
        client.kill();
        setChanged();
        notifyObservers(arg);
    }

    public void downloadFinished(String mapUID) {
        notifyListeners(RiskUtil.SUCCESS);
    }

    public void onXMLError(String string) {
        onError(string);
    }

    public void onDownloadError(String string) {
        onError(string);
    }

    public void gotResultCategories(String url, List categories) { }
    public void publishImg(Object param) { }

}
