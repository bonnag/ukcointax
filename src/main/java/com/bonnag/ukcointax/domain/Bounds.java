package com.bonnag.ukcointax.domain;

import com.bonnag.ukcointax.calculating.DayMapper;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Bounds {
    private final List<Asset> assets;
    private final TaxYear firstTaxYear;
    private final TaxYear lastTaxYear;

    public Bounds(List<Trade> trades, DayMapper dayMapper) {
        this.assets =
                Collections.unmodifiableList(
                        trades.stream()
                        .flatMap(t -> Stream.of(t.getBase().getAsset(), t.getQuoted().getAsset()))
                        .distinct().sorted().collect(Collectors.toList()));
        Optional<LocalDate> firstDay = trades.stream().map(t -> dayMapper.dayOfTrade(t.getTradedAt())).min(Comparator.naturalOrder());
        Optional<LocalDate> lastDay = trades.stream().map(t -> dayMapper.dayOfTrade(t.getTradedAt())).max(Comparator.naturalOrder());
        this.firstTaxYear = TaxYear.forDay(firstDay.orElseThrow(() -> new IllegalArgumentException("no first trade found")));
        this.lastTaxYear = TaxYear.forDay(lastDay.orElseThrow(() -> new IllegalArgumentException("no last trade found")));
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public List<Asset> getShareLikeAssets() {
        //return Collections.singletonList(new Asset("ADX"));
        return assets.stream().filter(a -> a.isShareLike()).collect(Collectors.toList());
    }

    public TaxYear getFirstTaxYear() {
        return firstTaxYear;
    }

    public TaxYear getLastTaxYear() {
        return lastTaxYear;
    }

    public List<TaxYear> getTaxYears() {
        List<TaxYear> taxYears = new ArrayList<>();
        for ( TaxYear taxYear = getFirstTaxYear();
              !taxYear.getStartDay().isAfter(getLastTaxYear().getStartDay());
              taxYear = taxYear.plusYears(1) ) {
            taxYears.add(taxYear);
        }
        return taxYears;
    }

    public List<LocalDate> getDaysForTaxYears() {
        List<LocalDate> days = new ArrayList<>();
        for (LocalDate day = getFirstTaxYear().getStartDay();
             !day.isAfter(getLastTaxYear().getEndDay());
             day = day.plusDays(1)) {
            days.add(day);
        }
        return Collections.unmodifiableList(days);
    }

    @Override
    public String toString() {
        return "Bounds{" + "assets=" + assets +
                ", firstTaxYear=" + firstTaxYear +
                ", lastTaxYear=" + lastTaxYear +
                '}';
    }
}
