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

import com.google.maps.android.SphericalUtil;
import com.shollmann.android.igcparser.model.BRecord;
import com.shollmann.android.igcparser.model.CRecordType;
import com.shollmann.android.igcparser.model.CRecordWayPoint;
import com.shollmann.android.igcparser.model.IGCFile;
import com.shollmann.android.igcparser.model.ILatLonRecord;

import java.util.HashMap;
import java.util.List;

public class WaypointUtilities {
    public static void calculateReachedAreas(int positionBRecord, BRecord bRecord, IGCFile igcFile, HashMap<String, Integer> mapAreaReached) {
        for (int i = 1; i < igcFile.getWaypoints().size() - 2; i++) {
            final ILatLonRecord waypoint = igcFile.getWaypoints().get(i);
            if (!CoordinatesUtilities.isZeroCoordinate(waypoint)) {
                float[] distance = new float[2];
                Location.distanceBetween(bRecord.getLatLon().getLat(), bRecord.getLatLon().getLon(),
                        waypoint.getLatLon().getLat(), waypoint.getLatLon().getLon(), distance);
                if (distance[0] < Constants.TASK.AREA_WDITH) {
                    String waypointKey = waypoint.getLatLon().toString();
                    if (mapAreaReached.get(waypointKey) == null) {
                        mapAreaReached.put(waypointKey, positionBRecord);
                    }
                }
            }
        }
    }

    public static void classifyWayPoints(List<ILatLonRecord> waypoints) {
        for (int i = 0; i < waypoints.size(); i++) {
            CRecordWayPoint aCRecord = (CRecordWayPoint) waypoints.get(i);
            int oppositePosition = waypoints.size() - 1 - i;
            CRecordWayPoint otherCRecord = (CRecordWayPoint) waypoints.get(oppositePosition);
            if (!aCRecord.getDescription().equalsIgnoreCase(otherCRecord.getDescription()) || oppositePosition == i) {
                aCRecord.setType(CRecordType.TURN);
            }
        }
    }

    public static double calculateTaskDistance(List<ILatLonRecord> waypoints) {
        return  SphericalUtil.computeLength(Utilities.getLatLngPoints(waypoints));
    }
}
