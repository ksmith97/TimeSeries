package com.phrozenspectrum;

import org.joda.time.DateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by kevin on 8/28/2017.
 */
public class TimeSeriesBuilder {
    static enum TimeSeriesUnit {
        HOURLY,
        DAILY,
        WEEKLY,
        MONTHLY
    }
    static class TimePoint {
        public Date datetime;
        public Number value;
    }

    Collection<TimePoint> build(List<TimePoint> points, TimeSeriesUnit unit) {
        switch(unit) {
            case HOURLY:
                break;
            case DAILY:
                break;
            case WEEKLY:
                break;
            case MONTHLY:
                break;
        }
    }

    public static Collection<LocalDate> getDayRange(LocalDate start, LocalDate end) {
        Collection<LocalDate> ret = new ArrayList<>();
        LocalDate tmp = start;
        while(tmp.isBefore(end) || tmp.equals(end)) {
            ret.add(tmp);
            tmp = tmp.plusDays(1);
        }
        return ret;
    }

    public Collection<LocalDateTime> getHourlyRange(LocalDateTime start, LocalDateTime end) {
        Collection<LocalDateTime> ret = new ArrayList<>();
        LocalDateTime tmp = start.truncatedTo(ChronoUnit.HOURS);
        while(tmp.isBefore(end) || tmp.equals(end)) {
            ret.add(tmp);
            tmp = tmp.plusHours(1);
        }
        return ret;
    }

    public Collection<LocalDate> getWeeklyRange(LocalDate start, LocalDate end) {
        Collection<LocalDate> ret = new ArrayList<>();
        start.
    }
}
