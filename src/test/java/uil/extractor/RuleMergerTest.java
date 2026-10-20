package uil.extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link RuleMerger}.
 */
class RuleMergerTest {

    @Test
    void joinsCellSegmentsOnTheSameLine() {
        List<Rule> raw = List.of(new Rule(316, 582, 100.2), new Rule(50, 316, 100.0));
        assertEquals(List.of(new Rule(50, 582, 100.0)), RuleMerger.tableBorders(raw));
    }

    @Test
    void doesNotJoinSegmentsSeparatedByAGap() {
        List<Rule> raw = List.of(new Rule(50, 200, 100), new Rule(400, 580, 100), new Rule(50, 580, 300));
        assertEquals(List.of(new Rule(50, 580, 300)), RuleMerger.tableBorders(raw));
    }

    @Test
    void dropsShortSegmentsSuchAsUnderlines() {
        List<Rule> raw = List.of(new Rule(50, 582, 100), new Rule(268, 464, 754));
        assertEquals(List.of(new Rule(50, 582, 100)), RuleMerger.tableBorders(raw));
    }

    @Test
    void keepsDistinctRowsApartAndOrdersThemTopDown() {
        List<Rule> raw = List.of(new Rule(50, 582, 300), new Rule(50, 582, 100), new Rule(50, 582, 200));
        List<Double> ys = RuleMerger.tableBorders(raw).stream().map(Rule::y).toList();
        assertEquals(List.of(100.0, 200.0, 300.0), ys);
    }

    @Test
    void returnsNothingForNoInput() {
        assertTrue(RuleMerger.tableBorders(List.of()).isEmpty());
    }
}
