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

import com.shollmann.android.igcparser.util.Utilities;

import java.util.ArrayList;
import java.util.List;

public class IGCFile {
    private List<ILatLonRecord> listTrackPoints;
    private List<ILatLonRecord> listWaypointsPoints;
    private double distance;
    private int maxAltitude;
    private int minAltitude;
    private String takeOffTime;
    private String landingTime;

    public IGCFile() {
        listWaypointsPoints = new ArrayList<>();
        listTrackPoints = new ArrayList<>();
    }

    public void appendTrackPoint(BRecordI bRecord) {
        listTrackPoints.add(bRecord);
    }

    public void appendWayPoint(CRecordWayPoint waypoint) {
        listWaypointsPoints.add(waypoint);
    }

    public List<ILatLonRecord> getTrackPoints() {
        return listTrackPoints;
    }

    public List<ILatLonRecord> getWaypoints() {
        return listWaypointsPoints;
    }

    @Override
    public String toString() {
        return "IGCFile --- Track Points: "
                + listTrackPoints.size() + " :: distance (in m): " + distance
                + " :: amountTrackPoints: " + listTrackPoints.size()
                + " :: amountWayPoints: " + listWaypointsPoints.size()
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

}
