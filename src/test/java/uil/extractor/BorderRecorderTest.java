package uil.extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link BorderRecorder}.
 */
class BorderRecorderTest {

    private final BorderRecorder recorder = new BorderRecorder(10, 800);

    private void rectangle(double x, double y, double width, double height) {
        recorder.appendRectangle(
                new Point2D.Double(x, y),
                new Point2D.Double(x + width, y),
                new Point2D.Double(x + width, y + height),
                new Point2D.Double(x, y + height));
    }

    @Test
    void convertsAThinFilledRectangleIntoATopDownRule() {
        rectangle(60, 700, 500, 0.5);
        recorder.paint(false, true);
        assertEquals(List.of(new Rule(50, 550, 99.75)), recorder.rules());
    }

    @Test
    void ignoresTallFilledRectangles() {
        rectangle(60, 700, 50, 9);
        recorder.paint(false, true);
        assertTrue(recorder.rules().isEmpty());
    }

    @Test
    void turnsAStrokedRectangleIntoItsTopAndBottomEdges() {
        rectangle(60, 600, 500, 40);
        recorder.paint(true, false);
        assertEquals(List.of(new Rule(50, 550, 200), new Rule(50, 550, 160)), recorder.rules());
    }

    @Test
    void keepsStrokedHorizontalLinesOnly() {
        recorder.moveTo(60, 500);
        recorder.lineTo(560, 500);
        recorder.moveTo(60, 400);
        recorder.lineTo(60, 300);
        recorder.paint(true, false);
        assertEquals(List.of(new Rule(50, 550, 300)), recorder.rules());
    }

    @Test
    void dropsDegenerateSegments() {
        rectangle(60, 700, 1, 0.5);
        recorder.paint(false, true);
        assertTrue(recorder.rules().isEmpty());
    }

    @Test
    void forgetsDiscardedPaths() {
        rectangle(60, 700, 500, 0.5);
        recorder.discard();
        recorder.paint(true, true);
        assertTrue(recorder.rules().isEmpty());
    }

    @Test
    void tracksTheCurrentPoint() {
        recorder.moveTo(5, 6);
        recorder.lineTo(7, 8);
        assertEquals(new Point2D.Double(7, 8), recorder.currentPoint());
        recorder.curveTo(9, 10);
        assertEquals(new Point2D.Double(9, 10), recorder.currentPoint());
    }
}
