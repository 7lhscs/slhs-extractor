package uil.extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ExtractorOptions}.
 */
class ExtractorOptionsTest {

    @Test
    void rejectsANonPositiveResolution() {
        assertThrows(IllegalArgumentException.class, () -> new ExtractorOptions(0f));
        assertThrows(IllegalArgumentException.class, () -> new ExtractorOptions(-1f));
    }

    @Test
    void defaultsTheResolution() {
        assertEquals(ExtractorOptions.DEFAULT_DPI, ExtractorOptions.defaults().dpi());
    }

    @Test
    void keepsTheRequestedResolution() {
        assertEquals(300f, new ExtractorOptions(300f).dpi());
    }
}
