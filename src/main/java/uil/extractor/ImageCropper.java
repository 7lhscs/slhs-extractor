package uil.extractor;

import java.awt.image.BufferedImage;

/**
 * Cuts question regions out of a rasterized page.
 */
public final class ImageCropper {

    private static final double POINTS_PER_INCH = 72.0;

    private ImageCropper() {
    }

    /**
     * Crops a region from a page image, clamping the result to the page bounds.
     *
     * @param page   the rasterized page
     * @param region the region to cut out, in points from the top-left corner of the page
     * @param dpi    the resolution the page was rasterized at
     * @return a view of the requested area, at least one pixel in each direction
     */
    public static BufferedImage crop(BufferedImage page, QuestionRegion region, float dpi) {
        double scale = dpi / POINTS_PER_INCH;
        int x = clamp((int) Math.floor(region.left() * scale), 0, page.getWidth() - 1);
        int y = clamp((int) Math.floor(region.top() * scale), 0, page.getHeight() - 1);
        int right = clamp((int) Math.ceil(region.right() * scale), x + 1, page.getWidth());
        int bottom = clamp((int) Math.ceil(region.bottom() * scale), y + 1, page.getHeight());
        return page.getSubimage(x, y, right - x, bottom - y);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
