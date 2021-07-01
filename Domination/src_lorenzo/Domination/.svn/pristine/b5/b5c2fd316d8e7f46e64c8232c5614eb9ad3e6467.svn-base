package net.yura.domination.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import net.yura.domination.engine.translation.TranslationBundle;
import net.yura.domination.mobile.PicturePanel;
import net.yura.mobile.gui.layout.XULLoader;
import javax.microedition.lcdui.Image;

public class ColorPickerActivity extends Activity {

    enum PlayerColor {
        PINK(Color.rgb(255, 175, 175), "pink"),
        RED(Color.RED, "red"),
        ORANGE(Color.rgb(255, 200, 0), "orange"),
        YELLOW(Color.YELLOW, "yellow"),
        GREEN(Color.GREEN, "green"),
        CYAN(Color.CYAN, "cyan"),
        BLUE(Color.BLUE, "blue"),
        MAGENTA(Color.MAGENTA, "magenta"),

        WHITE(Color.WHITE, "white"),
        LTGRAY(Color.LTGRAY, "lightgray"),
        //GRAY(Color.GRAY, "gray"),
        DKGRAY(Color.DKGRAY, "darkgray"),
        BLACK(Color.BLACK, "black");

        public final int rgb;
        public final String name;
        PlayerColor(int rgb, String name) {
            this.rgb = rgb;
            this.name = name;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GridView grid = new GridView(this);
        grid.setNumColumns(4);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                PlayerColor color = (PlayerColor) parent.getItemAtPosition(position);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("data", color.rgb);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });

        final int size = XULLoader.adjustSizeToDensity(75);

        grid.setAdapter(new BaseAdapter() {
            @Override
            public PlayerColor getItem(int position) {
                return PlayerColor.values()[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public int getCount() {
                return PlayerColor.values().length;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = new View(ColorPickerActivity.this);
                    // TODO Find a better way to work out what type of LayoutParams is needed here.
                    convertView.setLayoutParams(new GridView.LayoutParams(size, size));
                }

                int color = getItem(position).rgb;
                ColorDrawable colorDrawable = new ColorDrawable(color);
                Image image = PicturePanel.getIconForColor(color);

                if (image == null) {
                    convertView.setBackgroundDrawable(colorDrawable);
                }
                else {
                    LayerDrawable layers = new LayerDrawable(new Drawable[] {colorDrawable, new BitmapDrawable(image.getBitmap())});
                    int xPad = (size - image.getWidth())/2;
                    int yPad = (size - image.getHeight())/2;
                    layers.setLayerInset(1, xPad, yPad, xPad, yPad);
                    convertView.setBackgroundDrawable(layers);
                }
                return convertView;
            }
        });

        setTitle(TranslationBundle.getBundle().getString("newgame.label.color"));
        setContentView(grid);

        // HACK: no idea why this is needed, but on tablets the width is too big is this is not here.
        grid.getLayoutParams().width = size * 4;
    }
}
