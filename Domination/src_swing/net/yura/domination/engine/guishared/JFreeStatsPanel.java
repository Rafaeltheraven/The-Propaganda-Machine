// Yura Mamyrin

package net.yura.domination.engine.guishared;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import javax.swing.JPanel;
import net.yura.domination.engine.Risk;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.StatType;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Statistics Graphs Panel
 * @author Yura Mamyrin
 */
public class JFreeStatsPanel extends JPanel {

    private Risk risk;

    public JFreeStatsPanel(Risk myrisk) {
        risk = myrisk;
        setLayout(new BorderLayout());

        ChartFactory.setChartTheme(StandardChartTheme.createDarknessTheme());
    }

    public void repaintStats(StatType statType) {
        // create the chart...
        JFreeChart chart = ChartFactory.createXYLineChart(null,  // chart title
                                                          null,                  // x axis label
                                                          null,                  // y axis label
                                                          getDataset(statType),  // data
                                                          PlotOrientation.VERTICAL,
                                                          true,                 // include legend
                                                          true,                 // tooltips
                                                          true                 // urls
                                                          );
        chart.getXYPlot().setRenderer(getRenderer());

/* manual way of setting colors
        // panel
        chart.setBackgroundPaint(Color.BLACK);
        // KEY
        LegendTitle legend = chart.getLegend();
        legend.setBackgroundPaint(Color.BLACK);
        legend.setItemPaint(Color.WHITE);
        // graph
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.BLACK);
        plot.getDomainAxis().setTickLabelPaint(Color.WHITE);
        plot.getRangeAxis().setTickLabelPaint(Color.WHITE);
*/
        //plot.setShadowGenerator(new DefaultShadowGenerator(6, Color.WHITE, 1, 0, 0));

        removeAll();
        add( new ChartPanel(chart) );
        revalidate();
        repaint();
    }

    private XYItemRenderer getRenderer() {
        DefaultXYItemRenderer renderer = new DefaultXYItemRenderer();
        renderer.setBaseShapesVisible(false);
        List<Player> players = risk.getGame().getPlayers();
        for (int c=0;c<players.size();c++) {
            Color color = new Color(players.get(c).getColor());
            renderer.setSeriesPaint(c, color);
            renderer.setSeriesStroke(c, new BasicStroke(2));
        }
        return renderer;
    }

    private XYSeriesCollection getDataset(StatType statType) {
        // create a dataset...
        XYSeriesCollection dataset = new XYSeriesCollection();
        List<Player> players = risk.getGame().getPlayersStats();
        for (Player player: players) {
            XYSeries series = new XYSeries( player.getName() );
            double[] PointToDraw = player.getStatistics(statType);
            double newPoint=0;
            series.add( 0, newPoint ); // everything starts from 0
            for (int c=0;c<PointToDraw.length;c++) {
                double aPointToDraw = PointToDraw[c];
                if (statType.isSummable()) {
                    newPoint += aPointToDraw;
                }
                else {
                    newPoint = aPointToDraw;
                }
                series.add( c+1, newPoint );
            }
            dataset.addSeries(series);
        }
        return dataset;
    }
}
