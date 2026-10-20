package uil.extractor;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;

/**
 * Collects the horizontal borders drawn on a page.
 */
public final class RuleCollector extends PDFGraphicsStreamEngine {

    private final BorderRecorder recorder;

    private RuleCollector(PDPage page) {
        super(page);
        PDRectangle cropBox = page.getCropBox();
        this.recorder = new BorderRecorder(cropBox.getLowerLeftX(), cropBox.getUpperRightY());
    }

    /**
     * Scans one page for horizontal borders.
     *
     * @param page the page to scan
     * @return the segments found, in top-down page coordinates
     * @throws IOException if the page content cannot be read
     */
    public static List<Rule> collect(PDPage page) throws IOException {
        RuleCollector collector = new RuleCollector(page);
        collector.processPage(page);
        return collector.recorder.rules();
    }

    /**
     * Adds a rectangle to the current path.
     *
     * @param p0 first corner
     * @param p1 second corner
     * @param p2 third corner
     * @param p3 fourth corner
     */
    @Override
    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
        recorder.appendRectangle(p0, p1, p2, p3);
    }

    /**
     * Starts a new subpath.
     *
     * @param x x coordinate in user space
     * @param y y coordinate in user space
     */
    @Override
    public void moveTo(float x, float y) {
        recorder.moveTo(x, y);
    }

    /**
     * Adds a straight segment to the current path.
     *
     * @param x x coordinate of the end point in user space
     * @param y y coordinate of the end point in user space
     */
    @Override
    public void lineTo(float x, float y) {
        recorder.lineTo(x, y);
    }

    /**
     * Advances the current point past a curve.
     *
     * @param x1 first control point x
     * @param y1 first control point y
     * @param x2 second control point x
     * @param y2 second control point y
     * @param x3 end point x
     * @param y3 end point y
     */
    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) {
        recorder.curveTo(x3, y3);
    }

    /**
     * Returns the current point of the path under construction.
     *
     * @return the current point
     */
    @Override
    public Point2D getCurrentPoint() {
        return recorder.currentPoint();
    }

    /**
     * Ignored: closing a subpath does not change which segments exist.
     */
    @Override
    public void closePath() {
    }

    /**
     * Discards the path without painting it.
     */
    @Override
    public void endPath() {
        recorder.discard();
    }

    /**
     * Paints the path as an outline.
     */
    @Override
    public void strokePath() {
        recorder.paint(true, false);
    }

    /**
     * Paints the path as a fill.
     *
     * @param windingRule the winding rule, unused
     */
    @Override
    public void fillPath(int windingRule) {
        recorder.paint(false, true);
    }

    /**
     * Paints the path as a fill with an outline.
     *
     * @param windingRule the winding rule, unused
     */
    @Override
    public void fillAndStrokePath(int windingRule) {
        recorder.paint(true, true);
    }

    /**
     * Ignored: clipping does not draw borders.
     *
     * @param windingRule the winding rule, unused
     */
    @Override
    public void clip(int windingRule) {
    }

    /**
     * Ignored: images are not borders.
     *
     * @param pdImage the image, unused
     */
    @Override
    public void drawImage(PDImage pdImage) {
    }

    /**
     * Ignored: shading fills are not borders.
     *
     * @param shadingName the shading resource name, unused
     */
    @Override
    public void shadingFill(COSName shadingName) {
    }
}
