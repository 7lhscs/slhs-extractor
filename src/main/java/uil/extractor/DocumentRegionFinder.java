package uil.extractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public final class DocumentRegionFinder {

    private DocumentRegionFinder() {
    }

    /**
     * Scans all pages that contain question labels.
     *
     * @param document the open document
     * @return the regions in document order
     * @throws IOException           if the document cannot be read
     * @throws IllegalStateException if a page with questions is rotated
     */
    public static List<QuestionRegion> find(PDDocument document) throws IOException {
        List<QuestionRegion> regions = new ArrayList<>();
        for (Map.Entry<Integer, List<Marker>> entry : MarkerCollector.collect(document).entrySet()) {
            int pageIndex = entry.getKey();
            PDPage page = document.getPage(pageIndex);
            requireUnrotated(page, pageIndex);
            List<Rule> borders = RuleMerger.tableBorders(RuleCollector.collect(page));
            regions.addAll(RegionLocator.locate(pageIndex, entry.getValue(), borders));
        }
        return regions;
    }

    private static void requireUnrotated(PDPage page, int pageIndex) {
        if (page.getRotation() != 0) {
            throw new IllegalStateException("Page " + (pageIndex + 1) + " is rotated, which is not supported");
        }
    }
}
