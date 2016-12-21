package com.shollmann.android.igcparser;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.shollmann.android.igcparser.model.BRecord;
import com.shollmann.android.igcparser.model.IGCFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Parser {

    public static IGCFile parse(Context context, Uri filePath) {
        IGCFile igcFile = new IGCFile();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("sample1.igc"), "UTF-8"));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("B")) {
                    igcFile.appendTrackPoint(new BRecord(line));
                }
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        Log.e("Parser", igcFile.toString());
        return igcFile;
    }
}
