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

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.shollmann.igcparser.IGCViewerApplication;

public class PreferencesHelper {

    static {
        prefs = PreferenceManager.getDefaultSharedPreferences(IGCViewerApplication.getApplication());
    }


    private static SharedPreferences prefs;

    public static void clear() {
        prefs.edit().clear().commit();
    }

    public static int get(String key, int _default) {
        return prefs.getInt(key, _default);
    }

    public static String get(String key, String _default) {
        return prefs.getString(key, _default);
    }

    public static float get(String key, float _default) {
        return prefs.getFloat(key, _default);
    }

    public static boolean get(String key, boolean _default) {
        return prefs.getBoolean(key, _default);
    }

    public static long get(String key, long _default) {
        return prefs.getLong(key, _default);
    }

    public static void set(String key, long value) {
        prefs.edit().putLong(key, value).commit();
    }

    public static void set(String key, int value) {
        prefs.edit().putInt(key, value).commit();
    }

    public static void set(String key, String value) {
        prefs.edit().putString(key, value).commit();
    }

    public static void set(String key, float value) {
        prefs.edit().putFloat(key, value).commit();
    }

    public static void set(String key, boolean value) {
        prefs.edit().putBoolean(key, value).commit();
    }

    public static void remove(String key) {
        prefs.edit().remove(key).commit();
    }

}
