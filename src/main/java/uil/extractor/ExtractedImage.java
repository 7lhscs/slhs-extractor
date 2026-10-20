package uil.extractor;

import java.awt.image.BufferedImage;

/**
 * A question image that was cropped out of a packet, held in memory.
 *
 * <p>Callers decide how to persist or display the image; the extractor never touches the file
 * system.</p>
 *
 * @param region the page region the image was cropped from
 * @param image  the cropped image
 */
public record ExtractedImage(QuestionRegion region, BufferedImage image) {
}
