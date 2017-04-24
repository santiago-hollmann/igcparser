/*
 * MIT License
 *
 * Copyright (c) 2017 Santiago Hollmann
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

package com.shollmann.igcparser.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesHelper {

    private final String IS_RATED = "is_rated";
    private final String FLIGHT_VIEWED = "flight_viewed";
    private final String MIN_FLIGHTS_TO_RATE = "min_flights_to_rate";
    private final String AREA_WIDTH = "area_width";
    private final String START_LENGTH = "start_length";
    private final String FINISH_LENGTH = "finish_length";
    private SharedPreferences prefs;

    public PreferencesHelper(Context appContext) {
        prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
    }

    public int get(String key, int _default) {
        return prefs.getInt(key, _default);
    }

    public String get(String key, String _default) {
        return prefs.getString(key, _default);
    }

    public float get(String key, float _default) {
        return prefs.getFloat(key, _default);
    }

    public boolean get(String key, boolean _default) {
        return prefs.getBoolean(key, _default);
    }

    public long get(String key, long _default) {
        return prefs.getLong(key, _default);
    }

    public void set(String key, long value) {
        prefs.edit().putLong(key, value).commit();
    }

    public void set(String key, int value) {
        prefs.edit().putInt(key, value).commit();
    }

    public void set(String key, String value) {
        prefs.edit().putString(key, value).commit();
    }

    public void set(String key, float value) {
        prefs.edit().putFloat(key, value).commit();
    }

    public void set(String key, boolean value) {
        prefs.edit().putBoolean(key, value).commit();
    }

    public void remove(String key) {
        prefs.edit().remove(key).commit();
    }

    public void setIsRated() {
        set(IS_RATED, true);
    }

    public boolean isRated() {
        return get(IS_RATED, false);
    }

    public void resetViewedFlightsForRate() {
        set(FLIGHT_VIEWED, 0);
    }

    public void setViewedFlightsForRate() {
        int flightViewed = getViewedFlightCountForRate();
        flightViewed++;
        set(FLIGHT_VIEWED, flightViewed);
    }

    public int getViewedFlightCountForRate() {
        return get(FLIGHT_VIEWED, 0);
    }


    public int getMinFlightsViewedToRate() {
        return get(MIN_FLIGHTS_TO_RATE, Constants.App.MIN_FLIGHTS_TO_RATE_APP);
    }

    public void setMinFlightsViewedToRate() {
        int flightViewed = getMinFlightsViewedToRate();
        flightViewed = (int) (flightViewed * Constants.App.MIN_FLIGHTS_TO_RATE_APP_MULTIPLIER);
        set(MIN_FLIGHTS_TO_RATE, flightViewed);
    }

    public int getAreaWidth() {
        return get(AREA_WIDTH, Constants.Task.AREA_WIDTH_IN_METERS);
    }

    public void setAreaWidth(int value) {
        set(AREA_WIDTH, value);
    }


    public int getStartLength() {
        return get(START_LENGTH, Constants.Task.START_IN_METERS);
    }

    public void setStartLength(int value) {
        set(START_LENGTH, value);
    }


    public int getFinishLength() {
        return get(FINISH_LENGTH, Constants.Task.FINISH_IN_METERS);
    }

    public void setFinishLength(int value) {
        set(FINISH_LENGTH, value);
    }
}
