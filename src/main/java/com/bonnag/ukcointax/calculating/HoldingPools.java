package com.bonnag.ukcointax.calculating;

import com.bonnag.ukcointax.domain.Asset;

import java.util.HashMap;
import java.util.Map;

public class HoldingPools {
    private final Map<Asset,HoldingPool> holdingPools;

    public HoldingPools() {
        holdingPools = new HashMap<>();
    }

    public HoldingPool getOrCreateHoldingPool(Asset asset) {
        HoldingPool holdingPool = holdingPools.get(asset);
        if (holdingPool == null) {
            holdingPool = new HoldingPool(asset);
            holdingPools.put(asset, holdingPool);
        }
        return holdingPool;
    }
}
