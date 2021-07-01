package net.yura.domination.android;

import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import net.yura.android.AndroidMeApp;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;
import net.yura.domination.mobile.flashgui.DominationMain;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import net.yura.domination.engine.core.StatType;
import net.yura.domination.engine.translation.TranslationBundle;

public class StatsActivity extends Activity {

    private ResourceBundle resb = TranslationBundle.getBundle();
    
    List<Player> getPlayersStats() {
        DominationMain dmain = (DominationMain)AndroidMeApp.getMIDlet();
        RiskGame game = dmain.risk.getGame();
        // if we open the stats activity at the same time as closing the game, avoid throwing a error
        return game==null?Collections.EMPTY_LIST:game.getPlayersStats();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setTitle( resb.getString("swing.tab.statistics") );
        showGraph( StatType.COUNTRIES );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        for (StatType statType : StatType.values()) {
            menu.add(android.view.Menu.NONE, statType.ordinal(), android.view.Menu.NONE,
                    resb.getString("swing.toolbar." + statType.getName()));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        
        int id = item.getItemId();
        showGraph(StatType.fromOrdinal(id));
        return true;
    }
    
    public void showGraph(StatType statType) {
        setTitle(resb.getString("swing.tab.statistics") + " - "
                + resb.getString("swing.toolbar." + statType.getName()));
        
        GraphicalView gview = ChartFactory.getLineChartView(this, getDataset(statType), getRenderer());
        setContentView(gview);
    }
    
    private XYMultipleSeriesRenderer getRenderer() {
        
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        
        List<Player> players = getPlayersStats();

        for (Player p : players) {
            SimpleSeriesRenderer r = new XYSeriesRenderer();
            r.setColor( p.getColor() );
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }

    public XYMultipleSeriesDataset getDataset(StatType statType) {

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        
        List<Player> players = getPlayersStats();

        //draw each player graph.
        for (Player p : players) {

            CategorySeries series = new CategorySeries( p.getName() );

            double[] PointToDraw = p.getStatistics(statType);

            double newPoint=0;

            series.add( newPoint ); // everything starts from 0

            for (double aPointToDraw : PointToDraw) {

                if (statType.isSummable()) {
                    newPoint += aPointToDraw;
                }
                else {
                    newPoint = aPointToDraw;
                }

                series.add( newPoint );

            }

            dataset.addSeries(series.toXYSeries());

        }
        
        return dataset;

    }

}
