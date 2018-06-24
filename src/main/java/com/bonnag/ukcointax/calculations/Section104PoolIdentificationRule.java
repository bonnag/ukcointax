package com.bonnag.ukcointax.calculations;

import com.bonnag.ukcointax.domain.*;

import java.util.Optional;

/**
 * Rule TCGA92/S106A(5) and (5A) to identify disposals with acquisitions in the next 30 days.
 */
public class Section104PoolIdentificationRule implements IdentificationRule {
    private final HoldingPools holdingPools;

    public Section104PoolIdentificationRule(HoldingPools holdingPools) {
        this.holdingPools = holdingPools;
    }

    @Override
    public void apply(AssetDay assetDay, DailyAcquisitionsAndDisposals dailyAcquisitionsAndDisposals, Identifications identifications) {
        HoldingPool holdingPool = holdingPools.getOrCreateHoldingPool(assetDay.getAsset());
        Optional<DayAcquisition> optionalDayAcquisition = dailyAcquisitionsAndDisposals.getAcquisition(assetDay);
        if (optionalDayAcquisition.isPresent()) {
            applyAcquisition(optionalDayAcquisition.get(), holdingPool, identifications);
        }
        Optional<DayDisposal> optionalDayDisposal = dailyAcquisitionsAndDisposals.getDisposal(assetDay);
        if (optionalDayDisposal.isPresent()) {
            applyDisposal(optionalDayDisposal.get(), holdingPool, identifications);
        }
    }

    private void applyAcquisition(DayAcquisition dayAcquisition, HoldingPool holdingPool, Identifications identifications) {
        AssetAmount remaining = identifications.getAmountRemaining(dayAcquisition);
        if (remaining.isZero()) {
            return;
        }
        AssetAmount sterlingAmount = dayAcquisition.getSterlingCost().multiplyThenDivide(remaining, dayAcquisition.getBought());
        holdingPool.add(remaining, sterlingAmount);
        identifications.add(new PoolAcquisitionIdentification(dayAcquisition, remaining, sterlingAmount, IdentificationRuleCode.Section104Pool));
    }

    private void applyDisposal(DayDisposal dayDisposal, HoldingPool holdingPool, Identifications identifications) {
        AssetAmount remaining = identifications.getAmountRemaining(dayDisposal);
        if (remaining.isZero()) {
            return;
        }
        AssetAmount sterlingAmount = holdingPool.removeAndGetAllowableCost(remaining);
        identifications.add(new PoolDisposalIdentification(dayDisposal, remaining, sterlingAmount, IdentificationRuleCode.Section104Pool));
    }

}
