package papersize.chreez.com.papersize;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
    private TextView mToogleOrientation;

    private Paper paper;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paper_viewer, container, false);
        mHeader = (TextView) view.findViewById(R.id.header);
        mCanvas = (PaperCanvas) view.findViewById(R.id.canvas);

        mIncreaseBleed = (TextView) view.findViewById(R.id.text_increase_bleed);
        mIncreaseBleed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increasePaperBleed();
            }
        });

        mDecreaseBleed = (TextView) view.findViewById(R.id.text_decrease_bleed);
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

        return view;
    }

    @Override
    public void onResume() {
        updateInterface();
        super.onResume();
    }

    void increasePaperBleed() {
        if(paper.getBleed() < MAX_BLEEDING) {
            double bleed = paper.getBleed();
            paper.setBleed(Unit.MILLIMETER, bleed + 1);
            mCanvas.setPaper(paper);
        }
    }

    void decreasePaperBleed() {
        if(paper.getBleed() > 0) {
            double bleed = paper.getBleed();
            paper.setBleed(Unit.MILLIMETER, bleed - 1);
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
}
