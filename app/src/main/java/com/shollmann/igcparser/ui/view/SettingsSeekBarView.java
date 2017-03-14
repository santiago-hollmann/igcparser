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
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.shollmann.igcparser.R;
import com.shollmann.igcparser.util.PreferencesHelper;

public class SettingsSeekBarView extends RelativeLayout {
    private TextView txtValue;
    private TextView txtTitle;
    private ImageView imgIcon;
    private SeekBar seekbar;
    private SeekbarType type;

    public SettingsSeekBarView(Context context) {
        this(context, null);
    }

    public SettingsSeekBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsSeekBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        findViews();
    }

    public void init(SeekbarType type) {
        this.type = type;
        setTitle();
        setMaxValue();
        setIcon();
        setInitialValue();

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                setTextValue(value);
                saveValue(value * 1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setTextValue(int value) {
        float realValue;
        if (value == 0) {
            realValue = 0.5f;
            txtValue.setText(String.valueOf(realValue) + "km");
        } else {
            txtValue.setText(String.valueOf(value) + "km");
        }
    }

    private void setInitialValue() {
        int value;
        switch (type) {
            case AREA:
                value = PreferencesHelper.getAreaWidth();
                break;
            case START:
                value = PreferencesHelper.getStartLength();
                break;
            case FINISH:
                value = PreferencesHelper.getFinishLength();
                break;
            default:
                value = 0;
        }
        int valueInKm = value / 1000;
        setTextValue(valueInKm);
        seekbar.setProgress(valueInKm);
    }

    private void saveValue(int realValue) {
        switch (type) {
            case AREA:
                PreferencesHelper.setAreaWidth(realValue);
                break;
            case START:
                PreferencesHelper.setStartLength(realValue);
                break;
            case FINISH:
                PreferencesHelper.setFinishLength(realValue);
                break;
        }
    }

    private void setMaxValue() {
        switch (type) {
            case AREA:
                seekbar.setMax(120);
                break;
            case START:
                seekbar.setMax(40);
                break;
            case FINISH:
                seekbar.setMax(20);
                break;
        }
    }

    private void setIcon() {
        switch (type) {
            case AREA:
                imgIcon.setImageResource(R.drawable.ic_area);
                break;
            case START:
                imgIcon.setImageResource(R.drawable.ic_line);
                break;
            case FINISH:
                imgIcon.setImageResource(R.drawable.ic_line);
                break;
        }
    }

    private void setTitle() {
        switch (type) {
            case AREA:
                txtTitle.setText(R.string.settings_area);
                break;
            case START:
                txtTitle.setText(R.string.settings_start);
                break;
            case FINISH:
                txtTitle.setText(R.string.settings_finish);
                break;
        }
    }

    private void findViews() {
        inflate(getContext(), R.layout.view_settings_seekbar, this);

        txtTitle = (TextView) findViewById(R.id.seekbar_title);
        txtValue = (TextView) findViewById(R.id.seekbar_value);
        seekbar = (SeekBar) findViewById(R.id.seekbar_bar);
        imgIcon = (ImageView) findViewById(R.id.seekbar_icon);
    }

    public enum SeekbarType {
        AREA, START, FINISH
    }

}
