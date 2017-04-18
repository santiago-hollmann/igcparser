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

package com.shollmann.android.igcparser.model;

import com.shollmann.android.igcparser.util.Constants;
import com.shollmann.android.igcparser.util.Logger;
import com.shollmann.android.igcparser.util.Utilities;

import java.io.Serializable;

public class CRecordWayPoint implements ILatLonRecord, Serializable {

    private CRecordType type;
    private LatLon latLon;
    private String description;
    private String lat;
    private String lon;

    public CRecordWayPoint(String rawRecord) {
        try {
            lat = rawRecord.substring(1, 9);
            lon = rawRecord.substring(9, 18);
            latLon = Utilities.generateCoordinates(lat, lon);
            type = parseType(rawRecord);
            description = rawRecord.substring(getTypeLastCharPosition(rawRecord), rawRecord.length());
        } catch (Exception e) {
            Logger.logError(e.getMessage());
        }
    }

    private int getTypeLastCharPosition(String rawRecord) {
        return rawRecord.indexOf("W") == -1 ? rawRecord.indexOf("E") + 1 : rawRecord.indexOf("W") + 1;
    }

    private CRecordType parseType(String rawRecord) {
        rawRecord = rawRecord.toUpperCase();

        if (rawRecord.contains(Constants.CRecord.TAKEOFF)) {
            return CRecordType.TAKEOFF;
        } else if (rawRecord.contains(Constants.CRecord.START)) {
            return CRecordType.START;
        } else if (rawRecord.contains(Constants.CRecord.TURN)) {
            return CRecordType.TURN;
        } else if (rawRecord.contains(Constants.CRecord.FINISH)) {
            return CRecordType.FINISH;
        } else if (rawRecord.contains(Constants.CRecord.LANDING)) {
            return CRecordType.LANDING;
        }
        return CRecordType.UNDEFINED;
    }

    public String getDescription() {
        return description;
    }

    public LatLon getLatLon() {
        return latLon;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public CRecordType getType() {
        return type;
    }

    public void setType(CRecordType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return lat + lon + type;
    }
}
