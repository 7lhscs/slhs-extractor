package uil.extractor;

import java.util.List;

/**
 * The rectangular area of a page that holds one question or one group of questions that share
 * a single table row.
 *
 * @param questionNumbers numbers of the questions in this region, ordered top to bottom
 * @param pageIndex       zero-based index of the page containing the region
 * @param left            left edge in top-down page coordinates, in points
 * @param top             top edge in top-down page coordinates, in points
 * @param right           right edge in top-down page coordinates, in points
 * @param bottom          bottom edge in top-down page coordinates, in points
 */
public record QuestionRegion(
        List<Integer> questionNumbers, int pageIndex, double left, double top, double right, double bottom) {

    /**
     * Makes the question list immutable and rejects empty regions.
     *
     * @throws IllegalArgumentException if no question number is supplied
     */
    public QuestionRegion {
        if (questionNumbers.isEmpty()) {
            throw new IllegalArgumentException("a region needs at least one question");
        }
        questionNumbers = List.copyOf(questionNumbers);
    }

    /**
     * Returns the lowest question number in the region.
     *
     * @return the first question number
     */
    public int firstNumber() {
        return questionNumbers.get(0);
    }

    /**
     * Returns the highest question number in the region.
     *
     * @return the last question number
     */
    public int lastNumber() {
        return questionNumbers.get(questionNumbers.size() - 1);
    }

    /**
     * Returns the horizontal size of the region.
     *
     * @return the width in points
     */
    public double width() {
        return right - left;
    }

    /**
     * Returns the vertical size of the region.
     *
     * @return the height in points
     */
    public double height() {
        return bottom - top;
    }
}
