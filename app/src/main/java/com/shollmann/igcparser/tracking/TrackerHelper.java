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

package com.shollmann.igcparser.tracking;

public class TrackerHelper {

    private static final String FLIGHT_DISPLAYED = "Flight Displayed";
    private static final String FLIGHT_INFORMATION_CLOSE = "Flight Information Close";
    private static final String FLIGHT_INFORMATION_OPEN = "Flight Information Open";
    private static final String FLIGHT_PLAY = "Play Flight";
    private static final String FLIGHT_STOP = "Stop Flight";
    private static final String FLIGHT_FAST_FORWARD = "Fast Forward Flight";
    private static final String TAP_FILE = "Tap File";
    private static final String NO_FILES_FOUND = "No Files Found";

    public static void trackFlightDisplayed() {
        AnswersHelper.trackEvent(FLIGHT_DISPLAYED);
    }

    public static void trackCloseInformation() {
        AnswersHelper.trackEvent(FLIGHT_INFORMATION_CLOSE);

    }

    public static void trackOpenInformation() {
        AnswersHelper.trackEvent(FLIGHT_INFORMATION_OPEN);

    }

    public static void trackPlayFlight() {
        AnswersHelper.trackEvent(FLIGHT_PLAY);

    }

    public static void trackStopFlight() {
        AnswersHelper.trackEvent(FLIGHT_STOP);
    }

    public static void trackFastForwardFlight() {
        AnswersHelper.trackEvent(FLIGHT_FAST_FORWARD);
    }

    public static void trackTapFile() {
        AnswersHelper.trackEvent(TAP_FILE);
    }

    public static void trackNoFilesFound() {
        AnswersHelper.trackEvent(NO_FILES_FOUND);
    }
}
