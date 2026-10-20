package uil.extractor;

/**
 * A horizontal border segment of a table, expressed in top-down page coordinates.
 *
 * @param left  x coordinate of the left end, in points
 * @param right x coordinate of the right end, in points
 * @param y     distance from the top of the page, in points
 */
public record Rule(double left, double right, double y) {

    /**
     * Returns the horizontal extent of this segment.
     *
     * @return the width in points
     */
    public double width() {
        return right - left;
    }
}
