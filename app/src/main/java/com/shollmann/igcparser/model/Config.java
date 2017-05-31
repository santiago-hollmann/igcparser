package com.shollmann.igcparser.model;

import android.content.Context;

import com.shollmann.android.igcparser.model.ParserConfig;
import com.shollmann.igcparser.R;
import com.shollmann.igcparser.util.Constants;

public class Config extends ParserConfig {
    private static int trackWidthType = Constants.Settings.TRACK_WIDTH_DEFAULT;
    private static int trackWidthInPixels;

    public static int getTrackWidthType() {
        return trackWidthType;
    }

    public static void setTrackWidth(Context context, int trackWidth) {
        Config.trackWidthType = trackWidth;
        switch (trackWidth) {
            case Constants.Settings.TRACK_WIDTH_DEFAULT:
                Config.trackWidthInPixels = context.getResources().getDimensionPixelSize(R.dimen.track_line_width_default);
                break;
            case Constants.Settings.TRACK_WIDTH_BIG:
                Config.trackWidthInPixels = context.getResources().getDimensionPixelSize(R.dimen.track_line_width_big);
                break;
            case Constants.Settings.TRACK_WIDTH_EXTRA_BIG:
                Config.trackWidthInPixels = context.getResources().getDimensionPixelSize(R.dimen.track_line_width_extra_big);
                break;
        }
    }

    public static int getTrackWidthInPixels() {
        return trackWidthInPixels;
    }
}
