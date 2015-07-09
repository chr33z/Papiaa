package com.elegantwalrus.papersize;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.elegantwalrus.papersize.paper.Paper;
import com.elegantwalrus.papersize.paper.Unit;

import java.text.DecimalFormat;

/**
 * Dialog to calculate the pixel dimensions from a paper format using ppi as input.
 *
 * Created by chris on 14.05.15.
 */
public class PixelDimensionsDialog {

    private static final int MAX_PPI = 9999;

    private Context mContext;

    private Unit unit;

    private double mWidth;

    private double mHeight;

    private MaterialDialog mDialog;

    private TextView textResult;

    private DecimalFormat doubleFormat = new DecimalFormat("#.##");

    /**
     * True if the edittext contains either "" or "0". Every number a user taps
     * after this state overwrites the whole edittext.
     */
    private boolean startsAtBeginning = false;

    public PixelDimensionsDialog(@NonNull Context context, @NonNull Paper paper, @NonNull Unit unit) {
        this.mContext = context;
        this.unit = unit;
        this.mWidth = paper.getWidth();
        this.mHeight = paper.getHeight();

        initializeDialog();
    }

    private void initializeDialog() {
        boolean wrapInScrollView = true;
        String title = String.format(mContext.getString(R.string.dialog_format_title_dimension_in_pixel),
                doubleFormat.format(Unit.fromMillimeter(unit, mWidth)),
                doubleFormat.format(Unit.fromMillimeter(unit, mHeight)),
                unit.getName());
        mDialog = new MaterialDialog.Builder(mContext)
                .title(title)
                .customView(R.layout.dialog_dimension_in_pixel, wrapInScrollView)
                .positiveText(R.string.dialog_ok)
                .build();

        View view = mDialog.getCustomView();
        textResult = (TextView) view.findViewById(R.id.text2);

        final EditText editText = (EditText) view.findViewById(R.id.edittext1);
        editText.setSelection(editText.getText().length());

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString();
                int dpi = Integer.parseInt(!content.equals("") ? s.toString() : "0");

                /*
                    When the input is empty we consider every number after that as the new
                    first number. So when the input contains "0" a following "3" will
                    overwrite the input with "3" instead of "30" or "03".
                 */
                if (startsAtBeginning && content.length() == 2) {
                    content = content.replace("0", "");
                    editText.setText(content);
                    startsAtBeginning = false;
                }

                startsAtBeginning = content.equals("") || content.equals("0");

                if (dpi > MAX_PPI) {
                    editText.setText(String.valueOf(MAX_PPI));
                } else if (content.equals("")) {
                    editText.setText(String.valueOf(dpi));
                } else {
                    calculateAndSetResult(dpi);
                }

                editText.setSelection(editText.getText().length());
            }
        });

        mDialog.findViewById(R.id.container_edit_text).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(editText.requestFocus()) {
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE))
                            .showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }
                return true;
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
