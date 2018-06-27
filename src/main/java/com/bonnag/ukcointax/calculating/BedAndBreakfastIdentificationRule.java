package com.bonnag.ukcointax.calculating;

import com.bonnag.ukcointax.domain.*;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Rule TCGA92/S106A(5) and (5A) to identify disposals with acquisitions in the next 30 days.
 */
public class BedAndBreakfastIdentificationRule implements IdentificationRule {
    @Override
    public void apply(AssetDay assetDay, DailyAcquisitionsAndDisposals dailyAcquisitionsAndDisposals, Identifications identifications) {
        Optional<DayDisposal> optionalDayDisposal = dailyAcquisitionsAndDisposals.getDisposal(assetDay);
        if (!optionalDayDisposal.isPresent()) {
            return;
        }
        DayDisposal dayDisposal = optionalDayDisposal.get();
        for (LocalDate futureDay = assetDay.getDay().plusDays(1); !futureDay.isAfter(assetDay.getDay().plusDays(30)); futureDay = futureDay.plusDays(1)) {
            AssetAmount disposalRemaining = identifications.getAmountRemaining(dayDisposal);
            if (disposalRemaining.isEffectivelyZero()) {
                return;
            }
            AssetDay futureAssetDay = new AssetDay(assetDay.getAsset(), futureDay);
            Optional<DayAcquisition> optionalFutureDayAcquisition = dailyAcquisitionsAndDisposals.getAcquisition(futureAssetDay);
            if (!optionalFutureDayAcquisition.isPresent()) {
                continue;
            }
            DayAcquisition futureDayAcquisition = optionalFutureDayAcquisition.get();
            AssetAmount futureDayAcquisitionRemaining = identifications.getAmountRemaining(futureDayAcquisition);
            if (futureDayAcquisitionRemaining.isEffectivelyZero()) {
                continue;
            }
            AssetAmount commonAmount = AssetAmount.min(disposalRemaining, futureDayAcquisitionRemaining);
            identifications.add(new NonPoolIdentification(dayDisposal, futureDayAcquisition, commonAmount, IdentificationRuleCode.BedAndBreakfast));
        }
    }
}
