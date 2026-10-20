package uil.extractor;

/**
 * The printed "QUESTION n" label that starts a question row.
 *
 * @param number the question number printed in the label
 * @param x      left edge of the label, in points
 * @param y      baseline of the label measured from the top of the page, in points
 */
public record Marker(int number, double x, double y) {
}
