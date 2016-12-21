package com.shollmann.android.igcparser.model;

import java.util.ArrayList;
import java.util.List;

public class IGCFile {
    List<BRecord> listTrackPoints;

    public IGCFile() {
        listTrackPoints = new ArrayList<>();
    }

    public void appendTrackPoint(BRecord bRecord) {
        listTrackPoints.add(bRecord);
    }

    public List<BRecord> getTrackPoints() {
        return listTrackPoints;
    }

    @Override
    public String toString() {
        return "IGCFile --- Track Points: " + listTrackPoints.size();
    }
}
