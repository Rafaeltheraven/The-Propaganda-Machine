package net.yura.swing;

import java.awt.Image;
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

    private void adjustImage() {
        // we HAVE to reset the current image as otherwise we can get problems with either:
        // * disabled (getImage() then to grayscale) draw directly for disabled icons
        // * aimated (SwingUtilities.doesIconReferenceImage in JLabel needs to return true)
        if (GraphicsUtil.scale(getIconWidth()) != getIconWidth()) {
            // only scale default and fst work for animated gifs
            setImage(getImage().getScaledInstance(GraphicsUtil.scale(getIconWidth()), GraphicsUtil.scale(getIconHeight()), Image.SCALE_DEFAULT));
        }
    }
}
