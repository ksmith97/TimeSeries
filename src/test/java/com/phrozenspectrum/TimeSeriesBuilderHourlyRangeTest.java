package com.phrozenspectrum;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 */
class TimeSeriesBuilderHourlyRangeTest {
    private void testValues(String startStr, String endStr, int expectedSize, String expectedFirst,
                            String expectedLast) {

        LocalDateTime start = LocalDateTime.parse(startStr);
        LocalDateTime end = LocalDateTime.parse(endStr);

        Collection<LocalDateTime> result = TimeSeriesBuilder.getHourlyRange(start, end);

        assertThat(result).hasSize(expectedSize);
        assertThat(result).first().isEqualTo(LocalDateTime.parse(expectedFirst));
        assertThat(result).last().isEqualTo(LocalDateTime.parse(expectedLast));
    }

    @Test
    void singleValueTest() {
        LocalDateTime start = LocalDateTime.parse("2007-12-03T10:15:30");
        LocalDateTime end = LocalDateTime.parse("2007-12-03T10:15:30");

        Collection<LocalDateTime> result = TimeSeriesBuilder.getHourlyRange(start, end);

        assertThat(result).hasSize(1);
        assertThat(result).first().isEqualTo(LocalDateTime.parse("2007-12-03T10:00:00"));
    }


    @Test
    void singleDayValuesTest() {
        testValues("2007-12-03T00:00:00", "2007-12-03T23:59:59", 24, "2007-12-03T00:00:00", "2007-12-03T23:00:00");
        testValues("2007-12-03T00:59:59", "2007-12-03T23:00:00", 24, "2007-12-03T00:00:00", "2007-12-03T23:00:00");

    }

    @Test
    void multiDaysValuesTest() {
        testValues("2007-12-01T00:00:00", "2007-12-31T23:59:59", 744, "2007-12-01T00:00:00", "2007-12-31T23:00:00");
        testValues("2007-12-01T00:00:00", "2008-01-31T23:59:59", 1488, "2007-12-01T00:00:00", "2008-01-31T23:00:00");
    }
}
