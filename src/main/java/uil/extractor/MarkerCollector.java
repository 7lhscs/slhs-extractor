package uil.extractor;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

/**
 * Finds the "QUESTION n" labels of every page of a document.
 */
public final class MarkerCollector extends PDFTextStripper {

    private final Map<Integer, List<Marker>> markersByPage = new TreeMap<>();
    private MarkerParser parser = new MarkerParser();
    private int parserPageIndex = -1;

    private MarkerCollector() throws IOException {
        super();
    }

    /**
     * Scans a whole document for question labels.
     *
     * @param document the open document
     * @return labels grouped by zero-based page index, ordered by page
     * @throws IOException if the document text cannot be read
     */
    public static Map<Integer, List<Marker>> collect(PDDocument document) throws IOException {
        MarkerCollector collector = new MarkerCollector();
        collector.writeText(document, Writer.nullWriter());
        return collector.markersByPage;
    }

    /**
     * Feeds each extracted word to the label parser.
     *
     * @param text          the word text
     * @param textPositions the glyph positions that make up the word
     */
    @Override
    protected void writeString(String text, List<TextPosition> textPositions) {
        if (textPositions.isEmpty()) {
            return;
        }
        int pageIndex = getCurrentPageNo() - 1;
        if (pageIndex != parserPageIndex) {
            parser = new MarkerParser();
            parserPageIndex = pageIndex;
        }
        TextPosition first = textPositions.get(0);
        parser.accept(text.trim(), first.getXDirAdj(), first.getYDirAdj())
                .ifPresent(marker -> markersByPage.computeIfAbsent(pageIndex, key -> new ArrayList<>()).add(marker));
    }
}
