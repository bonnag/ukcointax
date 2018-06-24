package com.bonnag.ukcointax.calculations;

import com.bonnag.ukcointax.domain.*;

import java.util.Optional;

public class PoolDisposalIdentification implements Identification {
    private final DayDisposal dayDisposal;
    private final AssetAmount amount;
    private final AssetAmount sterlingAmount;
    private final IdentificationRuleCode identificationRuleCode;

    public PoolDisposalIdentification(DayDisposal dayDisposal, AssetAmount amount, AssetAmount sterlingAmount, IdentificationRuleCode identificationRuleCode) {
        this.dayDisposal = dayDisposal;
        this.amount = amount;
        this.sterlingAmount = sterlingAmount;
        this.identificationRuleCode = identificationRuleCode;
    }

    @Override
    public Optional<DayDisposal> getDayDisposal() {
        return Optional.of(dayDisposal);
    }

    @Override
    public Optional<DayAcquisition> getDayAcquisition() {
        return Optional.empty();
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
        return "PoolDisposalIdentification{" + "dayDisposal=" + dayDisposal +
                ", amount=" + amount +
                ", allowableCostSterling=" + getAllowableCostSterling() +
                ", identificationRuleCode=" + identificationRuleCode +
                '}';
    }
}
