package com.phrozenspectrum;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 */
class TimeSeriesBuilderDailyRangeTest {
    private void testValues(String startStr, String endStr, int expectedSize, String expectedFirst,
                            String expectedLast) {

        LocalDate start = LocalDate.parse(startStr);
        LocalDate end = LocalDate.parse(endStr);

        Collection<LocalDate> result = TimeSeriesBuilder.getDailyRange(start, end);

        assertThat(result).hasSize(expectedSize);
        assertThat(result).first().isEqualTo(LocalDate.parse(expectedFirst));
        assertThat(result).last().isEqualTo(LocalDate.parse(expectedLast));
    }

    @Test
    void singleValueTest() {
        LocalDate start = LocalDate.parse("2007-12-03");
        LocalDate end = LocalDate.parse("2007-12-03");

        Collection<LocalDate> result = TimeSeriesBuilder.getDailyRange(start, end);

        assertThat(result).hasSize(1);
        assertThat(result).first().isEqualTo(LocalDate.parse("2007-12-03"));
    }

    @Test
    void multiDaysValuesTest() {
        testValues("2007-12-01", "2007-12-31", 31, "2007-12-01", "2007-12-31");
        testValues("2007-12-01", "2008-01-31", 62, "2007-12-01", "2008-01-31");
        testValues("2007-01-01", "2007-12-31", 365, "2007-01-01", "2007-12-31");
        // Leap year
        testValues("2016-01-01", "2016-12-31", 366, "2016-01-01", "2016-12-31");
    }
}
