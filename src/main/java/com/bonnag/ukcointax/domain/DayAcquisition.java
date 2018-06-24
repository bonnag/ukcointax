package com.bonnag.ukcointax.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.MoreCollectors;

public class DayAcquisition {
    private final LocalDate day;
    private final AssetAmount bought;
    private final AssetAmount sterlingCost;
    private final List<ValuedTrade> valuedTrades;

    public DayAcquisition(List<ValuedTrade> valuedTrades) {
        this.day = valuedTrades.stream().map(vt -> vt.getDay()).distinct().collect(MoreCollectors.onlyElement());
        this.bought = valuedTrades.stream().map(vt -> vt.getTrade().getBought()).reduce((aa1, aa2) -> aa1.add(aa2)).get();
        this.sterlingCost = valuedTrades.stream().map(vt -> vt.getValue()).reduce((aa1, aa2) -> aa1.add(aa2)).get();
        this.valuedTrades = Collections.unmodifiableList(new ArrayList<>(valuedTrades));
    }

    public LocalDate getDay() {
        return day;
    }

    public AssetAmount getBought() {
        return bought;
    }

    public AssetAmount getSterlingCost() {
        return sterlingCost;
    }

    public List<ValuedTrade> getValuedTrades() {
        return valuedTrades;
    }

    public AssetDay getAssetDay() {
        return new AssetDay(getBought().getAsset(), day);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DayAcquisition that = (DayAcquisition) o;
        return Objects.equals(getAssetDay(), that.getAssetDay());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAssetDay());
    }

    @Override
    public String toString() {
        return "DayAcquisition{" + "day=" + day +
                ", bought=" + bought +
                ", sterlingCost=" + sterlingCost +
                ", valuedTrades=" + valuedTrades +
                '}';
    }
}
