package com.bonnag.ukcointax.calculating;

import com.bonnag.ukcointax.domain.*;

import java.util.Optional;

public class ValidatingIdentificationRule implements IdentificationRule {
    @Override
    public void apply(AssetDay assetDay, DailyAcquisitionsAndDisposals dailyAcquisitionsAndDisposals, Identifications identifications) {
        Optional<DayAcquisition> optionalDayAcquisition = dailyAcquisitionsAndDisposals.getAcquisition(assetDay);
        if (optionalDayAcquisition.isPresent()) {
            AssetAmount remaining = identifications.getAmountRemaining(optionalDayAcquisition.get());
            if (!remaining.isEffectivelyZero()) {
                throw new IllegalStateException("not all of " + optionalDayAcquisition  + " identified, " + remaining + " left");
            }
        }
        Optional<DayDisposal> optionalDayDisposal = dailyAcquisitionsAndDisposals.getDisposal(assetDay);
        if (optionalDayDisposal.isPresent()) {
            AssetAmount remaining = identifications.getAmountRemaining(optionalDayDisposal.get());
            if (!remaining.isEffectivelyZero()) {
                throw new IllegalStateException("not all of " + optionalDayDisposal  + " identified, " + remaining + " left");
            }
        }
    }

}
