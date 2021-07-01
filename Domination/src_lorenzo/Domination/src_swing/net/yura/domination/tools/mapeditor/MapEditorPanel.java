// Yura Mamyrin

package net.yura.domination.tools.mapeditor;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputListener;
import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.RiskGame;
import net.yura.domination.guishared.PicturePanel;

/**
 * @author Yura Mamyrin
 */
public class MapEditorPanel extends JPanel implements MouseInputListener,MouseWheelListener {

	public static final int MODE_MOVE = 0;
	public static final int MODE_MOVEALL = 1;
	public static final int MODE_JOIN = 2;
	public static final int MODE_JOIN1WAY = 3;
	public static final int MODE_DISJOIN = 4;
	public static final int MODE_DRAW = 5;
        
        public static final int DEFAULT_BRUSH_SIZE = 5;
        
	//private List countries; // every item in this list also has its position+1 stored as the "color" value of it
	//private List continents;
	private RiskGame myMap;
	private BufferedImage pic;
	private BufferedImage map;
	private BufferedImage drawImage;
	private Country selected;
	private Rectangle box;
	private int mode;
	private int brush = DEFAULT_BRUSH_SIZE;
	private float alpha;
	private Point dragpoint;
	private int zoom;

	private MapEditor editor;
        private ListSelectionListener selectionListener;

	public MapEditorPanel(MapEditor a) {
		editor = a;

        	addMouseMotionListener(this);
		addMouseListener(this);

		addMouseWheelListener(this);

		ToolTipManager.sharedInstance().setDismissDelay(10000);

		mode = MODE_MOVE;
	}
        
        public void setSelectionListener(ListSelectionListener listener) {
            selectionListener = listener;
        }

        public Country getSelectedCountry() {
            return selected;
        }

	public BufferedImage getImageMap() {
		return map;
	}


	public BufferedImage getImagePic() {
		return pic;
	}

	public void zoom(int a) {

	    zoom = a;

	    if (pic!=null) {

		Dimension size = new Dimension(pic.getWidth()*zoom, pic.getHeight()*zoom);

		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);

		revalidate();
		repaint();
	    }
	}

	public void setImagePic(BufferedImage a,boolean checkmap) {
		pic = a;
            
		if (a.getWidth()!=PicturePanel.PP_X || a.getHeight()!=PicturePanel.PP_Y) {
                        // dont care about such old versions of the game any more
			//JOptionPane.showMessageDialog(this,"Only Risk 1.0.9.5+ supports any size maps!\nfor older version use: width="+PicturePanel.PP_X+" height="+PicturePanel.PP_Y);
		}

                if (checkmap && (pic.getWidth()!=map.getWidth() || pic.getHeight()!=map.getHeight())) {

			int result = JOptionPane.showConfirmDialog(this,
				"This ImagePic does not match the ImageMap size!\n"
			      + "ImagePic: "+pic.getWidth()+"x"+pic.getHeight()+"\n"
			      + "ImageMap: "+map.getWidth()+"x"+map.getHeight()+"\n"
			      + "They should match for the game to work!\n"      
			      + "would you like to update the ImageMap size?",
				"?", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				resizeAndSetImageMap(map);
			}
                }

		zoom(zoom);
	}
        
	private void resizeAndSetImageMap(BufferedImage imageMap) {
		BufferedImage newmap = new BufferedImage(pic.getWidth(), pic.getHeight(), imageMap.getType());
		Graphics g = newmap.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, newmap.getWidth(), newmap.getHeight());
		g.drawImage(imageMap, 0, 0, this);
		g.dispose();
		setImageMap(newmap);
	}

	public void setImageMap(BufferedImage a) {

		if (a.getWidth() != pic.getWidth() || a.getHeight() != pic.getHeight() ) {
			int result = JOptionPane.showConfirmDialog(this,
				"This ImageMap does not match the ImagePic size!\n"
			      + "ImagePic: "+pic.getWidth()+"x"+pic.getHeight()+"\n"
			      + "ImageMap: "+a.getWidth()+"x"+a.getHeight()+"\n"
			      + "They should match for the game to work!\n"
			      + "would you like to update the ImageMap size?",
				"?", JOptionPane.YES_NO_OPTION);
                        if (result == JOptionPane.YES_OPTION) {
                            resizeAndSetImageMap(a);
                            return;
                        }
		}

		map = a;

		// some error happens when done like this?????
		//map = new BufferedImage( a.getWidth() , a.getHeight() , BufferedImage.TYPE_USHORT_GRAY);
		//Graphics g = map.getGraphics();
		//g.drawImage(a,0,0,this);
		//g.dispose();

		drawImage = new BufferedImage(a.getWidth(),a.getHeight(),BufferedImage.TYPE_BYTE_BINARY,
			new IndexColorModel(1, 2, new byte[] { 0, (byte)0xff }, new byte[] { 0, 0 }, new byte[] { 0, 0 }, 0)
		);

		box = new Rectangle( new Dimension(a.getWidth(), a.getHeight()) );
	}



	public void setMap(RiskGame a) {
		myMap = a;

		//countries = Arrays.asList( myMap.getCountries() );
		//continents = Arrays.asList( myMap.getContinents() );
	}

	public void update(Map a) {

		int width = map.getWidth();
		int height =  map.getHeight();

		// have to make new image coz if we reuse the old 1 we get 0 values for some reason		// cant use this as get 0 values

		// cant make a new image, coz ither removing stops working or drawing draws in a wrong color
		//BufferedImage newImageMap = new BufferedImage( width, height, BufferedImage.TYPE_BYTE_INDEXED ); //  TYPE_BYTE_GRAY

		int[] pixels = map.getRGB(0,0,width,height,null,0,width);

		int oldcolor,newcolor;

		for (int c=0;c<pixels.length;c++) {

			oldcolor = pixels[c] & 0xff;

//if (a.get( new Integer(oldcolor) ) == null) {
//System.out.println(oldcolor+" goes to "+ a.get( new Integer(oldcolor) ) );
//}
			Object obj = a.get( new Integer(oldcolor) );

			if (obj != null) {

				newcolor = ((Integer)obj).intValue();

			}
			else {

				newcolor = oldcolor;

				System.out.println("bad color: "+oldcolor);

			}

//if (newcolor == 0) {
//System.out.println( oldcolor+" goes to 0!!!" );
//}


			pixels[c] = ((newcolor & 0xFF) << 16) | ((newcolor & 0xFF) << 8) | ((newcolor & 0xFF) << 0);

		}

		//newImageMap.
		map.setRGB(0,0,width,height,pixels,0,width);
		//map = newImageMap;

		repaint();

	}

	public void setSelectedCountry(Country a) {
	    if (selected != a) {
		selected = a;
                repaintSelected();
                if (selectionListener != null) {
                    int index = selected == null ? -1 : selected.getColor() - 1;
                    selectionListener.valueChanged(new ListSelectionEvent(this, index, index, false));
                }
	    }
	}

        public void repaintSelected() {
            int width = map.getWidth();
            int height =  map.getHeight();

            int[] pixels1 = map.getRGB(0,0,width,height,null,0,width);
            int[] pixels2 = drawImage.getRGB(0,0,width,height,null,0,width);

            int redColor = Color.RED.getRGB();

            for (int c=0;c<pixels1.length;c++) {
                    if (selected!=null && selected.getColor() == (pixels1[c]&0xff) ) {
                            pixels2[c] = redColor;
                    }
                    else {
                            pixels2[c] = 0;
                    }
            }

            drawImage.setRGB(0,0,width,height,pixels2,0,width);
            repaint();
        }

	public void setAlpha(int a) {
		alpha = a/100F;
	}

	public void setBrush(int a) {
		brush = (a==0)?1:a;
	}
        
	public void setMode(int a) {
		mode = a;
		dragpoint = null;
		repaint();
	}

    public void paintComponent(Graphics g) {
	super.paintComponent(g);

	Graphics2D g0 = (Graphics2D)g;
	g0.scale(zoom,zoom);


	if (myMap!=null) {

	    //System.out.println(alpha);

	    if (alpha!=1) {

            	drawImage(g, pic, 0, 0, this);

	    	drawCountries(g);
	    }

	    if (alpha!=0) {

		Graphics2D g2 = (Graphics2D)g.create();
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		g2.setComposite(ac);

		drawImage(g2, map, 0, 0, this);

		//if (mode == MODE_DRAW) {

			drawImage(g2, drawImage, 0, 0, this);

		//}

	    }



	    if (mode == MODE_DRAW && dragpoint!=null) {

		g.setXORMode(Color.WHITE);

		g.setColor(Color.BLACK);
                
                // TODO sometimes (linux java1.8 after scrolling) this draws nothing, and there is no way to tell if that happens
		g.drawOval(dragpoint.x-(brush/2),dragpoint.y-(brush/2),brush,brush);

		g.setPaintMode();
	    }
	}
    }

    // if we dont do this, draw is very slow on OS X
    public static void drawImage(Graphics g, Image img, int x, int y, ImageObserver io) {
        Rectangle clip = g.getClipBounds();
        clip = clip.intersection(new Rectangle(x, y, img.getWidth(io), img.getHeight(io)));
        int imgX = clip.x - x;
        int imgY = clip.y - y;
        g.drawImage(img, clip.x, clip.y, clip.x + clip.width, clip.y + clip.height, imgX, imgY, imgX + clip.width, imgY + clip.height, io);
    }

    int badness;
    private void drawCountries(Graphics g) {
        
            long time = System.currentTimeMillis();

	    int d = myMap.getCircleSize();

	    int width = pic.getWidth();

	    Country[] countries = myMap.getCountries();
            
            g.setFont( new java.awt.Font("Arial", java.awt.Font.PLAIN, (d+2)/2 ) );
            int fontHeight = g.getFontMetrics().getAscent();

            for (int i = 0; i < countries.length; i++) {

                Country n = countries[i];
                int x = n.getX();
                int y = n.getY();

		g.setColor( new Color( n.getContinent().getColor() ) );
                int r = d/2;
		g.fillOval( x-r , y-r, d, d );

                List ney = n.getNeighbours();
                for (int j = 0; j < ney.size(); j++) {

                    Country n1 = (Country)ney.get(j);
                    int x1 = n1.getX();
                    int y1 = n1.getY();


                    if (n1.getNeighbours().contains(n)) {
                        g.setColor(Color.BLUE);
		    }
                    else {
                        g.setColor(Color.GREEN);
		    }



			if ( Math.abs( x - x1 ) > ( width  / 2) ) {


				if ( x > (width / 2) ) { // ie "n" is on the right
					g.drawLine( x, y, x1+width, y1);
					g.drawLine( x-width, y, x1, y1);

				}
				else { // the attacker is on the left
					g.drawLine( x, y, x1-width, y1);
					g.drawLine( x+width, y, x1, y1);
				}

			}
			else {

                    		g.drawLine(x,y,x1,y1);

			}

		}





                if (selected == n) {
                    g.setColor(Color.RED);
		}
		else {
                    g.setColor(Color.BLUE);
		}
                g.drawRect(x-2,y-2,4,4);
                g.drawRect(x-3,y-3,6,6);

                // some java platforms have a HUGE bug where XOR is REALLY EEALLY SLOW
                // so if we find this method is being very slow, we must turn off using XOR
                boolean doXor = badness < 5;

                if (doXor) {
                    g.setXORMode(Color.WHITE);
                    //((Graphics2D)g).setComposite(AlphaComposite.Xor); // not sure how this works
                }

		g.setColor(Color.BLACK);

		g.drawString(n.getIdString(), x, y);
		g.drawString(String.valueOf(i+1), x, y + fontHeight);

                if (doXor) {
                    g.setPaintMode();
                }
            }
            
            long timeTaken = (System.currentTimeMillis() - time);
            
            if (timeTaken > 100) {
                badness++;
                System.out.println("XORMode Badness: " + timeTaken + " " + badness);
            }

    }

    public boolean contains(int x, int y) {

	if (myMap!=null) {

            Country mynode = getCountryAt(x/zoom,y/zoom);

	    if (mynode!=null) {

		String show="<html><b>"+mynode.getIdString()+" ("+mynode.getColor()+")</b><br>Location: (x="+mynode.getX()+",y="+mynode.getY()+")<br>" +

		"Continent: " + mynode.getContinent();

                List ney = mynode.getNeighbours();
                for (int j = 0; j < ney.size(); j++) {

                    Country n1 = (Country)ney.get(j);

		    show = show + "<br>Neighbour: " + n1.getIdString() +" ("+n1.getColor()+")";
		}

		show = show + "</html>";

 		setToolTipText(show);

	    }
	    else {

		setToolTipText(null);

	    }

	    return true; // this is needed so the mouse listoner can use it

	}
	else {

	    return false;

	}
    }

	public void drawLine(Point a,Point b,boolean draw) {

                // this fixes a really odd bug with drawing lines on indexed images
                if (a.y>b.y) {

                        Point z = a;
                        a = b;
                        b = z;
                }

                //@YURA:TODO  should not do this each time
                Graphics2D g1 = (Graphics2D)drawImage.getGraphics();
                Graphics2D g2 = (Graphics2D)map.getGraphics();

                BasicStroke bs = new BasicStroke(brush,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);

                g1.setStroke(bs);
                g2.setStroke(bs);

                if (selected != null && draw) { g1.setColor(Color.RED); g2.setColor( new Color(selected.getColor(),selected.getColor(),selected.getColor()) ); }
                else { g1.setColor(Color.BLACK); g2.setColor(Color.WHITE); }

                g1.drawLine(a.x, a.y, b.x, b.y);
                g2.drawLine(a.x, a.y, b.x, b.y);

                g1.dispose();
                g2.dispose();
	}

	public Country getCountryAt(int x,int y) {

			Country[] countries = myMap.getCountries();

			int size = 4;

			Country mynode = null;
			for (int i = 0; i < countries.length; i++) {

				Country n = countries[i];
				int x1 = n.getX();
				int y1 = n.getY();
				if (x1 >= x - size && y1 >= y - size && x1 <= x + size && y1 <= y + size) {
					mynode = n;
					break;
				}
			}

			return mynode;

	}

	public Point getPointOnImage(MouseEvent e) {
		return new Point( e.getX()/zoom,e.getY()/zoom );
	}

	// #############################################################
	// ###################### mouse ###########################
	// ##################################################


    public void mouseWheelMoved(MouseWheelEvent e) {
	if (e.getWheelRotation() < 0) {
	    editor.zoom(true);
	}
	else {
	    editor.zoom(false);
	}
    }

	public void mouseClicked(MouseEvent e) {

		if (myMap!=null) {

		    Point point = getPointOnImage(e);

		    if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) == MouseEvent.BUTTON3_MASK) {

			if (mode != MODE_DRAW) {

				setSelectedCountry(null);

			}

		    }
		    else if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK) {

			if (mode == MODE_JOIN) {

				Country mynode = getCountryAt(point.x,point.y);

				if (mynode!=null && selected==null) {

					setSelectedCountry(mynode);

				}
				else if (mynode!=null && mynode==selected) {

					setSelectedCountry(null);

				}
				else if (mynode!=null) {

					if (!selected.getNeighbours().contains(mynode)) {
						selected.addNeighbour(mynode);
					}
					if (!mynode.getNeighbours().contains(selected)) {
						mynode.addNeighbour(selected);
					}
					repaint();
				}

			}
			else if (mode == MODE_JOIN1WAY) {

				Country mynode = getCountryAt(point.x,point.y);

				if (mynode!=null && selected==null) {

					setSelectedCountry(mynode);

				}
				else if (mynode!=null && mynode==selected) {

					setSelectedCountry(null);

				}
				else if (mynode!=null) {

					if (!selected.getNeighbours().contains(mynode)) {
						selected.addNeighbour(mynode);
					}

					repaint();
				}

			}
			else if (mode == MODE_DISJOIN) {

				Country mynode = getCountryAt(point.x,point.y);

				if (mynode!=null && selected==null) {

					setSelectedCountry(mynode);

				}
				else if (mynode!=null && mynode==selected) {

					setSelectedCountry(null);

				}
				else if (mynode!=null) {

					if (selected.getNeighbours().contains(mynode)) {
						selected.getNeighbours().remove(mynode);
					}
					if (mynode.getNeighbours().contains(selected)) {
						mynode.getNeighbours().remove(selected);
					}
					repaint();
				}
			}

		    }

		}
	}

	private boolean xdrag;
	public void mousePressed(MouseEvent e) {

		if ( myMap!=null && (

				( (e.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK) ||
				( (e.getModifiers() & MouseEvent.BUTTON3_MASK) == MouseEvent.BUTTON3_MASK)

		)) {

			Point point = getPointOnImage(e);

			if (mode==MODE_MOVE) {

				Country mynode = getCountryAt(point.x,point.y);

				if (mynode!=null) {

					setSelectedCountry(mynode);
					xdrag = true;
				}
				else {

					dragpoint = e.getPoint();
					xdrag = false;
				}

			}
			else if (mode==MODE_MOVEALL) {

				dragpoint = point;

			}
			else if (mode==MODE_DRAW) {

				dragpoint = point;

				drawLine(dragpoint,dragpoint, ( (e.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK) );

				repaint();
			}
		}
	}

        public void mouseReleased(MouseEvent e) {

		if (mode == MODE_MOVE) {
			dragpoint=null;
		}
		else if (mode == MODE_MOVEALL) {
			dragpoint=null;
		}				// ((JViewport)getParent()).getViewRect()
		else if (mode == MODE_DRAW && !getVisibleRect().contains(e.getPoint()) ) {

			// if mouse released outside the box
			dragpoint=null;
			repaint();
		}
	}

        public void mouseDragged(MouseEvent e) {

	    if (myMap!=null) {

		Point point = getPointOnImage(e);

		if (mode == MODE_MOVE) {

			if (xdrag && box!=null && selected!=null && box.contains(point.x,point.y)) {

				selected.setX(point.x);
				selected.setY(point.y);

				//dragpoint = point;

				scrollRectToVisible( new Rectangle(e.getX(), e.getY(), 1, 1) );
				repaint();
			}
			else if (!xdrag && dragpoint!=null) {

				Rectangle r = getVisibleRect();
				r.translate(dragpoint.x-e.getX(),dragpoint.y-e.getY());

				scrollRectToVisible( r );

			}
		}
		else if (mode == MODE_MOVEALL && dragpoint!=null) {

			int xdif = point.x - dragpoint.x;
			int ydif = point.y - dragpoint.y;

			Country[] countries = myMap.getCountries();
			for(int i = 0; i < countries.length; i++) {

				countries[i].setX( countries[i].getX()+xdif );
				countries[i].setY( countries[i].getY()+ydif );

			}

			dragpoint = point;

			repaint();
		}
		else if (mode == MODE_DRAW && dragpoint!=null) {

			Point end = point;

			if (

				( (e.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK) ||
				( (e.getModifiers() & MouseEvent.BUTTON3_MASK) == MouseEvent.BUTTON3_MASK)

			) {

				drawLine(dragpoint,end, ( (e.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK) );

                                // Only repaint the area where we have drawn something (or the draw is very slow on OS X)
                                int halfBrush = brush / 2;
                                Rectangle rect = new Rectangle(dragpoint.x - halfBrush, dragpoint.y - halfBrush, brush, brush);
                                rect.add(new Rectangle(end.x - halfBrush, end.y - halfBrush, brush, brush));
                                rect.grow(3, 3);
                                
                                // multiply by zoom to go from image coordinates to screen coordinates
                                repaint(zoom * rect.x, zoom * rect.y, zoom * rect.width, zoom * rect.height);
			}

			dragpoint = end;
		}
	    }
	}

        public void mouseExited(MouseEvent e) {
		if (mode == MODE_DRAW) {
			if (

				( (e.getModifiers() & MouseEvent.BUTTON1_MASK) != MouseEvent.BUTTON1_MASK) &&
				( (e.getModifiers() & MouseEvent.BUTTON3_MASK) != MouseEvent.BUTTON3_MASK)

			) {
				dragpoint = null;
				repaint();
			}
		}
	}

	public void mouseMoved(MouseEvent e) {
		if (mode == MODE_DRAW) {
			dragpoint = getPointOnImage(e);
			repaint();
		}
	}

        public void mouseEntered(MouseEvent e) {}

}
