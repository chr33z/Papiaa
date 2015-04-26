package papersize.chreez.com.papersize.paper;

/**
 * Created by Christopher Gebhardt on 23.04.15.
 */
public class Paper {

    /**
     * Width in mm
     */
    protected double width;

    /**
     * Height in mm
     */
    protected double height;

    /**
     * Additional size that is added around all edges of the paper in mm (Beschnitt)
     */
    protected double bleed;
    /**
     * Name of the paper, like "Din A 4"
     */
    protected String name = "";

    /**
     * A description of the paper. Can be empty
     */
    protected String description = "";

    public Paper(String name, String description, double width, double height) {
        this.name = name;
        this.description = description;
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
        return width;
    }

    public double getHeight() {
        return height;
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
}
