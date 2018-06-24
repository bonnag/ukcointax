package com.bonnag.ukcointax.calculations;

import com.bonnag.ukcointax.domain.*;

import java.util.Optional;

public class PoolAcquisitionIdentification implements Identification {
    private final DayAcquisition dayAcquisition;
    private final AssetAmount amount;
    private final AssetAmount sterlingAmount;
    private final IdentificationRuleCode identificationRuleCode;

    public PoolAcquisitionIdentification(DayAcquisition dayAcquisition, AssetAmount amount, AssetAmount sterlingAmount, IdentificationRuleCode identificationRuleCode) {
        this.dayAcquisition = dayAcquisition;
        this.amount = amount;
        this.sterlingAmount = sterlingAmount;
        this.identificationRuleCode = identificationRuleCode;
    }

    @Override
    public Optional<DayDisposal> getDayDisposal() {
        return Optional.empty();
    }

    @Override
    public Optional<DayAcquisition> getDayAcquisition() {
        return Optional.of(dayAcquisition);
    }

    @Override
    public AssetAmount getAmount() {
        return amount;
    }

    @Override
    public AssetAmount getAllowableCostSterling() {
        return sterlingAmount;
    }

    @Override
    public IdentificationRuleCode getIdentificationRuleCode() {
        return identificationRuleCode;
    }

    @Override
    public String toString() {
        return "PoolAcquisitionIdentification{" + "dayAcquisition=" + dayAcquisition +
                ", amount=" + amount +
                ", aAllowableCostSterling=" + getAllowableCostSterling() +
                ", identificationRuleCode=" + identificationRuleCode +
                '}';
    }
}
