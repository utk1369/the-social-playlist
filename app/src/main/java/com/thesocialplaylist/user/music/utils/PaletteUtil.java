package com.thesocialplaylist.user.music.utils;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import java.util.List;

/**
 * Created by user on 20-04-2017.
 */

public class PaletteUtil {

    private static Palette getPalette(Bitmap bmp) {
        return Palette.from(bmp).generate();
    }

    private static Palette.Swatch getSwatch(Bitmap bmp) {
        Palette p = getPalette(bmp);
        Palette.Swatch vibrantSwatch = p.getVibrantSwatch();
        if(vibrantSwatch == null) {
            Palette.Swatch dominantSwatch = p.getDominantSwatch();
            if(dominantSwatch == null) {
                List<Palette.Swatch> swatches = p.getSwatches();
                if(swatches != null && swatches.size() > 0) {
                    return swatches.get(0);
                }
            }
            return dominantSwatch;
        }
        return vibrantSwatch;
    }

    public static int getToolbarBackgroundColor(Bitmap background) {
        Palette.Swatch swatch = getSwatch(background);
        return swatch.getRgb();
    }

    public static int getTitleTextColor(Bitmap background) {
        Palette.Swatch swatch = getSwatch(background);
        return swatch.getTitleTextColor();
    }

    public static int getBodyTextColor(Bitmap background) {
        Palette.Swatch swatch = getSwatch(background);
        return swatch.getBodyTextColor();
    }
}
