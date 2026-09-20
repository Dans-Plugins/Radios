package dansplugins.radios.frequency;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FrequencyTest {

    @Test
    void equivalentSpellingsAreTheSameStation() {
        assertEquals(Frequency.parse("101.5"), Frequency.parse("101.50"));
        assertEquals(Frequency.parse(" 101.5 "), Frequency.of(101.4999));
        assertEquals("101.5", Frequency.of(101.5).canonical());
    }

    @Test
    void differentFrequenciesAreDifferentStations() {
        assertNotEquals(Frequency.of(101.5), Frequency.of(101.6));
    }

    @Test
    void integersAreCanonicalisedWithOneDecimal() {
        assertEquals("100.0", Frequency.parse("100").canonical());
    }

    @Test
    void garbageDoesNotParse() {
        assertNull(Frequency.parse("loud"));
        assertNull(Frequency.parse(""));
        assertNull(Frequency.parse(null));
    }

    @Test
    void nonFiniteValuesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> Frequency.of(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> Frequency.of(Double.POSITIVE_INFINITY));
        assertNull(Frequency.parse("Infinity"));
        assertNull(Frequency.parse("NaN"));
    }

    @Test
    void bandCheckIsInclusive() {
        assertTrue(Frequency.of(88.0).isWithin(88.0, 108.0));
        assertTrue(Frequency.of(108.0).isWithin(88.0, 108.0));
        assertFalse(Frequency.of(87.9).isWithin(88.0, 108.0));
        assertFalse(Frequency.of(108.1).isWithin(88.0, 108.0));
    }
}
