/*
 * MIT License
 *
 * Copyright (c) 2016 Santiago Hollmann
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
