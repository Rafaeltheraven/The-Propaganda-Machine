// Yura Mamyrin

package net.yura.domination.engine.guishared;

import net.yura.swing.GraphicsUtil;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JPanel;
import net.yura.domination.engine.Risk;
import net.yura.domination.engine.RiskUIUtil;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.StatType;

/**
 * Statistics Graphs Panel
 * @author Yura Mamyrin
 */
public class StatsPanel extends JPanel {

    //private int spX;
    //private int spY;
    private Risk risk;
    private BufferedImage graph;

    public StatsPanel(Risk r) {

	//spX=x;
	//spY=y;

	risk=r;

	//Dimension size = new Dimension(spX , spY);

	//setPreferredSize(size);
	//setMinimumSize(size);
	//setMaximumSize(size);

    }

    public void paintComponent(Graphics g) {

	//super.paintComponent(g);

	if (graph != null) {
	    g.drawImage(graph, 0, 0, getWidth(), getHeight(), this);
	}
	else {
	    g.fillRect(0,0,getWidth(),getHeight());
	}

    }

    public void repaintStats(StatType a) {

        double scale = GraphicsUtil.scale;

	BufferedImage tempgraph = new BufferedImage((int)(getWidth() * scale), (int)(getHeight() * scale), BufferedImage.TYPE_INT_RGB ); // spX, spY

	List players = risk.getGame().getPlayersStats();

	int maxValue = 0;
        int maxTurns = 0;
	for (int i = 0; i < players.size(); i++) {

	    Player p = (Player)players.get(i);

	    double[] pstats= p.getStatistics(a);

            int max = pstats.length;
            if ( max > maxTurns) {
                maxTurns = max;
	    }

	    double sum=0;

	    for (int j = 0; j < pstats.length; j++) {
	      if (a.isSummable()) {
		  sum += pstats[j];
	      }
	      else {
                  if (pstats[j] > maxValue) {
		    maxValue = (int)pstats[j];
                  }
	      }
	    }

            if (sum > maxValue) {
                maxValue = (int)sum;
            }
        }

	// adds a space at the top and to the right of the graph
	maxValue++;
	maxTurns++;

	Graphics2D g2 = tempgraph.createGraphics();

	int xOffset = 30; // offset from the left
	int yOffset = 30; // offset from the bottom

	// size of devision
	gridSizeX = (tempgraph.getWidth()-xOffset-20f) /maxTurns; // the 20 is the right offset
	gridSizeY = (tempgraph.getHeight()-yOffset-20f) /maxValue; // the 20 is the top offset

	// the co-ords of the Zero Zero
	ZeroX = xOffset;
	ZeroY = tempgraph.getHeight()-yOffset;

	int bob = (int)Math.round(15f/gridSizeY);

	// draw - lines and numbers
	for (int i = 0; i <= maxValue ; i++) {

	    if ( i == maxValue || bob == 0 || ( i % bob )==0 ) {

		g2.setColor(Color.gray);
		g2.drawLine(ZeroX,(int)(ZeroY-(i*gridSizeY)),(int)(maxTurns*gridSizeX)+ZeroX,(int)(ZeroY-(i*gridSizeY)));

		g2.setColor(Color.white);

		String label = String.valueOf(i);

		g2.drawString(label, ZeroX-(6 + ( label.length()*7 )), (int)(ZeroY-(i*gridSizeY)+5));

	    }
	}

	int fred = (int)Math.round(20f/gridSizeX);

	// draw | lines and numbers
	for (int i = 0; i <= maxTurns ; i++) {

	    g2.setColor(Color.gray);
	    g2.drawLine((int)(ZeroX + (i*gridSizeX)),ZeroY,(int)(ZeroX +i*gridSizeX), (int)( ZeroY-( maxValue *gridSizeY) ) );

	    if ( i == maxTurns || fred == 0 || ( i % fred )==0 ) {

		g2.setColor(Color.white);
		g2.drawString(String.valueOf(i),(int)(i*gridSizeX + ZeroX-3),ZeroY+20);

	    }
        }

	g2.setColor(Color.white);
	g2.drawLine(ZeroX, ZeroY, (int)(ZeroX+(maxTurns*gridSizeX)), ZeroY); // -
	g2.drawLine(ZeroX, ZeroY, ZeroX, (int)(ZeroY-( maxValue*gridSizeY)) ); // |

	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	// set hints
	BasicStroke bs = new BasicStroke( 2.0f,BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER );
	g2.setStroke(bs);

	//draw each player graph.
	for (int i = 0; i < players.size(); i++) {
	    drawPlayerGraph(a, (Player)players.get(i) , g2);
	}

	g2.dispose();

	graph = tempgraph;

    }

    private int ZeroX;
    private int ZeroY;
    private double gridSizeX;
    private double gridSizeY;

    private void drawPlayerGraph(StatType a, Player p, Graphics2D g) {

	double[] PointToDraw = p.getStatistics(a);
	g.setColor(new Color( p.getColor() ) );

	double oldPoint = 0;
	double newPoint = 0;
	int i;

	for (i = 0; i < PointToDraw.length; i++) {

	    if (a.isSummable()) {
                newPoint += PointToDraw[i];
	    }
	    else {
                newPoint = PointToDraw[i];
	    }

            int x1 = (int)(ZeroX + i*gridSizeX);
            int y1 = (int)(ZeroY-(oldPoint*gridSizeY));
            int x2 = (int)(ZeroX +(i+1)*gridSizeX);
            int y2 = (int)(ZeroY-(newPoint*gridSizeY));

            Color color = g.getColor();
            Stroke stroke = g.getStroke();

            if (Color.BLACK.equals( color )) {
                g.setColor(Color.WHITE);
                g.setStroke( new BasicStroke(3) );
                g.drawLine(x1,y1,x2,y2);
                g.setColor(color);
                g.setStroke(stroke);
            }

	    g.drawLine(x1,y1,x2,y2);
	    oldPoint = newPoint;
	}

        int x = (int)(ZeroX + i*gridSizeX);
        int y = (int)(ZeroY - (oldPoint*gridSizeY)+11);

        Color color = g.getColor();

        if (Color.BLACK.equals( color )) {
            g.setColor(Color.WHITE);
            g.drawString(p.getName(),x+1,y+1);
            g.drawString(p.getName(),x-1,y-1);
            g.drawString(p.getName(),x+1,y-1);
            g.drawString(p.getName(),x-1,y+1);
            g.setColor(color);
        }

	g.drawString(p.getName(),x,y);

    }

}
