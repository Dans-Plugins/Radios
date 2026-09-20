package dansplugins.radios.frequency;

import java.util.Locale;

/**
 * A radio frequency, normalised to one decimal place so that "101.5", "101.50" and 101.4999 all
 * name the same station. The canonical form is what is stored on receivers and compared on broadcast.
 *
 * <p>Kept free of Bukkit types so it can be unit-tested without a server.
 */
public final class Frequency {

    private final String canonical;

    private Frequency(String canonical) {
        this.canonical = canonical;
    }

    /**
     * Parses user input. Returns {@code null} when the input is not a finite number.
     */
    public static Frequency parse(String input) {
        if (input == null) {
            return null;
        }
        double value;
        try {
            value = Double.parseDouble(input.trim());
        } catch (NumberFormatException e) {
            return null;
        }
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return null;
        }
        return of(value);
    }

    public static Frequency of(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Frequency must be finite");
        }
        return new Frequency(String.format(Locale.ROOT, "%.1f", value));
    }

    /**
     * Whether this frequency lies inside the configured band, inclusive.
     */
    public boolean isWithin(double min, double max) {
        double v = value();
        return v >= min && v <= max;
    }

    public double value() {
        return Double.parseDouble(canonical);
    }

    /** The canonical text form, e.g. {@code 101.5}. */
    public String canonical() {
        return canonical;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Frequency && ((Frequency) o).canonical.equals(canonical);
    }

    @Override
    public int hashCode() {
        return canonical.hashCode();
    }

    @Override
    public String toString() {
        return canonical;
    }
}
