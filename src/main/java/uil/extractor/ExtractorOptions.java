package uil.extractor;

/**
 * User-controllable settings for an extraction run.
 *
 * @param dpi resolution used when rasterizing pages
 */
public record ExtractorOptions(float dpi) {

    /** Default */
    public static final float DEFAULT_DPI = 200f;

    /**
     * Validates the options.
     *
     * @throws IllegalArgumentException if the dpi is not positive
     */
    public ExtractorOptions {
        if (dpi <= 0) {
            throw new IllegalArgumentException("dpi must be positive");
        }
    }

    /**
     * Creates options that use the default resolution.
     *
     * @return the options
     */
    public static ExtractorOptions defaults() {
        return new ExtractorOptions(DEFAULT_DPI);
    }
}
