package net.yura.domination.tools.mapeditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.yura.domination.engine.ColorUtil;
import net.yura.domination.engine.core.Continent;
import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.RiskGame;
import net.yura.mobile.io.kxml2.KXmlParser;

/**
 * @author Yura Mamyrin
 */
public class TegMapLoader {
    
    class MyFilter extends RGBImageFilter {
        int color;
        public MyFilter(int color) {
            this.color = color;
        }
        public int filterRGB(int x, int y, int rgb) {
            int alpha = ColorUtil.getAlpha(rgb);
            return alpha > 127 ? color : 0;
        }
    }
    
    public void load(File xmlfile,RiskGame map,MapEditor editor) throws Exception {

        BufferedImage board=null,imgMap=null;

        File dir = xmlfile.getParentFile();
        
        InputStream input = new FileInputStream(xmlfile);
        
        KXmlParser parser = new KXmlParser();
        parser.setInput(input, null);
        parser.nextTag();
        // read start tag
        
        int continentId=0;
        int countryId=0;
        
        while (parser.nextTag() != KXmlParser.END_TAG) {
            String name = parser.getName();
            if ("continent".equals(name)) {
                String continentName = parser.getAttributeValue(null, "name");

                int continentX = Integer.parseInt( parser.getAttributeValue(null, "pos_x") );
                int continentY = Integer.parseInt( parser.getAttributeValue(null, "pos_y") );

                int myContinentId = convertContinentId(continentId);

                Continent continent = map.getContinents()[myContinentId];
                
                continent.setName(continentName);
                continent.setIdString(continentName.replace(' ', '_'));
                
                while (parser.nextTag() != KXmlParser.END_TAG) {
                    String name2 = parser.getName();

                    if ("country".equals(name2)) {

                        String countryName = parser.getAttributeValue(null, "name");
                        String file = parser.getAttributeValue(null, "file");

                        int pos_x = Integer.parseInt( parser.getAttributeValue(null, "pos_x") );
                        int pos_y = Integer.parseInt( parser.getAttributeValue(null, "pos_y") );
                        String army_x = parser.getAttributeValue(null, "army_x");
                        int armyX = (army_x==null||"".equals(army_x))?0:Integer.parseInt( army_x );
                        String army_y = parser.getAttributeValue(null, "army_y");
                        int armyY = (army_y==null||"".equals(army_y))?0:Integer.parseInt( army_y );

                        BufferedImage countryImage = ImageIO.read( new File(dir, file) );

                        Graphics g = board.getGraphics();
                        g.drawImage(countryImage, continentX+pos_x, continentY+pos_y, null);
                        g.dispose();

                        Color color = new Color(countryId+1, countryId+1, countryId+1);
                        FilteredImageSource filteredSrc = new FilteredImageSource(countryImage.getSource(), new MyFilter( color.getRGB() ) );
                        java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().createImage(filteredSrc);

                        Graphics g2 = imgMap.getGraphics();
                        g2.drawImage(image, continentX+pos_x, continentY+pos_y, null);
                        g2.dispose();

                        Country country = map.getCountries()[countryId];

                        country.setName(countryName);
                        country.setIdString(countryName.replace(' ', '_'));
                        country.setX( continentX+pos_x+  (countryImage.getWidth()/2)  +(armyX/2) );
                        country.setY( continentY+pos_y+  (countryImage.getHeight()/2)  +(armyY/2) );

                        countryId++;
                    }
                    // read end tag
                    parser.skipSubTree();
                }
                continentId++;
            }
            else {
                
                if ("board".equals(name) || "map".equals(name)) {
                    String file = parser.getAttributeValue(null, "file");
                    
                    File boardFile = new File(dir, file);
                    
                    board = MapEditor.makeRGBImage( ImageIO.read(boardFile) );
                    imgMap = MapEditor.newImageMap(board.getWidth(), board.getHeight());
                }
                
                // read end tag
                parser.skipSubTree();
            }
        }
        
        editor.setImagePic(board, null, false);
        editor.setImageMap(imgMap);
        
    }
    
    
    int convertContinentId(int tegId) {
        switch(tegId) {
            case 0: return 1;
            case 1: return 0;
            case 2: return 3;
            case 3: return 5;
            case 4: return 2;
            case 5: return 4;
            default: throw new RuntimeException("strange teg id "+tegId);
        }
    }
    
}
