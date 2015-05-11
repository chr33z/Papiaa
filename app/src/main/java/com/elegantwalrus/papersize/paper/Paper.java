package com.elegantwalrus.papersize.paper;

/**
 * Created by Christopher Gebhardt on 23.04.15.
 */
public class Paper {

    private static final int MAX_BLEEDING = 30;

    /**
     * Width in mm
     */
    private double width;

    /**
     * Height in mm
     */
    private double height;

    /**
     * Additional size that is added around all edges of the paper in mm (Beschnitt)
     */
    private double bleed;
    /**
     * Name of the paper, like "Din A 4"
     */
    private String name = "";

    /**
     * A unique identifier for this format
     */
    private String id = "";

    /**
     * A description of the paper. Can be empty
     */
    private String description = "";

    private boolean favorite = true;

    /**
     * Orientation of the paper. Each paper is originally oriented portrait.
     */
    private Orientation orientation = Orientation.PORTRAIT;

    public Paper(String name, String description, String id, double width, double height) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.width = width;
        this.height = height;
    }

    /**
     * Set the bleed of the paper
     *
     * @param unit unit of the bleed
     * @param bleed
     */
    public void setBleed(Unit unit, double bleed) {
        this.bleed = Unit.toMillimeter(unit, bleed);
    }

    public double getWidth() {
        return orientation == Orientation.PORTRAIT ? width : height;
    }

    public double getHeight() {
        return orientation == Orientation.PORTRAIT ? height : width;
    }

    public double getBleed() {
        return bleed;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public String getId() {
        return id;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void toggleOrientation() {
        orientation = orientation == Orientation.PORTRAIT ? Orientation.LANDSCAPE : Orientation.PORTRAIT;
    }

    /**
     * Get the maximum bleed for this paper. This is determined by its smallest size and
     * follows a quadratic equation
     * @return
     */
    public int getMaxBleed() {
        final double A = -0.0000167;
        final double B = 0.0442;
        final double C = 2.86;

        return  Math.min((int)(Math.ceil(A * width * width + B * width + C)), MAX_BLEEDING);
    }
}
