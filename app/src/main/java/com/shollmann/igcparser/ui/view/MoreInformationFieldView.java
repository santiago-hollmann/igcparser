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
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shollmann.igcparser.R;

public class MoreInformationFieldView extends RelativeLayout {
    private TextView txtTitle;
    private TextView txtValue;
    private ImageView imgIcon;

    public MoreInformationFieldView(Context context) {
        this(context, null);
    }

    public MoreInformationFieldView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MoreInformationFieldView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.view_more_info_field, this);

        txtTitle = (TextView) findViewById(R.id.more_info_title);
        txtValue = (TextView) findViewById(R.id.more_info_value);
        imgIcon = (ImageView) findViewById(R.id.more_info_icon);
    }

    public void show(int iconId, int titleStringId, String value) {
        txtTitle.setText(getResources().getString(titleStringId));
        txtValue.setText(value);
        imgIcon.setImageResource(iconId);
//        Drawable dr = getResources().getDrawable(iconId);
//        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
//        final int dimensionPixelSize = ResourcesHelper.getDimensionPixelSize(R.dimen.information_icon_size);
//        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, dimensionPixelSize, dimensionPixelSize, true));

        //setCompoundDrawablesWithIntrinsicBounds (image to left, top, right, bottom)
//        imgIcon.setBackgroundDrawable(d);
//        imgIcon.setColorFilter(getResources().getColor(R.color.colorAccent));
//        imgIcon.setColorFilter(ContextCompat.getColor(getContext(),R.color.colorAccent));

    }


}
