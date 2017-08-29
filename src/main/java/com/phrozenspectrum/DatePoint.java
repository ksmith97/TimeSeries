package com.phrozenspectrum;

import java.time.LocalDate;

/**
 */
public class DatePoint implements TemporalPoint {
    final LocalDate date;
    final int value;

    public DatePoint(LocalDate date, int value) {
        this.date = date;
        this.value = value;
    }
}
