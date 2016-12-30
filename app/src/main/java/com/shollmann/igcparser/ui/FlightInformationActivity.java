/*
 * MIT License
 *
 * Copyright (c) 2016 Santiago Hollmann
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

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.shollmann.android.igcparser.Parser;
import com.shollmann.android.igcparser.model.IGCFile;
import com.shollmann.android.igcparser.util.Utilities;
import com.shollmann.igcparser.R;
import com.shollmann.igcparser.util.Constants;

import java.util.List;


public class FlightInformationActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    private String fileToLoadPath;
    private IGCFile igcFile;
    private MapView mapView;
    private GoogleMap googleMap;
    private List<LatLng> listLatLngPoints;
    private TextView txtDistance;
    private TextView txtMaxAltitude;
    private TextView txtMinAltitude;
    private TextView txtTakeOffTime;
    private TextView txtLandingTime;
    private TextView txtFlightTime;
    private View btnCloseInformation;
    private View btnShowInformation;
    private View btnPlay;
    private View btnSpeedUp;
    private CardView cardviewInformation;
    private ProgressBar loading;
    private int duration;
    private int replaySpeed = Constants.Map.DEFAULT_REPLAY_SPEED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();
        handleIntent();
        setClickListeners();
        initMap(savedInstanceState);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            fileToLoadPath = (String) getIntent().getExtras().get(Constants.FILE_TO_LOAD_PATH);
        }

        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            fileToLoadPath = uri.getPath();
        }
    }

    private void initMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    private void setClickListeners() {
        btnCloseInformation.setOnClickListener(this);
        btnShowInformation.setOnClickListener(this);
    }

    private void findViews() {
        setContentView(R.layout.activity_flight_information);
        mapView = (MapView) findViewById(R.id.main_map);
        txtDistance = (TextView) findViewById(R.id.main_txt_distance);
        txtMaxAltitude = (TextView) findViewById(R.id.main_txt_max_altitude);
        txtMinAltitude = (TextView) findViewById(R.id.main_txt_start_altitude);
        txtTakeOffTime = (TextView) findViewById(R.id.main_txt_takeoff);
        txtLandingTime = (TextView) findViewById(R.id.main_txt_landing);
        txtFlightTime = (TextView) findViewById(R.id.main_txt_duration);
        cardviewInformation = (CardView) findViewById(R.id.main_cardview_information);
        loading = (ProgressBar) findViewById(R.id.main_loading);
        btnCloseInformation = findViewById(R.id.main_cardview_close);
        btnShowInformation = findViewById(R.id.main_information_btn);
        btnPlay = findViewById(R.id.main_btn_play);
        btnSpeedUp = findViewById(R.id.main_btn_speed_up);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setMapToolbarEnabled(true);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        this.googleMap.getUiSettings().setZoomGesturesEnabled(true);
        this.googleMap.getUiSettings().setRotateGesturesEnabled(false);

        new ParseIGCFileAsynkTask().execute();
    }

    private void displayFlightInformation() {
        txtDistance.setText(String.format(getString(R.string.information_distance), String.valueOf((int) (igcFile.getDistance() / Constants.Map.METERS_IN_ONE_KILOMETER))));
        txtMaxAltitude.setText(String.format(getString(R.string.information_max_altitude), Utilities.getFormattedNumber(igcFile.getMaxAltitude(), getResources().getConfiguration().locale)));
        txtMinAltitude.setText(String.format(getString(R.string.information_min_altitude), Utilities.getFormattedNumber(igcFile.getMinAltitude(), getResources().getConfiguration().locale)));
        txtLandingTime.setText(String.format(getString(R.string.information_landing), Utilities.getTimeHHMM(igcFile.getLandingTime())));
        txtTakeOffTime.setText(String.format(getString(R.string.information_takeoff), Utilities.getTimeHHMM(igcFile.getTakeOffTime())));
        txtFlightTime.setText(String.format(getString(R.string.information_duration), igcFile.getFlightTime()));
    }

    private void displayTrack() {
        PolylineOptions polyline = new PolylineOptions().width(Constants.Map.MAP_TRACK_POLYLINE_WIDTH).color(Color.BLUE);
        listLatLngPoints = Utilities.getLatLngPoints(igcFile.getTrackPoints());
        polyline.addAll(listLatLngPoints);
        googleMap.addPolyline(polyline);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_cardview_close:
                cardviewInformation.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_dae_out));
                cardviewInformation.setVisibility(View.GONE);
                btnShowInformation.setVisibility(View.VISIBLE);
                btnShowInformation.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_information_btn_enter));
                break;
            case R.id.main_information_btn:
                btnShowInformation.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_information_btn_leave));
                btnShowInformation.setVisibility(View.GONE);
                cardviewInformation.setVisibility(View.VISIBLE);
                cardviewInformation.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_fade_in));

                break;
        }
    }

    private class ParseIGCFileAsynkTask extends AsyncTask<Void, Void, Void> implements View.OnClickListener {
        protected Void doInBackground(Void... something) {
            igcFile = Parser.parse(Uri.parse(fileToLoadPath));
            return null;
        }

        protected void onProgressUpdate(Void... something) {
        }

        protected void onPostExecute(Void result) {
            displayTrack();
            displayFlightInformation();
            if (listLatLngPoints != null && !listLatLngPoints.isEmpty()) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(listLatLngPoints.get(0).latitude + Constants.Map.FIX_INITIAL_LATITUDE, listLatLngPoints.get(0).longitude), Constants.Map.MAP_DEFAULT_ZOOM));
            }
            loading.setVisibility(View.GONE);
            mapView.setVisibility(View.VISIBLE);
            cardviewInformation.setVisibility(View.VISIBLE);
            setReplayButtons();
        }

        private void setReplayButtons() {
            btnPlay.setVisibility(View.VISIBLE);
            btnSpeedUp.setVisibility(View.VISIBLE);
            btnPlay.setOnClickListener(this);
            btnSpeedUp.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.main_btn_play:
                    startFlightReplay();
                    break;
                case R.id.main_btn_speed_up:
                    speedUpReplay();
                    break;
            }
        }
    }

    private void speedUpReplay() {
        if (replaySpeed >= Constants.Map.MAX_REPLAY_SPEED) {
            replaySpeed = (int) (replaySpeed / Constants.Map.REPLAY_SPEED_INCREASER);
        }
    }

    public void startFlightReplay() {
        if (listLatLngPoints == null || listLatLngPoints.isEmpty()) {
            return;
        }
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(listLatLngPoints.get(0))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_glider))
        );

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(listLatLngPoints.get(0), googleMap.getCameraPosition().zoom));
        animateMarker(marker, true);
        btnSpeedUp.setVisibility(View.VISIBLE);
    }


    private void animateMarker(final Marker marker,
                               final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        duration = 300000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            int i = 0;

            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                if (i < listLatLngPoints.size()) {
                    Double heading = SphericalUtil.computeHeading(marker.getPosition(), listLatLngPoints.get(i));
                    marker.setRotation(heading.floatValue());
                    marker.setPosition(listLatLngPoints.get(i));
                }
                i++;

                if (t < 1.0 && i < listLatLngPoints.size()) {
                    handler.postDelayed(this, replaySpeed);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                    btnSpeedUp.setVisibility(View.GONE);
                    replaySpeed = Constants.Map.DEFAULT_REPLAY_SPEED;
                }
            }
        });
    }

}
