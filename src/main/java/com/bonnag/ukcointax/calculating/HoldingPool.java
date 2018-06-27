package com.bonnag.ukcointax.calculating;

import com.bonnag.ukcointax.domain.Asset;
import com.bonnag.ukcointax.domain.AssetAmount;

public class HoldingPool {
    private AssetAmount totalAmount;
    private AssetAmount totalSterlingCost;

    public HoldingPool(Asset asset) {
        totalAmount = AssetAmount.makeZeroFor(asset);
        totalSterlingCost = AssetAmount.makeZeroFor(Asset.Sterling);
    }

    public void add(AssetAmount amount, AssetAmount sterlingCost) {
        totalAmount = totalAmount.add(amount);
        totalSterlingCost = totalSterlingCost.add(sterlingCost);
    }

    public AssetAmount removeAndGetAllowableCost(AssetAmount amount) {
        if (amount.compare(totalAmount) > 0) {
            throw new IllegalStateException("insufficient holdings; got " + totalAmount + ", want " + amount);
        }
        AssetAmount sterlingCost = totalSterlingCost.multiplyThenDivide(amount, totalAmount);
        this.totalAmount = totalAmount.subtract(amount);
        this.totalSterlingCost = totalSterlingCost.subtract(sterlingCost);
        return sterlingCost;
    }
}
