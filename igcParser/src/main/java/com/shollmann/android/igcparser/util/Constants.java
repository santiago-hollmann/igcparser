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

public class Constants {
    public static final String EMPTY_STRING = "";
    public static final String COLON = ":";
    public static final String SPACE_STRING = " ";
    public static final String SLASH = "/";

    public static final String FLIGHT_DURATION_FORMAT = "%1$dh %2$dm";
    public static final String FLIGHT_DURATION_ERROR = "EE:EE";
    public static final int MARKER_TAKE_OFF_HEIGHT = 10;


    public static class Path {
        public static String XCSOARDATA = "/sdcard/XCSoarData/logs";
        public static String SAMSUNG_XCSOARDATA = "/sdcard/external_sd/XCSoarData/logs";
        public static String STORAGE_XCSOARDATA = "/storage/emulated/0/XCSoarData/logs";
        public static String STORAGE_LEGACY_XCSOARDATA = "/storage/emulated/legacy/XCSoarData/logs";
        public static String SDCARD = "/sdcard";
        public static String SAMSUNG_SDCARD = "/sdcard/external_sd";
        public static String STORAGE_SDCARD = "/storage/emulated/0";
        public static String STORAGE_LEGACY_SDCARD = "/storage/emulated/legacy";
    }

    public class CRecord {
        public static final String TAKEOFF = "TAKEOFF";
        public static final String START = "START";
        public static final String TURN = "TURN";
        public static final String FINISH = "FINISH";
        public static final String LANDING = "FINISH";
    }

    public class GeneralRecord {
        public static final String PILOT = "HFPLTPILOT";
        public static final String PILOT_IN_CHARGE = "HFPLTPILOTINCHARGE";
        public static final String GLIDER_ID = "HFGIDGLIDERID";
        public static final String GLIDER_TYPE = "HFGTYGLIDERTYPE";
        public static final String DATE = "HFDTE";
    }


}
