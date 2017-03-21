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

package com.shollmann.android.igcparser.model;

import com.shollmann.android.igcparser.util.Constants;

public class TaskConfig {
    private static int areaWidth = Constants.Task.AREA_WIDTH_IN_METERS;
    private static int startLength = Constants.Task.START_IN_METERS;
    private static int finishLength = Constants.Task.FINISH_IN_METERS;

    public static int getFinishLength() {
        return finishLength;
    }

    public static void setFinishLength(int finishLength) {
        TaskConfig.finishLength = finishLength;
    }

    public static int getStartLength() {
        return startLength;
    }

    public static void setStartLength(int startLength) {
        TaskConfig.startLength = startLength;
    }

    public static int getAreaWidth() {
        return areaWidth;
    }

    public static void setAreaWidth(int areaWidth) {
        TaskConfig.areaWidth = areaWidth;
    }

}
