package com.elegantwalrus.papersize;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.elegantwalrus.papersize.paper.Orientation;
import com.elegantwalrus.papersize.paper.Paper;
import com.elegantwalrus.papersize.paper.Unit;

import java.text.DecimalFormat;

/**
 * Created by Christopher Gebhardt on 24.04.15.
 */
public class PaperCanvas extends View {

    private Paper paper;

    private int canvasWidth;
    private int canvasHeight;

    private int padding;
    private int paddingTop;
    private int paddingPortrait;
    private int paddingLandscape;

    private int shadow;
    private int paperWidth;
    private int paperHeight;

    private float sizeFactor;
    private int bleed;

    private float favoriteIndicatorRadius;
    private float facoriteIndicatorPadding;

    private float animationFraction = 0.0f;

    private Rect paperRect = new Rect();
    private Rect shadowRect = new Rect();
    private Rect bleedRect = new Rect();

    private Paint paint = new Paint();

    private float textSizeNumbers;
    private float textSizePaperName;

    private double currentBleed = 0.0;

    private Typeface fontNormal;
    private Typeface fontBold;

    private Unit unit = Unit.MILLIMETER;

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
        fontNormal = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Light.ttf");
        fontBold = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Semibold.ttf");
        paint.setTypeface(fontNormal);

        paint.setTextSize(paint.getTextSize() * 4.0f);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                canvasWidth = getWidth();
                canvasHeight = getHeight();

                textSizeNumbers = canvasWidth * 0.045f;
                textSizePaperName = canvasWidth * 0.15f;

                paddingPortrait = (int) (canvasWidth * 0.2);
                paddingLandscape = (int) (canvasWidth * 0.085);

                favoriteIndicatorRadius = (float) (canvasHeight * 0.01);
                facoriteIndicatorPadding = (float) (canvasWidth * 0.035);

                shadow = (int) (canvasWidth * 0.025);

                if (paper != null) {
                    if (paper.getOrientation() == Orientation.PORTRAIT) {
                        paddingTop = paddingPortrait;
                        padding = paddingPortrait;
                    } else {
                        paddingTop = paddingPortrait;
                        padding = paddingLandscape;
                    }

                    paperWidth = canvasWidth - padding - padding;
                    paperHeight = (int) (paperWidth * (paper.getHeight() / paper.getWidth()));

                    sizeFactor = (float) (paperWidth / paper.getWidth());
                    bleed = (int) (sizeFactor * paper.getBleed());
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    public void setPaper(Paper paper) {
        unit = ((PaperApplication) getContext().getApplicationContext()).getApplicationUnit();

        if (currentBleed == 0 && currentBleed < paper.getBleed()) {
            animateBleedingTransition(false);
        } else if (paper.getBleed() == 0 && currentBleed > 0) {
            animateBleedingTransition(true);
        }
        currentBleed = paper.getBleed();
        this.paper = paper;
        invalidate();
    }

    public void setBleeding(Unit unit, double bleeding) {
        paper.setBleed(unit, bleeding);

        if (currentBleed == 0 && currentBleed < paper.getBleed()) {
            animateBleedingTransition(false);
        } else if (paper.getBleed() == 0 && currentBleed > 0) {
            animateBleedingTransition(true);
        }
        currentBleed = paper.getBleed();

        invalidate();
    }

    public void togglePaperOrientation() {
        Orientation orientation =
                paper.getOrientation() == Orientation.PORTRAIT ? Orientation.LANDSCAPE : Orientation.PORTRAIT;
        setOrientation(orientation);
    }

    public void setOrientation(Orientation orientation) {
        double w1 = paper.getWidth();
        double h1 = paper.getHeight();
        paper.setOrientation(orientation);
        double w2 = paper.getWidth();
        double h2 = paper.getHeight();

        double p1 = paper.getOrientation() == Orientation.PORTRAIT ? paddingLandscape : paddingPortrait;
        double p2 = paper.getOrientation() == Orientation.PORTRAIT ? paddingPortrait : paddingLandscape;

        animateOrientationTransition(w1, w2, h1, h2, p1, p2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(paper == null) {
            return;
        }
        paddingTop = (canvasHeight - paperHeight) / 2;
        bleed = (int) (sizeFactor * paper.getBleed());

        paperRect.left = padding + bleed;
        paperRect.right = padding + paperWidth - bleed;
        paperRect.top = paddingTop + bleed;
        paperRect.bottom = paddingTop + paperHeight - bleed;

        shadowRect.left = paperRect.left + shadow;
        shadowRect.right = paperRect.right + shadow;
        shadowRect.top = paperRect.top + shadow;
        shadowRect.bottom = paperRect.bottom + shadow;

        bleedRect.top = paddingTop;
        bleedRect.right = padding + paperWidth;
        bleedRect.bottom = paddingTop + paperHeight;
        bleedRect.left = padding;

        drawPaper(canvas);
        drawPrintMarkers(canvas);
        drawBleed(canvas);
        drawPaperName(canvas);
        drawFavoriteIndicator(canvas);

        super.onDraw(canvas);
    }

    private void drawFavoriteIndicator(Canvas canvas) {
        if(paper.isFavorite()) {
            int color = paint.getColor();
            paint.setColor(getResources().getColor(R.color.favorite_indicator));

            canvas.drawCircle(
                    bleedRect.right - facoriteIndicatorPadding,
                    bleedRect.top + facoriteIndicatorPadding,
                    favoriteIndicatorRadius,
                    paint);

            paint.setColor(color);
        }
    }

    private void drawPaperName(Canvas canvas) {
        // Draw paper name
        float paintSize = paint.getTextSize();
        paint.setTextSize(textSizePaperName);
        paint.setTypeface(fontBold);
        paint.setColor(Color.GRAY);
        paint.setAlpha(75);

        float x3 = canvasWidth / 2;
        float y3 = canvasHeight / 2 - ((paint.descent() + paint.ascent()) / 2);
        canvas.drawText(paper.getName(), x3, y3, paint);

        // restore paint
        paint.setTextSize(paintSize);
        paint.setTypeface(fontNormal);
        paint.setAlpha(255);
    }

    private void drawPrintMarkers(Canvas canvas) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);

        int strokePadding = (int) (canvasWidth * 0.01);
        int strokeLength = (int) (canvasWidth * 0.05);
        int borderTop = paddingTop;
        int borderRight = padding + paperWidth;
        int borderBottom = paddingTop + paperHeight;
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
        DecimalFormat df = unit == Unit.INCH ? new DecimalFormat("0.00") : new DecimalFormat("0.0");

        paint.setColor(Color.WHITE);
        paint.setAlpha((int) (255 * animationFraction));
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
        canvas.drawRect(bleedRect, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha((int) (128  * animationFraction));
        canvas.drawRect(bleedRect, paint);

        // Draw Dimensions
        paint.setColor(Color.WHITE);
        paint.setTextSize(textSizeNumbers);
        paint.setAlpha((int) (255 * animationFraction));
        paint.setPathEffect(null);

        // width of the paper - round to 5 cents first
        double widthRounded = Math.round(Unit.fromMillimeter(
                unit, paper.getWidth() + paper.getBleed() * 2) * 20) / 20.0;

        String widthText = df.format(widthRounded) + " " + unit.getName();

        float x1 = (canvasWidth / 2.0f);
        float y1 = (float) (paddingTop + paperHeight + canvasHeight * 0.04 * animationFraction);
        canvas.drawText(widthText, x1, y1, paint);

        // height of the paper
        double heightRounded = Math.round(Unit.fromMillimeter(
                unit, paper.getHeight() + paper.getBleed() * 2) * 20) / 20.0;
        String heightText = df.format(heightRounded) + " " + unit.getName();

        float x2 = (float) (padding - canvasHeight * 0.04 * animationFraction);
        float y2 = paddingTop + (paperHeight / 2);
        canvas.save();
        canvas.rotate(90, x2, y2);
        canvas.drawText(heightText, x2, y2, paint);
        canvas.restore();
    }

    private void drawPaper(Canvas canvas) {
        DecimalFormat df = new DecimalFormat("#.#");

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
        paint.setColor(Color.GRAY);
        paint.setTextSize(textSizeNumbers);

        String widthText = df.format(
                Unit.fromMillimeter(unit, paper.getWidth())) + " " + unit.getName();
        float x1 = (canvasWidth / 2.0f);
        float y1 = (float) (paddingTop + paperHeight - bleed - canvasHeight * 0.02);

        canvas.drawText(widthText, x1, y1, paint);

        String heightText = df.format(
                Unit.fromMillimeter(unit, paper.getHeight())) + " " + unit.getName();
        float x2 = (float) (padding + bleed + canvasHeight * 0.02);
        float y2 = paddingTop + (paperHeight / 2);

        canvas.save();
        canvas.rotate(90, x2, y2);
        canvas.drawText(heightText, x2, y2, paint);
        canvas.restore();
    }

    /**
     * Adnimate paper properties from one orientation to another
     * @param w1 width before
     * @param w2 width after
     * @param h1 height before
     * @param h2 height after
     * @param p1 padding before
     * @param p2 padding after
     */
    private void animateOrientationTransition(
            final double w1, final double w2,
            final double h1, final double h2,
            final double p1, final double p2) {

        ValueAnimator orientationAnimation = ValueAnimator.ofFloat(0.0f, 1.0f);
        orientationAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();

                double height = h1 + (h2 - h1) * f;
                double width = w1 + (w2 - w1) * f;
                padding = (int) (p1 + (p2 - p1) * f);

                paperWidth = canvasWidth - padding - padding;
                paperHeight = (int) (paperWidth * (height / width));
                postInvalidate();
            }
        });
        orientationAnimation.setInterpolator(new OvershootInterpolator());
        orientationAnimation.setDuration(500);
        orientationAnimation.start();
    }

    private void animateBleedingTransition(boolean reverse) {
        ValueAnimator bleedingAnimation;
        if(!reverse) {
            bleedingAnimation = ValueAnimator.ofFloat(0.0f, 1.0f);
        } else {
            bleedingAnimation = ValueAnimator.ofFloat(1.0f, 0.0f);
        }

        bleedingAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animationFraction = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        bleedingAnimation.setInterpolator(new DecelerateInterpolator());
        bleedingAnimation.setDuration(1000);
        bleedingAnimation.start();
    }
}
