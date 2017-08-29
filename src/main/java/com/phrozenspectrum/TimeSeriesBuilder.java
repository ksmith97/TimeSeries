package com.phrozenspectrum;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.previousOrSame;
import static java.util.stream.Collectors.toList;

/**
 * Created by kevin on 8/28/2017.
 */
public class TimeSeriesBuilder {
    /**
     * Used to produce List of Time/Value pairs for charting values within defined time windows.
     * Using the oldest and most future point it creates points at the specified Time Series unit.
     * It then transforms then maps the given points into the create TimeSeries and sums any values
     * in the same time window.
     *
     * @param points
     * @param unit
     * @return
     */
    public static List<? extends TemporalPoint> build(List<TimePoint> points, TimeSeriesUnit unit) {
        if (points.isEmpty()) {
            return Collections.emptyList();
        }

        switch (unit) {
            case HOURLY:
                return buildHourlyList(points);
            case DAILY:
                return buildDailyList(points);
            case WEEKLY:
                return buildWeeklyList(points);
            case MONTHLY:
                return buildMonthlyList(points);
            default:
                throw new IllegalArgumentException("Invalid Time Series Unit given to build!");
        }
    }

    /**
     * Builds a list of the summation of Hourly values from the given time points.
     *
     * @param points
     * @return
     */
    static List<TimePoint> buildHourlyList(List<TimePoint> points) {
        points = adjustHourly(points);
        Map<LocalDateTime, List<TimePoint>> pointMap = points.stream().collect(Collectors.groupingBy(p -> p.datetime));

        points.sort(Comparator.comparing(p -> p.datetime));
        List<LocalDateTime> hourPoints = getHourlyRange(points.get(0).datetime, points.get(points.size() - 1).datetime);

        return hourPoints.stream().map(hour -> {
            List<TimePoint> data = pointMap.get(hour);
            if (data == null || data.isEmpty()) {
                return new TimePoint(hour, 0);
            }

            int total = 0;
            for (TimePoint n : data) {
                total += n.value;
            }
            return new TimePoint(hour, total);
        }).collect(toList());
    }

    static List<DatePoint> buildDailyList(List<TimePoint> points) {
        List<DatePoint> dailyPoints = adjustDaily(points);
        return buildDateList(dailyPoints, TimeSeriesBuilder::getDailyRange);
    }

    static List<DatePoint> buildWeeklyList(List<TimePoint> points) {
        List<DatePoint> weeklyPoints = adjustWeekly(points);
        return buildDateList(weeklyPoints, TimeSeriesBuilder::getWeeklyRange);
    }

    static List<DatePoint> buildMonthlyList(List<TimePoint> points) {
        List<DatePoint> monthlyPoints = adjustMonthly(points);
        return buildDateList(monthlyPoints, TimeSeriesBuilder::getMonthlyRange);
    }

    /**
     * Builds a List of DatePoints containing a value at each increment defined by the given incrementFn.
     * All of the given points that contain a value will be summed at the matching DatePoint. This gives us a complete
     * list as defined by the increment function containing the point values.
     *
     * @param points
     * @param incrementFn
     * @return
     */
    static List<DatePoint> buildDateList(List<DatePoint> points,
                                         BiFunction<LocalDate, LocalDate, List<LocalDate>> incrementFn) {
        Map<LocalDate, List<DatePoint>> pointMap = points.stream()
                                                         .collect(Collectors.groupingBy(p -> p.date));

        points.sort(Comparator.comparing(p -> p.date));
        List<LocalDate> hourPoints = incrementFn.apply(points.get(0).date, points.get(points.size() - 1).date);

        return hourPoints.stream().map(hour -> {
            List<DatePoint> data = pointMap.get(hour);
            if (data == null || data.isEmpty()) {
                return new DatePoint(hour, 0);
            }

            int total = 0;
            for (DatePoint n : data) {
                total += n.value;
            }
            return new DatePoint(hour, total);
        }).collect(toList());
    }

    /**
     * Converts list of time points into time points on the hour.
     * eg. [2017-08-29T12:30:12] -> [2017-08-29T12:00:00]
     *
     * @param points
     * @return
     */
    static List<TimePoint> adjustHourly(List<TimePoint> points) {
        return points.stream().map(p -> new TimePoint(p.datetime.truncatedTo(ChronoUnit.HOURS), p.value))
                     .collect(toList());
    }
    /**
     * Converts list of time points into dates.
     * eg. [2017-08-29T12:00:00] -> [2017-08-29]
     *
     * @param points
     * @return
     */
    static List<DatePoint> adjustDaily(List<TimePoint> points) {
        return points.stream().map(p -> new DatePoint(p.datetime.toLocalDate(), p.value)).collect(toList());
    }

    /**
     * Converts list of time points into first day of week points.
     * eg. [2017-08-31] -> [2017-08-28]
     * @param points
     * @return
     */
    static List<DatePoint> adjustWeekly(List<TimePoint> points) {
        return points.stream()
                     .map(p -> new DatePoint(p.datetime.toLocalDate().with(previousOrSame(DayOfWeek.MONDAY)), p.value))
                     .collect(toList());
    }

    /**
     * Converts list of TimePoints into a list of first day of month points.
     * eg. [2017-08-29] -> [2017-08-01]
     *
     * @param points
     * @return
     */
    static List<DatePoint> adjustMonthly(List<TimePoint> points) {
        return points.stream()
                     .map(p -> new DatePoint(p.datetime.toLocalDate().with(firstDayOfMonth()), p.value))
                     .collect(toList());
    }

    /*
        Range functions. These funtions are responsible for producing a range of dates occurring at set intervals.
     */

    /**
     * Retrieves a date range at Daily increments between the start and end date(inclusive) in sorted order.
     *
     * ex. 2017-06-01, 2017-06-02, 2017-06-03
     *
     * @param start
     * @param end
     * @return
     */
    static List<LocalDate> getDailyRange(LocalDate start, LocalDate end) {
        List<LocalDate> ret = new ArrayList<>();
        LocalDate tmp = start;
        while (tmp.isBefore(end) || tmp.equals(end)) {
            ret.add(tmp);
            tmp = tmp.plusDays(1);
        }
        return ret;
    }

    /**
     * Retrieves a date range at hourly increments between the start and end date(inclusive) in sorted order.
     * Each date will be the first of a month.
     * ex. 2017-06-01T08:00:00, 2017-06-01T09:00:00, 2017-06-01T10:00:00
     *
     * @param start
     * @param end
     * @return
     */
    static List<LocalDateTime> getHourlyRange(LocalDateTime start, LocalDateTime end) {
        List<LocalDateTime> ret = new ArrayList<>();
        LocalDateTime tmp = start.truncatedTo(ChronoUnit.HOURS);
        LocalDateTime endTmp = end.truncatedTo(ChronoUnit.HOURS);
        while (tmp.isBefore(endTmp) || tmp.equals(endTmp)) {
            ret.add(tmp);
            tmp = tmp.plusHours(1);
        }
        return ret;
    }

    /**
     * Retrieves a date range at monthly increments between the start and end date(inclusive) in sorted order.
     * Each date will be the first of a month.
     * ex. 2017-06-01, 2017-07-01, 2017-08-01, 2017-09-01
     *
     * @param start
     * @param end
     * @return
     */
    static List<LocalDate> getMonthlyRange(LocalDate start, LocalDate end) {
        List<LocalDate> ret = new ArrayList<>();
        LocalDate tmp = start.with(firstDayOfMonth());
        LocalDate endTmp = end.with(firstDayOfMonth());
        while (tmp.isBefore(endTmp) || tmp.equals(endTmp)) {
            ret.add(tmp);
            tmp = tmp.plusMonths(1);
        }
        return ret;
    }

    /**
     * Retrieves a date range at weekly increments between the start and end date(inclusive) in sorted order.
     * Each date will be a monday for the week
     * ex. 2017-07-31, 2017-08-07, 2017-07-14
     *
     * @param start
     * @param end
     * @return
     */
    static List<LocalDate> getWeeklyRange(LocalDate start, LocalDate end) {
        List<LocalDate> ret = new ArrayList<>();
        LocalDate tmp = start.with(previousOrSame(DayOfWeek.MONDAY));
        LocalDate endTmp = end.with(previousOrSame(DayOfWeek.MONDAY));
        while (tmp.isBefore(endTmp) || tmp.equals(endTmp)) {
            ret.add(tmp);
            tmp = tmp.plusWeeks(1);
        }
        return ret;
    }
}
