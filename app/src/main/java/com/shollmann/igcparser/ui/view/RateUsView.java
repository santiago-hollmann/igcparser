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
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shollmann.igcparser.R;
import com.shollmann.igcparser.events.RateUsClickedEvent;
import com.shollmann.igcparser.tracking.TrackerHelper;
import com.shollmann.igcparser.util.Constants;
import com.shollmann.igcparser.util.PreferencesHelper;

import org.greenrobot.eventbus.EventBus;

public class RateUsView extends LinearLayout implements View.OnClickListener {
    private TextView btnOk;
    private TextView btnCancel;
    private TextView txtMessage;

    public RateUsView(Context context) {
        this(context, null);
    }

    public RateUsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RateUsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        inflate(getContext(), R.layout.view_rate_us, this);

        btnCancel = (TextView) findViewById(R.id.rate_us_no);
        btnOk = (TextView) findViewById(R.id.rate_us_yes);
        txtMessage = (TextView) findViewById(R.id.rate_us_text);
        txtMessage.setText(Html.fromHtml(getResources().getString(R.string.rate_us_text)));

        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);

        TrackerHelper.trackRateUsShow();
    }

    @Override
    public void onClick(View view) {
        EventBus.getDefault().post(new RateUsClickedEvent());
        switch (view.getId()) {
            case R.id.rate_us_yes:
                TrackerHelper.trackRateUsYes();
                PreferencesHelper.setIsRated();
                openPlayStore();
                break;
            case R.id.rate_us_no:
                PreferencesHelper.resetViewedFlightsForRate();
                PreferencesHelper.setMinFlightsViewedToRate();
                TrackerHelper.trackRateUsNo();
                break;
        }
    }

    private void openPlayStore() {
        try {
            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.App.PLAYSTORE_APP_URL + Constants.App.APP_PKG_NAME)));
        } catch (Throwable t) {
            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.App.PLAYSTORE_WEB_URL + Constants.App.APP_PKG_NAME)));
        }
    }
}
