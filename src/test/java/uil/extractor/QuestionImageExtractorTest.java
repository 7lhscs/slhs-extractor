package uil.extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Integration test for {@link QuestionImageExtractor}: builds a tiny packet page in memory and
 * checks that it comes back as one image.
 */
class QuestionImageExtractorTest {

    @TempDir
    Path tempDir;

    @Test
    void extractsAQuestionRowFromAPacket() throws IOException {
        Path pdf = tempDir.resolve("packet.pdf");
        try (PDDocument document = oneQuestionPacket()) {
            document.save(pdf.toFile());
        }
        List<ExtractedImage> images = new QuestionImageExtractor().extract(pdf);
        assertEquals(1, images.size());
        ExtractedImage image = images.get(0);
        assertEquals(1, image.region().firstNumber());
        assertTrue(image.image().getWidth() > 0);
        assertTrue(image.image().getHeight() > 0);
    }

    /** One page: two table-wide borders around a single "QUESTION 1" label. */
    private static PDDocument oneQuestionPacket() throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.LETTER);
        document.addPage(page);
        try (PDPageContentStream content = new PDPageContentStream(document, page)) {
            content.moveTo(50, 700);
            content.lineTo(550, 700);
            content.stroke();
            content.moveTo(50, 500);
            content.lineTo(550, 500);
            content.stroke();
            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
            content.newLineAtOffset(50, 650);
            content.showText("QUESTION 1");
            content.endText();
        }
        return document;
    }
}