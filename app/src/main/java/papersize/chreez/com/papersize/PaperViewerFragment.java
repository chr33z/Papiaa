package papersize.chreez.com.papersize;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.androidannotations.annotations.Click;

import papersize.chreez.com.papersize.paper.Paper;
import papersize.chreez.com.papersize.paper.Unit;

public class PaperViewerFragment extends Fragment {

    private static final int MAX_BLEEDING = 20;

    private PaperCanvas mCanvas;
    private TextView mHeader;
    private TextView mIncreaseBleed;
    private TextView mDecreaseBleed;
    private TextView mLabelBleeding;
    private TextView mToogleOrientation;

    private Paper paper;

    private float bleedingTouchX;
    private double currentBleeding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paper_viewer, container, false);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");

        mHeader = (TextView) view.findViewById(R.id.header);
        mCanvas = (PaperCanvas) view.findViewById(R.id.canvas);
        mLabelBleeding = (TextView) view.findViewById(R.id.text_bleed);
        mLabelBleeding.setOnTouchListener(bleedingTouchListener);

        mIncreaseBleed = (TextView) view.findViewById(R.id.text_increase_bleed);
        mIncreaseBleed.setTypeface(font);
        mIncreaseBleed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increasePaperBleed();
            }
        });

        mDecreaseBleed = (TextView) view.findViewById(R.id.text_decrease_bleed);
        mDecreaseBleed.setTypeface(font);
        mDecreaseBleed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreasePaperBleed();
            }
        });

        mToogleOrientation = (TextView) view.findViewById(R.id.text_toogle_orientation);
        mToogleOrientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePaperOrientation();
            }
        });

        updatePaperBleeding(0.0);

        return view;
    }

    @Override
    public void onResume() {
        updateInterface();
        super.onResume();
    }

    void increasePaperBleed() {
        double bleed = paper.getBleed();
        updatePaperBleeding(bleed + 1);
    }

    void decreasePaperBleed() {
        double bleed = paper.getBleed();
        updatePaperBleeding(bleed - 1);
    }

    private void updatePaperBleeding(double bleed) {
        if(bleed >= 0 && bleed <= MAX_BLEEDING) {
            if(bleed == 0) {
                mLabelBleeding.setText(
                        getText(R.string.label_add_bleeding));
            } else {
                mLabelBleeding.setText(
                        getText(R.string.label_bleeding) + " " + bleed + " " + Unit.MILLIMETER.getName());
            }

            if(bleed <= 0) {
                mDecreaseBleed.setEnabled(false);
            } else if(bleed >= MAX_BLEEDING) {
                mIncreaseBleed.setEnabled(false);
            } else {
                mIncreaseBleed.setEnabled(true);
                mDecreaseBleed.setEnabled(true);
            }

            paper.setBleed(Unit.MILLIMETER, bleed);
            mCanvas.setPaper(paper);
        }
    }

    void togglePaperOrientation() {
        mCanvas.togglePaperOrientation();
    }

    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    private void updateInterface() {
        if(mHeader != null && paper != null) {
            mHeader.setText(paper.getName());
        }

        if(mCanvas != null && paper != null) {
            mCanvas.setPaper(paper);
        }
    }

    private View.OnTouchListener bleedingTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    bleedingTouchX = event.getX();
                    currentBleeding = paper.getBleed();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float x = event.getX();
                    float difference = x - bleedingTouchX;

                    int maxValue = mCanvas.getWidth() / 2;
                    int stepSize = maxValue / MAX_BLEEDING;

                    updatePaperBleeding(currentBleeding + (int) (difference / stepSize));
                    return true;
            }

            return true;
        }
    };
}
