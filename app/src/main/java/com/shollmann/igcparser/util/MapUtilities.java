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

import com.shollmann.android.igcparser.model.BRecord;
import com.shollmann.android.igcparser.model.IGCFile;
import com.shollmann.android.igcparser.model.ILatLonRecord;
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
}
