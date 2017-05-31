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

public class SettingsTrackThickness extends LinearLayout {
    private RadioGroup radioGroup;

    public SettingsTrackThickness(Context context) {
        this(context, null);
    }

    public SettingsTrackThickness(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsTrackThickness(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_settings_track_thickness, this);

        final PreferencesHelper preferencesHelper = new PreferencesHelper(getContext());

        radioGroup = (RadioGroup) findViewById(R.id.track_width_radio_group);

        checkPreviousOption(preferencesHelper.getTrackWidthType());
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int radioId) {
                switch (radioId) {
                    case R.id.track_width_radio_default:
                        TrackerHelper.trackTrackThicknessDefault();
                        preferencesHelper.setTrackWidthType(Constants.Settings.TRACK_WIDTH_DEFAULT);
                        Config.setTrackWidth(getContext(), Constants.Settings.TRACK_WIDTH_DEFAULT);
                        break;
                    case R.id.track_width_radio_big:
                        TrackerHelper.trackTrackThicknessBig();
                        preferencesHelper.setTrackWidthType(Constants.Settings.TRACK_WIDTH_BIG);
                        Config.setTrackWidth(getContext(), Constants.Settings.TRACK_WIDTH_BIG);
                        break;
                    case R.id.track_width_radio_extra_big:
                        TrackerHelper.trackTrackThicknessExtraBig();
                        preferencesHelper.setTrackWidthType(Constants.Settings.TRACK_WIDTH_EXTRA_BIG);
                        Config.setTrackWidth(getContext(), Constants.Settings.TRACK_WIDTH_EXTRA_BIG);
                        break;
                }
            }
        });
    }

    private void checkPreviousOption(int trackWidthType) {
        int viewId = R.id.track_width_radio_default;
        switch (trackWidthType) {
            case Constants.Settings.TRACK_WIDTH_DEFAULT:
                viewId = R.id.track_width_radio_default;
                break;
            case Constants.Settings.TRACK_WIDTH_BIG:
                viewId = R.id.track_width_radio_big;
                break;
            case Constants.Settings.TRACK_WIDTH_EXTRA_BIG:
                viewId = R.id.track_width_radio_extra_big;
                break;
        }
        radioGroup.check(viewId);
    }
}
