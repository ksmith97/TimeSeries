package com.phrozenspectrum;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 */
class BuildDailyTest {
    @Test
    void singleValueTest() {
        LocalDateTime now = LocalDateTime.now();
        List<TimePoint> points = new ArrayList<>();
        points.add(new TimePoint(now, 10));

        List<DatePoint> result =
                (List<DatePoint>) TimeSeriesBuilder.build(points, TimeSeriesUnit.DAILY);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).date).isEqualTo(now.toLocalDate());
        assertThat(result.get(0).value).isEqualTo(10);
    }

    @Test
    void multipleValueTest() {
        LocalDateTime now = LocalDateTime.now();
        List<TimePoint> points = new ArrayList<>();
        points.add(new TimePoint(now, 10));
        points.add(new TimePoint(now, 10));
        points.add(new TimePoint(now, 10));
        points.add(new TimePoint(now, 10));

        LocalDateTime later = now.plusDays(1);
        points.add(new TimePoint(later, 10));
        points.add(new TimePoint(later, 10));

        //Gap hour here

        LocalDateTime much = later.plusDays(2);
        points.add(new TimePoint(much, 10));

        List<DatePoint> result =
                (List<DatePoint>) TimeSeriesBuilder.build(points, TimeSeriesUnit.DAILY);

        assertThat(result).hasSize(4);
        assertThat(result.get(0).date).isEqualTo(now.toLocalDate());
        assertThat(result.get(0).value).isEqualTo(40);

        assertThat(result.get(1).date).isEqualTo(later.toLocalDate());
        assertThat(result.get(1).value).isEqualTo(20);

        assertThat(result.get(2).date).isEqualTo(later.plusDays(1).toLocalDate());
        assertThat(result.get(2).value).isEqualTo(0);

        assertThat(result.get(3).date).isEqualTo(much.toLocalDate());
        assertThat(result.get(3).value).isEqualTo(10);
    }
}
