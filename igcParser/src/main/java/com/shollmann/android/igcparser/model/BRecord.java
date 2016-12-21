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

package com.shollmann.android.igcparser.model;

public class BRecord {
    private static final int TIME_START_INDEX = 1;
    private static final int TIME_END_INDEX = 7;
    private static final int LAT_END_INDEX = 15;
    private static final int LON_END_INDEX = 24;
    private static final int FIX_VALIDITY_START_INDEX = 25;
    private static final int ALTITUDE_PRESS_END_INDEX = 30;
    private static final int ALTITUDE_GPS_END_INDEX = 35;

    private long time;
    private String lat;
    private String lon;
    private int altitudePress;
    private int altitudeGps;


    public BRecord(String rawRecord) {
        time = Long.valueOf(rawRecord.substring(TIME_START_INDEX, TIME_END_INDEX));
        lat = rawRecord.substring(TIME_END_INDEX, LAT_END_INDEX);
        lon = rawRecord.substring(LAT_END_INDEX, LON_END_INDEX);
        altitudePress = Integer.valueOf(rawRecord.substring(FIX_VALIDITY_START_INDEX, ALTITUDE_PRESS_END_INDEX));
        altitudeGps = Integer.valueOf(rawRecord.substring(ALTITUDE_PRESS_END_INDEX, ALTITUDE_GPS_END_INDEX));
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public int getAltitudePress() {
        return altitudePress;
    }

    public void setAltitudePress(int altitudePress) {
        this.altitudePress = altitudePress;
    }

    public int getAltitudeGps() {
        return altitudeGps;
    }

    public void setAltitudeGps(int altitudeGps) {
        this.altitudeGps = altitudeGps;
    }
}
