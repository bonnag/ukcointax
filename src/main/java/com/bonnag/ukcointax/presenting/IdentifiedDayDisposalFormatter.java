package com.bonnag.ukcointax.presenting;

import com.bonnag.ukcointax.domain.AssetAmount;
import com.bonnag.ukcointax.domain.IdentifiedDayDisposal;

public class IdentifiedDayDisposalFormatter implements ItemFormatter<IdentifiedDayDisposal> {

    @Override
    public String[] getHeader() {
        return new String[] {
                "Asset",
                "Day",
                "ProceedsSterling",
                "AllowableCostsSterling",
                "NumberOfTrades",
                "NumberOfIdentifications",
                "TotalIdentificationCosts"
        };
    }

    @Override
    public String[] format(IdentifiedDayDisposal item) {
        return new String[] {
                item.getDayDisposal().getAssetDay().getAsset().toString(),
                item.getDayDisposal().getAssetDay().getDay().toString(),
                item.getProceeds().getAmountAsString(),
                item.getAllowableCosts().getAmountAsString(),
                "" + item.getDayDisposal().getValuedTrades().size(),
                "" + item.getIdentifications().size(),
                item.getIdentifications().stream().map(i -> i.getAllowableCostSterling()).reduce(AssetAmount::add).get().getAmountAsString()
        };
    }
}
