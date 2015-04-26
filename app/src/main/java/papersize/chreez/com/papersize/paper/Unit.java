package papersize.chreez.com.papersize.paper;

/**
 * All supported dimensions
 *
 * Created by Christopher Gebhardt on 23.04.15.
 */
public enum Unit {
    MILLIMETER("mm"),
    CENTIMETER("cm"),
    DEZIMETER("dm"),
    METER("m"),
    INCH("in"),
    FEET("ft");

    private String name;

    Unit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Convert any dimension of a certain unit to millimeter
     *
     * @param unit
     * @param dimension
     * @return the dimension in millimeter
     */
    public static double toMillimeter(Unit unit, double dimension) {
        switch (unit) {
            case MILLIMETER:
                return dimension;
            case CENTIMETER:
                return dimension * 10;
            case DEZIMETER:
                return dimension * 100;
            case METER:
                return dimension * 1000;
            case INCH:
                return dimension * 25.4;
            case FEET:
                return dimension * 304.8;
            default:
                return dimension;
        }
    }

    /**
     * Convert any dimension from millimeter to the specified unit
     *
     * @param unit
     * @param dimension
     *
     * @return the dimension converted from millimeter to the specified unit
     */
    public static double fromMillimeter(Unit unit, double dimension) {
        switch (unit) {
            case MILLIMETER:
                return dimension;
            case CENTIMETER:
                return dimension / 10;
            case DEZIMETER:
                return dimension / 100;
            case METER:
                return dimension / 1000;
            case INCH:
                return dimension / 25.4;
            case FEET:
                return dimension / 304.8;
            default:
                return dimension;
        }
    }
}
