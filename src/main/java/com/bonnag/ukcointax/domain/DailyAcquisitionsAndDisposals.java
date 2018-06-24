package com.bonnag.ukcointax.domain;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DailyAcquisitionsAndDisposals {
    private final Map<AssetDay, DayAcquisition> acquisitions;
    private final Map<AssetDay, DayDisposal> disposals;

    public DailyAcquisitionsAndDisposals(List<ValuedTrade> valuedTrades) {
        acquisitions = valuedTrades.stream()
                .filter(vt -> !vt.getBoughtAssetDay().getAsset().isSterling())
                .collect(Collectors.groupingBy(vt -> vt.getBoughtAssetDay(),
                        Collectors.collectingAndThen(Collectors.toList(),
                                vts -> new DayAcquisition(vts))));
        disposals = valuedTrades.stream()
                .filter(vt -> !vt.getSoldAssetDay().getAsset().isSterling())
                .collect(Collectors.groupingBy(vt -> vt.getSoldAssetDay(),
                        Collectors.collectingAndThen(Collectors.toList(),
                                vts -> new DayDisposal(vts))));
    }

    public Optional<DayAcquisition> getAcquisition(AssetDay assetDay) {
        return Optional.ofNullable(acquisitions.get(assetDay));
    }

    public Optional<DayDisposal> getDisposal(AssetDay assetDay) {
        return Optional.ofNullable(disposals.get(assetDay));
    }

    @Override
    public String toString() {
        return "DailyAcquisitionsAndDisposals{" + "acquisitions=" + acquisitions +
                ", disposals=" + disposals +
                '}';
    }
}
