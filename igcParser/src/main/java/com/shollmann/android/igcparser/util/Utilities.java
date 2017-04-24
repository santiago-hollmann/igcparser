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

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.shollmann.android.igcparser.model.ILatLonRecord;
import com.shollmann.android.igcparser.model.LatLon;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Utilities {
    private static final double METERS_IN_ONE_KILOMETER = 1000;
    private static DecimalFormat decimalFormatter = null;
    private static DecimalFormatSymbols symbols = null;

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

    public static List<LatLng> getLatLngPoints(List<ILatLonRecord> listRecord) {
        ArrayList<LatLng> listLatLng = new ArrayList<>();
        if (listRecord != null) {
            for (ILatLonRecord record : listRecord) {
                if (record != null && record.getLatLon() != null) {
                    final double lat = record.getLatLon().getLat();
                    final double lon = record.getLatLon().getLon();
                    if (lat != 0 && lon != 0) {
                        listLatLng.add(new LatLng(lat, lon));
                    }
                }
            }
        }
        return listLatLng;
    }

    public static LatLng getLatLng(ILatLonRecord record) {
        LatLng latLng = new LatLng(0, 0);
        if (record != null && record.getLatLon() != null) {
            final double lat = record.getLatLon().getLat();
            final double lon = record.getLatLon().getLon();
            latLng = new LatLng(lat, lon);
        }
        return latLng;
    }

    public static String generateTime(String igcTime) {
        try {
            final String hoursString = igcTime.substring(0, 2);
            final String minutesString = igcTime.substring(2, 4);
            final String secondsString = igcTime.substring(4, 6);
            return hoursString + ":" + minutesString + ":" + secondsString;
        } catch (Throwable e) {
            Logger.logError(e.getMessage());
        }
        return Constants.EMPTY_STRING;
    }

    public static String getFlightTime(String departureTime, String landingTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            Date departureDate = sdf.parse(departureTime);
            Date landingDate = sdf.parse(landingTime);

            long diff = landingDate.getTime() - departureDate.getTime();
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000);

            return String.format(Constants.FLIGHT_DURATION_FORMAT, diffHours, diffMinutes);
        } catch (Throwable e) {
            Logger.logError(e.getMessage());
        }
        return Constants.FLIGHT_DURATION_ERROR;
    }

    public static long getDiffTimeInSeconds(String startTime, String finishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            Date departureDate = sdf.parse(startTime);
            Date landingDate = sdf.parse(finishTime);

            long diff = landingDate.getTime() - departureDate.getTime();
            return TimeUnit.MILLISECONDS.toSeconds(diff);
        } catch (Throwable e) {
            Logger.logError(e.getMessage());
        }
        return 3600;
    }

    public static int calculateAverageSpeed(double distance, long flightTimeSeconds) {
        double metersPerSeconds = distance / flightTimeSeconds;
        return (int) (metersPerSeconds * Constants.Calculation.M_SECOND_KM_HOUR);
    }

    public static String getTimeHHMM(String timeHHMMSS) {
        try {
            return timeHHMMSS.substring(0, timeHHMMSS.length() - 3);
        } catch (Throwable e) {
            Logger.logError(e.getMessage());
        }
        return Constants.EMPTY_STRING;
    }

    public static String getFormattedNumber(int number, Locale locale) {
        if (decimalFormatter == null) {
            symbols = DecimalFormatSymbols.getInstance(locale);
            decimalFormatter = new DecimalFormat();
            decimalFormatter.setDecimalFormatSymbols(symbols);
        }
        return decimalFormatter.format(number);
    }

    public static File getXCSoarDataFolder() {
        File xcsoarDataFile = new File(Constants.Path.XCSOARDATA);

        if (xcsoarDataFile.listFiles() != null) {
            return xcsoarDataFile;
        }

        xcsoarDataFile = new File(Constants.Path.SAMSUNG_XCSOARDATA);
        if (xcsoarDataFile.listFiles() != null) {
            return xcsoarDataFile;
        }

        xcsoarDataFile = new File(Constants.Path.STORAGE_XCSOARDATA);
        if (xcsoarDataFile.listFiles() != null) {
            return xcsoarDataFile;
        }

        xcsoarDataFile = new File(Constants.Path.STORAGE_LEGACY_XCSOARDATA);
        if (xcsoarDataFile.listFiles() != null) {
            return xcsoarDataFile;
        }

        return xcsoarDataFile;
    }

    public static File getSdCardFolder() {
        File xcsoarDataFile = new File(Constants.Path.SDCARD);
        if (xcsoarDataFile.listFiles() != null) {
            return xcsoarDataFile;
        }

        xcsoarDataFile = new File(Constants.Path.STORAGE_SDCARD);
        if (xcsoarDataFile.listFiles() != null) {
            return xcsoarDataFile;
        }

        xcsoarDataFile = new File(Constants.Path.STORAGE_LEGACY_SDCARD);
        if (xcsoarDataFile.listFiles() != null) {
            return xcsoarDataFile;
        }

        xcsoarDataFile = new File(Constants.Path.SAMSUNG_SDCARD);
        if (xcsoarDataFile.listFiles() != null) {
            return xcsoarDataFile;
        }

        return xcsoarDataFile;
    }

    public static String capitalizeText(String title) {
        if (title == null) {
            return null;
        }
        StringBuilder standardizeTitle = new StringBuilder();
        for (String word : title.split(Constants.SPACE_STRING)) {
            word = word.toLowerCase();
            word = capitalize(word);
            standardizeTitle.append(word).append(Constants.SPACE_STRING);
        }

        return standardizeTitle.toString().trim();
    }

    private static String capitalize(String word) {
        if (TextUtils.isEmpty(word)) {
            return Constants.EMPTY_STRING;
        }
        char first = word.charAt(0);
        if (Character.isUpperCase(first)) {
            return word;
        } else {
            return Character.toUpperCase(first) + word.substring(1);
        }
    }

    @NonNull
    public static String getDistanceInKm(double distanceInMeters, Locale locale) {
        return Utilities.getFormattedNumber((int) (distanceInMeters / METERS_IN_ONE_KILOMETER), locale);
    }

    public static boolean isZero(double value) {
        return value >= -1 && value <= 0.1;
    }

    public static boolean isUnlikelyIGCFolder(File file) {
        if (file.isHidden()) {
            return true;
        }

        String pathUpperCase = file.getAbsolutePath().toUpperCase();
        if (pathUpperCase.contains(Constants.Path.DCIM)) {
            return true;
        }
        if (pathUpperCase.contains(Constants.Path.ANDROID)) {
            return true;
        }
        if (pathUpperCase.contains(Constants.Path.CAMERA)) {
            return true;
        }
        if (pathUpperCase.contains(Constants.Path.WHATSAPP)) {
            return true;
        }
        if (pathUpperCase.contains(Constants.Path.FACEBOOK)) {
            return true;
        }

        if (pathUpperCase.contains(Constants.Path.INSTAGRAM)) {
            return true;
        }
        if (pathUpperCase.contains(Constants.Path.SNAPCHAT)) {
            return true;
        }
        if (pathUpperCase.contains(Constants.Path.GOOGLE)) {
            return true;
        }
        if (pathUpperCase.contains(Constants.Path.PHOTOS)) {
            return true;
        }
        if (pathUpperCase.contains(Constants.Path.PICTURES)) {
            return true;
        }
        if (pathUpperCase.contains(Constants.Path.AUDIO)) {
            return true;
        }
        if (pathUpperCase.contains(Constants.Path.MOVIES)) {
            return true;
        }
        if (pathUpperCase.contains(Constants.Path.MUSIC)) {
            return true;
        }
        return false;
    }
}
