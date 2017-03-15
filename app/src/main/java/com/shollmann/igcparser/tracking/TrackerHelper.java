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
    private static final String FLIGHT_SPEED_UP = "Speed Up Flight";
    private static final String FLIGHT_SPEED_DOWN = "Speed Down Flight";
    private static final String TAP_FILE = "Tap File";
    private static final String NO_FILES_FOUND = "No Files Found";
    private static final String ABOUT = "About";
    private static final String SORT_BY_PILOT = "Sort by Pilot";
    private static final String SORT_BY_GLIDER = "Sort by Glider";
    private static final String SORT_BY_DATE = "Sort by Date";
    private static final String SEARCH_SD_CARD = "Search SD Card";
    private static final String REFRESH = "Refresh";
    private static final String SORT_DIALOG = "Sort Dialog Open";
    private static final String SHOW_MORE_INFORMATION = "Show More Information";
    private static final String RATE_US_SHOW = "Rate Us: Show";
    private static final String RATE_US_YES = "Rate Us: Yes";
    private static final String RATE_US_NO = "Rate Us: No";
    private static final String SHARE_APP = "Share app";
    private static final String SHARE_FLIGHT = "Share flight";
    private static final String OPEN_GMAIL_FLIGHT = "Open Gmail Flight";
    private static final String OPEN_SETTINGS = "Open Settings";
    private static final String SETTINGS_CHANGE_AREA = "Change Area";
    private static final String SETTINGS_CHANGE_START = "Change Start";
    private static final String SETTINGS_CHANGE_FINISH = "Change Finish";

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
        AnswersHelper.trackEvent(FLIGHT_SPEED_UP);
    }

    public static void trackSpeedDownFlight() {
        AnswersHelper.trackEvent(FLIGHT_SPEED_DOWN);
    }

    public static void trackTapFile() {
        AnswersHelper.trackEvent(TAP_FILE);
    }

    public static void trackNoFilesFound() {
        AnswersHelper.trackEvent(NO_FILES_FOUND);
    }

    public static void trackAbout() {
        AnswersHelper.trackEvent(ABOUT);
    }

    public static void trackSortByPilot() {
        AnswersHelper.trackEvent(SORT_BY_PILOT);
    }

    public static void trackSortByGlider() {
        AnswersHelper.trackEvent(SORT_BY_GLIDER);
    }

    public static void trackSortByDate() {
        AnswersHelper.trackEvent(SORT_BY_DATE);
    }

    public static void trackSearchSdCard() {
        AnswersHelper.trackEvent(SEARCH_SD_CARD);
    }

    public static void trackRefresh() {
        AnswersHelper.trackEvent(REFRESH);
    }

    public static void trackSortDialog() {
        AnswersHelper.trackEvent(SORT_DIALOG);
    }

    public static void trackOpenMoreInformation() {
        AnswersHelper.trackEvent(SHOW_MORE_INFORMATION);
    }

    public static void trackRateUsShow() {
        AnswersHelper.trackEvent(RATE_US_SHOW);
    }

    public static void trackRateUsYes() {
        AnswersHelper.trackEvent(RATE_US_YES);
    }

    public static void trackRateUsNo() {
        AnswersHelper.trackEvent(RATE_US_NO);
    }

    public static void trackShareApp() {
        AnswersHelper.trackEvent(SHARE_APP);
    }

    public static void trackShareFlight() {
        AnswersHelper.trackEvent(SHARE_FLIGHT);
    }

    public static void trackOpenGmailFlight() {
        AnswersHelper.trackEvent(OPEN_GMAIL_FLIGHT);
    }

    public static void trackSettings() {
        AnswersHelper.trackEvent(OPEN_SETTINGS);
    }

    public static void trackChangeArea() {
        AnswersHelper.trackEvent(SETTINGS_CHANGE_AREA);
    }

    public static void trackChangeStart() {
        AnswersHelper.trackEvent(SETTINGS_CHANGE_START);
    }

    public static void trackChangeFinish() {
        AnswersHelper.trackEvent(SETTINGS_CHANGE_FINISH);
    }
}
