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
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.shollmann.android.igcparser.Parser;
import com.shollmann.android.igcparser.model.CRecordWayPoint;
import com.shollmann.android.igcparser.model.IGCFile;
import com.shollmann.android.igcparser.model.ILatLonRecord;
import com.shollmann.android.igcparser.util.CoordinatesUtilities;
import com.shollmann.android.igcparser.util.Logger;
import com.shollmann.android.igcparser.util.Utilities;
import com.shollmann.igcparser.IGCViewerApplication;
import com.shollmann.igcparser.R;
import com.shollmann.igcparser.model.AltitudeTrackSegment;
import com.shollmann.igcparser.tracking.TrackerHelper;
import com.shollmann.igcparser.util.Constants;
import com.shollmann.igcparser.util.FileUtilities;
import com.shollmann.igcparser.util.MapUtilities;
import com.shollmann.igcparser.util.PreferencesHelper;
import com.shollmann.igcparser.util.ResourcesHelper;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class FlightPreviewActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    private boolean isFinishReplay = true;
    private int duration;
    private int replaySpeed = Constants.Map.DEFAULT_REPLAY_SPEED;
    private int speedCounter = Constants.Map.REPLAY_MIN_MULTIPLIER;
    private String fileToLoadPath;
    private IGCFile igcFile;
    private List<LatLng> listLatLngPoints;
    private MapView mapView;
    private GoogleMap googleMap;
    private Marker markerGlider;
    private TextView txtDistance;
    private TextView txtMaxAltitude;
    private TextView txtMinAltitude;
    private TextView txtTakeOffTime;
    private TextView txtLandingTime;
    private TextView txtFlightTime;
    private TextView txtPilot;
    private TextView txtGlider;
    private LinearLayout layoutPilot;
    private LinearLayout layoutGlider;
    private RelativeLayout layoutAltitudeReference;
    private TextView txtMoreInfo;
    private View btnCloseInformation;
    private View btnShowInformation;
    private ImageView btnPlay;
    private ImageView btnSpeedUp;
    private CardView cardviewInformation;
    private ProgressBar loading;
    private ImageView btnSpeedDown;
    private View viewAltitudeReferenceBar;
    private Toast toast;
    private Object lock = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();
        setClickListeners();
        initMap(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            fileToLoadPath = (String) getIntent().getExtras().get(Constants.FILE_TO_LOAD_PATH);
        }

        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            if (intent.getData() != null && Constants.App.CONTENT_URI.equalsIgnoreCase(intent.getData().getScheme())) {
                TrackerHelper.trackOpenGmailFlight();
                new GetGmailAttachmentAsyncTask(this).execute(intent.getDataString());
            } else {
                Uri uri = intent.getData();
                fileToLoadPath = uri.getPath();
            }
        }
    }

    private void initMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    private void setClickListeners() {
        btnCloseInformation.setOnClickListener(this);
        btnShowInformation.setOnClickListener(this);
        txtMoreInfo.setOnClickListener(this);
    }

    private void findViews() {
        setContentView(R.layout.activity_flight_preview);
        mapView = (MapView) findViewById(R.id.main_map);
        txtDistance = (TextView) findViewById(R.id.main_txt_distance);
        txtMaxAltitude = (TextView) findViewById(R.id.main_txt_max_altitude);
        txtMinAltitude = (TextView) findViewById(R.id.main_txt_start_altitude);
        txtTakeOffTime = (TextView) findViewById(R.id.main_txt_takeoff);
        txtLandingTime = (TextView) findViewById(R.id.main_txt_landing);
        txtFlightTime = (TextView) findViewById(R.id.main_txt_duration);
        txtGlider = (TextView) findViewById(R.id.main_txt_glider);
        txtPilot = (TextView) findViewById(R.id.main_txt_pilot);
        layoutGlider = (LinearLayout) findViewById(R.id.main_layout_glider);
        layoutPilot = (LinearLayout) findViewById(R.id.main_layout_pilot);
        txtMoreInfo = (TextView) findViewById(R.id.main_information_btn_viewmore);
        cardviewInformation = (CardView) findViewById(R.id.main_cardview_information);
        loading = (ProgressBar) findViewById(R.id.main_loading);
        btnCloseInformation = findViewById(R.id.main_cardview_close);
        btnShowInformation = findViewById(R.id.main_information_btn);
        btnPlay = (ImageView) findViewById(R.id.main_btn_play);
        btnSpeedUp = (ImageView) findViewById(R.id.main_btn_speed_up);
        btnSpeedDown = (ImageView) findViewById(R.id.main_btn_speed_down);
        viewAltitudeReferenceBar = findViewById(R.id.altitude_reference_bar);
        layoutAltitudeReference = (RelativeLayout) findViewById(R.id.altitude_reference_layout);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        this.googleMap.getUiSettings().setZoomGesturesEnabled(true);
        this.googleMap.getUiSettings().setRotateGesturesEnabled(false);

        if (!TextUtils.isEmpty(fileToLoadPath)) {
            new ParseIGCFileAsyncTask(this).execute();
        }
    }

    private void displayFlightInformation() {
        txtDistance.setText(Html.fromHtml(String.format(getString(R.string.information_distance), String.valueOf(Utilities.getFormattedNumber((int) (igcFile.getDistance() / Constants.Map.METERS_IN_ONE_KILOMETER), getResources().getConfiguration().locale)))));
        txtMaxAltitude.setText(Html.fromHtml(String.format(getString(R.string.information_max_altitude), Utilities.getFormattedNumber(igcFile.getMaxAltitude(), getResources().getConfiguration().locale))));
        txtMinAltitude.setText(Html.fromHtml(String.format(getString(R.string.information_min_altitude), Utilities.getFormattedNumber(igcFile.getMinAltitude(), getResources().getConfiguration().locale))));
        txtLandingTime.setText(Html.fromHtml(String.format(getString(R.string.information_landing), Utilities.getTimeHHMM(igcFile.getLandingTime()))));
        txtTakeOffTime.setText(Html.fromHtml(String.format(getString(R.string.information_takeoff), igcFile.getDate(), Utilities.getTimeHHMM(igcFile.getTakeOffTime()))));
        txtFlightTime.setText(Html.fromHtml(String.format(getString(R.string.information_duration), igcFile.getFlightTime())));
        displayPilot();
        displayGliderInformation();
    }

    private void displayGliderInformation() {
        final String gliderTypeAndId = igcFile.getGliderTypeAndId();
        if (!TextUtils.isEmpty(gliderTypeAndId)) {
            layoutGlider.setVisibility(View.VISIBLE);
            txtGlider.setText(Html.fromHtml(String.format(getResources().getString(R.string.information_glider), gliderTypeAndId)));
        }
    }

    private void displayPilot() {
        if (!TextUtils.isEmpty(igcFile.getPilotInCharge())) {
            layoutPilot.setVisibility(View.VISIBLE);
            txtPilot.setText(Html.fromHtml(String.format(getResources().getString(R.string.information_pilot), Utilities.capitalizeText(igcFile.getPilotInCharge()))));
        }
    }

    private void displayTrack() {
        ArrayList<AltitudeTrackSegment> listTrackSegment = MapUtilities.getAltitudeTrackSegments(igcFile);

        for (AltitudeTrackSegment trackSegment : listTrackSegment) {
            PolylineOptions polyline = getAltitudeTrackPolyline(trackSegment);
            polyline.addAll(Utilities.getLatLngPoints(trackSegment.getListRecords()));
            try {
                googleMap.addPolyline(polyline);
            } catch (Throwable t) {
                Crashlytics.log("FlightPreviewActivity :: Tried to draw polyline when googleMap is null");
                Crashlytics.logException(t);
            }
        }

    }

    private PolylineOptions getAltitudeTrackPolyline(AltitudeTrackSegment trackSegment1) {
        PolylineOptions polyline;
        switch (trackSegment1.getSegmentType()) {
            case ALTITUDE_0_100:
                polyline = new PolylineOptions().width(ResourcesHelper.getDimensionPixelSize(R.dimen.track_line_width)).color(getResources().getColor(R.color.altitude_0_100)).zIndex(0);
                break;
            case ALTITUDE_100_300:
                polyline = new PolylineOptions().width(ResourcesHelper.getDimensionPixelSize(R.dimen.track_line_width)).color(getResources().getColor(R.color.altitude_100_300)).zIndex(1);
                break;
            case ALTITUDE_300_500:
                polyline = new PolylineOptions().width(ResourcesHelper.getDimensionPixelSize(R.dimen.track_line_width)).color(getResources().getColor(R.color.altitude_300_500)).zIndex(2);
                break;
            case ALTITUDE_500_1000:
                polyline = new PolylineOptions().width(ResourcesHelper.getDimensionPixelSize(R.dimen.track_line_width)).color(getResources().getColor(R.color.altitude_500_1000)).zIndex(3);
                break;
            case ALTITUDE_1000_1500:
                polyline = new PolylineOptions().width(ResourcesHelper.getDimensionPixelSize(R.dimen.track_line_width)).color(getResources().getColor(R.color.altitude_1000_1500)).zIndex(4);
                break;
            case ALTITUDE_1500_2000:
                polyline = new PolylineOptions().width(ResourcesHelper.getDimensionPixelSize(R.dimen.track_line_width)).color(getResources().getColor(R.color.altitude_1500_2000)).zIndex(5);
                break;
            case ALTITUDE_2000_2500:
                polyline = new PolylineOptions().width(ResourcesHelper.getDimensionPixelSize(R.dimen.track_line_width)).color(getResources().getColor(R.color.altitude_2000_2500)).zIndex(6);
                break;
            case ALTITUDE_MORE_THAN_2500:
                polyline = new PolylineOptions().width(ResourcesHelper.getDimensionPixelSize(R.dimen.track_line_width)).color(getResources().getColor(R.color.altitude_more_than_2500)).zIndex(7);
                break;
            default:
                polyline = new PolylineOptions().width(ResourcesHelper.getDimensionPixelSize(R.dimen.track_line_width)).color(Color.BLACK).zIndex(-1);

        }

        return polyline;
    }

    private void displayWayPoints() {
        final List<ILatLonRecord> waypoints = igcFile.getWaypoints();
        if (!waypoints.isEmpty()) {
            displayLinesAndAreas(waypoints);
            displayMarkers(waypoints);
            displayFinishStartLines(waypoints);
        }
    }

    private void displayFinishStartLines(List<ILatLonRecord> waypoints) {
        try {
            googleMap.addPolyline(MapUtilities.getPerpendicularPolyline(waypoints.get(1), waypoints.get(2), Constants.Map.START_RADIUS));
            googleMap.addPolyline(MapUtilities.getPerpendicularPolyline(waypoints.get(waypoints.size() - 2), waypoints.get(waypoints.size() - 3), Constants.Map.FINISH_RADIUS));
        } catch (Throwable t) {
            Logger.logError("Error trying to draw task lines: " + t.getMessage());
        }
    }

    private void displayMarkers(List<ILatLonRecord> waypoints) {
        try {
            for (int i = 1; i < waypoints.size() - 2; i++) {
                final ILatLonRecord wayPoint = waypoints.get(i);
                if (!CoordinatesUtilities.isZeroCoordinate(wayPoint)) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(wayPoint.getLatLon().getLat(), wayPoint.getLatLon().getLon()))
                            .draggable(false)
                            .title(((CRecordWayPoint) wayPoint).getDescription()));
                }
            }
        } catch (Throwable t) {
            Logger.logError("Error trying to show markers: " + t.getMessage());
        }
    }

    private void displayLinesAndAreas(List<ILatLonRecord> waypoints) {
        try {
            PolylineOptions polyline = new PolylineOptions().width(ResourcesHelper.getDimensionPixelSize(R.dimen.task_line_width)).color(getResources().getColor(R.color.task_line));

            for (int i = 1; i < waypoints.size() - 1; i++) {
                polyline.add(new LatLng(waypoints.get(i).getLatLon().getLat(), waypoints.get(i).getLatLon().getLon()));
                if (i > 1 && i < waypoints.size() - 2) {
                    googleMap.addCircle(new CircleOptions().center(new LatLng(waypoints.get(i).getLatLon().getLat(), waypoints.get(i).getLatLon().getLon()))
                            .radius(Constants.Map.TASK_RADIUS).strokeColor(Color.TRANSPARENT).strokeWidth(getResources().getDimensionPixelSize(R.dimen.task_line_width))
                            .fillColor(getResources().getColor(R.color.task_fill_color)));
                }
            }
            googleMap.addPolyline(polyline);
        } catch (Throwable t) {
            Logger.logError("Error trying to draw waypoints: " + t.getMessage());
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        handleIntent();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        FileUtilities.deleteCache(this);
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
                TrackerHelper.trackCloseInformation();
                break;
            case R.id.main_information_btn:
                btnShowInformation.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_information_btn_leave));
                btnShowInformation.setVisibility(View.GONE);
                cardviewInformation.setVisibility(View.VISIBLE);
                cardviewInformation.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_fade_in));
                TrackerHelper.trackOpenInformation();
                break;
            case R.id.main_btn_play:
                startFlightReplay();
                break;
            case R.id.main_btn_speed_up:
                speedUpReplay();
                break;
            case R.id.main_btn_speed_down:
                speedDownReplay();
                break;
            case R.id.main_information_btn_viewmore:
                TrackerHelper.trackOpenMoreInformation();
                IGCViewerApplication.setCurrentIGCFile(igcFile);
                Intent intent = new Intent(this, FlightInformationActivity.class);
                startActivity(intent);
                break;
        }
    }

    private class ParseIGCFileAsyncTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<FlightPreviewActivity> activity;

        public ParseIGCFileAsyncTask(FlightPreviewActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        protected Void doInBackground(Void... something) {
            igcFile = Parser.parse(Uri.parse(fileToLoadPath));
            return null;
        }

        protected void onProgressUpdate(Void... something) {
        }

        protected void onPostExecute(Void result) {
            if (activity.get() != null) {
                handleIGCFileLoaded();
            }
        }
    }

    private void handleIGCFileLoaded() {
        listLatLngPoints = Utilities.getLatLngPoints(igcFile.getTrackPoints());
        displayWayPoints();
        displayTrack();
        displayFlightInformation();
        displayReplayViews();
        layoutAltitudeReference.setVisibility(View.VISIBLE);
        viewAltitudeReferenceBar.setBackground(getColorScala());
        mapView.setVisibility(View.VISIBLE);
        cardviewInformation.setVisibility(View.VISIBLE);
        PreferencesHelper.setViewedFlightsForRate();
        loading.setVisibility(View.GONE);
    }

    private void displayReplayViews() {
        if (listLatLngPoints != null && !listLatLngPoints.isEmpty()) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(listLatLngPoints.get(0).latitude + Constants.Map.FIX_INITIAL_LATITUDE, listLatLngPoints.get(0).longitude), Constants.Map.MAP_DEFAULT_ZOOM));
            markerGlider = googleMap.addMarker(new MarkerOptions()
                    .position(listLatLngPoints.get(0))
                    .zIndex(1.0f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_glider))
            );
            setReplayButtons();
            TrackerHelper.trackFlightDisplayed();
        }
    }

    private void setReplayButtons() {
        btnPlay.setOnClickListener(this);
        btnSpeedUp.setOnClickListener(this);
        btnSpeedDown.setOnClickListener(this);


        btnPlay.setImageResource(R.drawable.ic_play_arrow);
        btnSpeedUp.setImageResource(R.drawable.ic_rabbit);
        btnSpeedDown.setImageResource(R.drawable.ic_turtle);

        btnPlay.setVisibility(View.VISIBLE);
        btnSpeedUp.setVisibility(View.GONE);
        btnSpeedDown.setVisibility(View.GONE);

    }


    private void showSpeedToast(int stringId, int speed) {
        if (toast == null) {
            toast = Toast.makeText(this, Constants.EMPTY_STRING, Toast.LENGTH_SHORT);
        }
        if (speed != 0) {
            toast.setText(String.format(getString(R.string.replay_speed_indicator), speed));
        } else {
            toast.setText(stringId);
        }
        toast.show();

    }

    private void speedUpReplay() {
        if (speedCounter < Constants.Map.REPLAY_MAX_MULTIPLIER) {
            replaySpeed = (int) (replaySpeed / Constants.Map.REPLAY_SPEED_INCREASE);
            synchronized (lock) {
                speedCounter++;
            }
            showSpeedToast(R.string.replay_speed_indicator, speedCounter);
            TrackerHelper.trackFastForwardFlight();
        } else {
            showSpeedToast(R.string.replay_speed_maximum, Constants.ZERO);
        }
    }

    private void speedDownReplay() {
        if (speedCounter > Constants.Map.REPLAY_MIN_MULTIPLIER) {
            replaySpeed = (int) (replaySpeed * Constants.Map.REPLAY_SPEED_INCREASE);
            synchronized (lock) {
                speedCounter--;
            }
            showSpeedToast(R.string.replay_speed_indicator, speedCounter);
            TrackerHelper.trackSpeedDownFlight();
        } else {
            showSpeedToast(R.string.replay_speed_minimum, Constants.ZERO);
        }
    }

    public void startFlightReplay() {
        if (listLatLngPoints == null || listLatLngPoints.isEmpty()) {
            return;
        }
        isFinishReplay = !isFinishReplay;
        if (!isFinishReplay) {
            TrackerHelper.trackPlayFlight();
            btnPlay.setImageResource(R.drawable.ic_stop);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerGlider.getPosition(), googleMap.getCameraPosition().zoom));
            animateMarker();
            btnSpeedUp.setVisibility(View.VISIBLE);
            btnSpeedDown.setVisibility(View.VISIBLE);
        } else {
            TrackerHelper.trackStopFlight();
        }
    }


    private void animateMarker() {
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
                    Double heading = SphericalUtil.computeHeading(markerGlider.getPosition(), listLatLngPoints.get(i));
                    markerGlider.setRotation(heading.floatValue());
                    markerGlider.setPosition(listLatLngPoints.get(i));
                }
                i++;

                if (t < 1.0 && i < listLatLngPoints.size() && !isFinishReplay) {
                    handler.postDelayed(this, replaySpeed);
                } else {
                    finishReplay();
                }
            }
        });
    }

    private void finishReplay() {
        markerGlider.setPosition(listLatLngPoints.get(0));
        markerGlider.setRotation(0);
        btnSpeedUp.setVisibility(View.GONE);
        btnSpeedDown.setVisibility(View.GONE);
        replaySpeed = Constants.Map.DEFAULT_REPLAY_SPEED;
        btnPlay.setImageResource(R.drawable.ic_play_arrow);
        speedCounter = Constants.Map.REPLAY_MIN_MULTIPLIER;
        isFinishReplay = true;
        if (toast != null) {
            toast.cancel();
        }
    }

    @Override
    protected void onStop() {
        isFinishReplay = true;
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_share:
                new CopyFileToCacheAndShareAsyncTask(this).execute(fileToLoadPath);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public static PaintDrawable getColorScala() {
        ShapeDrawable.ShaderFactory shaderFactory = new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                LinearGradient linearGradient = new LinearGradient(width, height, 0, 0,
                        new int[]{
                                IGCViewerApplication.getApplication().getResources().getColor(R.color.altitude_0_100),
                                IGCViewerApplication.getApplication().getResources().getColor(R.color.altitude_100_300),
                                IGCViewerApplication.getApplication().getResources().getColor(R.color.altitude_300_500),
                                IGCViewerApplication.getApplication().getResources().getColor(R.color.altitude_500_1000),
                                IGCViewerApplication.getApplication().getResources().getColor(R.color.altitude_1000_1500),
                                IGCViewerApplication.getApplication().getResources().getColor(R.color.altitude_1500_2000),
                                IGCViewerApplication.getApplication().getResources().getColor(R.color.altitude_2000_2500),
                                IGCViewerApplication.getApplication().getResources().getColor(R.color.altitude_more_than_2500)},
                        new float[]{
                                0, 0.07f, 0.14f, 0.28f, 0.42f, 0.56f, 0.7f, 0.84f},
                        Shader.TileMode.REPEAT);
                return linearGradient;
            }
        };

        PaintDrawable paint = new PaintDrawable();
        paint.setShape(new RectShape());
        paint.setShaderFactory(shaderFactory);

        return paint;
    }

    private class GetGmailAttachmentAsyncTask extends AsyncTask<String, Void, String> {
        WeakReference<FlightPreviewActivity> referenceActivity;

        public GetGmailAttachmentAsyncTask(FlightPreviewActivity activity) {
            this.referenceActivity = new WeakReference<>(activity);
        }

        @Override
        protected String doInBackground(String... params) {
            File file = FileUtilities.copyFileToCacheFolder(FlightPreviewActivity.this, params[0], Constants.App.TEMP_TRACK_NAME);
            return file != null ? file.getPath() : Constants.EMPTY_STRING;
        }

        @Override
        protected void onPostExecute(String tempIgcFilePath) {
            if (referenceActivity.get() != null) {
                fileToLoadPath = tempIgcFilePath;
                new ParseIGCFileAsyncTask(FlightPreviewActivity.this).execute();
            }
            super.onPostExecute(tempIgcFilePath);
        }
    }

    private class CopyFileToCacheAndShareAsyncTask extends AsyncTask<String, Void, String> {
        WeakReference<FlightPreviewActivity> referenceActivity;

        public CopyFileToCacheAndShareAsyncTask(FlightPreviewActivity activity) {
            this.referenceActivity = new WeakReference<>(activity);
        }

        @Override
        protected String doInBackground(String... params) {
            File file = FileUtilities.copyFileToCacheFolder(FlightPreviewActivity.this, fileToLoadPath, Uri.parse(fileToLoadPath).getLastPathSegment());
            return file != null ? file.getPath() : Constants.EMPTY_STRING;
        }

        @Override
        protected void onPostExecute(String tempIgcFilePath) {
            launchShareFile(tempIgcFilePath);
            super.onPostExecute(tempIgcFilePath);
        }

    }

    private void launchShareFile(String tempIgcFilePath) {
        try {
            TrackerHelper.trackShareFlight();
            Intent intentEmail = new Intent(Intent.ACTION_SEND);
            intentEmail.setType(Constants.App.TEXT_HTML);
            intentEmail.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(FlightPreviewActivity.this, Constants.App.FILE_PROVIDER, new File(tempIgcFilePath)));
            intentEmail.putExtra(Intent.EXTRA_SUBJECT, String.format(getString(R.string.share_email_subject), Uri.parse(tempIgcFilePath).getLastPathSegment()));
            startActivity(Intent.createChooser(intentEmail, getString(R.string.share)));
        } catch (Throwable t) {
            Toast.makeText(this, R.string.sorry_error_happen, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.flight_preview_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
}
