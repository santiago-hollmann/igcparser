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

package com.shollmann.igcparser.ui.view;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.shollmann.igcparser.R;
import com.shollmann.igcparser.model.Config;
import com.shollmann.igcparser.tracking.TrackerHelper;
import com.shollmann.igcparser.util.Constants;
import com.shollmann.igcparser.util.PreferencesHelper;

public class SettingsMachine extends LinearLayout {
    private RadioGroup radioGroup;

    public SettingsMachine(Context context) {
        this(context, null);
    }

    public SettingsMachine(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsMachine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_settings_machine, this);

        final PreferencesHelper preferencesHelper = new PreferencesHelper(getContext());

        radioGroup = (RadioGroup) findViewById(R.id.machine_radio_group);

        checkPreviousOption(preferencesHelper.getMachine());
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int radioId) {
                switch (radioId) {
                    case R.id.machine_glider:
                        TrackerHelper.trackMachineGlider();
                        preferencesHelper.setMachine(Constants.Settings.MACHINE_GLIDER);
                        Config.setMachine(Constants.Settings.MACHINE_GLIDER);
                        break;
                    case R.id.machine_small_plane:
                        TrackerHelper.trackMachineSmallPlane();
                        preferencesHelper.setMachine(Constants.Settings.MACHINE_SMALL_PLANE);
                        Config.setMachine(Constants.Settings.MACHINE_SMALL_PLANE);
                        break;
                    case R.id.machine_paraglider:
                        TrackerHelper.trackMachineParaglider();
                        preferencesHelper.setMachine(Constants.Settings.MACHINE_PARAGLIDER);
                        Config.setMachine(Constants.Settings.MACHINE_PARAGLIDER);
                        break;
                    case R.id.machine_deltawing:
                        TrackerHelper.trackMachineDeltaWing();
                        preferencesHelper.setMachine(Constants.Settings.MACHINE_DELTAWING);
                        Config.setMachine(Constants.Settings.MACHINE_DELTAWING);
                        break;
                }
            }
        });
    }

    private void checkPreviousOption(int trackWidthType) {
        int viewId = R.id.machine_glider;
        switch (trackWidthType) {
            case Constants.Settings.MACHINE_GLIDER:
                viewId = R.id.machine_glider;
                break;
            case Constants.Settings.MACHINE_PARAGLIDER:
                viewId = R.id.machine_paraglider;
                break;
            case Constants.Settings.MACHINE_SMALL_PLANE:
                viewId = R.id.machine_small_plane;
                break;
            case Constants.Settings.MACHINE_DELTAWING:
                viewId = R.id.machine_deltawing;
                break;
        }
        radioGroup.check(viewId);
    }
}
