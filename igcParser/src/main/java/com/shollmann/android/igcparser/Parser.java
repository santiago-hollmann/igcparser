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
import android.util.Log;

import com.google.maps.android.SphericalUtil;
import com.shollmann.android.igcparser.model.BRecord;
import com.shollmann.android.igcparser.model.IGCFile;
import com.shollmann.android.igcparser.util.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Parser {

    public static IGCFile parse(Context context, Uri filePath) {
        IGCFile igcFile = new IGCFile();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("sample1.igc"), "UTF-8"));

            String line;
            int maxAltitude = -10000;
            int minAltitude = 10000;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("B")) {
                    BRecord bRecord = new BRecord(line);
                    igcFile.appendTrackPoint(bRecord);
                    if (bRecord.getAltitude() > maxAltitude) {
                        maxAltitude = bRecord.getAltitude();
                    }
                    if (bRecord.getAltitude() < minAltitude) {
                        minAltitude = bRecord.getAltitude();
                    }
                }
            }
            igcFile.setMaxAltitude(maxAltitude);
            igcFile.setMinAltitude(minAltitude);

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
        Log.e("Parser", igcFile.toString());
        return igcFile;
    }
}
