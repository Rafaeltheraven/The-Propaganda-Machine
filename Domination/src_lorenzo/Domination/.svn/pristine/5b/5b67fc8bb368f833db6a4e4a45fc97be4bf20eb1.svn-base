package net.yura.domination.ui.flashgui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.border.Border;
import net.yura.swing.GraphicsUtil;

/**
 * @author Yura Mamyrin
 */
public class FlashBorder implements Border {

    Image top,bottom,left,right;
    
    public FlashBorder(Image top, Image left, Image bottom, Image right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }
    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Insets insets = getBorderInsets(c);
        
        // top left
        g.drawImage(top,
                x, y, x + insets.left, y + insets.top,
                0, 0, left.getWidth(c), top.getHeight(c), c);
        // top right
        g.drawImage(top,
                x + width - insets.right, y, x + width, y + insets.top,
                top.getWidth(c) - right.getWidth(c), 0, top.getWidth(c), top.getHeight(c), c);
        // bottom left
        g.drawImage(bottom,
                x, y + height - insets.bottom, x + insets.left, y + height,
                0, 0, left.getWidth(c), bottom.getHeight(c), c);
        // bottom right
        g.drawImage(bottom,
                x + width - insets.right, y + height - insets.bottom, x + width, y + height,
                bottom.getWidth(c) - right.getWidth(c), 0, bottom.getWidth(c), bottom.getHeight(c), c);

        // top
        g.drawImage(top,
                x + insets.left, y, x + width - insets.right, y + insets.top,
                left.getWidth(c), 0, top.getWidth(c) - right.getWidth(c), top.getHeight(c),
                c);
        // bottom
        g.drawImage(bottom,
                x + insets.left, y + height - insets.bottom, x + width - insets.right, y + height,
                left.getWidth(c), 0, bottom.getWidth(c) - right.getWidth(c), bottom.getHeight(c),
                c);
        // left
        g.drawImage(left, x, y + insets.top, insets.left, height - insets.top - insets.bottom, c);
        // right
        g.drawImage(right, x + width - insets.right, y + insets.top, insets.right, height - insets.top - insets.bottom, c);
    }

    public Insets getBorderInsets(Component c) {
        return GraphicsUtil.newInsets(top.getHeight(c), left.getWidth(c), bottom.getHeight(c), right.getWidth(c));
    }

    public boolean isBorderOpaque() {
        return true;
    }
}
