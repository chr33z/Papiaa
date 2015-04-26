package papersize.chreez.com.papersize;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import papersize.chreez.com.papersize.paper.Paper;
import papersize.chreez.com.papersize.paper.Unit;

/**
 * Created by Christopher Gebhardt on 24.04.15.
 */
public class PaperCanvas extends View {

    private Paper paper;

    private int canvasWidth;
    private int canvasHeight;
    private int padding;
    private int shadow;
    private int paperWidth;
    private int paperHeight;

    private float sizeFactor;
    private int bleed;

    private Rect paperRect = new Rect();
    private Rect shadowRect = new Rect();
    private Rect bleedRect = new Rect();

    private Paint paint = new Paint();

    private boolean landscapeMode = false;

    public PaperCanvas(Context context) {
        super(context);

        initView();
    }

    public PaperCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView();
    }

    public PaperCanvas(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Light.ttf");
        paint.setTypeface(tf);
        paint.setTextSize(paint.getTextSize() * 4.0f);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    public void setPaper(Paper paper) {
        this.paper = paper;
        invalidate();
    }

    public void togglePaperOrientation() {
        if(landscapeMode) {
            setPortrait();
        } else {
            setLandscape();
        }
    }

    public void setLandscape(){
        landscapeMode = true;
        invalidate();
    }

    public void setPortrait() {
        landscapeMode = false;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(paper == null) {
            return;
        }

        canvasWidth = this.getWidth();
        canvasHeight = this.getHeight();

        sizeFactor = (float) (paperWidth / paper.getWidth());
        bleed = (int) (sizeFactor * paper.getBleed());

        padding = (int) (canvasWidth * 0.2);
        shadow = (int) (canvasWidth * 0.025);

        paperWidth = canvasWidth - padding - padding;
        paperHeight = (int) (paperWidth * (paper.getHeight() / paper.getWidth()));

        paperRect.left = padding + bleed;
        paperRect.right = padding + paperWidth - bleed;
        paperRect.top = padding + bleed;
        paperRect.bottom = padding + paperHeight - bleed;

        if(!landscapeMode) {
            shadowRect.left = paperRect.left + shadow;
            shadowRect.right = paperRect.right + shadow;
            shadowRect.top = paperRect.top + shadow;
            shadowRect.bottom = paperRect.bottom + shadow;
        } else {
            shadowRect.left = paperRect.left - shadow;
            shadowRect.right = paperRect.right - shadow;
            shadowRect.top = paperRect.top + shadow;
            shadowRect.bottom = paperRect.bottom + shadow;
        }

        bleedRect.top = padding;
        bleedRect.right = padding + paperWidth;
        bleedRect.bottom = padding + paperHeight;
        bleedRect.left = padding;

        if(landscapeMode) {
            canvas.rotate(-90, canvasWidth / 2, canvasHeight / 2);
        }

        drawPaper(canvas);
        drawPrintMarkers(canvas);
        drawBleed(canvas);

        super.onDraw(canvas);
    }

    private void drawPrintMarkers(Canvas canvas) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);

        int strokePadding = (int) (canvasWidth * 0.01);
        int strokeLength = (int) (canvasWidth * 0.05);
        int borderTop = padding;
        int borderRight = padding + paperWidth;
        int borderBottom = padding + paperHeight;
        int borderLeft = padding;

        // left upper corner
        canvas.drawLine(borderLeft - strokePadding, borderTop, borderLeft - strokeLength, borderTop, paint);
        canvas.drawLine(borderLeft, borderTop - strokePadding, borderLeft, borderTop - strokeLength, paint);

        // right upper corner
        canvas.drawLine(borderRight, borderTop - strokePadding, borderRight, borderTop - strokeLength, paint);
        canvas.drawLine(borderRight + strokePadding, borderTop, borderRight + strokeLength, borderTop, paint);

        // right lower corner
        canvas.drawLine(borderRight + strokePadding, borderBottom, borderRight + strokeLength, borderBottom, paint);
        canvas.drawLine(borderRight, borderBottom + strokePadding, borderRight, borderBottom + strokeLength, paint);

        // left lower corner
        canvas.drawLine(borderLeft - strokePadding, borderBottom, borderLeft - strokeLength, borderBottom, paint);
        canvas.drawLine(borderLeft, borderBottom + strokePadding, borderLeft, borderBottom + strokeLength, paint);
    }

    private void drawBleed(Canvas canvas) {
        if(paper.getBleed() == 0) {
            return;
        }

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));

        canvas.drawRect(bleedRect, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(128);

        canvas.drawRect(bleedRect, paint);

        // Draw Dimensions
        Unit unit = ((PaperApplication) getContext().getApplicationContext()).getApplicationUnit();
        paint.setColor(Color.WHITE);
        paint.setPathEffect(null);

        // width of the paper
        String widthText = Unit.fromMillimeter(
                unit, paper.getWidth() + paper.getBleed() * 2) + " " + unit.getName();

        float x1 = (canvasWidth / 2.0f);
        float y1 = (float) (padding + paperHeight + canvasHeight * 0.04);

        if(landscapeMode) {
            y1 = (float) (padding - canvasHeight * 0.04);
            canvas.save();
            canvas.rotate(180, x1, y1);
            canvas.drawText(widthText, x1, y1, paint);
            canvas.restore();
        } else {
            canvas.drawText(widthText, x1, y1, paint);
        }

        // height of the paper
        String heightText = Unit.fromMillimeter(
                unit, paper.getHeight() + paper.getBleed() * 2) + " " + unit.getName();

        float x2 = (float) (padding - canvasHeight * 0.04);
        float y2 = padding + (paperHeight / 2);
        canvas.save();
        canvas.rotate(90, x2, y2);
        canvas.drawText(heightText, x2, y2, paint);
        canvas.restore();
    }

    private void drawPaper(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);

        // Draw shadow
        paint.setColor(Color.GRAY);
        paint.setAlpha(128);
        canvas.drawRect(shadowRect, paint);

        // Draw paper
        paint.setColor(Color.WHITE);
        paint.setAlpha(255);
        canvas.drawRect(paperRect, paint);

        // Draw Dimensions
        Unit unit = ((PaperApplication) getContext().getApplicationContext()).getApplicationUnit();
        paint.setColor(Color.GRAY);

        String widthText = Unit.fromMillimeter(unit, paper.getWidth()) + " " + unit.getName();

        float x1 = (canvasWidth / 2.0f);
        float y1 = (float) (padding + paperHeight - bleed - canvasHeight * 0.02);

        if(landscapeMode) {
            y1 = (float) (padding + bleed + canvasHeight * 0.02);
            canvas.save();
            canvas.rotate(180, x1, y1);
            canvas.drawText(widthText, x1, y1, paint);
            canvas.restore();
        } else {
            canvas.drawText(widthText, x1, y1, paint);
        }

        String heightText = Unit.fromMillimeter(unit, paper.getHeight()) + " " + unit.getName();
        float x = (float) (padding + bleed + canvasHeight * 0.02);
        float y = padding + (paperHeight / 2);

        canvas.save();
        canvas.rotate(90, x, y);
        canvas.drawText(heightText, x, y, paint);
        canvas.restore();
    }
}
