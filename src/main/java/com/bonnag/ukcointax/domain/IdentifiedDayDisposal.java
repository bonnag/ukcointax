package com.bonnag.ukcointax.domain;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class IdentifiedDayDisposal implements Comparable<IdentifiedDayDisposal> {
    private final DayDisposal dayDisposal;
    private final AssetAmount allowableCosts;
    private final List<Identification> identifications;

    public IdentifiedDayDisposal(List<Identification> disposalIdentifications) {
        if (disposalIdentifications.isEmpty()) {
            throw new IllegalArgumentException("must be at least one identification");
        }
        if (!disposalIdentifications.stream().allMatch(di -> di.getDayDisposal().isPresent())) {
            throw new IllegalArgumentException("all identifications must involve a disposal");
        }
        Identification first = disposalIdentifications.get(0);
        this.dayDisposal = first.getDayDisposal().get();
        if (!disposalIdentifications.stream().allMatch(di -> di.getDayDisposal().get().equals(this.dayDisposal))) {
            throw new IllegalArgumentException("all identifications must be disposals of the same asset on the same day");
        }
        this.allowableCosts = disposalIdentifications.stream().map(di -> di.getAllowableCostSterling()).reduce(AssetAmount::add).get();
        this.identifications = Collections.unmodifiableList(new ArrayList<>(disposalIdentifications));
    }

    public DayDisposal getDayDisposal() {
        return dayDisposal;
    }

    public AssetAmount getProceeds() {
        return dayDisposal.getSterlingProceeds();
    }

    public AssetAmount getAllowableCosts() {
        return allowableCosts;
    }

    public List<Identification> getIdentifications() {
        return identifications;
    }

    public AssetDay getAssetDay() {
        return dayDisposal.getAssetDay();
    }

    @Override
    public int compareTo(@NotNull IdentifiedDayDisposal o) {
        return getAssetDay().compareTo(o.getAssetDay());
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
        return "IdentifiedDayDisposal{" + "dayDisposal=" + dayDisposal +
                ", proceeds=" + getProceeds() +
                ", allowableCosts=" + allowableCosts +
                ", identifications=" + identifications +
                '}';
    }
}
