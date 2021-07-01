// Yura Mamyrin

package net.yura.domination.tools.mapeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import net.yura.domination.engine.ColorUtil;
import net.yura.domination.engine.Risk;
import net.yura.domination.guishared.RiskUIUtil;
import net.yura.domination.engine.RiskUtil;
import net.yura.domination.engine.core.Card;
import net.yura.domination.engine.core.Continent;
import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.Mission;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;
import net.yura.swing.GraphicsUtil;
import net.yura.domination.guishared.PicturePanel;
import net.yura.domination.guishared.RiskFileFilter;
import net.yura.domination.mapstore.MapUpdateService;
import net.yura.domination.ui.swinggui.GameTab;
import net.yura.domination.ui.swinggui.SwingGUIPanel;
import net.yura.domination.ui.swinggui.SwingGUITab;
import net.yura.swing.ImageIcon;
import net.yura.swing.JTable;

/**
 * @author Yura Mamyrin
 */
public class MapEditor extends JPanel implements ActionListener, ChangeListener, SwingGUITab {

	private final static String IMAGE_MAP_EXTENSION;
	private final static String IMAGE_PIC_EXTENSION = "png";
	private final static int ZOOM_MAX = 8;
	private final static int ZOOM_MIN = 1;

	private Risk myrisk;
	private RiskGame myMap;
        private String fileName;
        private boolean usesDefaultCards;
        private File imgFile;
        
	private MapEditorPanel editPanel;
	private JToolBar toolbar;
	private MapEditorViews views;

	private JRadioButton move;
	private JRadioButton moveall;
	private JRadioButton join;
	private JRadioButton join1way;
	private JRadioButton disjoin;
	private JRadioButton draw;

	private JSlider fader;
	private JSlider brush;
        private JSpinner circle;

	private JButton save;
	private JButton play;
	private JButton loadimagepic;
	private JButton loadimagemap;
	private JButton delBadColorsButton;
        private JButton smartFill;
        private JButton autoDrawButton;
        private JButton cleanIslands;

	private JButton zoomin;
	private JButton zoomout;
	private JTextField zoom;
	private int zoomint;

	// force cards to be in the same order as countries
	// right now risk does NOT require this
	// if ever set to true, there must be a check for this added to the check method
	private boolean strictcards;

        private SwingGUIPanel panel;

	// i still bother with gif, coz java still is wrong sometimes when reading png color
	// not from files it saves itself but from files saved from other programs
	static {

		boolean usegif = false;

		String writerNames[] = ImageIO.getWriterFormatNames();

		for (int c=0;c<writerNames.length;c++) {

			if ("gif".equalsIgnoreCase(writerNames[c])) {

				usegif = true;
				break;
			}
		}

		if (usegif) {

			IMAGE_MAP_EXTENSION = "gif";
		}
		else {
			IMAGE_MAP_EXTENSION = "png";
		}
	}

	public MapEditor(Risk r,SwingGUIPanel panel) {
		this.panel = panel;

		setName( "Map Editor" );

		setOpaque(false);

		myrisk = r;

		toolbar = new JToolBar();

		toolbar.setRollover(true);
		toolbar.setFloatable(false);

		JButton newmap = new JButton("New map");
		newmap.setActionCommand("newmap");
		newmap.addActionListener(this);
		toolbar.add(newmap);

		JButton load = new JButton("Load map");
		load.setActionCommand("load");
		load.addActionListener(this);
		toolbar.add(load);

		JButton load2 = new JButton("Load current");
		load2.setActionCommand("load2");
		load2.addActionListener(this);
		toolbar.add(load2);

		save = new JButton("Save map");
		save.setActionCommand("save");
		save.addActionListener(this);
		toolbar.add(save);

		play = new JButton("Play Map");
		play.setActionCommand("play");
		play.addActionListener(this);
		toolbar.add(play);

		JButton publish = new JButton("Publish");
		publish.setActionCommand("publish");
		publish.addActionListener(this);
		toolbar.add(publish);
                
		toolbar.addSeparator();

		loadimagepic = new JButton("Load Image Pic",new ImageIcon(this.getClass().getResource("edit_pic.png")) );
		loadimagepic.setActionCommand("loadimagepic");
		loadimagepic.addActionListener(this);
		toolbar.add(loadimagepic);

		loadimagemap = new JButton("Load Image Map",new ImageIcon(this.getClass().getResource("edit_map.png")) );
		loadimagemap.setActionCommand("loadimagemap");
		loadimagemap.addActionListener(this);
		toolbar.add(loadimagemap);

		toolbar.addSeparator();

		zoomin = new JButton("Zoom in");
		zoomin.setActionCommand("zoomin");
		zoomin.addActionListener(this);
		toolbar.add(zoomin);

		zoomout = new JButton("Zoom out");
		zoomout.setActionCommand("zoomout");
		zoomout.addActionListener(this);
		toolbar.add(zoomout);

		toolbar.add( Box.createHorizontalGlue() );

		zoom = new JTextField(3);
		zoom.setEditable(false);

		Dimension size = GraphicsUtil.newDimension(25, 25);

		zoom.setMaximumSize(size);
		zoom.setMinimumSize(size);
		zoom.setPreferredSize(size);

		toolbar.add(new JLabel("Zoom:"));
		toolbar.add(zoom);

		save.setEnabled(false);
		play.setEnabled(false);
		loadimagepic.setEnabled(false);
		loadimagemap.setEnabled(false);

		editPanel = new MapEditorPanel(this);

		//editPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,0,0),1));

		JScrollPane scroll = new JScrollPane(editPanel);

		size = GraphicsUtil.newDimension(PicturePanel.PP_X, PicturePanel.PP_Y);

		scroll.setPreferredSize(size);
		scroll.setMinimumSize(size);
		scroll.setMaximumSize(size);

		scroll.setBorder(null);

		setLayout( new BorderLayout() );

		JPanel tmp = new JPanel( new BorderLayout() );
		tmp.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5,10,5,10),
				BorderFactory.createLineBorder(Color.BLACK,1)
			)
		);
		tmp.setOpaque(false);
		tmp.add(scroll);
		add( tmp );


		ButtonGroup modes = new ButtonGroup();
		JPanel modesPanel = new JPanel();
		modesPanel.setOpaque(false);

		modesPanel.add( new JLabel("Tools: ") );

		move = newJRadioButton("move",true,modes,modesPanel);
		moveall = newJRadioButton("move all",false,modes,modesPanel);
		join = newJRadioButton("join",false,modes,modesPanel);
		join1way = newJRadioButton("join 1 way",false,modes,modesPanel);
		disjoin = newJRadioButton("disjoin",false,modes,modesPanel);
		draw = newJRadioButton("draw",false,modes,modesPanel);

                smartFill = new JButton("Smart Fill");
		smartFill.setActionCommand("smartFill");
		smartFill.addActionListener(this);
		modesPanel.add(smartFill);
		smartFill.setEnabled(false);
                
		autoDrawButton = new JButton("Auto Draw");
		autoDrawButton.setActionCommand("autodraw");
		autoDrawButton.addActionListener(this);
		modesPanel.add(autoDrawButton);
		autoDrawButton.setEnabled(false);

		cleanIslands = new JButton("del islands");
		cleanIslands.setActionCommand("islands");
		cleanIslands.addActionListener(this);
		modesPanel.add(cleanIslands);
		cleanIslands.setEnabled(false);

		delBadColorsButton = new JButton("del bad map colors");
		delBadColorsButton.setActionCommand("fix");
		delBadColorsButton.addActionListener(this);

		add(modesPanel, BorderLayout.SOUTH );

		JPanel topPanel = new JPanel();
		topPanel.setOpaque(false);

		fader = new JSlider(0,100,0);
		fader.addChangeListener(this);
		fader.setOpaque(false);
		fader.setMajorTickSpacing(20);
		fader.setPaintLabels(true);

		brush = new JSlider(1, 100, MapEditorPanel.DEFAULT_BRUSH_SIZE);
		brush.addChangeListener(this);
		brush.setOpaque(false);
		brush.setMajorTickSpacing(20);
		brush.setPaintLabels(true);

                circle = new JSpinner(new SpinnerNumberModel(20,15,100,5) );
                circle.addChangeListener(this);
		circle.setOpaque(false);
		//circle.setMajorTickSpacing(20);
		//circle.setPaintLabels(true);
                //circle.setPreferredSize( new Dimension(100, circle.getPreferredSize().height) );
                
		topPanel.add( new JLabel("Image/Map Fade:") );
		topPanel.add(fader);
		topPanel.add( new JLabel("Draw Brush Size:") );
		topPanel.add(brush);
                topPanel.add( new JLabel("Circles:") );
		topPanel.add(circle);

		add(topPanel, BorderLayout.NORTH );

		setZoom(GraphicsUtil.scale(1));
	}

	private JRadioButton newJRadioButton(String a,boolean sel, ButtonGroup bg,JPanel jp) {
		JRadioButton b = new JRadioButton(a,sel);
		b.setActionCommand("mode");
		b.addActionListener(this);
		b.setOpaque(false);
		bg.add(b);
		jp.add(b);
		return b;
	}

	public void stateChanged(ChangeEvent e) {

		if (e.getSource() == fader) {

			editPanel.setAlpha(fader.getValue());
			editPanel.repaint();

		}
		else if (e.getSource() == brush) {

			editPanel.setBrush(brush.getValue());

		}
                else if (e.getSource() == circle) {

                    //circle.setToolTipText( String.valueOf( circle.getValue() ) );
                    
                    if (myMap!=null) {
                        //myMap.setCircleSize(circle.getValue());
                        myMap.setCircleSize(  ((Integer)circle.getValue()).intValue()  );
                        editPanel.repaint();
                    }
		}
	}

	public JToolBar getToolBar() {
		return toolbar;
	}
	public JMenu getMenu() {
		return null;
	}

	public void setVisible(boolean v) {

		super.setVisible(v);

		if (v && views == null ) {

			views = new MapEditorViews( RiskUIUtil.findParentFrame(this) , editPanel );
		}

		if (views!= null) {

			if (v) {

				Frame frame = RiskUIUtil.findParentFrame(this);

				Dimension frameSize = frame.getSize();
				Point frameLocation = frame.getLocation();

				views.setLocation(frameLocation.x+frameSize.width, frameLocation.y);
				views.setSize(GraphicsUtil.scale(200), frameSize.height);

			}
			views.setVisible(v);
		}
	}

	public void setNewMap(RiskGame m,BufferedImage ip,BufferedImage im, String fname, String cardsFile, File img) {
		myMap = m;

		editPanel.setMap(myMap);
		views.setMap(myMap);

		editPanel.setImagePic(ip,false);
		editPanel.setImageMap(im);

		save.setEnabled(true);
		play.setEnabled(true);
		loadimagepic.setEnabled(true);
		loadimagemap.setEnabled(true);
		smartFill.setEnabled(true);
                autoDrawButton.setEnabled(true);
                cleanIslands.setEnabled(true);

                fileName = fname;
                imgFile = img;
                usesDefaultCards = MapsTools.DEFAULT_RISK_CARD_SET.equals(cardsFile);
                
                circle.setValue( new Integer(m.getCircleSize()) );
                
		revalidate();
		repaint();
	}

	public RiskGame makeNewMap() throws Exception {
		RiskGame rg = new RiskGame();

		for (int c=1;c<=RiskGame.MAX_PLAYERS;c++) {
			rg.addPlayer(
				Player.PLAYER_HUMAN,
				"PLAYER"+c,
				ColorUtil.getColor( myrisk.getRiskConfig("default.player"+c+".color") ),
				null
			);
		}
		return rg;
	}
        
        private void loadMap(String name) throws Exception {
            RiskGame map = makeNewMap();
            map.setMapfile(name); // this is here just to update the cards option, also set the name and version
            String cardsFile = map.getCardsFile();
            map.loadMap();
            map.loadCards(true);

            InputStream in = RiskUtil.openMapStream(map.getImagePic());

            BufferedImage ipic = makeRGBImage( RiskUIUtil.read( in ) );
            BufferedImage imap = makeRGBImage( RiskUIUtil.read(RiskUtil.openMapStream(map.getImageMap()) ) );

            map.setMemoryLoad();

            File file=null;
            if (in instanceof RiskUIUtil.FileInputStream) {
                file = ( (RiskUIUtil.FileInputStream)in ).getFile();
            }

            setNewMap(map,ipic,imap,name,cardsFile,file);
        }
        
        void setImagePic(BufferedImage bufferedImage,File file,boolean checkmap) {
            editPanel.setImagePic(bufferedImage,checkmap);
            imgFile = file;
            revalidate();
            repaint();
        }
        
        void setImageMap(BufferedImage bufferedImage) {
            editPanel.setImageMap(bufferedImage);
            repaint();
        }
        
        static BufferedImage newImageMap(int w, int h) {
            BufferedImage imap = new BufferedImage(w , h, BufferedImage.TYPE_INT_BGR); // @YURA:TODO only works with this, but should be something else
            Graphics g = imap.getGraphics();
            g.setColor( Color.WHITE );
            g.fillRect(0,0,w , h);
            g.dispose();
            return imap;
        }

	public void actionPerformed(ActionEvent a) {
		String action = a.getActionCommand();

		if ("newmap".equals(action)) {
			try {
				RiskGame map = makeNewMap();
				map.setupNewMap();

				BufferedImage ipic = new BufferedImage(PicturePanel.PP_X , PicturePanel.PP_Y, BufferedImage.TYPE_INT_BGR);
				BufferedImage imap = newImageMap(PicturePanel.PP_X,PicturePanel.PP_Y);

				setNewMap(map,ipic,imap,null,null,null);
			}
			catch(Exception ex) {
				showError(ex);
			}
		}
		else if ("load".equals(action)) {

		    try {
			String name = RiskUIUtil.getNewFile( RiskUIUtil.findParentFrame(this), "map" );

			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			if (name!=null) {
                            if (name.endsWith(".xml")) {
                                loadMap("teg.map");
                                File file = new File(name);
                                TegMapLoader loader = new TegMapLoader();
                                loader.load( file , myMap , this);
                                myMap.setMapName(null);
                                myMap.setPreviewPic(null);
                                fileName = file.getParentFile().getName();
                            }
                            else {
                                loadMap(name);
                            }
			}
		    }
		    catch(Exception ex) {
			showError(ex);
		    }
                    finally {
                        setCursor(null);
                    }
		}
                else if ("load2".equals(action)) {
                    RiskGame game = myrisk.getGame();
                    try {
                        if (game == null || game.getMapFile() == null) {
                            String name = RiskUIUtil.getNewMap(RiskUIUtil.findParentFrame(this));
                            if (name != null) {
                                loadMap(name);
                            }
                        }
                        else {
                            loadMap(game.getMapFile());
                        }
                    }
                    catch (Exception ex) {
                        showError(ex);
                    }
                }
		else if (a.getActionCommand().equals("save")) {
		    checkMap();

		    if (!RiskUIUtil.checkForNoSandbox()) {
			RiskUIUtil.showAppletWarning(RiskUIUtil.findParentFrame(this));
			return;
		    }

                    JFileChooser fc = new JFileChooser( RiskUIUtil.getSaveMapDir() );
                    RiskFileFilter filter = new RiskFileFilter(RiskFileFilter.RISK_MAP_FILES);
                    fc.setFileFilter(filter);

                    String newFileName = fileName;

                    while(true) {
                        if (newFileName!=null) {
                            fc.setSelectedFile( new File( newFileName ) );
                        }                        
                        int returnVal = fc.showSaveDialog( RiskUIUtil.findParentFrame(this) );
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            newFileName = file.getName();

                            if (!MapsTools.isValidName(newFileName)) {
                                    JOptionPane.showMessageDialog(this, "please use only standard ASCII characters in the file name.");
                                    continue;
                            }

                            if (!(newFileName.endsWith( "." + RiskFileFilter.RISK_MAP_FILES ))) {
                                    newFileName = newFileName + "." + RiskFileFilter.RISK_MAP_FILES;
                            }

                            try {
                                boolean result = saveMap( new File(file.getParentFile(),newFileName) );
                                if (result) {
                                    fileName = newFileName;
                                    break; // everything is saved
                                }
                            }
                            catch(Throwable ex) {
                                showError(ex);
                            }
                        }
                        else {
                            break; // user cancelled!
                        }
                    }
		}
		else if (a.getActionCommand().equals("play")) {
			if ( checkMap() ) {
				try {
					myrisk.newMemoryGame(myMap,buildMapFile("mem.map", "mem.cards", "mem_map", "mem_pic"));
                                        panel.showMapImage( new ImageIcon( editPanel.getImagePic().getScaledInstance(203,127, java.awt.Image.SCALE_SMOOTH ) ) );
                                        panel.setSelectedTab(GameTab.class);
				}
                                catch (Exception e) {
					showError(e);
				}
			}
		}
                else if (a.getActionCommand().equals("publish")) {

                    if ( !checkMap() ) {
                        return;
                    }

                    if (fileName==null) {
                        JOptionPane.showMessageDialog(this, "please save to disk first!");
                        save.doClick();
                        return;
                    }

                    // someone may have an old map saved with an invalid filename, we want them to re-save this map
                    if (!MapsTools.isValidName(fileName)) {
                        JOptionPane.showMessageDialog(this, "please save the map using only standard ASCII characters in the file name.");
                        save.doClick();
                        return;
                    }

                    List maps = MapsTools.loadMaps(); // load big XML file

                    net.yura.domination.mapstore.Map map2 = MapsTools.findMap(maps,fileName);

                    // load info from server at start, as we can publish without this
                    final List categories = MapsTools.getCategories();

                    if (map2==null) { // we have never published this map before!

                        // check if someone else has published a map with this name
                        net.yura.domination.mapstore.Map map = MapUpdateService.getOnlineMap(fileName);
                        
                        if (map!=null) {

                            String mapUID = net.yura.domination.mapstore.MapChooser.getFileUID(map.getMapUrl());
                            if (!fileName.equals(mapUID)) {
                                JOptionPane.showMessageDialog(this, "File name clashes with existing map: \""+fileName+"\" and \""+mapUID+"\"\nplease pick a new unique file name for your map.");
                                save.doClick();
                                return;
                            }

                            int result = JOptionPane.showConfirmDialog(this, "There is already a map with the filename "+fileName+" in the MapStore,\n"
                                    + "is this a new version of that map?",null,JOptionPane.YES_NO_OPTION);

                            // TODO some file names can still clash even if the main name does not "Bob Map.map" and "BobMap.map" will have the same cards file?!

                            if (result==JOptionPane.NO_OPTION) {
                                JOptionPane.showMessageDialog(this, "please pick a new unique file name for your map.");
                                save.doClick();
                                return;
                            }

                            int localVersion = myMap.getVersion();
                            int onlineVersion = getVersion(map);
                            
                            if (localVersion <= onlineVersion) {
                                JOptionPane.showMessageDialog(this, "MapStore version: " + onlineVersion + "\n"
                                        + "Local version:" + localVersion + "\n"
                                        + "Saving map with new version: " + (onlineVersion + 1));
                                myMap.setVersion(onlineVersion + 1);
                                boolean successfulSave = false;
                                try {
                                    successfulSave = saveMap(new File(RiskUIUtil.getSaveMapDir(), fileName));
                                }
                                catch (Exception ex) {
                                    showError(ex);
                                }
                                if (!successfulSave) {
                                    // we failed to save for whatever reason, set old version back on game object and cancel publish action
                                    myMap.setVersion(localVersion);
                                    return;
                                }
                                // the save worked, this means the map now has a version higher then in the MapStore, we are all set to publish
                            }
                        }

                        // clear from cache to make sure we are loading the newest version of this maps data
                        net.yura.domination.mapstore.MapChooser.clearFromCache(fileName);

                        map2 = net.yura.domination.mapstore.MapChooser.createMap(fileName);
                        
                        map2.setDateAdded( String.valueOf( System.currentTimeMillis() ) ); // todays date
                        
                        if (map!=null) {
                            map2.setName( map.getName() );
                            map2.setAuthorName( map.getAuthorName() );
                            map2.setDescription( map.getDescription() );
                        }
                        else {
                            try {
                                map2.setAuthorName( System.getProperty("user.name") );
                            }
                            catch (Throwable th) { }
                        }
                        
                        maps.add(map2);
                    }

                    
                    JTextField authorName = new JTextField( map2.getAuthorName() );
                    MapEditorViews.OptionPaneTextArea description = new MapEditorViews.OptionPaneTextArea( map2.getDescription() );

                    JTextField mapName = new JTextField( map2.getName() );
                    JTextField authorEmail = new JTextField( map2.getAuthorId() ); // TODO using email as ID!!!

                    JList list = new JList( RiskUtil.asVector(categories) );
                    
                    String version = String.valueOf( myMap.getVersion() );

                    int result = showInputDialog(
                            new String[] {"Author's Full Name:","Email:","Map Name:","Description:","Categories:","Filename (Unique Map ID):","version:"},
                            new JComponent[] {authorName,authorEmail,mapName,description,list, new JLabel(fileName), new JLabel(version)},
                            "edit info"
                    );
                    
                    if (result == JOptionPane.OK_OPTION) {
                        
                        Object[] myCategories = list.getSelectedValues();
                        String[] myCategoriesIds = new String[myCategories.length];
                        for (int c=0;c<myCategories.length;c++) {
                            myCategoriesIds[c] = ((net.yura.domination.mapstore.Category)myCategories[c]).getId();
                        }
                        
                        // set back info on map object
                        map2.setAuthorName( authorName.getText() );
                        map2.setDescription( description.getText() );

                        map2.setName( mapName.getText() );
                        map2.setAuthorId (authorEmail.getText() ); // TODO using email as ID!!!

                        final BufferedImage fullimg = editPanel.getImagePic();
                        
                        // add extra info
                        map2.setMapWidth(fullimg.getWidth());
                        map2.setMapHeight(fullimg.getHeight());
                        
                        map2.setVersion( version );

                        // check if we already have a preview
                        if (map2.getPreviewUrl()==null || !map2.getPreviewUrl().startsWith(MapsTools.PREVIEW+"/")) {

                            BufferedImage prvimg = new BufferedImage(150, 94, BufferedImage.TYPE_INT_BGR );
                            Graphics g = prvimg.getGraphics();
                            g.drawImage(fullimg, 0, 0, prvimg.getWidth(), prvimg.getHeight(), this);
                            g.dispose();
                            
                            File mapsDir = RiskUIUtil.getSaveMapDir();

                            File previewDir = new File(mapsDir,MapsTools.PREVIEW);
                            if (!previewDir.isDirectory() && !previewDir.mkdirs()) { // if it does not exist and i cant make it
                                throw new RuntimeException("can not create dir "+previewDir);
                            }
                            
                            String previewFileName;
                            try {
                                previewFileName = MapsTools.makePreview(fileName,prvimg,previewDir,"jpg");
                            }
                            catch (Exception ex) {
                                try {
                                    previewFileName = MapsTools.makePreview(fileName,prvimg,previewDir,"png");
                                }
                                catch (Exception ex2) {
                                    RiskUtil.printStackTrace(ex);
                                    throw new RuntimeException(ex2);
                                }
                            }

                            map2.setPreviewUrl(previewFileName);
                        }

                        // save back to big XML file
                        MapsTools.saveMaps(maps);

                        if (myCategoriesIds.length>0) {
                            try {
                                String responce = MapsTools.publish(map2,myCategoriesIds);
                                JEditorPane editorPane = new JEditorPane();
                                editorPane.setEditable(false);
                                editorPane.setContentType( "text/html" );
                                editorPane.getDocument().putProperty("IgnoreCharsetDirective", Boolean.TRUE ); // not sure why this is needed, but it is
                                editorPane.setText( responce );
                                JScrollPane scroll = new JScrollPane(editorPane);
                                scroll.setPreferredSize(GraphicsUtil.newDimension(500, 250));
                                JOptionPane.showMessageDialog(this, new Object[] {"Congratulations! Your map has been sent to the MapStore server.\n"
                                        + "It will appear in the MapStore once it has been approved by one of the moderators.",scroll} );
                            }
                            catch (Exception ex) {
                                RiskUtil.printStackTrace(ex);
                                JOptionPane.showMessageDialog(this, "something went wrong! did you enter a valid email?\n" + ex.toString() );
                            }
                        }
                        else {
                            JOptionPane.showMessageDialog(this, "map info has been saved to disk, it has NOT been sent to the server as you did not select any Categories.");
                        }
                    }
                }
		else if (a.getActionCommand().equals("loadimagepic")) {

			NewImage img = getNewImage(true);

			if (img!=null) {
                            setImagePic(img.bufferedImage,img.file,true);
			}
		}
		else if (a.getActionCommand().equals("loadimagemap")) {

			NewImage img = getNewImage(false);

			if (img != null && checkNewImageMap(img.bufferedImage)) {
                            setImageMap(img.bufferedImage);
			}
		}
		else if (a.getActionCommand().equals("mode")) {
			if (move.isSelected()) {
				editPanel.setMode(MapEditorPanel.MODE_MOVE);
			}
			else if (moveall.isSelected()) {
				editPanel.setMode(MapEditorPanel.MODE_MOVEALL);
			}
			else if (join.isSelected()) {
				editPanel.setMode(MapEditorPanel.MODE_JOIN);
			}
			else if (join1way.isSelected()) {
				editPanel.setMode(MapEditorPanel.MODE_JOIN1WAY);
			}
			else if (disjoin.isSelected()) {
				editPanel.setMode(MapEditorPanel.MODE_DISJOIN);
			}
			else if (draw.isSelected()) {
				editPanel.setMode(MapEditorPanel.MODE_DRAW);
			}
			else {
				throw new RuntimeException("unknown mode");
			}
		}
		else if (a.getActionCommand().equals("zoomin")) {
			zoom(true);
		}
		else if (a.getActionCommand().equals("zoomout")) {
			zoom(false);
		}
		else if (a.getActionCommand().equals("fix")) {
			removeBadMapColors();
		}
                else if (a.getActionCommand().equals("autodraw")) {
                        Collection<Country> selectedCountries = views.getSelectedCountries();
                        if (selectedCountries.isEmpty()) {
                            selectedCountries = Arrays.asList(myMap.getCountries());
                        }

                        String[] options = {"Dots", "Flood Fill", "Cancel"};
                        int result = JOptionPane.showOptionDialog(this, new Object[] {
                                new ImageIcon(this.getClass().getResource("bam.png")),
                                getCountiresListMessage(selectedCountries),
                                "Are you sure you want to add country regions wherever the country badge is?\n"+
                                "This action is not reversible so please save a copy of your map first." },"Auto Draw?",
                                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                        if (result == JOptionPane.YES_OPTION || result == JOptionPane.NO_OPTION) {                            
                            autodraw(selectedCountries, result == JOptionPane.YES_OPTION);
                        }
		}
                else if ("smartFill".equals(a.getActionCommand())) {
                        Collection<Country> selectedCountries = views.getSelectedCountries();
                        if (selectedCountries.isEmpty()) {
                            selectedCountries = Arrays.asList(myMap.getCountries());
                        }

                        JSpinner tolerance = new JSpinner(new SpinnerNumberModel(20,0,255,1) );
                        int result = JOptionPane.showConfirmDialog(this, new Object[] {
                            getCountiresListMessage(selectedCountries),
                            "Smart Fill will use the color from the Image Pic\nto select the area in the Image Map. Tolerance:", tolerance}, "Smart Fill", JOptionPane.OK_CANCEL_OPTION);
                        if (result == JOptionPane.OK_OPTION) {
                            int t = ((Number)tolerance.getValue()).intValue();
                            for (Country country : selectedCountries) {
                                Color color = new Color(country.getColor(), country.getColor(), country.getColor());
                                ImageUtil.smartFill(editPanel.getImagePic(), editPanel.getImageMap(), country.getX(), country.getY(), color.getRGB(), t);
                            }
                            editPanel.repaintSelected();
                        }
                }
		else if (a.getActionCommand().equals("islands")) {
                        Collection<Country> selectedCountries = views.getSelectedCountries();
                        if (selectedCountries.isEmpty()) {
                            selectedCountries = Arrays.asList(myMap.getCountries());
                        }

			delIslands(selectedCountries);
		}
		else {
			throw new RuntimeException("unknown command: " + action);
		}
	}
 
        public static String getCountiresListMessage(Collection<Country> selectedCountries) {
            if (selectedCountries.size() == 1) {
                return "One selected country: " + selectedCountries.iterator().next();
            }
            if (selectedCountries.size() > 10) {
                return selectedCountries.size() + " selected countries";
            }
            List<Integer> numbers = new ArrayList<Integer>();
            for (Country country : selectedCountries) {
                numbers.add(country.getColor());
            }
            return "Selected countries: " + numbers;
        }

        private int showInputDialog(String[] labels, JComponent[] comps,String title) {
            
            if (labels.length != comps.length) {
                throw new RuntimeException();
            }

            JPanel panel = new JPanel();
            panel.setLayout(new java.awt.GridBagLayout());
            
            java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
            c.insets = new java.awt.Insets(3, 3, 3, 3);
            c.fill = java.awt.GridBagConstraints.BOTH;
            
            for (int i=0;i<labels.length;i++) {
            
                JLabel label = new JLabel(labels[i]);
                label.setHorizontalAlignment( JLabel.RIGHT );
                //label.setVerticalAlignment( JLabel.TOP ); // TODO not nice, as the lebels stick to the top too much

		c.gridx = 0; // col
		c.gridy = i; // row
		c.gridwidth = 1; // width
		c.gridheight = 1; // height
		c.weightx = 0;
                c.weighty = 0;
                panel.add(label, c);
                
                c.gridx = 1; // col
		c.gridy = i; // row
		c.gridwidth = 1; // width
		c.gridheight = 1; // height
                c.weightx = 1;
                c.weighty = (comps[i] instanceof JScrollPane)?1:0;
		panel.add(comps[i], c);
                
            }

            return JOptionPane.showConfirmDialog(this, panel,title,JOptionPane.OK_CANCEL_OPTION);
        }

        static class NewImage {
            public final BufferedImage bufferedImage;
            public final File file;
            public NewImage(BufferedImage bufferedImage,File file) {
                this.bufferedImage = bufferedImage;
                this.file = file;
            }
        }
        
	private NewImage getNewImage(boolean jpeg) {

		if (!RiskUIUtil.checkForNoSandbox()) {
			RiskUIUtil.showAppletWarning(RiskUIUtil.findParentFrame(this));
			return null;
		}

		try {

			JFileChooser fc = new JFileChooser( new File(new URI(RiskUIUtil.mapsdir.toString())) );

                        String[] extensions = ImageIO.getReaderFormatNames();
                        final ArrayList list = new ArrayList();
                        for (int c=0;c<extensions.length;c++) {
                            String extension = extensions[c].toLowerCase();
                            if (!list.contains(extension)) {
                                list.add(extension);
                            }
                        }

                        if (!jpeg) {
                            list.remove("jpg");
                            list.remove("jpeg");
                        }
                        
                        fc.setFileFilter( new FileFilter() {
                            public boolean accept(File file) {
                                if (file.isDirectory()) {
                                    return true;
                                }
                                String name = file.getName().toLowerCase();
                                for (int c=0;c<list.size();c++) {
                                    if (name.endsWith( (String)list.get(c) )) {
                                        return true;
                                    }
                                }
                                return false;
                            }
                            public String getDescription() {
                                return "Images "+list;
                            }
                        } );
                        
			int returnVal = fc.showOpenDialog( RiskUIUtil.findParentFrame(this) );

			if (returnVal == JFileChooser.APPROVE_OPTION) {

                            File newFile = fc.getSelectedFile();
                            
                            BufferedImage img = ImageIO.read( newFile );
                            
                            if (img!=null) {
                                return new NewImage(makeRGBImage(img),newFile);
                            }

                            JOptionPane.showMessageDialog(this,
                                    "no registered ImageReader claims to be able to read this file:\n"+
                                    newFile+"\n"+
                                    "supported file formats are:\n"+
                                    list );
			}
		}
		catch(Throwable ex) {
			RiskUtil.printStackTrace(ex);
			showError(ex);
		}

		return null;
	}

	private void removeBadMapColors() {
		java.util.Map updateMap = new HashMap();
		// go though ALL the colors that can be in the image map
		for (int c=0;c<256;c++) {
			if (myMap.getCountryInt(c)!=null) {
				updateMap.put(new Integer(c),new Integer(c));
			}
			else {
				updateMap.put(new Integer(c),new Integer(255));
			}
		}
		editPanel.update(updateMap);
		editPanel.repaint();
	}

        private void autodraw(Collection<Country> countries, boolean dots) {
            BufferedImage imgMap = editPanel.getImageMap();
            Graphics g = imgMap.getGraphics();
            int size = myMap.getCircleSize();
            for (Country country:countries) {
                Color color = new Color(country.getColor(), country.getColor(), country.getColor());
                if (dots) {
                    g.setColor(color);
                    g.fillOval(country.getX()-(size/2),country.getY()-(size/2),size,size);
                }
                else {
                    ImageUtil.floodFill(imgMap, country.getX(), country.getY(), color.getRGB());
                }
            }
            g.dispose();
            editPanel.repaintSelected();
        }

        public void delIslands(Collection<Country> countries) {
            Set<Integer> findColors = new HashSet();
            for (Country country : countries) {
                Color color = new Color(country.getColor(),country.getColor(),country.getColor());
                findColors.add(color.getRGB());
            }
            
            long startTime = System.currentTimeMillis();
            BufferedImage map = editPanel.getImageMap();
            int width = map.getWidth();
            int[] pixels = map.getRGB(0,0,width,map.getHeight(),null,0,width);

            Map<Integer,List<Integer>> colorToPositions = new HashMap();
            for (int c=0;c<pixels.length;c++) {
                if (findColors.contains(pixels[c])) {
                    List<Integer> positions = colorToPositions.get( pixels[c] );
                    if (positions==null) { positions = new ArrayList(); colorToPositions.put(pixels[c], positions); }
                    positions.add(c);
                }
            }

            List<List<Integer>> allIslands = new ArrayList();
            for (List<Integer> positions:colorToPositions.values()) {
                List<Integer> largestIsland=null;
                List<List<Integer>> islands = new ArrayList();
                while (!positions.isEmpty()) {
                    List<Integer> island = new ArrayList();
                    Stack<Integer> stack = new Stack();
                    stack.push( positions.get(0) );
                    while (!stack.isEmpty()) {
                        int position = stack.pop();
                        int index = positions.indexOf(position);
                        if (index>=0) {
                            int pos = positions.remove(index);
                            island.add(pos);
                            if (position >= width) {
                                stack.push( position-width ); // top
                            }
                            if (position % width != 0) {
                                stack.push( position-1 ); // left
                            }
                            if ((position+1) % width != 0) {
                                stack.push( position+1 ); // right
                            }
                            stack.push( position + width ); // bottom
                        }
                    }
                    islands.add(island);
                    if (largestIsland==null || island.size() > largestIsland.size()) {
                        largestIsland = island;
                    }
                }
                islands.remove(largestIsland);
                allIslands.addAll(islands);
            }
            System.out.println("finished! took "+(System.currentTimeMillis()-startTime));

            if (allIslands.isEmpty()) {
                JOptionPane.showMessageDialog(this, MapEditor.getCountiresListMessage(countries) + "\nNo islands found");
            }
            else {
                final Map<Integer,Integer> counts = new TreeMap(); // island size -> number of islands
                final Map<Integer,Set<Integer>> colors = new TreeMap(); // island size -> island colors
                for (List<Integer> island: allIslands) {
                    int islandSize = island.size();
                    if (counts.get(islandSize)==null) {
                        counts.put(islandSize, 1);
                        colors.put(islandSize, new TreeSet());
                    }
                    else {
                        counts.put(islandSize, counts.get(islandSize)+1);
                    }
                    colors.get(islandSize).add(Integer.valueOf(pixels[island.get(0).intValue()] & 0xff));
                }

                final List<Integer> islandSizes = new ArrayList(counts.keySet());
                final boolean[] del = new boolean[islandSizes.size()];
                TableModel islandsTable = new AbstractTableModel() {
                        private static final int BOOL_ROW = 3;
			private final String[] columnNames = {"size", "count", "Countries", "del"};
			public int getColumnCount() {
				return columnNames.length;
			}
			public String getColumnName(int col) {
				return columnNames[col];
			}
			public int getRowCount() {
				return islandSizes.size();
  			}
			public Object getValueAt(int row, int col) {
				switch (col) {
					case 0: return islandSizes.get(row);
					case 1: return counts.get(islandSizes.get(row));
					case 2: return colors.get(islandSizes.get(row));
                                        case BOOL_ROW: return del[row];
					default: throw new RuntimeException();
				}
			}
                        public boolean isCellEditable(int row, int col) {
                                return col == BOOL_ROW;
                        }
                        public Class<?> getColumnClass(int col) {
                                return col == BOOL_ROW ? Boolean.class : super.getColumnClass(col);
                        }
                        public void setValueAt(Object aValue, int row, int col) {
                                if (col != BOOL_ROW) throw new RuntimeException();
                                del[row] = (Boolean)aValue;
                        }
		};
/*
                StringBuilder table = new StringBuilder();
                table.append("<table border=\"1\"><tr><th>size</th><th>count</th></tr>");
                for (Integer islandSize: counts.keySet()) {
                    table.append("<tr><td>");
                    table.append( islandSize );
                    table.append("</td><td>");
                    table.append( counts.get(islandSize) );
                    table.append("</td></tr>");
                }
                table.append("</table>");
*/
                final JTable islandsJTable = new JTable(islandsTable);
                islandsJTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                        public void valueChanged(ListSelectionEvent event) {
                            if (!event.getValueIsAdjusting() && islandsJTable.getSelectedRow() != -1) {
                                Set<Integer> colorsForRow = colors.get(islandSizes.get(islandsJTable.getSelectedRow()));
                                editPanel.setSelectedCountry(myMap.getCountryInt(colorsForRow.iterator().next()));
                            }
                        }
                    });

                int result = JOptionPane.showConfirmDialog(this, new Object[] {
                    "<html>"+allIslands.size()+" islands found, are you sure you want to delete them from the map?",
                    new JScrollPane(islandsJTable)}, "Del Islands?", JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    for (List<Integer> island: allIslands) {
                        if (del[islandSizes.indexOf(island.size())]) {
                            for (int pos: island) {
                                pixels[pos] = 0xFFFFFFFF;
                            }
                        }
                    }
                    map.setRGB(0,0,width,map.getHeight(),pixels,0,width);
                    editPanel.repaintSelected();
                }
            }
        }

	static BufferedImage makeRGBImage(BufferedImage INipic) {

		BufferedImage ipic = new BufferedImage(INipic.getWidth(), INipic.getHeight(), BufferedImage.TYPE_INT_BGR);

		Graphics g1 = ipic.getGraphics();

		g1.setColor( Color.WHITE );

		g1.fillRect(0,0,INipic.getWidth(), INipic.getHeight());

		g1.drawImage(INipic,0,0,null);

		g1.dispose();

		return ipic;
	}

	public void zoom(boolean in) {
		setZoom( (in)?(zoomint+1):(zoomint-1) );
	}

	private void setZoom(int a) {

		if (a<ZOOM_MIN || a>ZOOM_MAX) { return; }

		zoomint = a;

		zoomout.setEnabled( !(a==ZOOM_MIN) );

		zoomin.setEnabled( !(a==ZOOM_MAX) );

		zoom.setText(zoomint+"x");

		editPanel.zoom(a);
	}

	public void showError(Throwable ex) {
		JOptionPane.showMessageDialog(this, "Error: "+ex.toString(), "ERROR!", JOptionPane.ERROR_MESSAGE);
		RiskUtil.printStackTrace(ex);
	}

	public BufferedImage getImageMap() {
		return editPanel.getImageMap();
	}


	public BufferedImage getImagePic() {
		return editPanel.getImagePic();
	}

	public static String getStringForContinent(Continent c, RiskGame context) {

		if (c == null) {
			return "0";
		}
		if (c == RiskGame.ANY_CONTINENT) {
			return "*";
		}

		Continent[] continents = context.getContinents();

		for (int i = 0; i < continents.length; i++) {
			if (continents[i] == c) {
				return String.valueOf(i+1);
			}
		}

		throw new RuntimeException();
	}

        private boolean checkNewImageMap(BufferedImage map) {
                int[] pixels = getAllPixels(map);
                int badPixels = 0;
                HashSet bad = new HashSet();
                for (int c=0;c<pixels.length;c++) {
                        Color color = new Color(pixels[c], true);
                        if (color.getRed() != color.getBlue() ||color.getRed() != color.getGreen()) {
                                bad.add(color);
                                badPixels++;
                        }
                }
                if (!bad.isEmpty()) {
                        showMessageDialog(this, "This image is not grayscale, ("+ bad.size() +" non greyscale colors, "+badPixels+" pixels)\n"
                                + "The ImageMap image should be grayscale.");
                        return false;
		}
		return true;
        }

	public boolean checkMap() {
		String errors="";

		if (myMap.getNoCountries() < 6) {
			errors = errors + "\n* Less then 6 countries on this map.";
		}
		else {
			List t = new ArrayList(Arrays.asList(myMap.getCountries()));
			List a = new ArrayList();

			Country country = ((Country)t.remove(0));
			a.add( country );

			myMap.getConnectedEmpire(
				t,
				a,
				country.getNeighbours(),
				null
			);

			if (a.size() != myMap.getNoCountries()) {
				errors = errors + "\n* Some countries are isolated from the rest: "+t;
			}
		}
                
                Country[] countries = myMap.getCountries();
                for (int a = 0; a < countries.length - 1; a++) {
                    for (int b = a + 1; b < countries.length; b++) {
                        if (countries[a].getX() > countries[b].getX() - myMap.getCircleSize()/2 &&
                                countries[a].getX() < countries[b].getX() + myMap.getCircleSize()/2 &&
                                countries[a].getY() > countries[b].getY() - myMap.getCircleSize()/2 &&
                                countries[a].getY() < countries[b].getY() + myMap.getCircleSize()/2) {
                            errors = errors + "\n* " + countries[a] + " circle overlaps " + countries[b] + " circle";
                        }
                    }
                }

		Continent[] continents = myMap.getContinents();

		for (int c=0;c<continents.length;c++) {
			if (continents[c].getTerritoriesContained().size() == 0) {
				errors = errors + "\n* The continent \""+continents[c]+"\" is empty.";
			}
		}


		BufferedImage map = editPanel.getImageMap();
		//if (map.getWidth()!=PicturePanel.PP_X || map.getHeight()!=PicturePanel.PP_Y) {
		//	errors = errors + "\n* Image Map is not a standard size of "+PicturePanel.PP_X+"x"+PicturePanel.PP_Y+".";
		//}

		BufferedImage pic = editPanel.getImagePic();
		if (pic.getWidth()!=map.getWidth() || pic.getHeight()!=map.getHeight()) {
			errors = errors + "\n* ImagePic and ImageMap are not the same size.";
		}


		int[] pixels = getAllPixels(map);

		int color,noc = myMap.getNoCountries();
		HashSet bad = new HashSet();
		HashSet good = new HashSet( Arrays.asList(myMap.getCountries()) );

		for (int c=0;c<pixels.length;c++) {

			color = pixels[c] & 0xff;

			if (color == 255) {
				// ignore
			}
			else if (color == 0 || color > noc) {
				bad.add( new Integer(color) );
			}
			else {
				good.remove( myMap.getCountryInt(color) );
			}
		}

		if (bad.size() > 0) {
			errors = errors + "\n* Image Map uses colors that do not match any country: "+bad;
		}
		if (good.size() > 0) {
			errors = errors + "\n* Image Map does not contain areas for some countries: "+good;
		}

		// missions checks:

		List missions = myMap.getMissions();

		if (missions.size()>0 && missions.size() <6) {
			errors = errors + "\n* You have chosen to have missions but you have less then is needed for a game with 6 players.";
		}

		for (int i = 0; i < missions.size(); i++) {

			Mission m = (Mission)missions.get(i);

                        if ("".equals( m.getDiscription() )) {
                            errors = errors + "\n* You have a mission with an empty Discription";
                        }

                        Player p = m.getPlayer();
			if (p !=null && m.getDiscription().indexOf( p.getName() ) == -1) {
				errors = errors + "\n* You have a mission that is to destroy "+p.getName()+", yet you do NOT have the text \""+p.getName()+"\" in the description.";
			}

                        if (m.getNoofcountries() > 0 && m.getNoofarmies() <= 0) {
                            errors = errors + "\n* You have a mission with impossible option: occupy "+m.getNoofcountries()+" countries with "+m.getNoofarmies()+" troops";
                        }
		}

		if (errors.length() > 0) {
                    
                    String errorMessage = "There are errors in this map that need to be fixed before it can be used:" + errors;
                    
                    if (bad.size() > 0) {
                        showMessageDialog(this, new Object[] {errorMessage, delBadColorsButton});
                    }
                    else {
			showMessageDialog(this, errorMessage);
                    }
		    return false;
		}
		return true;
	}
        
        private static int[] getAllPixels(BufferedImage map) {
            return map.getRGB(0, 0, map.getWidth(), map.getHeight(), null, 0, map.getWidth());
        }
        
        private void showMessageDialog(Component c, Object string) {
            System.out.println(string);
            JOptionPane pane = new JOptionPane() {
                public int getMaxCharactersPerLineCount() {
                    return 100;
                }
            };
            pane.setMessage(string);
            JDialog dialog = pane.createDialog(c, UIManager.getString("OptionPane.messageDialogTitle") );
            dialog.setVisible(true);
        }

        // ####################################################### MAKE CARDS FILE
        private String buildCardsFile(String cardsName) throws Exception {

            String n = System.getProperty("line.separator");
            
	    StringBuffer cardsBuffer = new StringBuffer();

	    cardsBuffer.append("; cards: ");
	    cardsBuffer.append(cardsName);
	    cardsBuffer.append(n);

	    cardsBuffer.append("; Made with yura.net ");
            cardsBuffer.append(RiskUtil.GAME_NAME);
            cardsBuffer.append(" ");
	    cardsBuffer.append(RiskUtil.RISK_VERSION);
	    cardsBuffer.append(n);
            
            cardsBuffer.append("; OS: ");
            cardsBuffer.append( RiskUIUtil.getOSString() );
	    cardsBuffer.append(n);

	    cardsBuffer.append(n);
	    cardsBuffer.append("[cards]");
	    cardsBuffer.append(n);

	    List cards = myMap.getCards();

            for (int i = 0; i < cards.size(); i++) {

                Card c = (Card)cards.get(i);

		cardsBuffer.append(c.getName());

		if (c.getCountry()!=null) {

			int color = c.getCountry().getColor();

			if (strictcards && color != (i+1)) { throw new Exception("cards missmatch with pos/id/color: "+c); }

			cardsBuffer.append(" ");
			cardsBuffer.append( String.valueOf(color) );

		}

		cardsBuffer.append(n);
	    }

	    List missions = myMap.getMissions();

	    if (missions.size()>0) {

	    	cardsBuffer.append(n);
	    	cardsBuffer.append("; destroy x occupy x x continents x x x");
	    	cardsBuffer.append(n);
	    	cardsBuffer.append("; destroy (Player) occupy (int int) continents (Continent Continent Continent)");
	    	cardsBuffer.append(n);
	    	cardsBuffer.append("[missions]");
	    	cardsBuffer.append(n);

		for (int i = 0; i < missions.size(); i++) {
			Mission m = (Mission)missions.get(i);
			cardsBuffer.append(getMissionString(m, myMap));
			cardsBuffer.append(n);
		}
	    }

            return cardsBuffer.toString();
        }

        private static String getMissionString(Mission m, RiskGame context) {
            StringBuffer cardsBuffer = new StringBuffer();
            
            if (m.getPlayer()!=null) {
                    cardsBuffer.append( m.getPlayer().getName().substring(6,7) ); // PLAYER1
            }
            else {
                    cardsBuffer.append("0");
            }

            cardsBuffer.append("\t");
            cardsBuffer.append(String.valueOf( m.getNoofcountries() ));
            cardsBuffer.append(" ");
            cardsBuffer.append(String.valueOf( m.getNoofarmies() ));
            cardsBuffer.append("\t");
            cardsBuffer.append( getStringForContinent(m.getContinent1(), context) );
            cardsBuffer.append(" ");
            cardsBuffer.append( getStringForContinent(m.getContinent2(), context) );
            cardsBuffer.append(" ");
            cardsBuffer.append( getStringForContinent(m.getContinent3(), context) );
            cardsBuffer.append("\t");
            cardsBuffer.append(m.getDiscription());
            
            return cardsBuffer.toString();
        }

        // ####################################################### MAKE MAP FILE
        private String buildMapFile(String mapName, String cardsName, String imageMapName, String imagePicName) throws Exception {

            String n = System.getProperty("line.separator");
            
            StringBuffer buffer = new StringBuffer();

            buffer.append("; map: ");
            buffer.append(mapName);
            buffer.append(n);

            buffer.append("; Made with yura.net ");
            buffer.append(RiskUtil.GAME_NAME);
            buffer.append(" ");
	    buffer.append(RiskUtil.RISK_VERSION);
            buffer.append(n);

            buffer.append("; OS: ");
            buffer.append( RiskUIUtil.getOSString() );
            buffer.append(n);
            buffer.append(n); // empty line

//            String name = myMap.getMapName();
//            if (name!=null) {
//                buffer.append("name ");
//                buffer.append(name);
//                buffer.append(n);
//            }
//            int version = myMap.getVersion();
//            if (version!=1) { // 1 is the default, we do not need to save that
//                buffer.append("ver ");
//                buffer.append(version);
//                buffer.append(n);
//            }
//            if (name!=null || version!=1) {
//                buffer.append(n); // in case we put a name or a version, add a extra empty line
//            }

            Map properties = myMap.getProperties();
            if (!properties.isEmpty()) {
                Iterator keyvals = properties.entrySet().iterator();
                while (keyvals.hasNext()) {
                    Map.Entry entry = (Map.Entry)keyvals.next();
                    buffer.append( entry.getKey() );
                    buffer.append(' ');
                    buffer.append( entry.getValue() );
                    buffer.append(n);
                }
                buffer.append(n);
            }

            buffer.append("[files]");
            buffer.append(n);

            buffer.append("pic ");
            buffer.append(imagePicName);
            buffer.append(n);
            buffer.append("map ");
            buffer.append(imageMapName);
            buffer.append(n);
            buffer.append("crd ");
            buffer.append(cardsName);
            buffer.append(n);

            String prv = myMap.getPreviewPic();
            if (prv!=null) {
                buffer.append("prv ");
                buffer.append(prv);
                buffer.append(n);
            }

            buffer.append(n);
            buffer.append("[continents]");
            buffer.append(n);

            Continent[] continents = myMap.getContinents();

            for (int i = 0; i < continents.length; i++) {

                Continent c = continents[i];

                buffer.append(c.getIdString());
                buffer.append(" ");
                buffer.append(c.getArmyValue());
                buffer.append(" ");
		buffer.append( ColorUtil.getStringForColor( c.getColor() ) );
                buffer.append(n);
            }

            buffer.append(n);
            buffer.append("[countries]");
            buffer.append(n);

            Country[] countries = myMap.getCountries();

            for (int i = 0; i < countries.length; i++) {

                Country c = countries[i];

                int color = c.getColor();

		if (color != (i+1)) { throw new Exception("country missmatch with pos/id/color: "+c); }

                buffer.append(String.valueOf(color));
                buffer.append(" ");
                buffer.append(c.getIdString());
                buffer.append(" ");
		buffer.append( getStringForContinent(c.getContinent(), myMap));
                buffer.append(" ");
		buffer.append( c.getX() );
                buffer.append(" ");
		buffer.append( c.getY() );
                buffer.append(n);
            }


            buffer.append(n);
            buffer.append("[borders]");
            buffer.append(n);


            for (int i = 0; i < countries.length; i++) {

                Country c = countries[i];

		buffer.append(String.valueOf(i+1));


                List ney = c.getNeighbours();
                for (int j = 0; j < ney.size(); j++) {

                    Country n1 = (Country)ney.get(j);

                    buffer.append(" ");
		    buffer.append(String.valueOf( n1.getColor() ) );
                }

                buffer.append(n);

            }
            return buffer.toString();
        }

        // TODO really the net.yura.domination.mapstore.Map#getVersion() method should return a int
        public static int getVersion(net.yura.domination.mapstore.Map map) {
                String version = map.getVersion();
                if (version == null || "".equals(version)) {
                    return 1;
                }
                else {
                    return Integer.parseInt(version);
                }
        }

        /**
         * @return true if everything is ok, false if the user cancelled
         */
	public boolean saveMap(File mapFile) throws Exception {

            String mapName = mapFile.getName();

            net.yura.domination.mapstore.Map map2 = MapsTools.findMap( MapsTools.loadMaps() ,mapName);

            if (map2!=null) { // this means it has been published at least once
                int newVersion = getVersion(map2) + 1;
                myMap.setVersion( newVersion );
            }
            
	    String safeName = MapsTools.getSafeMapID(mapName);

	    String cardsName = safeName + "." + RiskFileFilter.RISK_CARDS_FILES;
	    String imageMapName = safeName+"_map."+IMAGE_MAP_EXTENSION;

            String pic_extension = IMAGE_PIC_EXTENSION;
            boolean doCopy = false;
            if (imgFile!=null && imgFile.exists() ) {
                String extension = MapsTools.getExtension(imgFile).toLowerCase();
                if ("jpeg".equals(extension)) { extension="jpg"; }

                // these are the file formats we do not want to re-encode
                if ("jpg".equals(extension) || "png".equals(extension) || "gif".equals(extension)) {
                    doCopy = true;
                    pic_extension = extension;
                }
            }

	    String imagePicName = safeName+"_pic."+pic_extension;

	    File cardsFile = new File( mapFile.getParentFile(),cardsName );
	    File imageMapFile = new File( mapFile.getParentFile(),imageMapName );
	    File imagePicFile = new File( mapFile.getParentFile(),imagePicName );

	    if (mapFile.exists() || cardsFile.exists() || imageMapFile.exists() || imagePicFile.exists()) {

		int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to replace:\n"+
		(mapFile.exists()?mapFile+"\n":"")+
		(cardsFile.exists()?cardsFile+"\n":"")+
		(imageMapFile.exists()?imageMapFile+"\n":"")+
		(imagePicFile.exists()?imagePicFile+"\n":""), "Replace?", JOptionPane.YES_NO_OPTION);

		if (result != JOptionPane.YES_OPTION) {
			return false;
		}
	    }
            
            if (usesDefaultCards && cardsSameAsDefaultRiskCards()) {
                cardsName = MapsTools.DEFAULT_RISK_CARD_SET;
            }
            else {
                String cardsBuffer = buildCardsFile(cardsName);
                saveMap(cardsBuffer, new FileOutputStream(cardsFile));
            }
            
	    String buffer = buildMapFile(mapName, cardsName, imageMapName, imagePicName);
            saveMap(buffer, new FileOutputStream(mapFile));

            saveImage( editPanel.getImageMap() , IMAGE_MAP_EXTENSION , imageMapFile );

            if (doCopy) {
                if (imgFile.equals( imagePicFile )) {
                    // do nothing
                    System.out.println("no change in pic, no save needed: "+imgFile);
                }
                else {
                    RiskUtil.copy(imgFile, imagePicFile);
                }
            }
            else {
                saveImage( editPanel.getImagePic() , IMAGE_PIC_EXTENSION , imagePicFile );
            }

            return true;
	}
        
        private boolean cardsSameAsDefaultRiskCards() {
            try {
                RiskGame risk = makeNewMap();
                risk.setMapfile("risk.map"); // just in case someone changed the defualt map in a config file
                risk.loadMap();
                risk.loadCards(true);
                
                if (!risk.getCards().equals(myMap.getCards()) || risk.getMissions().size() != myMap.getMissions().size()) {
                    return false;
                }
                
                for (int c = 0; c < myMap.getMissions().size(); c++) {
                    Mission m1 = (Mission)myMap.getMissions().get(c);
                    Mission m2 = (Mission)risk.getMissions().get(c);
                    if (!getMissionString(m1, myMap).equals(getMissionString(m2, risk))) {
                        return false;
                    }
                }
                return true;
            }
            catch (Exception ex) {
                RiskUtil.printStackTrace(ex);
                return false;
            }
        }

        void saveImage(BufferedImage im, String formatName, File output) throws Exception {
	    if ( !ImageIO.write( im , formatName , output ) ) {
		// this should NEVER happen
		throw new Exception("unable to save image files! "+output+" format="+formatName);
	    }
        }

    private void saveMap(String text, OutputStream outputStream) throws IOException {
            Writer output = null;
	    try {

                boolean utf8 = false;
                for (int c=0,l=text.length();c<l;c++) {
                    char ch = text.charAt(c);
                    if (ch >= 256) {
                        utf8 = true;
                        break;
                    }
                }

                if (utf8) {
                    outputStream.write(0xEF);
                    outputStream.write(0xBB);
                    outputStream.write(0xBF);
                    output = new BufferedWriter( new OutputStreamWriter(outputStream,"UTF-8") );
                    output.write("; 1.1.0.7+ (UTF-8)");
                    output.write( System.getProperty("line.separator") );
                }
                else {
                    output = new BufferedWriter( new OutputStreamWriter(outputStream,"ISO-8859-1") );
                }

		output.write( text );
            }
	    finally {
		if (output != null) output.close();
	    }
    }
}
