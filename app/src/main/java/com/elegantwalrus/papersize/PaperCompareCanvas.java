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
public class PaperCompareCanvas extends View {

    /**
     * The renderer for the paper. This class handles all the drawing and
     * animation on the canvas
     */
    private PaperRenderer paperRendererFront;

    private PaperRenderer paperRendererBack;

    private float scaleX = 0.0f;

    private float scaleY = 0.0f;

    private boolean originalIsLarger = false;

    public PaperCompareCanvas(Context context) {
        super(context);
    }

    public PaperCompareCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaperCompareCanvas(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPaper(Paper paper) {
        if(paperRendererFront == null) {
            paperRendererFront = new PaperRenderer(getContext(), paper);
        } else {
            paperRendererFront.setPaper(paper);
        }

        invalidate();
    }

    public void setPaperToCompareTo(Paper paper) {
        if(paperRendererBack == null) {
            paperRendererBack = new PaperRenderer(getContext(), paper);
        } else {
            paperRendererBack.setPaper(paper);
        }

        invalidate();
    }

    public void setBleeding(Unit unit, double bleeding) {
        if(paperRendererFront != null) {
            paperRendererFront.setBleeding(unit, bleeding, this);
        }
    }

    public void togglePaperOrientation() {
        if(paperRendererFront != null) {
            paperRendererFront.togglePaperOrientation(this);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(paperRendererBack == null || paperRendererFront == null) {
            return;
        }

        // calculate scale and determine if front or back is bigger
        if(scaleX == 0.0f || scaleY == 0.0f){
            scaleX = (float) (paperRendererFront.getPaper().getWidth() / paperRendererBack.getPaper().getWidth());
            scaleY = (float) (paperRendererFront.getPaper().getHeight() / paperRendererBack.getPaper().getHeight());

            if(scaleY > 1) {
                scaleX = 1 / scaleX;
                scaleY = 1 / scaleY;

                originalIsLarger = true;
            }
        }

        if(!originalIsLarger) {
            // Scale the paper we compare and draw it in the front

            if(!paperRendererBack.isInitialized()) {
                paperRendererBack.initialize(getWidth(), getHeight());
                paperRendererBack.setDrawStyle( PaperRenderer.Style.COMPARED_TO_BACK);
            }

            if(!paperRendererFront.isInitialized()) {
                paperRendererFront.initialize(getWidth(), getHeight(), scaleX, scaleY);
                paperRendererFront.setDrawStyle( PaperRenderer.Style.ORIGINAL_FRONT);
            }

            paperRendererBack.draw(canvas);
            paperRendererFront.draw(canvas);

        } else {
            // Scale the paper we COMPARE TO and draw it in the front

            if(!paperRendererBack.isInitialized()) {
                paperRendererBack.initialize(getWidth(), getHeight(), scaleX, scaleY);
                paperRendererBack.setDrawStyle( PaperRenderer.Style.COMPARED_TO_FRONT);
            }

            if(!paperRendererFront.isInitialized()) {
                paperRendererFront.initialize(getWidth(), getHeight());
                paperRendererFront.setDrawStyle( PaperRenderer.Style.ORIGINAL_BACK);
            }

            paperRendererFront.draw(canvas);
            paperRendererBack.draw(canvas);
        }

        super.onDraw(canvas);
    }
}
