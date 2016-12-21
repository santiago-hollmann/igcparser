package com.shollmann.android.igcparser.util;

import com.shollmann.android.igcparser.model.LatLon;

public class Utilities {
    public static LatLon generateCoordinates(String lat, String lon) {
        double latDegrees = Double.valueOf(lat.substring(0, 2));
        double latMinutes = Double.valueOf(lat.substring(2, lat.length() - 1)) / 60;
        double latDecimalFormat = latDegrees + latMinutes;
        if (hasSuffix(lat, 'S')) {
            latDecimalFormat = -1 * latDecimalFormat;
        }

        double lonDegrees = Double.valueOf(lon.substring(0, 3));
        double lonMinutes = Double.valueOf(lon.substring(3, lon.length() - 1)) / 60;
        double lonDecimalFormat = lonDegrees + lonMinutes;
        if (hasSuffix(lon, 'W')) {
            lonDecimalFormat = -1 * lonDecimalFormat;
        }
        return new LatLon(latDecimalFormat, lonDecimalFormat);
    }

    private static boolean hasSuffix(String lat, char c) {
        return lat.charAt(lat.length() - 1) == c;
    }
}
