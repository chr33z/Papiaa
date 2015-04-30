package papersize.chreez.com.papersize.paper;

/**
 * Created by Christopher Gebhardt on 23.04.15.
 */
public class Paper {

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
     * A description of the paper. Can be empty
     */
    private String description = "";

    /**
     * Orientation of the paper. Each paper is originally oriented portrait.
     */
    private Orientation orientation = Orientation.PORTRAIT;

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

    public void toggleOrientation() {
        orientation = orientation == Orientation.PORTRAIT ? Orientation.LANDSCAPE : Orientation.PORTRAIT;
    }
}
