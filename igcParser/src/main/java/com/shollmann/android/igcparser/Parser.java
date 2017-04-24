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

package com.shollmann.android.igcparser;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.maps.android.SphericalUtil;
import com.shollmann.android.igcparser.model.BRecord;
import com.shollmann.android.igcparser.model.CRecordWayPoint;
import com.shollmann.android.igcparser.model.IGCFile;
import com.shollmann.android.igcparser.util.Constants;
import com.shollmann.android.igcparser.util.CoordinatesUtilities;
import com.shollmann.android.igcparser.util.Logger;
import com.shollmann.android.igcparser.util.Utilities;
import com.shollmann.android.igcparser.util.WaypointUtilities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Parser {

    public static IGCFile parse(Uri filePath) {
        IGCFile igcFile = new IGCFile();
        int maxAltitude = -10000;
        int minAltitude = 10000;
        boolean isFirstCRecord = true;
        BRecord firstBRecord = null;
        String takeOffTime = Constants.EMPTY_STRING;
        String landingTime = Constants.EMPTY_STRING;
        BufferedReader reader = null;
        HashMap<String, Integer> mapAreaReached = new HashMap<>();
        try {
            reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filePath.toString()), "UTF-8"));

            String line;
            while ((line = reader.readLine()) != null) {
                if (isBRecord(line)) {
                    if (firstBRecord == null) { // Classify waypoints to later determine reached areas
                        WaypointUtilities.classifyWayPoints(igcFile.getWaypoints());
                    }

                    BRecord bRecord = new BRecord(line);

                    firstBRecord = setFirstBRecord(bRecord, firstBRecord);
                    maxAltitude = setMaxAltitude(bRecord, maxAltitude);
                    minAltitude = setMinAltitude(bRecord, minAltitude);
                    takeOffTime = setTakeOffTime(firstBRecord, takeOffTime, bRecord);
                    landingTime = bRecord.getTime();

                    igcFile.appendTrackPoint(bRecord);

                    if (igcFile.getWaypoints() != null && !igcFile.getWaypoints().isEmpty()) {
                        WaypointUtilities.calculateReachedAreas(igcFile.getTrackPoints().size() - 1, bRecord, igcFile, mapAreaReached);
                    }
                } else {
                    if (isCRecord(line)) {
                        if (!isFirstCRecord) {
                            CRecordWayPoint waypoint = new CRecordWayPoint(line);
                            if (!CoordinatesUtilities.isZeroCoordinate(waypoint)) {
                                igcFile.appendWayPoint(waypoint);
                            }
                        }
                        isFirstCRecord = false;
                    } else {
                        if (isPilotInChargeRecord(line)) {
                            igcFile.setPilotInCharge(getValueOfColonField(line));
                        }

                        if (isGliderIdRecord(line)) {
                            igcFile.setGliderId(getValueOfColonField(line));
                        }

                        if (isGliderTypeRecord(line)) {
                            igcFile.setGliderType(getValueOfColonField(line));
                        }

                        if (isDateRecord(line)) {
                            igcFile.setDate(parseDate(line));
                        }
                    }
                }
            }
            igcFile.setStartAltitude(firstBRecord != null ? firstBRecord.getAltitude() : 0);
            igcFile.setMaxAltitude(maxAltitude);
            igcFile.setMinAltitude(minAltitude);
            igcFile.setTakeOffTime(TextUtils.isEmpty(takeOffTime) && firstBRecord != null ? firstBRecord.getTime() : takeOffTime);
            igcFile.setLandingTime(landingTime);
            igcFile.setFileData(filePath);
            double distance = SphericalUtil.computeLength(Utilities.getLatLngPoints(igcFile.getTrackPoints()));
            igcFile.setDistance(distance);
            // By default we discount a 500 meters tug which means 8000 meters length and 240 seconds
            igcFile.setAverageSpeed(Utilities.calculateAverageSpeed(distance - Constants.Calculation.TUG_DEFAULT_DISTANCE_METERS,
                    Utilities.getDiffTimeInSeconds(takeOffTime, landingTime) - Constants.Calculation.TUG_DEFAULT_DURATION_SECONDS));

            if (!igcFile.getWaypoints().isEmpty()) {
                calculateTaskStats(igcFile, mapAreaReached);
            }

        } catch (IOException e) {
            Logger.logError(e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Logger.logError(e.getMessage());
                }
            }
        }
        Logger.log(igcFile.toString());
        return igcFile;
    }

    private static void calculateTaskStats(IGCFile igcFile, HashMap<String, Integer> mapAreaReached) {
        igcFile.setTaskDistance(WaypointUtilities.calculateTaskDistance(igcFile.getWaypoints()));
        igcFile.setTaskCompleted(WaypointUtilities.isTaskCompleted(igcFile.getWaypoints(), mapAreaReached));
        igcFile.setTraveledTaskDistance(WaypointUtilities.getTaskTraveledDistance(igcFile, mapAreaReached));
        igcFile.setTaskAverageSpeed(WaypointUtilities.getTaskAverageSpeed(igcFile, mapAreaReached));
        igcFile.setTaskDuration(WaypointUtilities.getTaskDuration(igcFile, mapAreaReached));
    }


    public static IGCFile quickParse(Uri filePath) {
        IGCFile igcFile = new IGCFile();
        boolean isFirstCRecord = true;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filePath.toString()), "UTF-8"));

            String line;
            boolean isFinished = false;
            while ((line = reader.readLine()) != null && !isFinished) {
                if (isBRecord(line)) {
                    isFinished = true;
                }
                if (isCRecord(line)) {
                    if (!isFirstCRecord) {
                        igcFile.appendWayPoint(new CRecordWayPoint(line));
                    }
                    isFirstCRecord = false;
                } else {
                    if (isPilotInChargeRecord(line)) {
                        igcFile.setPilotInCharge(getValueOfColonField(line));
                    }

                    if (isGliderIdRecord(line)) {
                        igcFile.setGliderId(getValueOfColonField(line));
                    }

                    if (isGliderTypeRecord(line)) {
                        igcFile.setGliderType(getValueOfColonField(line));
                    }

                    if (isDateRecord(line)) {
                        igcFile.setDate(parseDate(line));
                    }
                }
            }

            if (!igcFile.getWaypoints().isEmpty()) {
                double taskDistance = SphericalUtil.computeLength(Utilities.getLatLngPoints(igcFile.getWaypoints()));
                igcFile.setTaskDistance(taskDistance);
            }

            igcFile.setFileData(filePath);

        } catch (IOException e) {
            Logger.logError(e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Logger.logError(e.getMessage());
                }
            }
        }
        Logger.log(igcFile.toString());
        return igcFile;
    }

    private static String parseDate(String line) {
        StringBuilder sb = new StringBuilder();
        line = line.toUpperCase().replace(Constants.GeneralRecord.DATE, Constants.EMPTY_STRING);
        try {
            sb.append(line.substring(0, 2)).append(Constants.SLASH);
            sb.append(line.substring(2, 4)).append(Constants.SLASH);
            sb.append(line.substring(4));
        } catch (Throwable t) {
            Logger.logError("Unable to parse date");
        }
        return sb.toString();
    }

    @NonNull
    private static String getValueOfColonField(String line) {
        try {
            String value = line.substring(line.indexOf(Constants.COLON), line.length());
            if (value.equals(Constants.COLON)) {
                return Constants.EMPTY_STRING;
            }
            return value.trim().replace(Constants.COLON, Constants.EMPTY_STRING).trim();
        } catch (Throwable t) {
            Logger.logError("Couldn't parse line: " + line);
            return Constants.EMPTY_STRING;
        }
    }

    private static boolean isPilotInChargeRecord(String line) {
        final String lineUpperCase = line.toUpperCase();
        return lineUpperCase.startsWith(Constants.GeneralRecord.PILOT) || lineUpperCase.startsWith(Constants.GeneralRecord.PILOT_IN_CHARGE);
    }

    private static boolean isGliderTypeRecord(String line) {
        final String lineUpperCase = line.toUpperCase();
        return lineUpperCase.startsWith(Constants.GeneralRecord.GLIDER_TYPE);
    }

    private static boolean isDateRecord(String line) {
        final String lineUpperCase = line.toUpperCase();
        return lineUpperCase.startsWith(Constants.GeneralRecord.DATE);
    }

    private static boolean isGliderIdRecord(String line) {
        final String lineUpperCase = line.toUpperCase();
        return lineUpperCase.startsWith(Constants.GeneralRecord.GLIDER_ID);
    }

    private static boolean isBRecord(String line) {
        return line.startsWith("B");
    }

    private static boolean isCRecord(String line) {
        return line.startsWith("C");
    }

    @Nullable
    private static String setTakeOffTime(BRecord firstBRecord, String takeOffTime, BRecord bRecord) {
        if (TextUtils.isEmpty(takeOffTime) && (bRecord.getAltitude() - firstBRecord.getAltitude() >= Constants.Calculation.MARKER_TAKE_OFF_HEIGHT)) {
            takeOffTime = bRecord.getTime();
        }
        return takeOffTime;
    }

    private static BRecord setFirstBRecord(BRecord bRecord, BRecord firstBRecord) {
        if (firstBRecord == null) {
            firstBRecord = bRecord;
        }
        return firstBRecord;
    }

    private static int setMinAltitude(BRecord bRecord, int minAltitude) {
        if (bRecord.getAltitude() < minAltitude) {
            minAltitude = bRecord.getAltitude();
        }
        return minAltitude;
    }

    private static int setMaxAltitude(BRecord bRecord, int maxAltitude) {
        if (bRecord.getAltitude() > maxAltitude) {
            maxAltitude = bRecord.getAltitude();
        }
        return maxAltitude;
    }

}
