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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;

import com.shollmann.android.igcparser.util.Logger;
import com.shollmann.igcparser.R;
import com.shollmann.igcparser.events.FileClickEvent;
import com.shollmann.igcparser.ui.adapter.FilesAdapter;
import com.shollmann.igcparser.util.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class IGCFilesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FilesAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<File> listFiles = new ArrayList<>();
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_igc_files);

        findViews();

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FilesAdapter(listFiles);
        recyclerView.setAdapter(adapter);

        new findIGCFilesAsynkTask().execute(Constants.XCSOAR_lOG_PATH);

    }

    private void findViews() {
        recyclerView = (RecyclerView) findViewById(R.id.files_recyclerview);
        loading = (ProgressBar) findViewById(R.id.files_loading);
    }

    private List<File> getListFiles(File parentDir) {
        List<File> inFiles = new ArrayList<>();
        Queue<File> files = new LinkedList<>();
        files.addAll(Arrays.asList(parentDir.listFiles()));
        while (!files.isEmpty()) {
            File file = files.remove();
            if (file != null && file.isDirectory()) {
                files.addAll(Arrays.asList(file.listFiles()));
            } else if (file != null && (file.getName().toLowerCase().endsWith(".igc"))) {
                inFiles.add(file);
            }
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
        Intent intent = new Intent(this, FlightInformationActivity.class);
        intent.putExtra(Constants.FILE_TO_LOAD_PATH, event.getFile().getAbsoluteFile().toString());
        startActivity(intent);
    }

    private class findIGCFilesAsynkTask extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... path) {
            listFiles = getListFiles(new File(path[0]));
            return path[0].equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath());
        }

        protected void onProgressUpdate(Void... something) {
        }

        protected void onPostExecute(Boolean isEntireFolder) {
            if (!listFiles.isEmpty()) {
                loading.setVisibility(RecyclerView.GONE);
                adapter.setDataset(listFiles);
                adapter.notifyDataSetChanged();
            }
            if (!isEntireFolder) {
                Logger.log("No igc files found on XCSoar folder. Searching in the entire SD Card");
                new findIGCFilesAsynkTask().execute(Environment.getExternalStorageDirectory().getAbsolutePath());
            }
        }
    }
}
