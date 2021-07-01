package net.yura.domination.lobby.client;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import net.yura.domination.engine.RiskUtil;
import net.yura.domination.mapstore.Map;
import net.yura.domination.mapstore.MapChooser;
import net.yura.domination.mapstore.MapUpdateService;
import net.yura.swing.GraphicsUtil;

public class RiskMap {

    private static final Logger logger = Logger.getLogger(RiskMap.class.getName());
    private static ExecutorService executor = Executors.newFixedThreadPool(4);
    private static java.util.Map<String, RiskMap> mapUIDToIcon = new WeakHashMap();

    private java.util.Map<Long, Icon> iconMap = new HashMap();
    private List<Component> components = new ArrayList();
    private Image image;
    private String mapUID;
    private Map map;
    private AtomicBoolean requestMade = new AtomicBoolean();
    private String[] missions;

    private RiskMap(String mapUID) {
        this.mapUID = mapUID;
    }

    /**
     * @Nullable: This method may or may not return a MapStore.Map object, it depends on if the icon has been requested and returned.
     */
    public Map getMap() {
        return map;
    }

    public Icon getIcon(int w, int h, Component comp) {
        final int width = GraphicsUtil.scale(w);
        final int height = GraphicsUtil.scale(h);
        long id = ByteBuffer.allocate(8).putInt(width).putInt(height).getLong(0);
        Icon icon = iconMap.get(id);
        if (icon == null) {
            icon = new Icon() {
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    if (image == null) {
                        if (!requestMade.getAndSet(true)) {
                            executor.submit(new Runnable() {
                                public void run() {
                                    try {
                                        if (isLocalMap()) {
                                            //PicturePanel.getImage(RiskGame) can also get a icon, but MapChooser caches the small preview
                                            map = MapChooser.createMap(mapUID);
                                            net.yura.mobile.gui.Icon icon = MapChooser.getLocalIconForMap(map);
                                            javax.microedition.lcdui.Image img = icon.getImage();
                                            setImage(img._image);
                                        }
                                        else {
                                            map = MapUpdateService.getOnlineMap(mapUID);
                                            // map is null if we can not connect to the server to get it
                                            if (map != null) {
                                                setImage(ImageIO.read(new URL(new URL(MapChooser.MAP_PAGE), map.getPreviewUrl())));
                                            }
                                            else {
                                                logger.log(Level.INFO, "no online map found " + mapUID);
                                            }
                                        }
                                    }
                                    catch (Throwable ex) {
                                        logger.log(Level.WARNING, "failed to get " + mapUID, ex);
                                    }
                                }
                            });
                        }
                    } else {
                        g.drawImage(image, x, y, width, height, c);
                    }
                }

                public int getIconWidth() {
                    return width;
                }

                public int getIconHeight() {
                    return height;
                }
            };
            iconMap.put(id, icon);
        }

        List<Component> comps = components;
        if (comps != null) {
            comps.add(comp);
        }
        return icon;
    }

    public boolean isLocalMap() {
        // map.getMapUrl().lastIndexOf('/') < 0 this is not a good check, as remote file does not always need to have a /
        return MapChooser.haveLocalMap(mapUID) && !MapUpdateService.getInstance().contains(mapUID);
        // OR in applet mode, but we dont care any more as no one uses applets.
    }

    private void setImage(Image icon) {
        image = icon;
        // warning, items can be added to list while this is being called
        // we can NOT use new java for loop as it will throw ConcurrentModificationException
        for (int c = 0; c < components.size(); c++) {
            components.get(c).repaint();
        }
        components = null;
    }

    @Override
    public String toString() {
        return map == null ? mapUID : map.getName();
    }

    public String getID() {
        return mapUID;
    }

    public String[] getMissions() {
        if (missions != null) {
            return missions;
        }

        java.util.Map mapinfo = RiskUtil.loadInfo(mapUID, false);
        String cardsFile = (String) mapinfo.get("crd");
        if (cardsFile != null) {
            java.util.Map cardsinfo = RiskUtil.loadInfo(cardsFile, true);
            missions = (String[]) cardsinfo.get("missions");
        }
        else {
            logger.warning("no crd file in " + mapUID);
            // should not happen
            missions = new String[0];
        }
        return missions;
    }

    public static RiskMap getMapIcon(final String mapUID) {
        RiskMap icon  = mapUIDToIcon.get(mapUID);
        if (icon == null) {
            icon = new RiskMap(mapUID);
            mapUIDToIcon.put(mapUID, icon);
        }
        return icon;
    }
}
