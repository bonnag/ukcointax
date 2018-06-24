package com.bonnag.ukcointax.domain;

import java.util.List;

public class TaxSummary {
    private final int numberOfDisposals;
    private final AssetAmount disposalProceeds;
    private final AssetAmount allowableCosts;
    private final AssetAmount gainsBeforeLosses;
    private final AssetAmount losses;

    public TaxSummary() {
        numberOfDisposals = 0;
        disposalProceeds = AssetAmount.makeZeroFor(Asset.Sterling);
        allowableCosts = AssetAmount.makeZeroFor(Asset.Sterling);
        gainsBeforeLosses = AssetAmount.makeZeroFor(Asset.Sterling);
        losses = AssetAmount.makeZeroFor(Asset.Sterling);
    }

    public TaxSummary(int numberOfDisposals, AssetAmount disposalProceeds, AssetAmount allowableCosts, AssetAmount gainsBeforeLosses, AssetAmount losses) {
        this.numberOfDisposals = numberOfDisposals;
        this.disposalProceeds = disposalProceeds;
        this.allowableCosts = allowableCosts;
        this.gainsBeforeLosses = gainsBeforeLosses;
        this.losses = losses;
    }

    public TaxSummary add(AssetAmount individualDisposalProceeds, AssetAmount individualAllowableCosts) {
        boolean isGain = individualDisposalProceeds.compare(individualAllowableCosts) > 0;
        return new TaxSummary(
                numberOfDisposals + 1,
                disposalProceeds.add(individualDisposalProceeds),
                allowableCosts.add(individualAllowableCosts),
                (isGain ? gainsBeforeLosses.add(individualDisposalProceeds.subtract(individualAllowableCosts)) : gainsBeforeLosses),
                (isGain? losses : losses.add(individualAllowableCosts.subtract(individualDisposalProceeds))));
    }

    public int getNumberOfDisposals() {
        return numberOfDisposals;
    }

    public AssetAmount getDisposalProceeds() {
        return disposalProceeds;
    }

    public AssetAmount getAllowableCosts() {
        return allowableCosts;
    }

    public AssetAmount getGainsBeforeLosses() {
        return gainsBeforeLosses;
    }

    public AssetAmount getLosses() {
        return losses;
    }

    public AssetAmount getNetGains() {
        return gainsBeforeLosses.subtract(losses);
    }

    @Override
    public String toString() {
        return "TaxSummary{" + "numberOfDisposals=" + numberOfDisposals +
                ", disposalProceeds=" + disposalProceeds +
                ", allowableCosts=" + allowableCosts +
                ", gainsBeforeLosses=" + gainsBeforeLosses +
                ", losses=" + losses +
                ", netGains=" + getNetGains() +
                '}';
    }

    public static TaxSummary fromIdentifiedDayDisposals(List<IdentifiedDayDisposal> identifiedDayDisposals) {
        TaxSummary taxSummary = new TaxSummary();
        for (IdentifiedDayDisposal identifiedDayDisposal : identifiedDayDisposals) {
            taxSummary = taxSummary.add(identifiedDayDisposal.getProceeds(), identifiedDayDisposal.getAllowableCosts());
        }
        return taxSummary;
    }
}
