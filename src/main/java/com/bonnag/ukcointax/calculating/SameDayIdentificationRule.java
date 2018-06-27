package com.bonnag.ukcointax.calculating;

import com.bonnag.ukcointax.domain.*;

import java.util.Optional;

/**
 * Rule TCGA92/S105 (1)(a) to identify disposals with acquisitions on the same day.
 */
public class SameDayIdentificationRule implements IdentificationRule {
    @Override
    public void apply(AssetDay assetDay, DailyAcquisitionsAndDisposals dailyAcquisitionsAndDisposals, Identifications identifications) {
        Optional<DayAcquisition> optionalDayAcquisition = dailyAcquisitionsAndDisposals.getAcquisition(assetDay);
        Optional<DayDisposal> optionalDayDisposal = dailyAcquisitionsAndDisposals.getDisposal(assetDay);
        if (!optionalDayAcquisition.isPresent() || !optionalDayDisposal.isPresent()) {
            return;
        }
        if (!identifications.getAmountIdentifiedFromAcquisition(assetDay).isZero() ||
                !identifications.getAmountIdentifiedFromDisposal(assetDay).isZero()) {
            throw new IllegalStateException("expected same-day rule to be applied before other rules");
        }
        DayAcquisition dayAcquisition = optionalDayAcquisition.get();
        DayDisposal dayDisposal = optionalDayDisposal.get();
        AssetAmount commonAmount = AssetAmount.min(dayDisposal.getSold(), dayAcquisition.getBought());
        identifications.add(new NonPoolIdentification(dayDisposal, dayAcquisition, commonAmount, IdentificationRuleCode.SameDay));
    }
}
