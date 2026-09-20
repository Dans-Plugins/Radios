package dansplugins.radios.frequency;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BandTest {

    private final Band fm = new Band(88.0, 108.0, 0.1);

    @Test
    void containsIsInclusive() {
        assertTrue(fm.contains(Frequency.of(88.0)));
        assertTrue(fm.contains(Frequency.of(108.0)));
        assertTrue(fm.contains(Frequency.of(101.5)));
        assertFalse(fm.contains(Frequency.of(87.9)));
        assertFalse(fm.contains(Frequency.of(108.1)));
    }

    @Test
    void stepGridStartsAtMin() {
        Band coarse = new Band(88.0, 108.0, 0.5);
        assertTrue(coarse.isOnStep(Frequency.of(88.0)));
        assertTrue(coarse.isOnStep(Frequency.of(101.5)));
        assertTrue(coarse.isOnStep(Frequency.of(108.0)));
        assertFalse(coarse.isOnStep(Frequency.of(101.4)));
        assertFalse(coarse.isOnStep(Frequency.of(88.1)));
    }

    @Test
    void tenthStepAcceptsEveryCanonicalFrequency() {
        for (double v = 88.0; v <= 108.0; v += 0.1) {
            assertTrue(fm.isOnStep(Frequency.of(v)), String.valueOf(v));
        }
    }

    @Test
    void invalidBandsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Band(108.0, 88.0, 0.1));
        assertThrows(IllegalArgumentException.class, () -> new Band(88.0, 108.0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Band(88.0, 108.0, -0.1));
        assertThrows(IllegalArgumentException.class, () -> new Band(Double.NaN, 108.0, 0.1));
    }
}
