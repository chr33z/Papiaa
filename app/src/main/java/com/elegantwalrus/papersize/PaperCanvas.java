package com.elegantwalrus.papersize;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.elegantwalrus.papersize.paper.Paper;
import com.elegantwalrus.papersize.paper.Unit;
import com.elegantwalrus.papersize.renderer.PaperRenderer;

/**
 * Created by Christopher Gebhardt on 24.04.15.
 */
public class PaperCanvas extends View {

    /**
     * The renderer for the paper. This class handles all the drawing and
     * animation on the canvas
     */
    private PaperRenderer paperRenderer;

    public PaperCanvas(Context context) {
        super(context);
    }

    public PaperCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaperCanvas(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPaper(Paper paper) {
        if(paperRenderer == null) {
            paperRenderer = new PaperRenderer(getContext(), paper);
        } else {
            paperRenderer.setPaper(paper);
        }

        invalidate();
    }

    public void setBleeding(Unit unit, double bleeding) {
        if(paperRenderer != null) {
            paperRenderer.setBleeding(unit, bleeding, this);
        }
    }

    public void togglePaperOrientation() {
        if(paperRenderer != null) {
            paperRenderer.togglePaperOrientation(this);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(paperRenderer != null) {
            if(!paperRenderer.isInitialized()) {
                paperRenderer.initialize(getWidth(), getHeight());
            }
            paperRenderer.draw(canvas);
        }

        super.onDraw(canvas);
    }
}
