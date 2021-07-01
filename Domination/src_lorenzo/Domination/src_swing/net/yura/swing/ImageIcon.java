package net.yura.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;

/**
 * TODO: add support for apple image@2x.png
 */
public class ImageIcon extends javax.swing.ImageIcon {

    public ImageIcon (String filename) {
        super(filename);
        adjustImage();
    }

    public ImageIcon(URL location) {
        super(location);
        adjustImage();
    }

    public ImageIcon(Image image) {
        super(image);
        adjustImage();
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        try {
            super.paintIcon(c, g, x, y);
        }
        catch (ClassCastException ex) {
            // java 1.8 on Linux has some bug in drawing a scaled image, so we try scaleing it ourselves instead
            if (original != null) {
                BufferedImage newImg = new BufferedImage(getIconWidth(), getIconHeight(), BufferedImage.TYPE_INT_BGR);
                Graphics g2 = newImg.getGraphics();
                g2.drawImage(original, 0, 0, getIconWidth(), getIconHeight(), 0, 0, original.getWidth(c), original.getHeight(c), c);
                g2.setColor(Color.RED);
                g2.fillOval(GraphicsUtil.scale(5), GraphicsUtil.scale(5), GraphicsUtil.scale(10), GraphicsUtil.scale(10));
                g2.dispose();
                setImage(newImg);
                super.paintIcon(c, g, x, y);
            }
            else {
                throw ex;
            }
        }

        // as soon as we have painted once, we now know if the new scaled version works, so we can dump this backup
        original = null;
    }
    
    /**
     * keep a backup of the original in case the scaling failed and we can not draw it
     */
    private Image original;

    private void adjustImage() {
        // we HAVE to reset the current image as otherwise we can get problems with either:
        // * disabled (getImage() then to grayscale) draw directly for disabled icons
        // * aimated (SwingUtilities.doesIconReferenceImage in JLabel needs to return true)
        if (GraphicsUtil.scale(getIconWidth()) != getIconWidth()) {
            // only scale default and fst work for animated gifs
            original = getImage();
            setImage(getImage().getScaledInstance(GraphicsUtil.scale(getIconWidth()), GraphicsUtil.scale(getIconHeight()), Image.SCALE_DEFAULT));
        }
    }
}
