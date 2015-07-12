package com.elegantwalrus.papersize.renderer;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.elegantwalrus.papersize.R;
import com.elegantwalrus.papersize.paper.Orientation;
import com.elegantwalrus.papersize.paper.Paper;
import com.elegantwalrus.papersize.paper.Unit;

import java.text.DecimalFormat;

/**
 * This renderer draws a paper on a canvas. This class handles all animations, transistions,
 * drawing, etc.
 *
 * Created by Christopher Gebhardt on 10.07.15.
 * PaperSize
 */
public class PaperRenderer {

    public enum Style {
        NORMAL, COMPARED_TO_FRONT, COMPARED_TO_BACK, ORIGINAL_FRONT, ORIGINAL_BACK
    }

    private Context context;

    private Paper paper;

    private Style drawStyle = Style.NORMAL;

    private Paint paintPaper = new Paint();
    private Paint paintPrintMarks = new Paint();
    private Paint paintTextDimensions = new Paint();
    private Paint paintTextName = new Paint();
    private Paint paintShadow = new Paint();
    private Paint paintFavorite = new Paint();
    private Paint paintBleed = new Paint();

    /** Indicates whether the renderer is initialized with the parents dimensions */
    private boolean isInitialized = false;

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

    private float textSizeNumbers;
    private float textSizePaperName;

    private double currentBleed = 0.0;

    private Unit unit = Unit.MILLIMETER;

    private float scaleX = 1.0f;
    private float scaleY = 1.0f;

    private int paperCompareToColor;

    public PaperRenderer(Context context, Paper paper) {
        this.context = context;
        this.paper = paper;

        prepare();
    }

    private void prepare() {
        Typeface fontNormal = Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf");
        Typeface fontBold = Typeface.createFromAsset(context.getAssets(), "OpenSans-Semibold.ttf");

        int favoriteIndicatorColor = context.getResources().getColor(R.color.favorite_indicator);
        paperCompareToColor = context.getResources().getColor(R.color.material_blue_grey_900);

        // Paint settings text
        paintTextDimensions.setTypeface(fontNormal);
        paintTextDimensions.setTextSize(paintPaper.getTextSize() * 4.0f);
        paintTextDimensions.setTextAlign(Paint.Align.CENTER);

        paintTextName.setTypeface(fontNormal);
        paintTextName.setTextSize(paintPaper.getTextSize() * 4.0f);
        paintTextName.setTextAlign(Paint.Align.CENTER);

        // Paint settings colors and style
        paintFavorite.setColor(favoriteIndicatorColor);
        paintTextName.setColor(Color.GRAY);
        paintTextName.setAlpha(75);
        paintTextName.setTypeface(fontBold);
        paintPrintMarks.setColor(Color.WHITE);
        paintPrintMarks.setStyle(Paint.Style.STROKE);
        paintPrintMarks.setStrokeWidth(2);
        paintTextDimensions.setColor(Color.GRAY);
        paintPaper.setColor(Color.WHITE);
        paintShadow.setColor(Color.GRAY);
        paintShadow.setAlpha(128);
        paintBleed.setColor(Color.WHITE);

        // Paint settings flags
        paintPaper.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintPrintMarks.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintShadow.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintTextDimensions.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintTextName.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintFavorite.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    /**
     * Call this when the parent view has measured its dimensions. A good time
     * is in the first draw call.
     *
     * @param width
     * @param height
     */
    public void initialize(int width, int height) {
        initialize(width, height, 1.0f, 1.0f);
    }

    public void initialize(int width, int height, float scaleX, float scaleY) {
        canvasWidth = width;
        canvasHeight = height;

        this.scaleX = scaleX;
        this.scaleY = scaleY;

        textSizeNumbers = canvasWidth * 0.045f;
        textSizePaperName = canvasWidth * 0.15f;

        paddingPortrait = (int) (((canvasWidth - (canvasWidth * 0.2 * 2)) * (1 - scaleX) + (canvasWidth * 0.2 * 2)) * 0.5);
        paddingLandscape = (int) (int) (((canvasWidth - (canvasWidth * 0.085 * 2)) * (1 - scaleX) + (canvasWidth * 0.085 * 2)) * 0.5);

        favoriteIndicatorRadius = (float) (canvasHeight * 0.01);
        facoriteIndicatorPadding = (float) (canvasWidth * 0.035);

        shadow = (int) (canvasWidth * 0.025 * scaleX);

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

        isInitialized = true;
    }

    /**
     * @return whether this renderer is initialized with the parents dimensions
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    public void setDrawStyle(Style style) {
        this.drawStyle = style;
    }

    /**
     * Set the paper that is used to render on canvas
     * @param paper
     */
    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    public Paper getPaper() {
        return paper;
    }

    /**
     * Draw the paper on the canvas
     * @param canvas
     */
    public void draw(Canvas canvas) {
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

        switch (drawStyle) {
            case NORMAL:
                drawPaper(canvas);
                drawPrintMarkers(canvas);
                drawBleed(canvas);
                drawPaperName(canvas);
                drawFavoriteIndicator(canvas);
                break;
            case COMPARED_TO_BACK:
                drawPaperCompareToBack(canvas);
                break;
            case COMPARED_TO_FRONT:
                canvas.save();
                // translate to lower right corner
                canvas.translate(
                        -((paperWidth / scaleX) - paperWidth) * 0.5f,
                        -((paperHeight / scaleY) - paperHeight) * 0.5f);

                drawPaperCompareToFront(canvas);

                canvas.restore();
                break;
            case ORIGINAL_FRONT:
                canvas.save();
                // translate to lower right corner
                canvas.translate(
                        ((paperWidth / scaleX) - paperWidth) * 0.5f,
                        ((paperHeight / scaleY) - paperHeight) * 0.5f);

                drawPaperOriginalFront(canvas);

                canvas.restore();
                break;
            case ORIGINAL_BACK:
                drawPaperOriginalFront(canvas);
                break;
        }
    }

    private void drawFavoriteIndicator(Canvas canvas) {
        if(paper.isFavorite()) {
            canvas.drawCircle(
                    bleedRect.right - facoriteIndicatorPadding,
                    bleedRect.top + facoriteIndicatorPadding,
                    favoriteIndicatorRadius,
                    paintFavorite);
        }
    }

    private void drawPaperName(Canvas canvas) {
        // Draw paper name
        paintTextName.setTextSize(textSizePaperName);

        float x3 = canvasWidth / 2;
        float y3 = canvasHeight / 2 - ((paintTextName.descent() + paintTextName.ascent()) / 2);
        canvas.drawText(paper.getName(), x3, y3, paintTextName);
    }

    private void drawPrintMarkers(Canvas canvas) {
        int strokePadding = (int) (canvasWidth * 0.01);
        int strokeLength = (int) (canvasWidth * 0.05);
        int borderTop = paddingTop;
        int borderRight = padding + paperWidth;
        int borderBottom = paddingTop + paperHeight;
        int borderLeft = padding;

        // left upper corner
        canvas.drawLine(borderLeft - strokePadding, borderTop, borderLeft - strokeLength, borderTop, paintPrintMarks);
        canvas.drawLine(borderLeft, borderTop - strokePadding, borderLeft, borderTop - strokeLength, paintPrintMarks);

        // right upper corner
        canvas.drawLine(borderRight, borderTop - strokePadding, borderRight, borderTop - strokeLength, paintPrintMarks);
        canvas.drawLine(borderRight + strokePadding, borderTop, borderRight + strokeLength, borderTop, paintPrintMarks);

        // right lower corner
        canvas.drawLine(borderRight + strokePadding, borderBottom, borderRight + strokeLength, borderBottom, paintPrintMarks);
        canvas.drawLine(borderRight, borderBottom + strokePadding, borderRight, borderBottom + strokeLength, paintPrintMarks);

        // left lower corner
        canvas.drawLine(borderLeft - strokePadding, borderBottom, borderLeft - strokeLength, borderBottom, paintPrintMarks);
        canvas.drawLine(borderLeft, borderBottom + strokePadding, borderLeft, borderBottom + strokeLength, paintPrintMarks);
    }

    private void drawBleed(Canvas canvas) {
        DecimalFormat df = unit == Unit.INCH ? new DecimalFormat("0.00") : new DecimalFormat("0.0");

        paintBleed.setAlpha((int) (255 * animationFraction));
        paintBleed.setStyle(Paint.Style.STROKE);
        paintBleed.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
        canvas.drawRect(bleedRect, paintBleed);

        paintBleed.setStyle(Paint.Style.FILL);
        paintBleed.setAlpha((int) (128 * animationFraction));
        canvas.drawRect(bleedRect, paintBleed);

        // Draw Dimensions
        paintBleed.setTextSize(textSizeNumbers);
        paintBleed.setAlpha((int) (255 * animationFraction));
        paintBleed.setPathEffect(null);

        // width of the paper - round to 5 cents first
        double widthRounded = Math.round(Unit.fromMillimeter(
                unit, paper.getWidth() + paper.getBleed() * 2) * 20) / 20.0;

        String widthText = df.format(widthRounded) + " " + unit.getName();

        float x1 = (canvasWidth / 2.0f);
        float y1 = (float) (paddingTop + paperHeight + canvasHeight * 0.04 * animationFraction);
        canvas.drawText(widthText, x1, y1, paintBleed);

        // height of the paper
        double heightRounded = Math.round(Unit.fromMillimeter(
                unit, paper.getHeight() + paper.getBleed() * 2) * 20) / 20.0;
        String heightText = df.format(heightRounded) + " " + unit.getName();

        float x2 = (float) (padding - canvasHeight * 0.04 * animationFraction);
        float y2 = paddingTop + (paperHeight / 2);
        canvas.save();
        canvas.rotate(90, x2, y2);
        canvas.drawText(heightText, x2, y2, paintBleed);
        canvas.restore();
    }

    private void drawPaper(Canvas canvas) {
        DecimalFormat df = new DecimalFormat("#.#");

        // Draw shadow
        canvas.drawRect(shadowRect, paintShadow);

        // Draw paper
        canvas.drawRect(paperRect, paintPaper);

        // Draw Dimensions
        paintTextDimensions.setTextSize(textSizeNumbers);

        String widthText = df.format(
                Unit.fromMillimeter(unit, paper.getWidth())) + " " + unit.getName();
        float x1 = (canvasWidth / 2.0f);
        float y1 = (float) (paddingTop + paperHeight - bleed - canvasHeight * 0.02);

        canvas.drawText(widthText, x1, y1, paintTextDimensions);

        String heightText = df.format(
                Unit.fromMillimeter(unit, paper.getHeight())) + " " + unit.getName();
        float x2 = (float) (padding + bleed + canvasHeight * 0.02);
        float y2 = paddingTop + (paperHeight / 2);

        canvas.save();
        canvas.rotate(90, x2, y2);
        canvas.drawText(heightText, x2, y2, paintTextDimensions);
        canvas.restore();
    }

    private void drawPaperCompareToBack(Canvas canvas) {
        DecimalFormat df = new DecimalFormat("#.#");

        // Draw paper outline
        paintPaper.setStyle(Paint.Style.STROKE);
        paintPaper.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
        paintPaper.setStrokeWidth(2.0f);
        paintPaper.setAlpha(255);
        canvas.drawRect(paperRect, paintPaper);

        // Draw paper fill
        paintPaper.setStyle(Paint.Style.FILL);
        paintPaper.setAlpha(50);
        canvas.drawRect(paperRect, paintPaper);

        // Draw paper name (upper left corner)
        paintTextName.setTextSize(textSizePaperName * 0.5f);
        paintTextName.setTextAlign(Paint.Align.LEFT);
        paintTextName.setColor(Color.WHITE);
        paintTextName.setAlpha(50);
        float x3 = padding;
        float y3 = paddingTop - canvasHeight * 0.01f;
        canvas.drawText(paper.getName(), x3, y3, paintTextName);


        paintTextDimensions.setTextSize(textSizeNumbers);
        paintTextDimensions.setColor(paintTextName.getColor());
        paintTextDimensions.setTextAlign(Paint.Align.LEFT);
        String dimensionText =
                df.format(Unit.fromMillimeter(unit, paper.getWidth())) + " x " +
                df.format(Unit.fromMillimeter(unit, paper.getHeight())) + " " +
                unit.getName();

        float x1 = padding + paintTextName.measureText(paper.getName()) + canvasWidth * 0.01f;
        float y1 = paddingTop - canvasHeight * 0.01f;

        canvas.drawText(dimensionText, x1, y1, paintTextDimensions);
    }

    private void drawPaperCompareToFront(Canvas canvas) {
        DecimalFormat df = new DecimalFormat("#.#");

        // Clear background
//        paintPaper.setColor(paperCompareToColor);
//        paintPaper.setStyle(Paint.Style.FILL);
//        canvas.drawRect(paperRect, paintPaper);

        // draw rectangle
        paintPaper.setStyle(Paint.Style.FILL);
        paintPaper.setColor(paperCompareToColor);
        paintPaper.setAlpha(50);
        canvas.drawRect(paperRect, paintPaper);

        // Draw paper outline
        paintPaper.setColor(paperCompareToColor);
        paintPaper.setStyle(Paint.Style.STROKE);
        paintPaper.setAlpha(255);
        paintPaper.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
        paintPaper.setStrokeWidth(2.0f);
        canvas.drawRect(paperRect, paintPaper);

        // Draw paper name (upper left corner)
        paintTextName.setTextSize(textSizePaperName * 0.5f);
        paintTextName.setTextAlign(Paint.Align.LEFT);
        paintTextName.setColor(Color.WHITE);
        paintTextName.setAlpha(50);
        float x3 = padding;
        float y3 = paddingTop - canvasHeight * 0.01f;
        canvas.drawText(paper.getName(), x3, y3, paintTextName);

        paintTextDimensions.setTextSize(textSizeNumbers);
        paintTextDimensions.setColor(paintTextName.getColor());
        paintTextDimensions.setTextAlign(Paint.Align.LEFT);
        String dimensionText =
                df.format(Unit.fromMillimeter(unit, paper.getWidth())) + " x " +
                        df.format(Unit.fromMillimeter(unit, paper.getHeight())) + " " +
                        unit.getName();

        float x1 = padding + paintTextName.measureText(paper.getName()) + canvasWidth * 0.01f;

        canvas.drawText(dimensionText, x1, y3, paintTextDimensions);
    }

    private void drawPaperOriginalFront(Canvas canvas) {
        DecimalFormat df = new DecimalFormat("#.#");

        // Draw paper fill
        canvas.drawRect(paperRect, paintPaper);

        // Draw paper name (lower right corner)
        paintTextName.setTextSize(textSizePaperName * 0.5f);
        paintTextName.setTextAlign(Paint.Align.RIGHT);
        paintTextName.setColor(Color.WHITE);

        Rect textBounds = new Rect();
        paintTextName.getTextBounds(paper.getName(), 0, 1, textBounds);

        float x3 = padding + paperWidth;
        float y3 = paddingTop + paperHeight + canvasHeight * 0.01f + textBounds.height();
        canvas.drawText(paper.getName(), x3, y3, paintTextName);

        paintTextDimensions.setTextSize(textSizeNumbers);
        paintTextDimensions.setColor(paintTextName.getColor());
        paintTextDimensions.setTextAlign(Paint.Align.RIGHT);
        String dimensionText =
                df.format(Unit.fromMillimeter(unit, paper.getWidth())) + " x " +
                        df.format(Unit.fromMillimeter(unit, paper.getHeight())) + " " +
                        unit.getName();

        float x1 = padding + paperWidth - (paintTextName.measureText(paper.getName()) + canvasWidth * 0.01f);
        float y1 = paddingTop + paperHeight + canvasHeight * 0.01f + textBounds.height();

        canvas.drawText(dimensionText, x1, y1, paintTextDimensions);
    }

    public void togglePaperOrientation(View view) {
        Orientation orientation =
                paper.getOrientation() == Orientation.PORTRAIT ? Orientation.LANDSCAPE : Orientation.PORTRAIT;

        double w1 = paper.getWidth();
        double h1 = paper.getHeight();
        paper.setOrientation(orientation);
        double w2 = paper.getWidth();
        double h2 = paper.getHeight();

        double p1 = paper.getOrientation() == Orientation.PORTRAIT ? paddingLandscape : paddingPortrait;
        double p2 = paper.getOrientation() == Orientation.PORTRAIT ? paddingPortrait : paddingLandscape;

        animateOrientationTransition(w1, w2, h1, h2, p1, p2, view);
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
            final double p1, final double p2,
            final View view) {

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
                view.postInvalidate();
            }
        });
        orientationAnimation.setInterpolator(new OvershootInterpolator());
        orientationAnimation.setDuration(500);
        orientationAnimation.start();
    }

    public void setBleeding(Unit unit, double bleeding, View view) {
        paper.setBleed(unit, bleeding);

        if (currentBleed == 0 && currentBleed < paper.getBleed()) {
            animateBleedingTransition(false, view);
        } else if (paper.getBleed() == 0 && currentBleed > 0) {
            animateBleedingTransition(true, view);
        } else {
            view.invalidate();
        }
        currentBleed = paper.getBleed();
    }

    private void animateBleedingTransition(boolean reverse, final View view) {
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
                view.invalidate();
            }
        });
        bleedingAnimation.setInterpolator(new DecelerateInterpolator());
        bleedingAnimation.setDuration(1000);
        bleedingAnimation.start();
    }
}
