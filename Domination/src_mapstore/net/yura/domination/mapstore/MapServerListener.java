package net.yura.domination.mapstore;

import java.util.List;

/**
 * @author Yura Mamyrin
 */
public interface MapServerListener {

    public void gotResultCategories(String url, List categories);
    public void gotResultMaps(String url, List maps);
    public void onXMLError(String string);

    public void downloadFinished(String mapUID);
    public void onDownloadError(String string); // (map file download errors)

    /**
     * this is ONLY called when the image comes from the server, NOT if it comes from the cache
     * @param key either a {@link Map} or a {@link Category} (or mapUID for lobby game icons)
     */
    public void publishImg(Object key); // image has been set to the icon for a Map/Category
}
