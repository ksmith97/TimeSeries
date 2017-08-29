package com.phrozenspectrum;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 */
class TimeSeriesBuilderWeeklyRangeTest {
    private void testValues(String startStr, String endStr, int expectedSize, String expectedFirst,
                            String expectedLast) {

        LocalDate start = LocalDate.parse(startStr);
        LocalDate end = LocalDate.parse(endStr);

        Collection<LocalDate> result = TimeSeriesBuilder.getWeeklyRange(start, end);

        assertThat(result).hasSize(expectedSize);
        assertThat(result).first().isEqualTo(LocalDate.parse(expectedFirst));
        assertThat(result).last().isEqualTo(LocalDate.parse(expectedLast));
    }

    @Test
    void singleValueTest() {
        testValues("2007-12-03", "2007-12-03", 1, "2007-12-03", "2007-12-03");
        testValues("2007-12-02", "2007-12-02", 1, "2007-11-26", "2007-11-26");
        testValues("2007-12-04", "2007-12-09", 1, "2007-12-03", "2007-12-03");
    }

    @Test
    void multiWeeksValuesTest() {
        // Crazy I accidentally picked two mondays for this test.
        testValues("2007-01-01", "2007-12-31", 53, "2007-01-01", "2007-12-31");
        testValues("2017-01-05", "2017-12-30", 52, "2017-01-02", "2017-12-25");
    }
}
