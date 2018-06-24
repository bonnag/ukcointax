package com.bonnag.ukcointax.calculations;

import com.bonnag.ukcointax.domain.*;

import java.util.Optional;

public class NonPoolIdentification implements Identification {
    private final DayDisposal dayDisposal;
    private final DayAcquisition dayAcquisition;
    private final AssetAmount amount;
    private final IdentificationRuleCode identificationRuleCode;

    public NonPoolIdentification(DayDisposal dayDisposal, DayAcquisition dayAcquisition, AssetAmount amount, IdentificationRuleCode identificationRuleCode) {
        this.dayDisposal = dayDisposal;
        this.dayAcquisition = dayAcquisition;
        this.amount = amount;
        this.identificationRuleCode = identificationRuleCode;
    }

    public Optional<DayDisposal> getDayDisposal() {
        return Optional.of(dayDisposal);
    }

    public Optional<DayAcquisition> getDayAcquisition() {
        return Optional.of(dayAcquisition);
    }

    public AssetAmount getAmount() {
        return amount;
    }

    @Override
    public AssetAmount getAllowableCostSterling() {
        return dayAcquisition.getSterlingCost().multiplyThenDivide(amount, dayAcquisition.getBought());
    }

    public IdentificationRuleCode getIdentificationRuleCode() {
        return identificationRuleCode;
    }

    @Override
    public String toString() {
        return "NonPoolIdentification{" + "dayDisposal=" + dayDisposal +
                ", dayAcquisition=" + dayAcquisition +
                ", amount=" + amount +
                ", allowableCostSterling=" + getAllowableCostSterling() +
                ", identificationRuleCode=" + identificationRuleCode +
                '}';
    }
}
