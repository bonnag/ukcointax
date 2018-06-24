package com.bonnag.ukcointax.domain;

import com.google.common.collect.MoreCollectors;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DayDisposal {
    private final LocalDate day;
    private final AssetAmount sold;
    private final AssetAmount sterlingProceeds;
    private final List<ValuedTrade> valuedTrades;

    public DayDisposal(List<ValuedTrade> valuedTrades) {
        this.day = valuedTrades.stream().map(vt -> vt.getDay()).distinct().collect(MoreCollectors.onlyElement());
        this.sold = valuedTrades.stream().map(vt -> vt.getTrade().getSold()).reduce((aa1, aa2) -> aa1.add(aa2)).get();
        this.sterlingProceeds = valuedTrades.stream().map(vt -> vt.getValue()).reduce((aa1, aa2) -> aa1.add(aa2)).get();
        this.valuedTrades = Collections.unmodifiableList(new ArrayList<>(valuedTrades));
    }

    public LocalDate getDay() {
        return day;
    }

    public AssetAmount getSold() {
        return sold;
    }

    public AssetAmount getSterlingProceeds() {
        return sterlingProceeds;
    }

    public List<ValuedTrade> getValuedTrades() {
        return valuedTrades;
    }

    public AssetDay getAssetDay() {
        return new AssetDay(getSold().getAsset(), day);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DayDisposal that = (DayDisposal) o;
        return Objects.equals(getAssetDay(), that.getAssetDay());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAssetDay());
    }

    @Override
    public String toString() {
        return "DayDisposal{" + "day=" + day +
                ", sold=" + sold +
                ", sterlingProceeds=" + sterlingProceeds +
                ", valuedTrades=" + valuedTrades +
                '}';
    }
}
