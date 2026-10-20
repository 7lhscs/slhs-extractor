package uil.extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ImageCropper}.
 */
class ImageCropperTest {

    private final BufferedImage page = new BufferedImage(1000, 800, BufferedImage.TYPE_INT_RGB);

    private static QuestionRegion region(double left, double top, double right, double bottom) {
        return new QuestionRegion(List.of(1), 0, left, top, right, bottom);
    }

    @Test
    void scalesPointsToPixels() {
        BufferedImage crop = ImageCropper.crop(page, region(10, 20, 60, 70), 144f);
        assertEquals(100, crop.getWidth());
        assertEquals(100, crop.getHeight());
    }

    @Test
    void widensFractionalEdgesOutward() {
        BufferedImage crop = ImageCropper.crop(page, region(10.1, 20.1, 59.9, 69.9), 144f);
        assertEquals(100, crop.getWidth());
        assertEquals(100, crop.getHeight());
    }

    @Test
    void clampsToThePageBounds() {
        BufferedImage crop = ImageCropper.crop(page, region(-5, -5, 5000, 5000), 72f);
        assertEquals(1000, crop.getWidth());
        assertEquals(800, crop.getHeight());
    }

    @Test
    void neverReturnsAnEmptyImage() {
        BufferedImage crop = ImageCropper.crop(page, region(2000, 2000, 2100, 2100), 72f);
        assertEquals(1, crop.getWidth());
        assertEquals(1, crop.getHeight());
    }
}
