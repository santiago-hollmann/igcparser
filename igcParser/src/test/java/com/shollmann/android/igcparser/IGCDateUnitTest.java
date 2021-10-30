package com.shollmann.android.igcparser;

import com.shollmann.android.igcparser.model.IGCDate;

import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class IGCDateUnitTest {

    private static Date targetDate;

    @Before
    public void setUp() throws Exception {
        targetDate = new SimpleDateFormat("dd/MM/yyyy").parse("02/05/2021");
    }

    @Test
    public void parseOldFormat() {
        String str = "HFDTE020521";
        IGCDate igcDate = new IGCDate(str);

        assertEquals(igcDate.getDate(), targetDate);
        assertNull(igcDate.getFlightNumber());
    }

    @Test
    public void parseNewFormat() {
        String str = "HFDTEDATE:020521,01";
        IGCDate igcDate = new IGCDate(str);

        assertEquals(igcDate.getDate(), targetDate);
        assertEquals(igcDate.getFlightNumber(), (Integer)1);
    }
}
