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

package com.shollmann.igcparser.util;

public class Constants extends com.shollmann.android.igcparser.util.Constants {
    public static final String FILE_TO_LOAD_PATH = "file_to_load_path";
    public static final String IGC_FILE = "igc_file";

    public static class App {
        public static final String APP_PKG_NAME = "com.shollmann.igcparser";
        public static final String PLAYSTORE_APP_URL = "market://details?id=";
        public static final String PLAYSTORE_WEB_URL = "https://play.google.com/store/apps/details?id=";
        public static final String CONTENT_URI = "content";
        public static final String TEMP_TRACK_NAME = "tmp_record.igc";
        public static final String TEXT_HTML = "text/html";
        public static final String FILE_PROVIDER = "com.shollmann.fileprovider";
        public static final int MIN_FLIGHTS_TO_RATE_APP = 7;
    }

    public class Map {
        public static final int MAP_DEFAULT_ZOOM = 12;
        public static final int METERS_IN_ONE_KILOMETER = 1000;
        public static final double FIX_INITIAL_LATITUDE = 0.0195;
        public static final int DEFAULT_REPLAY_SPEED = 76;
        public static final double REPLAY_SPEED_INCREASE = 1.5;
        public static final int REPLAY_MIN_MULTIPLIER = 1;
        public static final int REPLAY_MAX_MULTIPLIER = 8;
        public static final int START_RADIUS = 5;
        public static final int FINISH_RADIUS = 1;
    }

    public class Chart {
        public static final float LABEL_SIZE = 14f;
        public static final int ANIMATION_DURATION = 400;
        public static final int ALPHA_FILL = 60;
        public static final int POINTS_SIMPLIFIER = 30;
    }
}
