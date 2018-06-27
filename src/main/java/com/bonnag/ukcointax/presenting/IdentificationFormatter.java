package com.bonnag.ukcointax.presenting;

import com.bonnag.ukcointax.domain.Identification;

public class IdentificationFormatter implements ItemFormatter<Identification> {
    @Override
    public String[] getHeader() {
        return new String[] {
                "Asset",
                "Day",
                "Amount",
                "AllowableCostSterling",
                "IdentificationRuleCode",
                "DisposalDay",
                "DisposalSoldAmount",
                "DisposalProceedsSterling",
                "AcquisitionDay",
                "AcquisitionBoughtAmount",
                "AcquisitionCostSterling",
        };
    }

    @Override
    public String[] format(Identification item) {
        return new String[] {
                item.getEarliestAssetDay().getAsset().getAssetCode(),
                item.getEarliestAssetDay().getDay().toString(),
                item.getAmount().getAmountAsString(),
                item.getAllowableCostSterling().getAmountAsString(),
                item.getIdentificationRuleCode().toString(),
                item.getDayDisposal().map(dd -> dd.getDay().toString()).orElse("n/a"),
                item.getDayDisposal().map(dd -> dd.getSold().getAmountAsString()).orElse("n/a"),
                item.getDayDisposal().map(dd -> dd.getSterlingProceeds().getAmountAsString()).orElse("n/a"),
                item.getDayAcquisition().map(da -> da.getDay().toString()).orElse("n/a"),
                item.getDayAcquisition().map(da -> da.getBought().getAmountAsString()).orElse("n/a"),
                item.getDayAcquisition().map(da -> da.getSterlingCost().getAmountAsString()).orElse("n/a"),
        };
    }
}
