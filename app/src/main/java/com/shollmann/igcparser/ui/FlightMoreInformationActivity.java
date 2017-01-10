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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.shollmann.android.igcparser.model.BRecord;
import com.shollmann.android.igcparser.model.CRecordWayPoint;
import com.shollmann.android.igcparser.model.IGCFile;
import com.shollmann.android.igcparser.model.ILatLonRecord;
import com.shollmann.android.igcparser.util.Utilities;
import com.shollmann.igcparser.R;
import com.shollmann.igcparser.ui.view.MoreInformationFieldView;
import com.shollmann.igcparser.util.Constants;
import com.shollmann.igcparser.util.ResourcesHelper;

import java.util.ArrayList;
import java.util.List;

public class FlightMoreInformationActivity extends AppCompatActivity {
    private LinearLayout layoutFieldsContainer;
    private RelativeLayout layoutTaskContainer;
    private LinearLayout layoutWayPointsContainer;
    private LineChart chart;
    private IGCFile igcFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_more_information);

        layoutFieldsContainer = (LinearLayout) findViewById(R.id.more_info_field_container);
        layoutTaskContainer = (RelativeLayout) findViewById(R.id.more_info_task_container);
        layoutWayPointsContainer = (LinearLayout) findViewById(R.id.more_info_waypoints_container);
        chart = (LineChart) findViewById(R.id.more_info_chart);


        igcFile = (IGCFile) getIntent().getExtras().getSerializable(Constants.IGC_FILE);

        if (igcFile != null) {
            showInformation();
        } else {
            Toast.makeText(getApplication().getBaseContext(), R.string.sorry_error_happen, Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void showInformation() {
        insertField(R.drawable.ic_calendar, R.string.date, igcFile.getDate());
        insertField(R.drawable.ic_person, R.string.pilot_in_charge, Utilities.capitalizeText(igcFile.getPilotInCharge()));
        insertField(R.drawable.ic_glider_big, R.string.glider, igcFile.getGliderTypeAndId());
        insertField(R.drawable.ic_time, R.string.duration, igcFile.getFlightTime());
        insertField(R.drawable.ic_distance, R.string.distance, Utilities.getDistanceInKm(igcFile.getDistance(), getResources().getConfiguration().locale) + "km");
        insertField(R.drawable.ic_min, R.string.min_altitude, Utilities.getFormattedNumber(igcFile.getMinAltitude(), getResources().getConfiguration().locale) + "m");
        insertField(R.drawable.ic_max, R.string.max_altitude, Utilities.getFormattedNumber(igcFile.getMaxAltitude(), getResources().getConfiguration().locale) + "m");
        insertField(R.drawable.ic_departure, R.string.take_off, Utilities.getTimeHHMM(igcFile.getTakeOffTime()));
        insertField(R.drawable.ic_landing, R.string.landing, Utilities.getTimeHHMM(igcFile.getLandingTime()));
        if (!igcFile.getWaypoints().isEmpty() || igcFile.getTaskDistance() != 0) {
            layoutTaskContainer.setVisibility(View.VISIBLE);
            populateTask();
        }
        showAltitudeChart();

    }

    private void showAltitudeChart() {
        List<Entry> entries = new ArrayList<>();

        final List<ILatLonRecord> trackPoints = igcFile.getTrackPoints();
        for (int i = 0; i < trackPoints.size(); i++) {
            BRecord bRecord = (BRecord) trackPoints.get(i);
            entries.add(new Entry(i, bRecord.getAltitude()));
        }
        LineDataSet dataSet = new LineDataSet(entries, Constants.EMPTY_STRING);
        dataSet.setDrawCircles(false);
        dataSet.setDrawCircleHole(false);
        dataSet.setLineWidth(ResourcesHelper.getDimensionPixelSize(R.dimen.one_dp));
        dataSet.setColor(getResources().getColor(R.color.colorPrimary));
        LineData lineData = new LineData(dataSet);
        Description desc = new Description();
        desc.setText(Constants.EMPTY_STRING);
        chart.setDescription(desc);
        chart.getXAxis().setDrawLabels(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisLeft().setTextSize(14f);
        chart.setTouchEnabled(false);
        chart.getAxisLeft().setTextColor(getResources().getColor(R.color.gray));

        chart.setData(lineData);
        chart.invalidate();
    }

    private void populateTask() {
        for (ILatLonRecord waypoint : igcFile.getWaypoints()) {
            CRecordWayPoint cRecord = (CRecordWayPoint) waypoint;
            if (!TextUtils.isEmpty(cRecord.getDescription())) {
                layoutWayPointsContainer.addView(createTaskTextView(cRecord.getDescription()));
            }
        }
        if (igcFile.getTaskDistance() != 0) {
            final String taskDistanceText = String.format(getResources().getString(R.string.information_distance), Utilities.getDistanceInKm(igcFile.getTaskDistance(), getResources().getConfiguration().locale));
            layoutWayPointsContainer.addView(createTaskTextView(taskDistanceText));
        }
    }

    @NonNull
    private TextView createTaskTextView(String value) {
        TextView txtWaypoint = new TextView(this);
        txtWaypoint.setEllipsize(TextUtils.TruncateAt.END);
        txtWaypoint.setMaxLines(2);
        txtWaypoint.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.material_text_subhead));
        txtWaypoint.setText(value);
        return txtWaypoint;
    }

    private String getReadableCRecordType(CRecordWayPoint cRecord) {
        int stringId;
        switch (cRecord.getType()) {
            case FINISH:
                stringId = R.string.finish;
                break;
            case START:
                stringId = R.string.start;
                break;
            case TAKEOFF:
                stringId = R.string.take_off;
                break;
            case LANDING:
                stringId = R.string.landing;
                break;
            case TURN:
                stringId = R.string.turn;
                break;
            default:
                stringId = R.string.turn;
                break;
        }
        return getResources().getString(stringId);
    }

    public void insertField(int iconId, int titleStringId, String value) {
        if (!TextUtils.isEmpty(value)) {
            MoreInformationFieldView fieldView = new MoreInformationFieldView(this);
            fieldView.show(iconId, titleStringId, value);
            layoutFieldsContainer.addView(fieldView);
        }
    }

}
