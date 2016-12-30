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

public class Constants {
    public static final String FILE_TO_LOAD_PATH = "file_to_load_path";
    public static String XCSOAR_LOG_PATH = "/sdcard/XCSoarData/logs";

    public class Map {
        public static final int MAP_DEFAULT_ZOOM = 12;
        public static final int METERS_IN_ONE_KILOMETER = 1000;
        public static final float MAP_TRACK_POLYLINE_WIDTH = 3.0f;
        public static final double FIX_INITIAL_LATITUDE = 0.0195;
        public static final int DEFAULT_REPLAY_SPEED = 76;
        public static final int MAX_REPLAY_SPEED = 2;
        public static final double REPLAY_SPEED_INCREASER = 1.5;
    }
}
