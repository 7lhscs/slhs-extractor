package uil.extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link QuestionRegion}.
 */
class QuestionRegionTest {

    @Test
    void reportsFirstAndLastQuestion() {
        QuestionRegion region = new QuestionRegion(List.of(32, 33, 34), 8, 50, 100, 580, 400);
        assertEquals(32, region.firstNumber());
        assertEquals(34, region.lastNumber());
        assertEquals(530.0, region.width());
        assertEquals(300.0, region.height());
    }

    @Test
    void rejectsAnEmptyRegion() {
        assertThrows(IllegalArgumentException.class, () -> new QuestionRegion(List.of(), 0, 0, 0, 1, 1));
    }
}
