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

import android.graphics.Color;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.shollmann.android.igcparser.model.BRecord;
import com.shollmann.android.igcparser.model.IGCFile;
import com.shollmann.android.igcparser.model.ILatLonRecord;
import com.shollmann.android.igcparser.util.Utilities;
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

    public static PolylineOptions getStartPolyline(ILatLonRecord aCoordinate, ILatLonRecord bCoordinate, ILatLonRecord startCoordinate) {
        double perpendicularLeftX = (aCoordinate.getLatLon().getLat() + bCoordinate.getLatLon().getLat()) / 2;
        double perpendicularLeftY = (aCoordinate.getLatLon().getLon() + bCoordinate.getLatLon().getLon()) / 2;
        double perpendicularRightX = (bCoordinate.getLatLon().getLat() - aCoordinate.getLatLon().getLat()) / 2;
        double perpendicularRightY = (bCoordinate.getLatLon().getLon() - aCoordinate.getLatLon().getLon()) / 2;

//        LatLon startPointStartCoordinate =new LatLon(startCoordinate.getLat(), perpendicularLon);
//        LatLon startPointEndCoordinate =new LatLon(startCoordinate.getLon(), perpendicularLon);
        final LatLng perpendicularLeftCoordinate = new LatLng(perpendicularLeftX + perpendicularRightY, perpendicularLeftY - perpendicularRightX);
        final LatLng perpendicularRightCoordinate = new LatLng(perpendicularLeftX - perpendicularRightY, perpendicularLeftY + perpendicularRightX);
        PolylineOptions polyline = new PolylineOptions().color(Color.BLUE).width(5)
                .add(new LatLng(startCoordinate.getLatLon().getLat() + (startCoordinate.getLatLon().getLat() - perpendicularLeftCoordinate.latitude) , perpendicularLeftCoordinate.longitude))
                .add(new LatLng(startCoordinate.getLatLon().getLat() + (perpendicularRightCoordinate.latitude - startCoordinate.getLatLon().getLat()) , perpendicularRightCoordinate.longitude));
//                .add(new LatLng(startCoordinate.getLatLon().getLat(), perpendicularRightCoordinate.longitude));
//                .add(new LatLng(perpendicularLeftCoordinate.latitude, startCoordinate.getLatLon().getLon()-0.0625))
//                .add(new LatLng(perpendicularRightCoordinate.latitude, startCoordinate.getLatLon().getLon() ));
//                .add(perpendicularRightCoordinate);
//                .add(new LatLng(perpendicularRightCoordinate.latitude, startCoordinate.getLatLon().getLon() - 0.0625))
//                .add(new LatLng(startCoordinate.getLatLon().getLat() - (startCoordinate.getLatLon().getLat() - perpendicularLeftCoordinate.latitude), startCoordinate.getLatLon().getLon() + (startCoordinate.getLatLon().getLon() - perpendicularLeftCoordinate.longitude)));
        return polyline;
    }

    public static boolean isZeroCoordinate(ILatLonRecord wayPoint) {
        return Utilities.isZero(wayPoint.getLatLon().getLat()) && Utilities.isZero(wayPoint.getLatLon().getLon());
    }

    /**
     * So you have two points with coordinates (x1,y1) and (x2, y2) and you want to draw a line segment
     * whose length is the distance between them, which is the perpendicular bisector of the segment connecting them,
     * and which is bisected by said segment?

     Probably the simplest way is to set
     cx = (x1 + x2)/2
     cy = (y1+y2)/2)
     dx = (x2-x1)/2
     dy = (y2-y1)/2
     and then draw a line from
     (cx-dy, cy+dx) to (cx+dy, cy-dx).

     perpLeftX = (x1 + x2)/2
     perpLeftY = (y1+y2)/2)
     perpRightX = (x2-x1)/2
     perpRightY = (y2-y1)/2
     and then draw a line from
     (perpLeftX - perpRightY, perpLefty + perpRightX) to (cx+dy, cy-dx).

     This works because (cx, cy) is the midpoint of the segment you want, and then you're just taking
     the vector from that midpoint to (x2,y2) and rotating it by plus and minus 90 degrees to find the
     endpoints of the segment you want to draw.
     */
}
