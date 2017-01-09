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

import android.content.Context;
import android.util.TypedValue;

import com.shollmann.igcparser.IGCViewerApplication;

public class ResourcesHelper {

    public static int getDimensionPixelSize(int resource) {
        Context app = IGCViewerApplication.getApplication();
        TypedValue rawValue = new TypedValue();
        app.getResources().getValue(resource, rawValue, true);
        if (rawValue.type == TypedValue.TYPE_FIRST_INT) {
            //This is the case in which a dimen references an int like this: <dimen name="mydimen">@integer/custom_match_parent</dimen>
            return app.getResources().getInteger(resource);
        } else {
            //This is the standard case of reading a dimen as a dimen like this: <dimen name="mydimen">400dp</dimen>
            return app.getResources().getDimensionPixelSize(resource);
        }
    }
}
