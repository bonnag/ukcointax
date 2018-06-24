package com.bonnag.ukcointax.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class Calculated {
    private final Bounds bounds;
    private final List<ValuedTrade> valuedTrades;
    private final DailyAcquisitionsAndDisposals dailyAcquisitionsAndDisposals;
    private final Identifications identifications;
    private final LinkedHashMap<TaxYear, TaxSummary> taxYearSummaries;

    public Calculated(
            Bounds bounds,
            List<ValuedTrade> valuedTrades,
            DailyAcquisitionsAndDisposals dailyAcquisitionsAndDisposals,
            Identifications identifications,
            LinkedHashMap<TaxYear, TaxSummary> taxYearSummaries) {
        this.bounds = bounds;
        this.valuedTrades = Collections.unmodifiableList(valuedTrades);
        this.dailyAcquisitionsAndDisposals = dailyAcquisitionsAndDisposals;
        this.identifications = identifications;
        this.taxYearSummaries = taxYearSummaries;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public List<ValuedTrade> getValuedTrades() {
        return valuedTrades;
    }

    public DailyAcquisitionsAndDisposals getDailyAcquisitionsAndDisposals() {
        return dailyAcquisitionsAndDisposals;
    }

    public Identifications getIdentifications() {
        return identifications;
    }

    public LinkedHashMap<TaxYear, TaxSummary> getTaxYearSummaries() {
        return taxYearSummaries;
    }

    @Override
    public String toString() {
        return "Calculated{" + "bounds=" + bounds +
                ",\n valuedTrades=" + valuedTrades +
                ",\n dailyAcquisitionsAndDisposals=" + dailyAcquisitionsAndDisposals +
                ",\n identifications=" + identifications +
                ",\n taxYearSummaries=" + taxYearSummaries +
                '}';
    }
}
