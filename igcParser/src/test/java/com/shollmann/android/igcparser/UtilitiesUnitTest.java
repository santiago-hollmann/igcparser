package com.shollmann.android.igcparser;

import com.shollmann.android.igcparser.model.LatLon;
import com.shollmann.android.igcparser.util.Utilities;

import junit.framework.Assert;

import org.junit.Test;


public class UtilitiesUnitTest {
    @Test
    public void parse_isCorrect_North_West() throws Exception {
        LatLon latLon = Utilities.generateCoordinates("5206.343N", "00006.198W");
        Assert.assertEquals(52.105716666666666, latLon.getLat());
        Assert.assertEquals(-000.1033000, latLon.getLon());
    }

    @Test
    public void parse_isCorrect_South_East() throws Exception {
        LatLon latLon = Utilities.generateCoordinates("5206.343S", "00006.198E");
        Assert.assertEquals(-52.105716666666666, latLon.getLat());
        Assert.assertEquals(000.1033000, latLon.getLon());
    }
}