package uil.extractor;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * Renders pages and cuts every question region out of them, keeping the results in memory.
 *
 * <p>Each page is rasterized at most once, no matter how many regions it holds. Cropped images
 * are copied out of the page so a caller can hold on to one image without keeping the whole page
 * alive.</p>
 */
public final class RegionRenderer {

    private final ExtractorOptions options;

    /**
     * Creates a renderer.
     *
     * @param options the resolution to use when rasterizing pages
     */
    public RegionRenderer(ExtractorOptions options) {
        this.options = options;
    }

    /**
     * Crops every region from its rendered page.
     *
     * @param document the open document the regions belong to
     * @param regions  the regions to render
     * @return one image per region, in page order
     * @throws IOException if a page cannot be rendered
     */
    public List<ExtractedImage> render(PDDocument document, List<QuestionRegion> regions) throws IOException {
        PDFRenderer renderer = new PDFRenderer(document);
        Map<Integer, List<QuestionRegion>> byPage = regions.stream()
                .collect(Collectors.groupingBy(QuestionRegion::pageIndex, TreeMap::new, Collectors.toList()));
        List<ExtractedImage> rendered = new ArrayList<>();
        for (Map.Entry<Integer, List<QuestionRegion>> entry : byPage.entrySet()) {
            BufferedImage page = renderer.renderImageWithDPI(entry.getKey(), options.dpi(), ImageType.RGB);
            for (QuestionRegion region : entry.getValue()) {
                rendered.add(new ExtractedImage(region, detach(ImageCropper.crop(page, region, options.dpi()))));
            }
        }
        return List.copyOf(rendered);
    }

    /**
     * Copies a crop out of its page so the page can be garbage collected.
     *
     * @param crop the sub-image to copy
     * @return an independent image holding the same pixels
     */
    private static BufferedImage detach(BufferedImage crop) {
        BufferedImage copy = new BufferedImage(crop.getWidth(), crop.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = copy.createGraphics();
        try {
            graphics.drawImage(crop, 0, 0, null);
        } finally {
            graphics.dispose();
        }
        return copy;
    }
}