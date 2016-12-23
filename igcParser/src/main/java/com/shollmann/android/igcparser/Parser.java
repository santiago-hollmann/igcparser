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

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.google.maps.android.SphericalUtil;
import com.shollmann.android.igcparser.model.BRecord;
import com.shollmann.android.igcparser.model.IGCFile;
import com.shollmann.android.igcparser.util.Constants;
import com.shollmann.android.igcparser.util.Logger;
import com.shollmann.android.igcparser.util.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Parser {
    private static int maxAltitude = -10000;
    private static int minAltitude = 10000;
    private static BRecord firstBRecord;
    private static String takeOffTime = Constants.EMPTY_STRING;
    private static String landingTime = Constants.EMPTY_STRING;

    public static IGCFile parse(Context context, Uri filePath) {
        IGCFile igcFile = new IGCFile();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("sample2.igc"), "UTF-8"));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("B")) {
                    BRecord bRecord = new BRecord(line);
                    setFirstBRecord(bRecord);
                    setMaxAltitude(bRecord);
                    setMinAltitude(bRecord);
                    setTakeOffTime(bRecord);
                    setLandingTime(bRecord);

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
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        Logger.log(igcFile.toString());
        return igcFile;
    }

    private static void setLandingTime(BRecord bRecord) {
        if ((bRecord.getAltitude() - Constants.MARKER_LANDING_HEIGHT) <= firstBRecord.getAltitude()) {
            landingTime = bRecord.getTime();
        }
    }

    private static void setFirstBRecord(BRecord bRecord) {
        if (firstBRecord == null) {
            firstBRecord = bRecord;
        }
    }

    private static void setMinAltitude(BRecord bRecord) {
        if (bRecord.getAltitude() < minAltitude) {
            minAltitude = bRecord.getAltitude();
        }
    }

    private static void setMaxAltitude(BRecord bRecord) {
        if (bRecord.getAltitude() > maxAltitude) {
            maxAltitude = bRecord.getAltitude();
        }
    }

    public static void setTakeOffTime(BRecord bRecord) {
        if (TextUtils.isEmpty(takeOffTime) && bRecord.getAltitude() - firstBRecord.getAltitude() >= Constants.MARKER_TAKE_OFF_HEIGHT) {
            takeOffTime = bRecord.getTime();
        }
    }
}
