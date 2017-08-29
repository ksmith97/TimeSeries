package com.phrozenspectrum;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 */
class BuildHourlyTest {
    @Test
    void singleValueTest() {
        LocalDateTime now = LocalDateTime.now();
        List<TimePoint> points = new ArrayList<>();
        points.add(new TimePoint(now, 10));

        List<TimePoint> result =
                (List<TimePoint>) TimeSeriesBuilder.build(points, TimeSeriesUnit.HOURLY);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).datetime).isEqualTo(now.truncatedTo(HOURS));
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

        LocalDateTime later = now.plusHours(1);
        points.add(new TimePoint(later, 10));
        points.add(new TimePoint(later, 10));

        //Gap hour here

        LocalDateTime much = later.plusHours(2);
        points.add(new TimePoint(much, 10));

        List<TimePoint> result =
                (List<TimePoint>) TimeSeriesBuilder.build(points, TimeSeriesUnit.HOURLY);

        assertThat(result).hasSize(4);
        assertThat(result.get(0).datetime).isEqualTo(now.truncatedTo(HOURS));
        assertThat(result.get(0).value).isEqualTo(40);

        assertThat(result.get(1).datetime).isEqualTo(later.truncatedTo(HOURS));
        assertThat(result.get(1).value).isEqualTo(20);

        assertThat(result.get(2).datetime).isEqualTo(later.plusHours(1).truncatedTo(HOURS));
        assertThat(result.get(2).value).isEqualTo(0);

        assertThat(result.get(3).datetime).isEqualTo(much.truncatedTo(HOURS));
        assertThat(result.get(3).value).isEqualTo(10);
    }
}
