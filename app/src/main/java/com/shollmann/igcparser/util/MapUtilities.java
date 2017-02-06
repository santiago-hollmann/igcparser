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

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.shollmann.android.igcparser.model.BRecord;
import com.shollmann.android.igcparser.model.IGCFile;
import com.shollmann.android.igcparser.model.ILatLonRecord;
import com.shollmann.android.igcparser.util.Utilities;
import com.shollmann.igcparser.IGCViewerApplication;
import com.shollmann.igcparser.R;
import com.shollmann.igcparser.model.AltitudeSegment;
import com.shollmann.igcparser.model.AltitudeTrackSegment;

import java.util.ArrayList;

public class MapUtilities {

    @NonNull
    public static ArrayList<AltitudeTrackSegment> getAltitudeTrackSegments(IGCFile igcFile) {
        int altitude;

        AltitudeTrackSegment trackSegment = new AltitudeTrackSegment();
        ILatLonRecord lastRecord = null;
        AltitudeSegment currentSegment = AltitudeSegment.UNDEFINED;
        AltitudeSegment lastSegment = AltitudeSegment.UNDEFINED;

        ArrayList<AltitudeTrackSegment> listTrackSegment = new ArrayList<>();

        for (ILatLonRecord record : igcFile.getTrackPoints()) {
            altitude = ((BRecord) record).getAltitude() - igcFile.getStartAltitude();

            if (altitude < 100) {
                currentSegment = AltitudeSegment.ALTITUDE_0_100;
            } else if (between(altitude, 100, 300)) {
                currentSegment = AltitudeSegment.ALTITUDE_100_300;
            } else if (between(altitude, 300, 500)) {
                currentSegment = AltitudeSegment.ALTITUDE_300_500;
            } else if (between(altitude, 500, 1000)) {
                currentSegment = AltitudeSegment.ALTITUDE_500_1000;
            } else if (between(altitude, 1000, 1500)) {
                currentSegment = AltitudeSegment.ALTITUDE_1000_1500;
            } else if (between(altitude, 1500, 2000)) {
                currentSegment = AltitudeSegment.ALTITUDE_1500_2000;
            } else if (between(altitude, 2000, 2500)) {
                currentSegment = AltitudeSegment.ALTITUDE_2000_2500;
            } else if (altitude > 2500) {
                currentSegment = AltitudeSegment.ALTITUDE_MORE_THAN_2500;
            }

            if (lastSegment == currentSegment) {
                trackSegment.addRecord(record);
                lastRecord = record;
            } else {
                trackSegment = new AltitudeTrackSegment();
                trackSegment.setSegment(currentSegment);
                if (lastRecord != null) {
                    //First time this code runs is null
                    // If we don't add this point then it will appear an empty space
                    // among the different polyline
                    trackSegment.addRecord(lastRecord);
                }
                trackSegment.addRecord(record);
                listTrackSegment.add(trackSegment);
                lastSegment = currentSegment;
            }
        }
        return listTrackSegment;
    }

    private static boolean between(int value, int min, int max) {
        return min < value && value <= max;
    }

    public static boolean isZeroCoordinate(ILatLonRecord wayPoint) {
        return Utilities.isZero(wayPoint.getLatLon().getLat()) && Utilities.isZero(wayPoint.getLatLon().getLon());
    }

    /**
     * Returns line through pt1, at right angles to line between pt1 and pt2, length lineRadius.
     *
     * @param point1
     * @param point2
     * @param lineRadius
     * @return
     */
    public static PolylineOptions getPerpendicularPolyline(ILatLonRecord point1, ILatLonRecord point2, int lineRadius) {
        //returns line through pt1, at right angles to line between pt1 and pt2, length lineRadius.

        //Use Pythogoras- accurate enough on this scale
        double latDiff = point2.getLatLon().getLat() - point1.getLatLon().getLat();

        //need radians for cosine function
        double northMean = (point1.getLatLon().getLat() + point2.getLatLon().getLat()) * Math.PI / 360;
        double startRads = point1.getLatLon().getLat() * Math.PI / 180;
        double longDiff = (point1.getLatLon().getLon() - point2.getLatLon().getLon()) * Math.cos(northMean);
        double hypotenuse = Math.sqrt(latDiff * latDiff + longDiff * longDiff);

        //assume earth is a sphere circumference 40030 Km
        double latDelta = lineRadius * longDiff / hypotenuse / 111.1949269;
        double longDelta = lineRadius * latDiff / hypotenuse / 111.1949269 / Math.cos(startRads);
        LatLng lineStart = new LatLng(point1.getLatLon().getLat() - latDelta, point1.getLatLon().getLon() - longDelta);
        LatLng lineEnd = new LatLng(point1.getLatLon().getLat() + latDelta, longDelta + point1.getLatLon().getLon());

        PolylineOptions polyline = new PolylineOptions().color(IGCViewerApplication.getApplication().getResources().getColor(R.color.start_finish_color)).width(ResourcesHelper.getDimensionPixelSize(R.dimen.task_line_width))
                .zIndex(16)
                .add(lineStart)
                .add(lineEnd);

        return polyline;

    }
}
