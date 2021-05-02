package com.shollmann.android.igcparser.model;

import android.support.annotation.NonNull;

import com.shollmann.android.igcparser.util.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Models a date for an IGC track-log.
 * Created by Thomas Fischer on 02/05/2021.
 */
public class IGCDate implements Comparable<IGCDate> {
    /**
     * Regular expression that matches encoded date strings as specified by the IGC data format.
     * <p>
     * There are two possible formats for the encoded date
     * The original FAI-IGC FlightRecorder specification for the DTE record was 'HFDTE{DD}{MM}{YY}'
     * while after the 2015/09 specification changes it became 'HFDTEDATE:{DD}{MM}{YY},{NN}'.
     * Both formats are supported here.
     * E.g. a date for the first flight on the 2nd of May 2021 would be encoded as 'HFDTE020521'
     * or 'HFDTEDATE:020521,01' using the old and new formats respectively.
     * <p>
     * See: http://vali.fai-civl.org/faq.html
     */
    private static final Pattern pattern = Pattern.compile("(?:HFDTE(?:DATE:)?)(\\d{2}\\d{2}\\d{2})(?:,(\\d{2}))?");
    private static final DateFormat dateFormatInput = new SimpleDateFormat("ddMMyy", Locale.getDefault());
    private static final DateFormat dateFormatDisplay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Date date;
    private Integer flightNumber;

    public IGCDate(String str) {
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            try {
                date = dateFormatInput.parse(matcher.group(1));
            } catch (ParseException e) {
                e.printStackTrace();
                String errorMessage = "IGCDate :: Unable to parse date string. Not a date.";
                Logger.logError(errorMessage);
            }
            try {
                // Note: Flight number will be null for old format strings that don't specify it.
                flightNumber = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : null;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                String errorMessage = "IGCDate :: Unable to parse date string. Not a number.";
                Logger.logError(errorMessage);
            }
        } else {
            String errorMessage = "IGCDate :: Unable to parse date string. Unexpected format.";
            Logger.logError(errorMessage);
        }
    }

    public Date getDate() {
        return date;
    }

    public Integer getFlightNumber() {
        return flightNumber;
    }

    @Override
    public String toString() {
        return dateFormatDisplay.format(date) +  (flightNumber == null ? "" : String.format(" %02d", flightNumber));
    }

    @Override
    public int compareTo(@NonNull IGCDate igcDate) {
        int result = date.compareTo(igcDate.date);
        if (result != 0)
            return result;
        return flightNumber.compareTo(igcDate.flightNumber);
    }
}
