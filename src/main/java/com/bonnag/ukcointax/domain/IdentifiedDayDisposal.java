package com.bonnag.ukcointax.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IdentifiedDayDisposal {
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

    @Override
    public String toString() {
        return "IdentifiedDayDisposal{" + "dayDisposal=" + dayDisposal +
                ", proceeds=" + getProceeds() +
                ", allowableCosts=" + allowableCosts +
                ", identifications=" + identifications +
                '}';
    }
}
