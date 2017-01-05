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