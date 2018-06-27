package com.bonnag.ukcointax.calculating;

import java.time.*;

public class DayMapper {
    private static final ZoneId london = ZoneId.of("Europe/London");

    public LocalDate dayOfTrade(Instant tradedAt) {
        return LocalDateTime.ofInstant(tradedAt, london).toLocalDate();
    }

    public Instant quoteCutoff(LocalDate dayOfTrade) {
        return Instant.from(dayOfTrade.atTime(17,0).atZone(london));
    }
}
