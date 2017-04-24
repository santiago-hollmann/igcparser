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

package com.shollmann.igcparser;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.shollmann.android.igcparser.model.IGCFile;
import com.shollmann.android.igcparser.model.TaskConfig;
import com.shollmann.igcparser.util.PreferencesHelper;

import io.fabric.sdk.android.Fabric;

public class IGCViewerApplication extends Application {
    private static IGCFile currentIGCFile;

    @Override
    public void onCreate() {
        super.onCreate();
        setupCrashlytics();
        setupTaskConfig();

    }

    private void setupTaskConfig() {
        PreferencesHelper preferencesHelper = new PreferencesHelper(this);
        TaskConfig.setAreaWidth(preferencesHelper.getAreaWidth());
        TaskConfig.setStartLength(preferencesHelper.getStartLength());
        TaskConfig.setFinishLength(preferencesHelper.getFinishLength());
    }

    private void setupCrashlytics() {
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();

        Fabric.with(this, crashlyticsKit);
    }

    public Context getApplication() {
        return this;
    }

    public static IGCFile getCurrentIGCFile() {
        return currentIGCFile;
    }

    public static void setCurrentIGCFile(IGCFile currentIGCFile) {
        IGCViewerApplication.currentIGCFile = currentIGCFile;
    }
}
