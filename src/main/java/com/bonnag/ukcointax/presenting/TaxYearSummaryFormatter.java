package com.bonnag.ukcointax.presenting;

import com.bonnag.ukcointax.domain.TaxSummary;
import com.bonnag.ukcointax.domain.TaxYear;

import java.util.Map;

public class TaxYearSummaryFormatter implements ItemFormatter<Map.Entry<TaxYear,TaxSummary>> {
    @Override
    public String[] getHeader() {
        return new String[] {
                "TaxYear",
                "NumberOfDisposals",
                "DisposalProceeds",
                "AllowableCosts",
                "GainsBeforeLosses",
                "Losses",
                "NetGains"
        };
    }

    @Override
    public String[] format(Map.Entry<TaxYear, TaxSummary> item) {
        TaxSummary summary = item.getValue();
        return new String[] {
                item.getKey().getName(),
                "" + summary.getNumberOfDisposals(),
                summary.getDisposalProceeds().getAmountAsString(),
                summary.getAllowableCosts().getAmountAsString(),
                summary.getGainsBeforeLosses().getAmountAsString(),
                summary.getLosses().getAmountAsString(),
                summary.getNetGains().getAmountAsString(),
        };
    }
}
