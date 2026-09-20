package dansplugins.radios.frequency;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * The tunable range: {@code frequency.min}..{@code frequency.max} inclusive, in increments of
 * {@code frequency.step}. Membership ({@link #contains}) is what a broadcast is checked against;
 * {@link #isOnStep} is additionally enforced when a receiver is tuned.
 *
 * <p>Kept free of Bukkit types so it can be unit-tested without a server.
 */
public final class Band {

    private final double min;
    private final double max;
    private final double step;

    public Band(double min, double max, double step) {
        if (Double.isNaN(min) || Double.isNaN(max) || Double.isNaN(step)
                || Double.isInfinite(min) || Double.isInfinite(max) || Double.isInfinite(step)) {
            throw new IllegalArgumentException("Band bounds must be finite");
        }
        if (min > max) {
            throw new IllegalArgumentException("Band min " + min + " is above max " + max);
        }
        if (step <= 0) {
            throw new IllegalArgumentException("Band step must be positive, was " + step);
        }
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public boolean contains(Frequency frequency) {
        return frequency.isWithin(min, max);
    }

    /** Whether the frequency sits on the step grid starting at {@code min} (to one decimal place). */
    public boolean isOnStep(Frequency frequency) {
        BigDecimal offset = BigDecimal.valueOf(frequency.value()).subtract(BigDecimal.valueOf(min));
        BigDecimal remainder = offset.remainder(BigDecimal.valueOf(step)).setScale(1, RoundingMode.HALF_UP);
        return remainder.signum() == 0 || remainder.compareTo(BigDecimal.valueOf(step).setScale(1, RoundingMode.HALF_UP)) == 0;
    }

    public double min() {
        return min;
    }

    public double max() {
        return max;
    }

    public double step() {
        return step;
    }
}
