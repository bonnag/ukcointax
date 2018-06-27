package com.bonnag.ukcointax.calculating;

import com.bonnag.ukcointax.domain.AssetDay;
import com.bonnag.ukcointax.domain.DailyAcquisitionsAndDisposals;
import com.bonnag.ukcointax.domain.Identifications;

public interface IdentificationRule {
    void apply(AssetDay assetDay, DailyAcquisitionsAndDisposals dailyAcquisitionsAndDisposals, Identifications identifications);
}
