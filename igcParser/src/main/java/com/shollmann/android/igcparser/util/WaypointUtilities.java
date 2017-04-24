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

package com.shollmann.android.igcparser.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.shollmann.android.igcparser.model.BRecord;
import com.shollmann.android.igcparser.model.CRecordType;
import com.shollmann.android.igcparser.model.CRecordWayPoint;
import com.shollmann.android.igcparser.model.IGCFile;
import com.shollmann.android.igcparser.model.ILatLonRecord;
import com.shollmann.android.igcparser.model.TaskConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class WaypointUtilities {
    public static void calculateReachedAreas(int positionBRecord, BRecord bRecord, IGCFile igcFile, HashMap<String, Integer> mapAreaReached) {
        List<ILatLonRecord> waypoints = igcFile.getWaypoints();
        boolean isPointToAdd = false;
        float[] distance = new float[2];

        for (int i = 0; i < waypoints.size(); i++) {
            final CRecordWayPoint waypoint = (CRecordWayPoint) waypoints.get(i);
            if (waypoint.getType() == CRecordType.TURN) {
                Location.distanceBetween(bRecord.getLatLon().getLat(), bRecord.getLatLon().getLon(),
                        waypoint.getLatLon().getLat(), waypoint.getLatLon().getLon(), distance);
                if (distance[0] <= TaskConfig.getAreaWidth()) {
                    isPointToAdd = true;
                }
            } else if (waypoint.getType() == CRecordType.START) {
                if (isLineCrossed(bRecord, waypoints.get(i), waypoints.get(i + 1), TaskConfig.getStartLength())) {
                    isPointToAdd = true;
                }
            } else if (waypoint.getType() == CRecordType.FINISH) {
                // Added check for i != 0 to avoid OutOfBoundException when doing i-1
                if (i != 0 && isLineCrossed(bRecord, waypoints.get(i), waypoints.get(i - 1), TaskConfig.getFinishLength())) {
                    isPointToAdd = true;
                }
            }

            if (isPointToAdd) {
                String waypointKey = waypoint.toString();
                if (mapAreaReached.get(waypointKey) == null) {
                    //We kept only the first point of the reached area
                    mapAreaReached.put(waypointKey, positionBRecord);
                }
            }

            isPointToAdd = false;
        }
    }

    private static boolean isLineCrossed(BRecord bRecord, ILatLonRecord oneRecord, ILatLonRecord otherRecord, float width) {
        PerpendicularLineCoordinates perpendicularLine = getPerpendicularLine(oneRecord, otherRecord, (int) width);

        float[] centerDistance = new float[2];
        Location.distanceBetween(bRecord.getLatLon().getLat(), bRecord.getLatLon().getLon(),
                perpendicularLine.center.latitude, perpendicularLine.center.longitude, centerDistance);
        if (centerDistance[0] <= Constants.Task.MIN_TOLERANCE_IN_METERS) {
            return true;
        }

        float[] startDistance = new float[2];
        Location.distanceBetween(bRecord.getLatLon().getLat(), bRecord.getLatLon().getLon(),
                perpendicularLine.start.latitude, perpendicularLine.start.longitude, startDistance);
        if (startDistance[0] <= Constants.Task.MIN_TOLERANCE_IN_METERS) {
            return true;
        }

        float[] endDistance = new float[2];
        Location.distanceBetween(bRecord.getLatLon().getLat(), bRecord.getLatLon().getLon(),
                perpendicularLine.end.latitude, perpendicularLine.end.longitude, endDistance);
        if (endDistance[0] <= Constants.Task.MIN_TOLERANCE_IN_METERS) {
            return true;
        }

        if (centerDistance[0] <= (width / 2)) { // Avoid doing this calculation if the point is not even close to the line
            float distanceToClosestEndLine = startDistance[0] > endDistance[0] ? endDistance[0] : startDistance[0];
            if (distanceToClosestEndLine + centerDistance[0] <= Constants.Task.MIN_TOLERANCE_IN_METERS + (width / 2)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTaskCompleted(List<ILatLonRecord> waypoints, HashMap<String, Integer> mapAreaReached) {
        for (ILatLonRecord waypoint : waypoints) {
            CRecordWayPoint cRecord = (CRecordWayPoint) waypoint;
            if (cRecord.getType() != CRecordType.TAKEOFF
                    && cRecord.getType() != CRecordType.LANDING
                    && mapAreaReached.get(cRecord.toString()) == null) {
                return false;
            }
        }
        return true;
    }

    public static void classifyWayPoints(List<ILatLonRecord> waypoints) {
        for (int i = 0; i < waypoints.size(); i++) {
            CRecordWayPoint aCRecord = (CRecordWayPoint) waypoints.get(i);
            int oppositePosition = waypoints.size() - 1 - i;
            CRecordWayPoint otherCRecord = (CRecordWayPoint) waypoints.get(oppositePosition);
            if (!includesTakeOffOrLanding(aCRecord)
                    && (!aCRecord.getDescription().equalsIgnoreCase(otherCRecord.getDescription())
                    || oppositePosition == i)) {
                aCRecord.setType(CRecordType.TURN);
            } else {
                if (i == 1) { //it has take off, start, finish and landing way points
                    ((CRecordWayPoint) waypoints.get(0)).setType(CRecordType.TAKEOFF);
                    ((CRecordWayPoint) waypoints.get(1)).setType(CRecordType.START);
                    ((CRecordWayPoint) waypoints.get(waypoints.size() - 2)).setType(CRecordType.FINISH);
                    ((CRecordWayPoint) waypoints.get(waypoints.size() - 1)).setType(CRecordType.LANDING);
                } else if (i == 0) {
                    ((CRecordWayPoint) waypoints.get(0)).setType(CRecordType.START);
                    ((CRecordWayPoint) waypoints.get(waypoints.size() - 1)).setType(CRecordType.FINISH);
                }
            }
        }
    }

    private static boolean includesTakeOffOrLanding(CRecordWayPoint aCRecord) {
        return aCRecord.getDescription().toUpperCase().contains("TAKE_OFF")
                || aCRecord.getDescription().toUpperCase().contains("TAKEOFF")
                || aCRecord.getDescription().toUpperCase().contains("LANDING");
    }

    public static double calculateTaskDistance(List<ILatLonRecord> waypoints) {
        ArrayList<ILatLonRecord> listCRecord = new ArrayList<>();
        for (ILatLonRecord waypoint : waypoints) {
            CRecordWayPoint cRecord = (CRecordWayPoint) waypoint;
            if (cRecord.getType() != CRecordType.LANDING && cRecord.getType() != CRecordType.TAKEOFF) {
                listCRecord.add(cRecord);
            }
        }
        return SphericalUtil.computeLength(Utilities.getLatLngPoints(listCRecord));
    }


    /*
     * Returns line through point1, at right angles to line between point1 and point2, length lineRadius in meters.
     *
     * @param point1
     * @param point2
     * @param lineRadiusInMeters
     * @return
     */
    public static PerpendicularLineCoordinates getPerpendicularLine(ILatLonRecord point1, ILatLonRecord point2, int lineRadiusInMeters) {
        //Use Pythogoras is accurate enough on this scale
        double latDiff = point2.getLatLon().getLat() - point1.getLatLon().getLat();

        //need radians for cosine function
        double northMean = (point1.getLatLon().getLat() + point2.getLatLon().getLat()) * Math.PI / 360;
        double startRads = point1.getLatLon().getLat() * Math.PI / 180;
        double longDiff = (point1.getLatLon().getLon() - point2.getLatLon().getLon()) * Math.cos(northMean);
        double hypotenuse = Math.sqrt(latDiff * latDiff + longDiff * longDiff);

        //assume earth is a sphere circumference 40030 Km
        int lineRadius = lineRadiusInMeters / 1000;
        double latDelta = lineRadius * longDiff / hypotenuse / 111.1949269;
        double longDelta = lineRadius * latDiff / hypotenuse / 111.1949269 / Math.cos(startRads);
        LatLng lineStart = new LatLng(point1.getLatLon().getLat() - latDelta, point1.getLatLon().getLon() - longDelta);
        LatLng lineEnd = new LatLng(point1.getLatLon().getLat() + latDelta, longDelta + point1.getLatLon().getLon());

        return new PerpendicularLineCoordinates(lineStart, lineEnd, point1);

    }

    public static int getTaskAverageSpeed(IGCFile igcFile, HashMap<String, Integer> mapAreaReached) {
        try {
            ArrayList<Integer> listReachedAreas = getListReachedAreas(mapAreaReached);
            String startTime = ((BRecord) igcFile.getTrackPoints().get(listReachedAreas.get(0))).getTime();
            String finishTime = ((BRecord) igcFile.getTrackPoints().get(listReachedAreas.get(listReachedAreas.size() - 1))).getTime();
            return Utilities.calculateAverageSpeed(igcFile.getTraveledTaskDistance(), Utilities.getDiffTimeInSeconds(startTime, finishTime));
        } catch (Throwable t) {
            return -1;
        }
    }

    public static String getTaskDuration(IGCFile igcFile, HashMap<String, Integer> mapAreaReached) {
        try {
            ArrayList<Integer> listReachedAreas = getListReachedAreas(mapAreaReached);
            String startTime = ((BRecord) igcFile.getTrackPoints().get(listReachedAreas.get(0))).getTime();
            String finishTime = ((BRecord) igcFile.getTrackPoints().get(listReachedAreas.get(listReachedAreas.size() - 1))).getTime();
            return Utilities.getFlightTime(startTime, finishTime);
        } catch (Throwable t) {
            return "EE:EE";
        }
    }

    public static double getTaskTraveledDistance(IGCFile igcFile, HashMap<String, Integer> mapAreaReached) {
        ArrayList<ILatLonRecord> list = new ArrayList<>();
        if (!igcFile.isTaskCompleted()) {
            return igcFile.getDistance();
        }

        ArrayList<Integer> listReachedAreas = getListReachedAreas(mapAreaReached);
        for (int i = 0; i < listReachedAreas.size(); i++) {
            list.add(igcFile.getTrackPoints().get(listReachedAreas.get(i)));
        }
        return SphericalUtil.computeLength(Utilities.getLatLngPoints(list));
    }

    private static ArrayList<Integer> getListReachedAreas(HashMap<String, Integer> mapAreaReached) {
        ArrayList<Integer> values = new ArrayList<>(mapAreaReached.values());
        Collections.sort(values);
        return values;
    }

    public static class PerpendicularLineCoordinates {
        public LatLng start;
        public LatLng end;
        LatLng center;

        PerpendicularLineCoordinates(LatLng lineStart, LatLng lineEnd, ILatLonRecord point1) {
            start = lineStart;
            end = lineEnd;
            center = new LatLng(point1.getLatLon().getLat(), point1.getLatLon().getLon());
        }
    }
}
