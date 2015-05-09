package papersize.chreez.com.papersize;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import papersize.chreez.com.papersize.paper.Paper;
import papersize.chreez.com.papersize.paper.Unit;

/**
 * Created by chris on 28.04.15.
 */
public class PaperCanvasFragment extends Fragment {

    private Paper mPaper;

    private PaperCanvas mCanvas;

    public PaperCanvasFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_paper_canvas, container, false);

        mCanvas = (PaperCanvas) rootView.findViewById(R.id.canvas);
        mCanvas.setPaper(mPaper);
        return rootView;
    }

    public void setPaper(Paper paper) {
        this.mPaper = paper;
    }

    public Paper getPaper() {
        return mPaper;
    }

    public void setBleeding(Unit unit, double bleeding) {
        if(mCanvas != null) {
            mCanvas.setBleeding(unit, bleeding);
        }
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
