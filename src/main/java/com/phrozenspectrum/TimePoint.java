package com.phrozenspectrum;

import java.time.LocalDateTime;

/**
 */
public class TimePoint implements TemporalPoint {
    final LocalDateTime datetime;
    final int value;

    public TimePoint(LocalDateTime datetime, int value) {
        this.datetime = datetime;
        this.value = value;
    }
}
