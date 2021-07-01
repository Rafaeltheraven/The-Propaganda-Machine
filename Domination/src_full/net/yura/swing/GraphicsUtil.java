package net.yura.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import javax.swing.plaf.basic.BasicGraphicsUtils;

public class GraphicsUtil {

    public static final double density = getDisplayDensity();
    public static final double scale = getScale();

    public static int scale(int i) {
        return (int) (i * density / scale);
    }

    public static boolean insideButton(int x, int y, int bx, int by, int bw, int bh) {
        return x >= GraphicsUtil.scale(bx) && x < GraphicsUtil.scale(bx + bw) && y >= GraphicsUtil.scale(by) && y < GraphicsUtil.scale(by + bh);
    }

    public static void setBounds(Component comp, int x, int y, int w, int h) {
        comp.setBounds(scale(x), scale(y), scale(w), scale(h));
    }

    public static Dimension newDimension(int width, int height) {
        return new Dimension(scale(width), scale(height));
    }

    public static Insets newInsets(int top, int left, int bottom, int right) {
        return new Insets(scale(top), scale(left), scale(bottom), scale(right));
    }

    public static Polygon newPolygon(int[] xCoords, int[] yCoords) {
        Polygon polygon = new Polygon();
        for (int c = 0; c < xCoords.length; c++) {
            polygon.addPoint(scale(xCoords[c]), scale(yCoords[c]));
        }
        return polygon;
    }

    public static RoundRectangle2D newRoundRectangle(int x, int y, int w, int h, int arcw, int arch) {
        return new RoundRectangle2D.Float(scale(x), scale(y), scale(w), scale(h), scale(arcw), scale(arch));
    }

    public static void drawImage(Graphics g, Image img, int x, int y, ImageObserver observer) {
        g.drawImage(img,
                scale(x),
                scale(y),
                scale(img.getWidth(observer)),
                scale(img.getHeight(observer)),
                observer);
    }

    public static void drawImage(Graphics g, Image img, int x, int y, int w, int h, ImageObserver observer) {
        g.drawImage(img,
                scale(x),
                scale(y),
                scale(w),
                scale(h),
                observer);
    }

    public static void drawImage(Graphics g, Image img,
            int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2,
            ImageObserver observer) {
        g.drawImage(img,
                scale(dx1),
                scale(dy1),
                scale(dx2),
                scale(dy2),
                sx1, sy1, sx2, sy2, observer);
    }

    public static void fillRect(Graphics g, int x, int y, int width, int height) {
        g.fillRect(scale(x), scale(y), scale(width), scale(height));
    }

    public static void fillArc(Graphics g, int x, int y, int width, int height, int startAngle, int arcAngle) {
        g.fillArc(scale(x), scale(y), scale(width), scale(height), startAngle, arcAngle);
    }

    public static void fillOval(Graphics g, int x, int y, int width, int height) {
        g.fillOval(scale(x), scale(y), scale(width), scale(height));
    }

    public static void drawString(Graphics g, String string, int x, int y) {
        g.drawString(string, scale(x), scale(y));
    }

    public static void drawStringCenteredAt(Graphics g, String text, int x, int y) {
        drawStringCenteredAt(g, text, '\0', x, y);
    }

    public static void drawStringCenteredAt(Graphics g, String text, char ch, int x, int y) {
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        BasicGraphicsUtils.drawString(g, text, ch, GraphicsUtil.scale(x) - metrics.stringWidth(text) / 2, GraphicsUtil.scale(y));
    }

    public static void drawStringCenteredAt(Graphics g, String text, int centerX, int startY, int wrapWidth) {
        AttributedString as = new AttributedString(text);
        as.addAttribute(TextAttribute.FONT, g.getFont());

        AttributedCharacterIterator aci = as.getIterator();
        FontRenderContext frc = ((Graphics2D)g).getFontRenderContext();
            
        LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);

        int x = scale(centerX);
        int y = scale(startY);
        int width = scale(wrapWidth);

        lbm.setPosition( 0 );

        TextLayout tl;
        while (lbm.getPosition() < text.length()) {
            tl = lbm.nextLayout(width);
            tl.draw((Graphics2D)g, (float)(x - tl.getBounds().getWidth() / 2), y += tl.getAscent());
            y += tl.getDescent() + tl.getLeading();
        }
    }

    private static double getDisplayDensity() {
        try {
            return ((Double)Class.forName("javax.microedition.midlet.ApplicationManager")
                    .getMethod("getDisplayDensity").invoke(null)).doubleValue();
        }
        catch (Throwable th) { }
        return 1;
    }
    
    private static double getScale() {
        try {
            return ((Double)Class.forName("javax.microedition.midlet.ApplicationManager")
                    .getMethod("getScale").invoke(null)).doubleValue();
        }
        catch (Throwable th) { }
        return 1;
    }
}
