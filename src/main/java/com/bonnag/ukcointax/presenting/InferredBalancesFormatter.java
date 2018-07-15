package com.bonnag.ukcointax.presenting;

import com.bonnag.ukcointax.domain.*;

import java.util.List;

public class InferredBalancesFormatter implements ItemFormatter<EndOfDayInferredBalances> {

    private final Bounds bounds;

    public InferredBalancesFormatter(Bounds bounds) {
        this.bounds = bounds;
    }

    @Override
    public String[] getHeader() {
        List<Asset> assets = bounds.getAssets();
        String[] header = new String[1 + assets.size()];
        header[0] = "Day";
        for (int i = 0; i < assets.size(); i++) {
            header[i + 1] = assets.get(i).getAssetCode();
        }
        return header;
    }

    @Override
    public String[] format(EndOfDayInferredBalances item) {
        List<Asset> assets = bounds.getAssets();
        List<AssetAmount> assetAmounts = item.balances;
        String[] row = new String[1 + assetAmounts.size()];
        row[0] = item.day.toString();
        for (int i = 0; i < assetAmounts.size(); i++) {
            AssetAmount assetAmount = assetAmounts.get(i);
            if (!assetAmount.getAsset().equals(assets.get(i))) {
                throw new IllegalStateException("balances don't match assets");
            }
            row[i + 1] = assetAmount.getAmountAsString();
        }
        return row;
    }
}
