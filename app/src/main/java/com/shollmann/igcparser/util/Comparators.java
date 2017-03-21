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

package com.shollmann.igcparser.util;

import android.text.TextUtils;

import com.shollmann.android.igcparser.model.IGCFile;

import java.text.SimpleDateFormat;
import java.util.Comparator;

public class Comparators {

    public static Comparator<IGCFile> compareByPilot = new Comparator<IGCFile>() {
        @Override
        public int compare(IGCFile file0, IGCFile file1) {
            return genericCompare(file0.getPilotInCharge(), file1.getPilotInCharge());
        }
    };

    private static int genericCompare(String string0, String string1) {
        if (TextUtils.isEmpty(string0) && TextUtils.isEmpty(string1)) {
            return -10000;
        }
        if (!TextUtils.isEmpty(string0) && TextUtils.isEmpty(string1)) {
            return -1;
        }
        if (TextUtils.isEmpty(string0) && !TextUtils.isEmpty(string1)) {
            return 1;
        }

        string0.toUpperCase();
        string1.toUpperCase();

        return string0.compareToIgnoreCase(string1);
    }

    public static Comparator<IGCFile> compareByGlider = new Comparator<IGCFile>() {
        @Override
        public int compare(IGCFile file0, IGCFile file1) {
            return genericCompare(file0.getGliderTypeAndId(), file1.getGliderTypeAndId());
        }
    };

    public static Comparator<IGCFile> compareByDate = new Comparator<IGCFile>() {
        private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

        @Override
        public int compare(IGCFile file0, IGCFile file1) {
            try {
                return sdf.parse(file1.getDate()).compareTo(sdf.parse(file0.getDate()));
            } catch (Throwable t) {
                return 0;
            }

        }
    };
}

