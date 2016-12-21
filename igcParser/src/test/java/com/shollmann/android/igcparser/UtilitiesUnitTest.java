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

import com.shollmann.android.igcparser.model.LatLon;
import com.shollmann.android.igcparser.util.Utilities;

import junit.framework.Assert;

import org.junit.Test;


public class UtilitiesUnitTest {
    @Test
    public void parse_isCorrect_North_West() throws Exception {
        LatLon latLon = Utilities.generateCoordinates("5206343N", "00006198W");
        Assert.assertEquals(52.105716666666666, latLon.getLat());
        Assert.assertEquals(-0.1033, latLon.getLon());
    }

    @Test
    public void parse_isCorrect_South_East() throws Exception {
        LatLon latLon = Utilities.generateCoordinates("5206343S", "00006198E");
        Assert.assertEquals(-52.105716666666666, latLon.getLat());
        Assert.assertEquals(0.1033, latLon.getLon());
    }

    @Test
    public void parse_isCorrect_South_West() throws Exception {
        LatLon latLon = Utilities.generateCoordinates("3428508S", "05925277W");
        Assert.assertEquals(-34.47513333333333, latLon.getLat());
        Assert.assertEquals(-59.421283333333335, latLon.getLon());
    }
}