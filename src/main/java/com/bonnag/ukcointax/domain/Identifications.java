package com.bonnag.ukcointax.domain;

import java.util.*;
import java.util.stream.Collectors;

// TODO - don't really like the way this turned out
public class Identifications {
    private final List<Identification> identifications;
    private final Map<AssetDay, AssetAmount> amountIdentifiedFromAcquisitionOn;
    private final Map<AssetDay, AssetAmount> amountIdentifiedFromDisposalOn;

    public Identifications() {
        identifications = new ArrayList<>();
        amountIdentifiedFromAcquisitionOn = new LinkedHashMap<>();
        amountIdentifiedFromDisposalOn = new LinkedHashMap<>();
    }

    public void add(Identification identification) {
        identifications.add(identification);
        identification.getDayAcquisition().ifPresent(
                da -> amountIdentifiedFromAcquisitionOn.put(da.getAssetDay(),
                        identification.getAmount().add(
                                getAmountIdentifiedFromAcquisition(da.getAssetDay()))));
        identification.getDayDisposal().ifPresent(
                dd -> amountIdentifiedFromDisposalOn.put(dd.getAssetDay(),
                        identification.getAmount().add(
                                getAmountIdentifiedFromDisposal(dd.getAssetDay()))));
    }

    public AssetAmount getAmountIdentifiedFromAcquisition(AssetDay assetDay) {
        return Optional.ofNullable(amountIdentifiedFromAcquisitionOn.get(assetDay))
                .orElse(AssetAmount.makeZeroFor(assetDay.getAsset()));
    }

    public AssetAmount getAmountIdentifiedFromDisposal(AssetDay assetDay) {
        return Optional.ofNullable(amountIdentifiedFromDisposalOn.get(assetDay))
                .orElse(AssetAmount.makeZeroFor(assetDay.getAsset()));
    }

    public AssetAmount getAmountRemaining(DayAcquisition dayAcquisition) {
        return dayAcquisition.getBought().subtract(getAmountIdentifiedFromAcquisition(dayAcquisition.getAssetDay()));
    }

    public AssetAmount getAmountRemaining(DayDisposal dayDisposal) {
        return dayDisposal.getSold().subtract(getAmountIdentifiedFromDisposal(dayDisposal.getAssetDay()));
    }

    public List<Identification> getIdentifications() {
        return identifications.stream().sorted(Comparator.comparing(i -> i.getEarliestAssetDay())).collect(Collectors.toList());
    }

    public List<IdentifiedDayDisposal> getIdentifiedDayDisposals() {
        Map<AssetDay,List<Identification>> identificationsByAssetDay  =
                identifications.stream()
                        .filter(i -> i.getDayDisposal().isPresent())
                        .collect(
                                Collectors.groupingBy(i -> i.getDayDisposal().get().getAssetDay()));
        return identificationsByAssetDay.values().stream().map(is -> new IdentifiedDayDisposal(is))
                .sorted().collect(Collectors.toList());
    }

    public List<IdentifiedDayDisposal> getIdentifiedDayDisposalsDuring(TaxYear taxYear) {
        Map<AssetDay,List<Identification>> identificationsByAssetDay  =
                identifications.stream()
                        .filter(i -> i.getDayDisposal().isPresent() && taxYear.contains(i.getDayDisposal().get().getDay()))
                        .collect(
                                Collectors.groupingBy(i -> i.getDayDisposal().get().getAssetDay()));
        return identificationsByAssetDay.values().stream().map(is -> new IdentifiedDayDisposal(is))
                .sorted().collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return identifications.stream().map(id -> id.toString()).collect(Collectors.joining(",\n"));
    }
}
