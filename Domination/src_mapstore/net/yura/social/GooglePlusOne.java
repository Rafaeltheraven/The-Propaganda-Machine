package net.yura.social;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.yura.mobile.io.JSONUtil;
import net.yura.mobile.io.json.JSONWriter;

/**
 * http://www.tomanthony.co.uk/blog/google_plus_one_button_seo_count_api/
 */
public class GooglePlusOne {

    public static final Logger logger = Logger.getLogger(GooglePlusOne.class.getName());

    public static final String URL = "https://clients6.google.com/rpc?key=AIzaSyCKSbrvQasunBoV16zDH9R33D88CeLr9gQ";

    private static JSONUtil util = new JSONUtil() {
        @Override
	protected void saveObject(JSONWriter serializer, Object object) throws IOException {
	    if (object instanceof Map && !(object instanceof Hashtable)) {
		super.saveObject(serializer, new Hashtable((Map) object));
	    }
	    else if (object instanceof List && !(object instanceof Vector)) {
		super.saveObject(serializer, new Vector((List) object));
	    }
	    else {
		super.saveObject(serializer, object);
	    }
	}
    };

    public static byte[] getRequest(List<String> urls) {
        try {
            Object[] request = new Object[urls.size()];
            int c=0;
            for (String url:urls) {
                request[c++] = getRequestParam(url);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            util.save(out, request );
            return out.toByteArray();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * [{"method":"pos.plusones.get","id":"p","params":{"nolog":true,"id":"http://www.test.com","source":"widget","userId":"@viewer","groupId":"@self"},"jsonrpc":"2.0","key":"p","apiVersion":"v1"}]
     */
    public static byte[] getRequest(String url) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            util.save(out, new Object[] { getRequestParam(url) } );
            return out.toByteArray();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Map<String, Object> getRequestParam(String url) {
        Map<String, Object> params = new HashMap();
        params.put("nolog",true);
        params.put("id",url);
        params.put("source","widget");
        params.put("userId","@viewer");
        params.put("groupId","@self");

        Map<String, Object> request = new HashMap();
        request.put("method","pos.plusones.get");
        request.put("id","p");
        request.put("params", params);
        request.put("jsonrpc","2.0");
        request.put("key","p");
        request.put("apiVersion","v1");
        return request;
    }

    /**
     * [{"result": { "kind": "pos#plusones", "id": "http://www.google.com/", "isSetByViewer": false, "metadata": {"type": "URL", "globalCounts": {"count": 3097.0} } } "id": "p"}]
     * unfortunately the urls are re-encoded on the google end so may not exactly match the encoded urls we send. so we need to decode them before comparing.
     */
    public static Map<String,Integer> getCount(InputStream is) throws IOException {
	Object[] object = (Object[])util.load(is);
        Map<String,Integer> urlToValue = new HashMap();
        for (int c=0;c<object.length;c++) {
            Map<String, Object> response = (Map)object[c];
            try {
                Map<String, Object> result = (Map)response.get("result");
                if (result != null) {
                    String url = (String) result.get("id");
                    Map<String, Object> metadata = (Map) result.get("metadata");
                    Map<String, Object> globalCounts = (Map) metadata.get("globalCounts");
                    double count = (Double) globalCounts.get("count");
                    urlToValue.put(url, (int) count);
                }
                else {
                    // {"error":{"message":"Backend Error","code":-32099,"data":[{"message":"Backend Error","domain":"global","reason":"backendError"}]},"id":"p"}
                    logger.info("error getting count from: " + toJSON(response));
                }
            }
            catch (Exception ex) {
                logger.log(Level.WARNING, "error getting count from: " + toJSON(response), ex);
                // do not throw here as other responses may be fine
                //IOException ex2 = new IOException("error in "+responce);
                //ex2.initCause(ex); // Android 1.6
                //throw ex2;
            }
        }
	return urlToValue;
    }

    static String toJSON(Object obj) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            util.save(out, obj);
            return out.toString("UTF-8");
        }
        catch (Exception ex) {
            return ex.toString();
        }
    }
}
