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
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.shollmann.android.igcparser.model.BRecord;
import com.shollmann.android.igcparser.model.CRecordWayPoint;
import com.shollmann.android.igcparser.model.IGCFile;
import com.shollmann.android.igcparser.model.ILatLonRecord;
import com.shollmann.android.igcparser.util.Utilities;
import com.shollmann.igcparser.IGCViewerApplication;
import com.shollmann.igcparser.R;
import com.shollmann.igcparser.ui.view.MoreInformationFieldView;
import com.shollmann.igcparser.util.Constants;
import com.shollmann.igcparser.util.ResourcesHelper;

import java.util.ArrayList;
import java.util.List;

public class FlightInformationActivity extends AppCompatActivity {
    private LinearLayout layoutFieldsContainer;
    private RelativeLayout layoutTaskContainer;
    private LinearLayout layoutWayPointsContainer;
    private LinearLayout layoutChartContainer;
    private LineChart altitudeLineChart;
    private LineChart speedLineChart;
    private IGCFile igcFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();

        igcFile = IGCViewerApplication.getCurrentIGCFile();

        if (igcFile != null) {
            showInformation();
        } else {
            Crashlytics.log("FlightInformationActivity :: IGC File is null");
            Toast.makeText(getApplication().getBaseContext(), R.string.sorry_error_happen, Toast.LENGTH_SHORT).show();
            finish();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void findViews() {
        setContentView(R.layout.activity_flight_information);

        layoutFieldsContainer = (LinearLayout) findViewById(R.id.more_info_field_container);
        layoutTaskContainer = (RelativeLayout) findViewById(R.id.more_info_task_container);
        layoutWayPointsContainer = (LinearLayout) findViewById(R.id.more_info_waypoints_container);
        layoutChartContainer = (LinearLayout) findViewById(R.id.more_info_chart_container);
        altitudeLineChart = (LineChart) findViewById(R.id.more_info_altitude_line_chart);
        speedLineChart = (LineChart) findViewById(R.id.more_info_speed_line_chart);
    }

    private void showInformation() {
        insertField(R.drawable.ic_calendar, R.string.date, igcFile.getDate());
        insertField(R.drawable.ic_person, R.string.pilot_in_charge, Utilities.capitalizeText(igcFile.getPilotInCharge()));
        insertField(R.drawable.ic_plane, R.string.glider, igcFile.getGliderTypeAndId());
        insertField(R.drawable.ic_speed, R.string.avg_speed, igcFile.getAverageSpeed() + "km/h");
        insertField(R.drawable.ic_time, R.string.duration, igcFile.getFlightTime());
        insertField(R.drawable.ic_distance, R.string.distance, Utilities.getDistanceInKm(igcFile.getDistance(), getResources().getConfiguration().locale) + "km");
        insertField(R.drawable.ic_min, R.string.min_altitude, Utilities.getFormattedNumber(igcFile.getMinAltitude(), getResources().getConfiguration().locale) + "m");
        insertField(R.drawable.ic_max, R.string.max_altitude, Utilities.getFormattedNumber(igcFile.getMaxAltitude(), getResources().getConfiguration().locale) + "m");
        insertField(R.drawable.ic_departure, R.string.take_off, Utilities.getTimeHHMM(igcFile.getTakeOffTime()) + " UTC");
        insertField(R.drawable.ic_landing, R.string.landing, Utilities.getTimeHHMM(igcFile.getLandingTime()) + " UTC");
        if (!igcFile.getWaypoints().isEmpty() && !Utilities.isZero(igcFile.getTaskDistance())) {
            layoutTaskContainer.setVisibility(View.VISIBLE);
            populateTask();
        }
        if (!igcFile.getTrackPoints().isEmpty()) {
            showLineCharts();
        } else {
            layoutChartContainer.setVisibility(View.GONE);
        }

    }

    private void showLineCharts() {
        List<Entry> altitudeEntries = new ArrayList<>();
        List<Entry> speedEntries = new ArrayList<>();
        BRecord lastRecord = null;
        int lastAverageSpeed = 0;

        final List<ILatLonRecord> trackPoints = igcFile.getTrackPoints();
        int i;
        int lastI = 0;

        for (i = 0; i < trackPoints.size(); i++) {
            BRecord bRecord = (BRecord) trackPoints.get(i);

            if (i % Constants.Chart.POINTS_SIMPLIFIER == 0) {
                altitudeEntries.add(new Entry(i, bRecord.getAltitude()));
            }

            if (i % Constants.Chart.SPEED_POINTS_SIMPLIFIER == 0) {
                if (lastRecord == null) {
                    speedEntries.add(new Entry(0, 0)); // start with 0 speed maybe not needed
                    lastRecord = (BRecord) trackPoints.get(0);
                }
                double distanceBetween = SphericalUtil.computeLength(Utilities.getLatLngPoints(trackPoints.subList(lastI, i)));
                long timeBetween = Utilities.getDiffTimeInSeconds(lastRecord.getTime(), bRecord.getTime());
                int averageSpeed = Utilities.calculateAverageSpeed(distanceBetween, timeBetween);
                if (averageSpeed < 40) {
                    averageSpeed = lastAverageSpeed;
                }
                speedEntries.add(new Entry(i, averageSpeed));
                lastAverageSpeed = averageSpeed;
                lastRecord = bRecord;
                lastI = i;
            }
        }

        //Hack to always finish the graph where the glider has landed after the graph is simplified
        altitudeEntries.add(new Entry(i + 1, ((BRecord) igcFile.getTrackPoints().get(igcFile.getTrackPoints().size() - 1)).getAltitude()));
        speedEntries.add(new Entry(i + 1, 0));

        setupGraphic(altitudeLineChart, altitudeEntries, igcFile.getMinAltitude());
        setupGraphic(speedLineChart, speedEntries, 0);
    }

    private void setupGraphic(LineChart chart, List<Entry> entries, float axisMinimum) {
        LineDataSet dataSet = new LineDataSet(entries, Constants.EMPTY_STRING);
        dataSet.setDrawCircles(false);
        dataSet.setDrawCircleHole(false);
        dataSet.setLineWidth(ResourcesHelper.getDimensionPixelSize(R.dimen.half_dp));
        dataSet.setFillColor(getResources().getColor(R.color.colorPrimary));
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(Constants.Chart.ALPHA_FILL);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setColor(getResources().getColor(R.color.colorPrimary));
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);

        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);

        chart.getXAxis().setDrawLabels(false);
        chart.getXAxis().setDrawGridLines(false);

        chart.getAxisLeft().removeAllLimitLines();
        chart.getAxisLeft().setTextColor(getResources().getColor(R.color.gray));
        chart.getAxisLeft().setAxisMinimum(axisMinimum);
        chart.getAxisLeft().setTextSize(Constants.Chart.LABEL_SIZE);

        chart.animateX(Constants.Chart.ANIMATION_DURATION, Easing.EasingOption.EaseInSine);

        chart.setData(lineData);
        chart.invalidate();
    }

    private void populateTask() {
        for (ILatLonRecord waypoint : igcFile.getWaypoints()) {
            CRecordWayPoint cRecord = (CRecordWayPoint) waypoint;
            if (!TextUtils.isEmpty(cRecord.getDescription().trim())) {
                layoutWayPointsContainer.addView(createTaskTextView(cRecord.getDescription()));
            }
        }
        if (!Utilities.isZero(igcFile.getTaskDistance())) {
            final String taskDistanceText = String.format(getResources().getString(R.string.task_distance), Utilities.getDistanceInKm(igcFile.getTaskDistance(), getResources().getConfiguration().locale));
            layoutWayPointsContainer.addView(createTaskTextView(taskDistanceText));
        } else {
            layoutWayPointsContainer.setVisibility(View.GONE);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onStop();
        IGCViewerApplication.setCurrentIGCFile(null);
    }
}
