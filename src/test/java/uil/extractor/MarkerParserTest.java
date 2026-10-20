package uil.extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MarkerParser}.
 */
class MarkerParserTest {

    private final MarkerParser parser = new MarkerParser();

    @Test
    void joinsKeywordAndNumberFromSeparateWords() {
        assertTrue(parser.accept("QUESTION", 50, 100).isEmpty());
        assertEquals(Optional.of(new Marker(13, 50, 100)), parser.accept("13", 90, 100));
    }

    @Test
    void ignoresTextGluedToTheNumber() {
        parser.accept("QUESTION", 50, 100);
        assertEquals(29, parser.accept("29xx", 90, 100).orElseThrow().number());
    }

    @Test
    void acceptsKeywordFusedWithNumber() {
        assertEquals(Optional.of(new Marker(12, 50, 80)), parser.accept("QUESTION12", 50, 80));
    }

    @Test
    void acceptsWholeLineLabelWithSpaceAndTrailingJunk() {
        assertEquals(Optional.of(new Marker(1, 50, 80)), parser.accept("QUESTION 1 xx", 50, 80));
        assertEquals(Optional.of(new Marker(29, 50, 100)), parser.accept("QUESTION 29xx", 50, 100));
    }

    @Test
    void ignoresProseThatMerelyStartsWithTheKeyword() {
        assertTrue(parser.accept("questions", 340, 100).isEmpty());
        assertTrue(parser.accept("21-22.", 400, 100).isEmpty());
    }

    @Test
    void abandonsAKeywordNotFollowedByANumber() {
        parser.accept("QUESTION", 50, 100);
        assertTrue(parser.accept("was", 90, 100).isEmpty());
        parser.accept("QUESTION", 50, 200);
        assertEquals(Optional.of(new Marker(4, 50, 200)), parser.accept("4", 90, 200));
    }

    @Test
    void restartsWhenTheKeywordRepeatsImmediately() {
        parser.accept("QUESTION", 50, 100);
        assertTrue(parser.accept("QUESTION", 50, 200).isEmpty());
        assertEquals(Optional.of(new Marker(5, 50, 200)), parser.accept("5", 90, 200));
    }
}
