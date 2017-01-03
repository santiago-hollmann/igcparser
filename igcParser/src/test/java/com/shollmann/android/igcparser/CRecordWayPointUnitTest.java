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

package com.shollmann.android.igcparser;

import com.shollmann.android.igcparser.model.CRecordType;
import com.shollmann.android.igcparser.model.CRecordWayPoint;

import org.junit.Assert;
import org.junit.Test;


public class CRecordWayPointUnitTest {

    @Test
    public void parse_takeoff_isCorrect() throws Exception {
        CRecordWayPoint cRecord = new CRecordWayPoint("C5111359N00101899WTAKEOFFLasham Clubhouse");
        Assert.assertEquals("5111359N", cRecord.getLat());
        Assert.assertEquals("00101899W", cRecord.getLon());
        Assert.assertEquals("Lasham Clubhouse", cRecord.getDescription());
        Assert.assertEquals(CRecordType.TAKEOFF, cRecord.getType());
    }

    @Test
    public void parse_turn_isCorrect() throws Exception {
        CRecordWayPoint cRecord = new CRecordWayPoint("C5111359N00101899WTURNAEP");
        Assert.assertEquals("5111359N", cRecord.getLat());
        Assert.assertEquals("00101899W", cRecord.getLon());
        Assert.assertEquals("AEP", cRecord.getDescription());
        Assert.assertEquals(CRecordType.TURN, cRecord.getType());
    }

    @Test
    public void parse_turn_isCorrect_1() throws Exception {
        CRecordWayPoint cRecord = new CRecordWayPoint("C3413889S05912629WAE CHENAULT");
        Assert.assertEquals("3413889S", cRecord.getLat());
        Assert.assertEquals("05912629W", cRecord.getLon());
        Assert.assertEquals("AE CHENAULT", cRecord.getDescription());
        Assert.assertEquals(CRecordType.TURN, cRecord.getType());
    }

    @Test
    public void parse_turn_isCorrect_2() throws Exception {
        CRecordWayPoint cRecord = new CRecordWayPoint("C3446383S05941433WSUIPACHA");
        Assert.assertEquals("3446383S", cRecord.getLat());
        Assert.assertEquals("05941433W", cRecord.getLon());
        Assert.assertEquals("SUIPACHA", cRecord.getDescription());
        Assert.assertEquals(CRecordType.TURN, cRecord.getType());
    }

    @Test
    public void parse_takeoff_isCorrect_1() throws Exception {
        CRecordWayPoint cRecord = new CRecordWayPoint("C0000000N00000000ETAKEOFF");
        Assert.assertEquals("0000000N", cRecord.getLat());
        Assert.assertEquals("00000000E", cRecord.getLon());
        Assert.assertEquals(CRecordType.TAKEOFF, cRecord.getType());
    }
}