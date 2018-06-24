package com.bonnag.ukcointax.domain;

import java.util.Optional;

public interface Identification {
    Optional<DayDisposal> getDayDisposal();
    Optional<DayAcquisition> getDayAcquisition();
    AssetAmount getAmount();
    AssetAmount getAllowableCostSterling();
    IdentificationRuleCode getIdentificationRuleCode();
}
