package net.yura.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.WeakHashMap;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.Timer;

/**
 * TODO: add support for apple image@2x.png
 */
public class SpriteIcon implements Icon {

    Image image;
    /**
     * The size of a single frame in the SOURCE image.
     */
    int width, height;
    
    int delay = 100;

    public SpriteIcon(URL img, int rows, int cols) {
        this(readfully(img), rows, cols);
    }

    /**
     * using ImageIO instead of Toolkit to get fully loaded image
     */
    private static Image readfully(URL url) {
        try {
            return ImageIO.read(url);
        }
        catch(IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public SpriteIcon(Image img, int rows, int cols) {
        image = img;
        width = image.getWidth(null) / cols;
        height = image.getHeight(null) / rows;
    }

    public int getIconWidth() {
        return GraphicsUtil.scale(width);
    }

    public int getIconHeight() {
        return GraphicsUtil.scale(height);
    }
 
    public void paintIcon(final Component c, Graphics g, int x, int y) {
        // can be used in many components
        // can be used many times in the same component
        
        int cols = image.getWidth(null) / width;
        int rows = image.getHeight(null) / height;
        int totalFrames = cols * rows;
        long time = System.currentTimeMillis();
        int frame = (int) (time % (totalFrames * delay)) / delay;
        
        int drawX = (frame % cols) * width;
        int drawY = (frame / cols) * height;
        g.drawImage(image, x, y, x + getIconWidth(), y + getIconHeight(), drawX, drawY, drawX + width, drawY + height, c);

        delayedRepaint(c);
    }

    // very weak map, both keys and values are weak
    private WeakHashMap<Component, WeakReference<Timer>> timers = new WeakHashMap();

    /**
     * Every component has its own timer that will trigger it to repaint after a delay.
     */
    private void delayedRepaint(final Component component) {
        
        WeakReference<Timer> maybeTimer = timers.get(component);
        Timer timer = maybeTimer == null ? null : maybeTimer.get();
        
        if (timer == null) {
            timer = new Timer(delay, null);
            timer.setRepeats(false);
            timer.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    component.repaint();
                }
            });
            timers.put(component, new WeakReference<Timer>(timer));
        }

        if (!timer.isRunning()) timer.restart();
    }
}
