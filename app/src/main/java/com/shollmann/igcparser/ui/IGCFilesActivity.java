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

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.shollmann.android.igcparser.Parser;
import com.shollmann.android.igcparser.model.IGCFile;
import com.shollmann.android.igcparser.util.Logger;
import com.shollmann.android.igcparser.util.Utilities;
import com.shollmann.igcparser.R;
import com.shollmann.igcparser.events.FileClickEvent;
import com.shollmann.igcparser.events.RateUsClickedEvent;
import com.shollmann.igcparser.tracking.TrackerHelper;
import com.shollmann.igcparser.ui.adapter.FilesAdapter;
import com.shollmann.igcparser.ui.view.RateUsView;
import com.shollmann.igcparser.util.Comparators;
import com.shollmann.igcparser.util.Constants;
import com.shollmann.igcparser.util.PreferencesHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class IGCFilesActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener, PopupMenu.OnMenuItemClickListener {

    private static final int EXTERNAL_STORAGE_PERMISSION_REQUEST = 1001;
    private RateUsView viewRateUs;
    private LinearLayout layoutLoading;
    private LinearLayout layoutEmpty;
    private RecyclerView recyclerView;
    private TextView txtLoading;
    private ProgressBar progress;
    private FilesAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<IGCFile> listFiles = new ArrayList<>();
    private File lastSearchedPath;
    private boolean isSearching = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_igc_files);
        findViews();
        setupFilesList();
        checkForStoragePemrission();
    }

    private void setupRateUsView() {
        if (!PreferencesHelper.isRated() && PreferencesHelper.getViewedFlightCountForRate() >= PreferencesHelper.getMinFlightsViewedToRate()) {
            viewRateUs.setVisibility(View.VISIBLE);
        } else {
            viewRateUs.setVisibility(View.GONE);
        }
    }

    private void setupFilesList() {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FilesAdapter(listFiles);
        recyclerView.setAdapter(adapter);
    }

    private void findViews() {
        recyclerView = (RecyclerView) findViewById(R.id.files_recyclerview);
        layoutLoading = (LinearLayout) findViewById(R.id.files_layout_loading);
        layoutEmpty = (LinearLayout) findViewById(R.id.files_layout_empty_state);
        txtLoading = (TextView) findViewById(R.id.files_loading_text);
        progress = (ProgressBar) findViewById(R.id.files_loading_progress);
        viewRateUs = (RateUsView) findViewById(R.id.view_rate_us);
    }

    private List<IGCFile> getListIGCFiles(File parentDir) {
        List<IGCFile> inFiles = new ArrayList<>();
        Queue<File> files = new LinkedList<>();
        try {
            files.addAll(Arrays.asList(parentDir.listFiles()));
            while (!files.isEmpty()) {
                File file = files.remove();
                if (!Utilities.isUnlikelyIGCFolder(file)) {
                    if (file != null && file.isDirectory()) {
                        files.addAll(Arrays.asList(file.listFiles()));
                    } else if (file != null && (file.getName().toLowerCase().endsWith(".igc"))) {
                        inFiles.add(Parser.quickParse(Uri.parse(file.getAbsolutePath())));
                    }
                }
            }
            Collections.sort(inFiles, Comparators.compareByDate);
        } catch (Throwable t) {
            final String message = "Couldn't open files";
            Crashlytics.log(message);
            Crashlytics.logException(t);
            Logger.logError(message);
        }


        return inFiles;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FileClickEvent event) {
        TrackerHelper.trackTapFile();
        Intent intent = new Intent(this, FlightPreviewActivity.class);
        intent.putExtra(Constants.FILE_TO_LOAD_PATH, event.getFile().getAbsoluteFile().toString());
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RateUsClickedEvent event) {
        viewRateUs.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.files_menu, menu);
        MenuItem menuSearchEntireSdCard = menu.findItem(R.id.menu_search_sdcard);
        MenuItem menuRefresh = menu.findItem(R.id.menu_refresh);
        MenuItem menuAbout = menu.findItem(R.id.menu_about);
        MenuItem menuShare = menu.findItem(R.id.menu_share);

        ImageView viewAttach = (ImageView) menu.findItem(R.id.menu_sort).getActionView();
        viewAttach.setBackgroundResource(R.drawable.drawable_sort_icon);
        viewAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortDialog(v);
            }

        });

        menuSearchEntireSdCard.setOnMenuItemClickListener(this);
        menuRefresh.setOnMenuItemClickListener(this);
        menuAbout.setOnMenuItemClickListener(this);
        menuShare.setOnMenuItemClickListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    private void showSortDialog(View v) {
        TrackerHelper.trackSortDialog();
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.sort_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (isSearching) {
            Toast.makeText(this, R.string.search_flights_wait, Toast.LENGTH_LONG).show();
            return false;
        }
        switch (menuItem.getItemId()) {
            case R.id.menu_refresh:
                TrackerHelper.trackRefresh();
                searchForFiles(listFiles.isEmpty() ? Utilities.getXCSoarDataFolder() : lastSearchedPath);
                break;
            case R.id.menu_search_sdcard:
                TrackerHelper.trackSearchSdCard();
                searchForFiles(Utilities.getSdCardFolder());
                break;
            case R.id.menu_share:
                TrackerHelper.trackShareApp();
                shareApp();
                break;
            case R.id.menu_sort_glider:
                TrackerHelper.trackSortByGlider();
                sortBy(Comparators.compareByGlider);
                break;
            case R.id.menu_sort_pilot:
                TrackerHelper.trackSortByPilot();
                sortBy(Comparators.compareByPilot);
                break;
            case R.id.menu_sort_date:
                TrackerHelper.trackSortByDate();
                sortBy(Comparators.compareByDate);
                break;
            case R.id.menu_about:
                TrackerHelper.trackAbout();
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
        }
        return false;
    }

    private void searchForFiles(File path) {
        listFiles.clear();
        adapter.notifyDataSetChanged();
        txtLoading.setText(getString(R.string.searching_igc_files));
        showProgressViews();
        new FindIGCFilesAsyncTask(this).execute(path);
    }

    private void showProgressViews() {
        layoutEmpty.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.VISIBLE);
        progress.setVisibility(View.VISIBLE);
    }

    private void sortBy(Comparator<IGCFile> comparator) {
        if (!isSearching) {
            Collections.sort(listFiles, comparator);
            adapter.notifyDataSetChanged();
        }
    }

    private class FindIGCFilesAsyncTask extends AsyncTask<File, Void, Boolean> {
        WeakReference<IGCFilesActivity> activity;

        public FindIGCFilesAsyncTask(IGCFilesActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        protected Boolean doInBackground(File... file) {
            isSearching = true;
            lastSearchedPath = file[0];
            listFiles = getListIGCFiles(file[0]);
            return file[0].getAbsolutePath().equals(Utilities.getSdCardFolder().getAbsolutePath());
        }

        protected void onProgressUpdate(Void... something) {
        }

        protected void onPostExecute(Boolean isEntireFolder) {
            if (activity.get() != null) {
                handleFinishFilesLoad(isEntireFolder);
            }
        }

        private void handleFinishFilesLoad(Boolean isEntireFolder) {
            if (!listFiles.isEmpty()) {
                layoutLoading.setVisibility(RecyclerView.GONE);
                adapter.setDataset(listFiles);
                adapter.notifyDataSetChanged();
            } else {
                if (!isEntireFolder) {
                    final String message = "No IGC files found on XCSoar folder. Searching on other folders";
                    Logger.log(message);
                    Crashlytics.log(message);
                    txtLoading.setText(getString(R.string.searching_igc_files));
                    new FindIGCFilesAsyncTask(activity.get()).execute(Utilities.getSdCardFolder());
                } else {
                    viewRateUs.setVisibility(View.GONE);
                    layoutLoading.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.VISIBLE);
                    TrackerHelper.trackNoFilesFound();
                }
            }
            isSearching = false;
        }
    }

    private void checkForStoragePemrission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            txtLoading.setClickable(false);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_PERMISSION_REQUEST);

        } else {
            new FindIGCFilesAsyncTask(this).execute(Utilities.getSdCardFolder());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_PERMISSION_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new FindIGCFilesAsyncTask(this).execute(Utilities.getSdCardFolder());
                } else {
                    layoutEmpty.setVisibility(View.GONE);
                    progress.setVisibility(View.GONE);
                    txtLoading.setClickable(true);
                    txtLoading.setText(R.string.need_storage_access);
                    txtLoading.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkForStoragePemrission();
                        }
                    });
                }
                return;
            }
        }
    }

    private void shareApp() {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_app_link));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Throwable t) {
            Toast.makeText(this, R.string.sorry_error_happen, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupRateUsView();
    }
}
