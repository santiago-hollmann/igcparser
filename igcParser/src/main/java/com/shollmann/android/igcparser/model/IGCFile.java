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

package com.shollmann.android.igcparser.model;

import android.net.Uri;
import android.text.TextUtils;

import com.shollmann.android.igcparser.util.Constants;
import com.shollmann.android.igcparser.util.Utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IGCFile implements Serializable {
    private List<ILatLonRecord> listTrackPoints;
    private List<ILatLonRecord> listWayPoints;
    private double distance;
    private double taskDistance;
    private int maxAltitude;
    private int minAltitude;
    private int startAltitude;
    private int averageSpeed;
    private String takeOffTime;
    private String landingTime;
    private String pilotInCharge;
    private String gliderId;
    private String gliderType;
    private String date;
    private String fileName;
    private String filePath;

    public IGCFile() {
        listWayPoints = new ArrayList<>();
        listTrackPoints = new ArrayList<>();
        taskDistance = 0;
    }

    public void appendTrackPoint(BRecord bRecord) {
        listTrackPoints.add(bRecord);
    }

    public void appendWayPoint(CRecordWayPoint waypoint) {
        listWayPoints.add(waypoint);
    }

    public List<ILatLonRecord> getTrackPoints() {
        return listTrackPoints;
    }

    public List<ILatLonRecord> getWaypoints() {
        return listWayPoints;
    }

    @Override
    public String toString() {
        return "IGCFile --- Track Points: "
                + listTrackPoints.size() + " :: distance (in m): " + distance
                + " :: amountTrackPoints: " + listTrackPoints.size()
                + " :: amountWayPoints: " + listWayPoints.size()
                + " :: maxAltitude: " + maxAltitude
                + " :: minAltitude: " + minAltitude
                + " :: landingTime: " + landingTime
                + " :: takeOffTime: " + takeOffTime;
    }


    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setMaxAltitude(int maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    public int getMaxAltitude() {
        return maxAltitude;
    }

    public void setMinAltitude(int minAltitude) {
        this.minAltitude = minAltitude;
    }

    public int getMinAltitude() {
        return this.minAltitude;
    }

    public void setTakeOffTime(String departureTime) {
        this.takeOffTime = departureTime;
    }

    public String getTakeOffTime() {
        return takeOffTime;
    }

    public void setLandingTime(String landingTime) {
        this.landingTime = landingTime;
    }

    public String getLandingTime() {
        return landingTime;
    }

    public String getFlightTime() {
        return Utilities.getFlightTime(takeOffTime, landingTime);
    }

    public String getGliderType() {
        return gliderType;
    }

    public void setGliderType(String gliderType) {
        this.gliderType = gliderType;
    }

    public String getGliderId() {
        return gliderId;
    }

    public void setGliderId(String gliderId) {
        this.gliderId = gliderId;
    }

    public String getPilotInCharge() {
        return pilotInCharge;
    }

    public void setPilotInCharge(String pilotInCharge) {
        this.pilotInCharge = pilotInCharge;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getGliderTypeAndId() {
        if (TextUtils.isEmpty(gliderId) && TextUtils.isEmpty(gliderType)) {
            return Constants.EMPTY_STRING;
        }

        if (TextUtils.isEmpty(gliderType)) {
            return gliderId;
        } else {
            if (!TextUtils.isEmpty(gliderId)) {
                String gliderInformationPlaceholder = "%1$s (%2$s)";
                return String.format(gliderInformationPlaceholder, gliderType, gliderId);
            } else {
                return gliderType;
            }
        }
    }

    public void setFileData(Uri filePath) {
        this.filePath = filePath.toString();
        this.fileName = filePath.getLastPathSegment();
    }

    public void setTaskDistance(double taskDistance) {
        this.taskDistance = taskDistance;
    }

    public double getTaskDistance() {
        return taskDistance;
    }

    public int getStartAltitude() {
        return startAltitude;
    }

    public void setStartAltitude(int startAltitude) {
        this.startAltitude = startAltitude;
    }

    public int getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(int averageSpeed) {
        this.averageSpeed = averageSpeed;
    }
}
