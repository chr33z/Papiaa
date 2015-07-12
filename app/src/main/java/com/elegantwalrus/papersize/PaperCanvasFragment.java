package com.elegantwalrus.papersize;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegantwalrus.papersize.paper.Paper;
import com.elegantwalrus.papersize.paper.Unit;

/**
 * Created by chris on 28.04.15.
 */
public class PaperCanvasFragment extends Fragment {

    private Paper mPaper;

    private Paper paperToCompare;

    private PaperCompareCanvas mCanvas;

    public PaperCanvasFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_paper_canvas, container, false);

        mCanvas = (PaperCompareCanvas) rootView.findViewById(R.id.canvas);
        mCanvas.setPaper(mPaper);
        mCanvas.setPaperToCompareTo(paperToCompare);
        return rootView;
    }

    public void setPaper(Paper paper) {
        this.mPaper = paper;
    }

    public void setPaperToCompareTo(Paper paper) {
        this.paperToCompare = paper;
    }

    public Paper getPaper() {
        return mPaper;
    }

    public void setBleeding(Unit unit, double bleeding) {
        if(mCanvas != null) {
            mCanvas.setBleeding(unit, bleeding);
        }
    }

    public void setScaling(float scaling) {
        if(mCanvas != null) {
            mCanvas.setScaleX(scaling);
            mCanvas.setScaleY(scaling);
        }
    }

    public void setAlpha(float alpha) {
        mCanvas.setAlpha(alpha);
    }

    public void setRotation(float amount, int dregrees, int direction) {
        mCanvas.setRotationY(amount * dregrees * direction);
    }

    public void invalidate() {
        mCanvas.invalidate();
    }

    public void togglePaperOrientation() {
        if(mCanvas != null) {
            mCanvas.togglePaperOrientation();
        }
    }
}
