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
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.maps.android.SphericalUtil;
import com.shollmann.android.igcparser.model.BRecord;
import com.shollmann.android.igcparser.model.IGCFile;
import com.shollmann.android.igcparser.util.Constants;
import com.shollmann.android.igcparser.util.Logger;
import com.shollmann.android.igcparser.util.Utilities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Parser {
    public static IGCFile parse(Uri filePath) {
        IGCFile igcFile = new IGCFile();
        int maxAltitude = -10000;
        int minAltitude = 10000;
        BRecord firstBRecord = null;
        String takeOffTime = Constants.EMPTY_STRING;
        String landingTime = Constants.EMPTY_STRING;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filePath.toString()), "UTF-8"));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("B")) {
                    BRecord bRecord = new BRecord(line);

                    firstBRecord = setFirstBRecord(bRecord, firstBRecord);
                    maxAltitude = setMaxAltitude(bRecord, maxAltitude);
                    minAltitude = setMinAltitude(bRecord, minAltitude);
                    takeOffTime = setTakeOffTime(firstBRecord, takeOffTime, bRecord);
                    landingTime = setLandingTime(bRecord, landingTime, firstBRecord);

                    igcFile.appendTrackPoint(bRecord);
                }
            }
            igcFile.setMaxAltitude(maxAltitude);
            igcFile.setMinAltitude(minAltitude);
            igcFile.setTakeOffTime(takeOffTime);
            igcFile.setLandingTime(landingTime);

            double distance = SphericalUtil.computeLength(Utilities.getLatLngPoints(igcFile.getTrackPoints()));
            igcFile.setDistance(distance);
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

    @Nullable
    private static String setTakeOffTime(BRecord firstBRecord, String takeOffTime, BRecord bRecord) {
        if (TextUtils.isEmpty(takeOffTime) && bRecord.getAltitude() - firstBRecord.getAltitude() >= Constants.MARKER_TAKE_OFF_HEIGHT) {
            takeOffTime = bRecord.getTime();
        }
        return takeOffTime;
    }

    private static String setLandingTime(BRecord bRecord, String landingTime, BRecord firstBRecord) {
        if ((bRecord.getAltitude() - Constants.MARKER_LANDING_HEIGHT) <= firstBRecord.getAltitude()) {
            landingTime = bRecord.getTime();
        }
        return landingTime;
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