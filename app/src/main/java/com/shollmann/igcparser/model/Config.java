package com.shollmann.igcparser.model;

import android.content.Context;

import com.shollmann.android.igcparser.model.ParserConfig;
import com.shollmann.igcparser.R;
import com.shollmann.igcparser.util.Constants;

public class Config extends ParserConfig {
    private static int trackWidthType = Constants.Settings.TRACK_WIDTH_DEFAULT;
    private static int trackWidthInPixels;
    private static int machine = Constants.Settings.MACHINE_GLIDER;
    private static int machineDrawableResId = R.drawable.img_glider;

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

    public static void setMachine(int machine) {
        Config.machine = machine;
        switch (machine) {
            case Constants.Settings.MACHINE_GLIDER:
                Config.machineDrawableResId = R.drawable.img_glider;
                break;
            case Constants.Settings.MACHINE_PARAGLIDER:
                Config.machineDrawableResId = R.drawable.img_paraglider;
                break;
            case Constants.Settings.MACHINE_SMALL_PLANE:
                Config.machineDrawableResId = R.drawable.img_small_plane;
                break;
            case Constants.Settings.MACHINE_DELTAWING:
                Config.machineDrawableResId = R.drawable.img_deltawing;
                break;
        }
    }

    public static int getMachineDrawableResId() {
        return machineDrawableResId;
    }
}
