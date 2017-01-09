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

package com.shollmann.igcparser.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.shollmann.android.igcparser.model.IGCFile;
import com.shollmann.android.igcparser.util.Utilities;
import com.shollmann.igcparser.R;
import com.shollmann.igcparser.ui.view.MoreInformationFieldView;
import com.shollmann.igcparser.util.Constants;

public class FlightMoreInformationActivity extends AppCompatActivity {
    private LinearLayout layoutFieldsContainer;
    private IGCFile igcFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_more_information);

        layoutFieldsContainer = (LinearLayout) findViewById(R.id.more_info_field_container);
        igcFile = (IGCFile) getIntent().getExtras().getSerializable(Constants.IGC_FILE);

        if (igcFile != null) {
            showInformation();
        } else {
            Toast.makeText(getApplication().getBaseContext(), "Sorry, an error ocurred", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void showInformation() {
        insertField(R.drawable.ic_person, R.string.pilot_in_charge, Utilities.capitalizeText(igcFile.getPilotInCharge()));
        insertField(R.drawable.ic_calendar, R.string.date, igcFile.getDate());
        insertField(R.drawable.ic_depature, R.string.take_off, Utilities.getTimeHHMM(igcFile.getTakeOffTime()));
        insertField(R.drawable.ic_landing, R.string.landing, Utilities.getTimeHHMM(igcFile.getLandingTime()));
        insertField(R.drawable.ic_min, R.string.min_altitude, Utilities.getFormattedNumber(igcFile.getMinAltitude(), getResources().getConfiguration().locale));
        insertField(R.drawable.ic_max, R.string.max_altitude, Utilities.getFormattedNumber(igcFile.getMaxAltitude(), getResources().getConfiguration().locale));


    }

    public void insertField(int iconId, int titleStringId, String value) {
        if (!TextUtils.isEmpty(value)) {
            MoreInformationFieldView fieldView = new MoreInformationFieldView(this);
            fieldView.show(iconId, titleStringId, value);
            layoutFieldsContainer.addView(fieldView);
        }
    }

}
