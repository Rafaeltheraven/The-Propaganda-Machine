package net.yura.domination.tools.mapeditor;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;
import net.yura.domination.engine.ColorUtil;

public class ImageUtil {

    public static void floodFill(BufferedImage source, int x, int y, int newColor) {

        int imgWidth = source.getWidth();
        int imgHeight = source.getHeight();
        int oldColor = source.getRGB(x, y);

        if (oldColor == newColor) return;
        
        Queue<Point> queue = new LinkedList<Point>();
        int[] scanLine = new int[imgWidth];

        queue.add(new Point(x, y));

        while (!queue.isEmpty()) {
            Point n = queue.poll();
            // If the color of n is equal to target-color.
            if (source.getRGB(n.x, n.y) == oldColor) {
                // Set w and e equal to n.
                int wx = n.x;
                int ex = n.x + 1;

                source.getRGB(0, n.y, imgWidth, 1, scanLine, 0, imgWidth);

                // Move w to the west until the color of the node to the
                // west of w no longer matches target-color.
                while (wx >= 0 && scanLine[wx] == oldColor) {
                    scanLine[wx] = newColor;
                    wx--;
                }

                // Move e to the east until the color of the node to the
                // east of e no longer matches target-color.
                while (ex <= imgWidth - 1 && scanLine[ex] == oldColor) {
                    scanLine[ex] = newColor;
                    ex++;
                }

                // Set the color of nodes between w and e to
                // replacement-color.
                int length = ex - wx - 1;
                if (length > 0) {
                    source.setRGB(wx + 1, n.y, length, 1, scanLine, wx + 1, imgWidth);
                }

                // For each node n between w and e.
                for (int ix = wx + 1; ix < ex; ix++) {
                    // If the color of the node to the north of n is
                    // target-color, add that node to Q.
                    if (n.y - 1 >= 0 && source.getRGB(ix, n.y - 1) == oldColor) {
                        queue.add(new Point(ix, n.y - 1));
                    }

                    // If the color of the node to the south of n is
                    // target-color, add that node to Q.
                    if (n.y + 1 < imgHeight && source.getRGB(ix, n.y + 1) == oldColor) {
                        queue.add(new Point(ix, n.y + 1));
                    }
                }
            }
        }
    }

    public static void smartFill(BufferedImage source, BufferedImage target, int x, int y, int newColor, int tolerance) {

        int imgWidth = source.getWidth();
        int imgHeight = source.getHeight();
        int oldColor = source.getRGB(x, y);

        Queue<Point> queue = new LinkedList<Point>();
        int[] sourceScanLine = new int[imgWidth];
        int[] targetScanLine = new int[imgWidth];
        queue.add(new Point(x, y));

        while (!queue.isEmpty()) {
            Point n = queue.poll();
            // If the color of n is equal to target-color.
            if (checkColor(target.getRGB(n.x, n.y), newColor, source.getRGB(n.x, n.y), oldColor, tolerance)) {
                // Set w and e equal to n.
                int wx = n.x;
                int ex = n.x + 1;

                source.getRGB(0, n.y, imgWidth, 1, sourceScanLine, 0, imgWidth);
                target.getRGB(0, n.y, imgWidth, 1, targetScanLine, 0, imgWidth);

                // Move w to the west until the color of the node to the
                // west of w no longer matches target-color.
                while (wx >= 0 && checkColor(targetScanLine[wx], newColor, sourceScanLine[wx], oldColor, tolerance)) {
                    targetScanLine[wx] = newColor;
                    wx--;
                }

                // Move e to the east until the color of the node to the
                // east of e no longer matches target-color.
                while (ex <= imgWidth - 1 && checkColor(targetScanLine[ex], newColor, sourceScanLine[ex], oldColor, tolerance)) {
                    targetScanLine[ex] = newColor;
                    ex++;
                }

                // Set the color of nodes between w and e to
                // replacement-color.
                int length = ex - wx - 1;
                if (length > 0) {
                    target.setRGB(wx + 1, n.y, length, 1, targetScanLine, wx + 1, imgWidth);
                }

                // For each node n between w and e.
                for (int ix = wx + 1; ix < ex; ix++) {
                    // If the color of the node to the north of n is
                    // target-color, add that node to Q.
                    if (n.y - 1 >= 0 && checkColor(target.getRGB(ix, n.y - 1), newColor, source.getRGB(ix, n.y - 1), oldColor, tolerance)) {
                        queue.add(new Point(ix, n.y - 1));
                    }

                    // If the color of the node to the south of n is
                    // target-color, add that node to Q.
                    if (n.y + 1 < imgHeight && checkColor(target.getRGB(ix, n.y + 1), newColor, source.getRGB(ix, n.y + 1), oldColor, tolerance)) {
                        queue.add(new Point(ix, n.y + 1));
                    }
                }
            }
        }
    }

    private static boolean checkColor(int target, int newColor, int rgb1, int rgb2, int tolerance) {
        if (target == newColor) {
            return false;
        }

        if (Math.abs(ColorUtil.getRed(rgb1) - ColorUtil.getRed(rgb2)) > tolerance) {
            return false;
        }
        if (Math.abs(ColorUtil.getGreen(rgb1) - ColorUtil.getGreen(rgb2)) > tolerance) {
            return false;
        }
        if (Math.abs(ColorUtil.getBlue(rgb1) - ColorUtil.getBlue(rgb2)) > tolerance) {
            return false;
        }
        return true;
    }
}
