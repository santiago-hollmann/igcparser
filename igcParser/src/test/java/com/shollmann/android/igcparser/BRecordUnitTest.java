package com.shollmann.android.igcparser;

import com.shollmann.android.igcparser.model.BRecord;

import org.junit.Assert;
import org.junit.Test;


public class BRecordUnitTest {
    @Test
    public void parse_isCorrect() throws Exception {
        BRecord brecord = new BRecord("B1935253428508S05925277WA000970005100000");
        Assert.assertEquals(193525, brecord.getTime());
        Assert.assertEquals("3428508S", brecord.getLat());
        Assert.assertEquals("05925277W",brecord.getLon());
        Assert.assertEquals(97,brecord.getAltitudePress());
        Assert.assertEquals(51, brecord.getAltitudeGps());
    }

    @Test
    public void parse_isCorrect_1() throws Exception {
        BRecord brecord = new BRecord("B1935253428508N45925277EA123971235100000");
        Assert.assertEquals(193525, brecord.getTime());
        Assert.assertEquals("3428508N", brecord.getLat());
        Assert.assertEquals("45925277W",brecord.getLon());
        Assert.assertEquals(12397,brecord.getAltitudePress());
        Assert.assertEquals(12351, brecord.getAltitudeGps());
    }
}