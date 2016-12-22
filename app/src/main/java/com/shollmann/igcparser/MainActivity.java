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

package com.shollmann.igcparser;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.shollmann.android.igcparser.Parser;
import com.shollmann.android.igcparser.model.IGCFile;
import com.shollmann.android.igcparser.util.Utilities;

import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final int MAP_DEFAULT_ZOOM = 12;
    public static final int METERS_IN_ONE_KILOMETER = 1000;
    public static final float MAP_TRACK_POLYLINE_WIDTH = 2.0f;
    private IGCFile igcFile;
    private MapView mapView;
    private GoogleMap googleMap;
    private List<LatLng> latLngPoints;
    private TextView txtDistance;
    private TextView txtMaxAltitude;
    private TextView txtMinAltitude;
    private TextView txtTakeOffTime;
    private TextView txtLandingTime;
    private TextView txtFlightTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();

        igcFile = Parser.parse(getBaseContext(), Uri.parse("http://google.com"));

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

    }

    private void findViews() {
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.main_map);
        txtDistance = (TextView) findViewById(R.id.main_txt_distance);
        txtMaxAltitude = (TextView) findViewById(R.id.main_txt_max_altitude);
        txtMinAltitude = (TextView) findViewById(R.id.main_txt_start_altitude);
        txtTakeOffTime = (TextView) findViewById(R.id.main_txt_takeoff);
        txtLandingTime = (TextView) findViewById(R.id.main_txt_landing);
        txtFlightTime = (TextView) findViewById(R.id.main_txt_duration);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setZoomGesturesEnabled(true);
        this.googleMap.getUiSettings().setMapToolbarEnabled(true);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);

        displayTrack();
        displayFlightInformation();
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngPoints.get(0), MAP_DEFAULT_ZOOM));
    }

    private void displayFlightInformation() {
        txtDistance.setText(String.format(getString(R.string.information_distance), String.valueOf((int) (igcFile.getDistance() / METERS_IN_ONE_KILOMETER))));
        txtMaxAltitude.setText(String.format(getString(R.string.information_max_altitude), igcFile.getMaxAltitude()));
        txtMinAltitude.setText(String.format(getString(R.string.information_min_altitude), igcFile.getMinAltitude()));
        txtLandingTime.setText(String.format(getString(R.string.information_landing), Utilities.getTimeHHMM(igcFile.getLandingTime())));
        txtTakeOffTime.setText(String.format(getString(R.string.information_takeoff), Utilities.getTimeHHMM(igcFile.getTakeOffTime())));
        txtFlightTime.setText(String.format(getString(R.string.information_duration), igcFile.getFlightTime()));
    }

    private void displayTrack() {
        PolylineOptions polyline = new PolylineOptions().width(MAP_TRACK_POLYLINE_WIDTH).color(Color.BLUE);
        latLngPoints = Utilities.getLatLngPoints(igcFile.getTrackPoints());
        polyline.addAll(latLngPoints);
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
}
