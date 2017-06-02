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

package com.shollmann.igcparser.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shollmann.android.igcparser.model.IGCFile;
import com.shollmann.android.igcparser.util.Utilities;
import com.shollmann.igcparser.R;
import com.shollmann.igcparser.events.FileClickEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class FileViewHolder extends RecyclerView.ViewHolder {
    private TextView txtDate;
    private TextView txtPilot;
    private TextView txtGlider;
    private RelativeLayout container;
    private TextView txtFileName;

    public FileViewHolder(View v) {
        super(v);
        container = (RelativeLayout) v.findViewById(R.id.view_file_container);
        txtFileName = (TextView) v.findViewById(R.id.view_file_txt_name);
        txtDate = (TextView) v.findViewById(R.id.view_file_txt_date);
        txtPilot = (TextView) v.findViewById(R.id.view_file_txt_pilot);
        txtGlider = (TextView) v.findViewById(R.id.view_file_txt_glider);
    }

    public void setData(final IGCFile igcFile) {
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (igcFile != null) {
                    EventBus.getDefault().post(new FileClickEvent(new File(igcFile.getFilePath())));
                }
            }
        });

        txtFileName.setText(igcFile.getFileName());
        txtPilot.setText(Utilities.capitalizeText(igcFile.getPilotInCharge()));
        txtPilot.setVisibility(TextUtils.isEmpty(igcFile.getPilotInCharge()) ? View.GONE : View.VISIBLE);
        txtDate.setText(igcFile.getDate());
        txtGlider.setText(igcFile.getGliderTypeAndId());
        txtGlider.setVisibility(TextUtils.isEmpty(igcFile.getGliderTypeAndId()) ? View.GONE : View.VISIBLE);
    }
}
