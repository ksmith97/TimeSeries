package com.phrozenspectrum;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 */
class TimeSeriesBuilderMonthlyRangeTest {
    private void testValues(String startStr, String endStr, int expectedSize, String expectedFirst,
                            String expectedLast) {

        LocalDate start = LocalDate.parse(startStr);
        LocalDate end = LocalDate.parse(endStr);

        Collection<LocalDate> result = TimeSeriesBuilder.getMonthlyRange(start, end);

        assertThat(result).hasSize(expectedSize);
        assertThat(result).first().isEqualTo(LocalDate.parse(expectedFirst));
        assertThat(result).last().isEqualTo(LocalDate.parse(expectedLast));
    }

    @Test
    void singleValueTest() {
        testValues("2007-12-03", "2007-12-03", 1, "2007-12-01", "2007-12-01");
        testValues("2007-12-01", "2007-12-01", 1, "2007-12-01", "2007-12-01");
    }

    @Test
    void multiMonthsValuesTest() {
        testValues("2007-01-01", "2007-12-31", 12, "2007-01-01", "2007-12-01");
        testValues("2017-01-05", "2018-12-01", 24, "2017-01-01", "2018-12-01");
    }
}
