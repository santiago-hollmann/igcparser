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
