package uil.extractor;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Recognizes "QUESTION n" labels in a stream of words and turns them into {@link Marker}s.
 *
 * <p>Words must be fed in reading order. The label may arrive as two words ("QUESTION", "12"),
 * as one fused word ("QUESTION12") or as one string with the number separated by whitespace
 * ("QUESTION 12 xx"); trailing junk after the digits ("29xx") is ignored.</p>
 */
public final class MarkerParser {

    private static final Pattern KEYWORD = Pattern.compile("^QUESTION(?![A-Za-z])\\s*(\\d{0,4})", Pattern.CASE_INSENSITIVE);
    private static final Pattern NUMBER = Pattern.compile("^(\\d{1,4})");

    private boolean awaitingNumber;
    private double pendingX;
    private double pendingY;

    /**
     * Consumes the next word of the page.
     *
     * @param word the word text
     * @param x    left edge of the word, in points
     * @param y    baseline of the word measured from the top of the page, in points
     * @return the completed marker, or empty if this word does not finish one
     */
    public Optional<Marker> accept(String word, double x, double y) {
        if (awaitingNumber) {
            awaitingNumber = false;
            Optional<Marker> completed = completeFrom(word);
            if (completed.isPresent()) {
                return completed;
            }
        }
        return startFrom(word, x, y);
    }

    private Optional<Marker> completeFrom(String word) {
        Matcher number = NUMBER.matcher(word);
        if (!number.find()) {
            return Optional.empty();
        }
        return Optional.of(new Marker(Integer.parseInt(number.group(1)), pendingX, pendingY));
    }

    private Optional<Marker> startFrom(String word, double x, double y) {
        Matcher keyword = KEYWORD.matcher(word);
        if (!keyword.find()) {
            return Optional.empty();
        }
        if (!keyword.group(1).isEmpty()) {
            return Optional.of(new Marker(Integer.parseInt(keyword.group(1)), x, y));
        }
        awaitingNumber = true;
        pendingX = x;
        pendingY = y;
        return Optional.empty();
    }
}
