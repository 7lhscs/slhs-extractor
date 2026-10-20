package uil.extractor;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Tracks the path being drawn on a page and turns painted paths into horizontal borders.
 *
 * <p>Input coordinates use the PDF convention (origin at the bottom left). Recorded rules use
 * top-down coordinates relative to the page origin supplied at construction.</p>
 */
final class BorderRecorder {

    private static final double MAX_THICKNESS = 2.0;
    private static final double MIN_LENGTH = 5.0;
    private static final double LEVEL_TOLERANCE = 0.5;

    private final double originX;
    private final double topY;
    private final List<Rule> rules = new ArrayList<>();
    private final List<Rectangle2D> rectangles = new ArrayList<>();
    private final List<Line2D> lines = new ArrayList<>();
    private Point2D currentPoint = new Point2D.Double();

    /**
     * Creates a recorder.
     *
     * @param originX x coordinate of the left edge of the page, in PDF user space
     * @param topY    y coordinate of the top edge of the page, in PDF user space
     */
    BorderRecorder(double originX, double topY) {
        this.originX = originX;
        this.topY = topY;
    }

    /**
     * Adds a rectangle to the current path.
     *
     * @param corners the four corners of the rectangle
     */
    void appendRectangle(Point2D... corners) {
        Rectangle2D bounds = new Rectangle2D.Double(corners[0].getX(), corners[0].getY(), 0, 0);
        for (Point2D corner : corners) {
            bounds.add(corner);
        }
        rectangles.add(bounds);
    }

    /**
     * Starts a new subpath.
     *
     * @param x x coordinate in user space
     * @param y y coordinate in user space
     */
    void moveTo(double x, double y) {
        currentPoint = new Point2D.Double(x, y);
    }

    /**
     * Adds a straight segment from the current point.
     *
     * @param x x coordinate of the end point in user space
     * @param y y coordinate of the end point in user space
     */
    void lineTo(double x, double y) {
        Point2D end = new Point2D.Double(x, y);
        lines.add(new Line2D.Double(currentPoint, end));
        currentPoint = end;
    }

    /**
     * Moves the current point to the end of a curve, which can never be a border.
     *
     * @param x x coordinate of the end point in user space
     * @param y y coordinate of the end point in user space
     */
    void curveTo(double x, double y) {
        currentPoint = new Point2D.Double(x, y);
    }

    /**
     * Returns the current point of the path.
     *
     * @return the current point in user space
     */
    Point2D currentPoint() {
        return currentPoint;
    }

    /**
     * Paints the current path, keeping any horizontal borders it contains, then clears it.
     *
     * @param stroked whether the path outline is drawn
     * @param filled  whether the path interior is filled
     */
    void paint(boolean stroked, boolean filled) {
        for (Rectangle2D rectangle : rectangles) {
            if (filled && rectangle.getHeight() <= MAX_THICKNESS) {
                addRule(rectangle.getMinX(), rectangle.getMaxX(), rectangle.getCenterY());
            }
            if (stroked) {
                addRule(rectangle.getMinX(), rectangle.getMaxX(), rectangle.getMinY());
                addRule(rectangle.getMinX(), rectangle.getMaxX(), rectangle.getMaxY());
            }
        }
        if (stroked) {
            lines.stream()
                    .filter(line -> Math.abs(line.getY1() - line.getY2()) <= LEVEL_TOLERANCE)
                    .forEach(line -> addRule(
                            Math.min(line.getX1(), line.getX2()),
                            Math.max(line.getX1(), line.getX2()),
                            (line.getY1() + line.getY2()) / 2));
        }
        discard();
    }

    /**
     * Clears the current path without painting it.
     */
    void discard() {
        rectangles.clear();
        lines.clear();
    }

    /**
     * Returns the borders recorded so far.
     *
     * @return an immutable copy of the recorded rules
     */
    List<Rule> rules() {
        return List.copyOf(rules);
    }

    private void addRule(double left, double right, double y) {
        if (right - left >= MIN_LENGTH) {
            rules.add(new Rule(left - originX, right - originX, topY - y));
        }
    }
}
