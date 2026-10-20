package uil.extractor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

public final class QuestionImageExtractor {

    private final ExtractorOptions options;

    /**
     * Creates an extractor.
     */
    public QuestionImageExtractor() {
        this(ExtractorOptions.defaults());
    }

    /**
     * Creates an extractor.
     *
     * @param options the resolution to use when rasterizing pages
     */
    public QuestionImageExtractor(ExtractorOptions options) {
        this.options = options;
    }

    /**
     * Extracts every question image from a packet.
     *
     * @param pdf the packet to read
     * @return the extracted images, in document order
     * @throws IOException if the packet cannot be read
     */
    public List<ExtractedImage> extract(Path pdf) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdf.toFile())) {
            List<QuestionRegion> regions = DocumentRegionFinder.find(document);
            return new RegionRenderer(options).render(document, regions);
        }
    }
}
