package com.elegantwalrus.papersize;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.elegantwalrus.papersize.paper.Paper;
import com.elegantwalrus.papersize.paper.Unit;

/**
 * Dialog to calculate the pixel dimensions from a paper format using ppi as input.
 *
 * Created by chris on 14.05.15.
 */
public class PixelDimensionsDialog {

    private Context mContext;

    private double mWidth;

    private double mHeight;

    private double mPPI;

    private int mWidthInPixel;

    private int mHeightInPixel;

    private MaterialDialog mDialog;

    private TextView textResult;

    public PixelDimensionsDialog(@NonNull Context context, @NonNull Paper paper) {
        this.mContext = context;
        this.mWidth = paper.getWidth();
        this.mHeight = paper.getHeight();

        initializeDialog();
    }

    private void initializeDialog() {
        boolean wrapInScrollView = true;
        mDialog = new MaterialDialog.Builder(mContext)
                .title(R.string.dialog_title_dimension_in_pixel)
                .customView(R.layout.dialog_dimension_in_pixel, wrapInScrollView)
                .positiveText(R.string.dialog_close)
                .build();

        View view = mDialog.getCustomView();
        textResult = (TextView) view.findViewById(R.id.text2);

        EditText editText = (EditText) view.findViewById(R.id.edittext1);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int dpi = Integer.parseInt(!s.toString().equals("") ? s.toString() : "0");
                calculateAndSetResult(dpi);
            }
        });

        // calculate pixel at startup
        calculateAndSetResult(Integer.parseInt(editText.getText().toString()));
    }

    private void calculateAndSetResult(int dpi) {
        int widthInPixels = (int) (Unit.fromMillimeter(Unit.INCH, mWidth) * dpi);
        int heightInPixels = (int) (Unit.fromMillimeter(Unit.INCH, mHeight) * dpi);

        textResult.setText(String.format(
                mContext.getString(R.string.format_pixel_size), widthInPixels, heightInPixels));
    }

    public void show() {
        mDialog.show();
    }
}
