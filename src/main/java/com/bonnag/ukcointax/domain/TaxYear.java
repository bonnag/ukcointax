package com.bonnag.ukcointax.domain;

import java.time.LocalDate;
import java.time.Month;
import java.util.Objects;

public class TaxYear {
    private final int startCalendarYear;
    private final String name;

    public TaxYear(String name) {
        if (!name.matches("^[1-2][0-9]{3}/[0-9]{2}$")) {
            throw new IllegalArgumentException("expected name like 2016/17, not " + name);
        }
        this.startCalendarYear = Integer.parseInt(name.substring(0 ,4));
        this.name = name;
    }

    private TaxYear(int startCalendarYear) {
        this.startCalendarYear = startCalendarYear;
        int endingCalendarYear = startCalendarYear + 1;
        this.name = startCalendarYear + "/" + String.format("%02d", endingCalendarYear % 100);
    }

    public int getStartCalendarYear() {
        return startCalendarYear;
    }

    public String getName() {
        return name;
    }

    public LocalDate getStartDay() {
        return LocalDate.of(startCalendarYear, Month.APRIL, 6);
    }

    public LocalDate getEndDay() {
        return LocalDate.of(startCalendarYear + 1, Month.APRIL, 5);
    }

    public TaxYear plusYears(int numberOfYears) {
        return TaxYear.startingInCalendarYear(getStartCalendarYear() + numberOfYears);
    }

    public boolean contains(LocalDate day) {
        return !day.isBefore(getStartDay()) && !day.isAfter(getEndDay());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaxYear taxYear = (TaxYear) o;
        return startCalendarYear == taxYear.startCalendarYear;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startCalendarYear);
    }

    @Override
    public String toString() {
        return name;
    }

    public String toSafeString() {
        return name.replace('/', '-');
    }

    public static TaxYear forDay(LocalDate day) {
        LocalDate taxYearStart = LocalDate.of(day.getYear(), Month.APRIL, 6);
        int startCalendarYear = day.isBefore(taxYearStart) ? day.getYear() - 1 : day.getYear();
        return TaxYear.startingInCalendarYear(startCalendarYear);
    }

    public static TaxYear startingInCalendarYear(int calendarYear) {
        return new TaxYear(calendarYear);
    }

}
