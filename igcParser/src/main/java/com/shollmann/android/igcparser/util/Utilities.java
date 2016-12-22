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

import com.google.android.gms.maps.model.LatLng;
import com.shollmann.android.igcparser.model.BRecord;
import com.shollmann.android.igcparser.model.LatLon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utilities {
    public static LatLon generateCoordinates(String lat, String lon) {
        double latDegrees = Double.valueOf(lat.substring(0, 2));
        String latMinutes = lat.substring(2, 4);
        String latSeconds = lat.substring(4, lat.length() - 1);
        double latRealMinutes = Double.valueOf(latMinutes + "." + latSeconds) / 60;
        double latDecimalFormat = latDegrees + latRealMinutes;
        if (hasSuffix(lat, 'S')) {
            latDecimalFormat = -1 * latDecimalFormat;
        }

        double lonDegrees = Double.valueOf(lon.substring(0, 3));
        String lonMinutes = lon.substring(3, 5);
        String lonSeconds = lon.substring(5, lon.length() - 1);
        double lonRealMinutes = Double.valueOf(lonMinutes + "." + lonSeconds) / 60;
        double lonDecimalFormat = lonDegrees + lonRealMinutes;
        if (hasSuffix(lon, 'W')) {
            lonDecimalFormat = -1 * lonDecimalFormat;
        }
        return new LatLon(latDecimalFormat, lonDecimalFormat);
    }

    private static boolean hasSuffix(String lat, char c) {
        return lat.charAt(lat.length() - 1) == c;
    }

    public static List<LatLng> getLatLngPoints(List<BRecord> listBRecord) {
        ArrayList<LatLng> listLatLng = new ArrayList<>();
        for (BRecord bRecord : listBRecord) {
            listLatLng.add(new LatLng(bRecord.getLatLon().getLat(), bRecord.getLatLon().getLon()));
        }
        return listLatLng;
    }

    public static String generateTime(String igcTime) {
        final String hoursString = igcTime.substring(0, 2);
        final String minutesString = igcTime.substring(2, 4);
        final String secondsString = igcTime.substring(4, 6);
        return hoursString + ":" + minutesString + ":" + secondsString;
    }

    public static String getFlightTime(String departureTime, String landingTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            Date departureDate = sdf.parse(departureTime);
            Date landingDate = sdf.parse(landingTime);

            long diff = landingDate.getTime() - departureDate.getTime();
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000);

            return diffHours + ":" + diffMinutes;
        } catch (ParseException e) {
            Logger.log(e.getMessage());
        }
        return "EE:EE:EE";
    }

    public static String getTimeHHMM(String timeHHMMSS){
        return timeHHMMSS.substring(0, timeHHMMSS.length() - 3);
    }
}
