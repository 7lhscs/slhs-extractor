package uil.extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link RegionLocator}.
 */
class RegionLocatorTest {

    private static final List<Rule> BORDERS =
            List.of(new Rule(50, 580, 50), new Rule(50, 580, 100), new Rule(50, 580, 150));

    @Test
    void givesEachQuestionRowItsOwnRegion() {
        List<Marker> markers = List.of(new Marker(1, 51, 60), new Marker(2, 51, 110));
        List<QuestionRegion> regions = RegionLocator.locate(3, markers, BORDERS);
        assertEquals(List.of(1), regions.get(0).questionNumbers());
        assertEquals(List.of(2), regions.get(1).questionNumbers());
        assertEquals(50.0, regions.get(0).top());
        assertEquals(100.0, regions.get(0).bottom());
        assertEquals(150.0, regions.get(1).bottom());
        assertEquals(3, regions.get(1).pageIndex());
    }

    @Test
    void keepsQuestionsThatShareARowTogether() {
        List<Marker> markers = List.of(new Marker(22, 51, 130), new Marker(21, 51, 60));
        List<Rule> rowBorders = List.of(new Rule(50, 580, 50), new Rule(50, 580, 150));
        List<QuestionRegion> regions = RegionLocator.locate(0, markers, rowBorders);
        assertEquals(1, regions.size());
        assertEquals(List.of(21, 22), regions.get(0).questionNumbers());
        assertEquals(50.0, regions.get(0).top());
        assertEquals(150.0, regions.get(0).bottom());
    }

    @Test
    void ignoresLabelsOutsideTheLeftColumn() {
        List<Marker> markers = List.of(new Marker(1, 51, 60), new Marker(9, 340, 70));
        List<QuestionRegion> regions = RegionLocator.locate(0, markers, BORDERS);
        assertEquals(List.of(List.of(1)), regions.stream().map(QuestionRegion::questionNumbers).toList());
    }

    @Test
    void ignoresLabelsAboveTheFirstBorder() {
        assertTrue(RegionLocator.locate(0, List.of(new Marker(1, 51, 20)), BORDERS).isEmpty());
    }

    @Test
    void returnsNothingWithoutBorders() {
        assertTrue(RegionLocator.locate(0, List.of(new Marker(1, 51, 60)), List.of()).isEmpty());
    }

    @Test
    void skipsALastRowWithNoBorderBelowIt() {
        List<Rule> single = List.of(new Rule(50, 580, 50));
        assertTrue(RegionLocator.locate(0, List.of(new Marker(1, 51, 60)), single).isEmpty());
    }
}
