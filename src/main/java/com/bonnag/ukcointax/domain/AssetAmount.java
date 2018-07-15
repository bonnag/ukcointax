package com.bonnag.ukcointax.domain;

import java.math.BigDecimal;
import java.math.MathContext;

public class AssetAmount {
    private final Asset asset;
    private final double amount;

    public AssetAmount(Asset asset, double amount) {
        this.asset = asset;
        this.amount = amount;
    }

    public AssetAmount(String assetCode, double amount) {
        this(new Asset(assetCode), amount);
    }

    public Asset getAsset() {
        return asset;
    }

    public double getAmount() {
        return amount;
    }

    public String getAmountAsString() {
        return Double.toString(amount);
    }

    public boolean isZero() {
        return amount == 0.0;
    }

    public boolean isEffectivelyZero() {
        return Math.abs(amount) < 1e-12;
    }

    public boolean isProperlyNegative() {
        return amount < -1e-11;
    }

    public AssetAmount add(AssetAmount other) {
        assetSameAsset(this, other);
        return new AssetAmount(asset, amount + other.amount);
    }

    public AssetAmount subtract(AssetAmount other) {
        assetSameAsset(this, other);
        return new AssetAmount(asset, amount - other.amount);
    }

    public int compare(AssetAmount other) {
        assetSameAsset(this, other);
        return Double.compare(this.amount, other.amount);
    }

    public AssetAmount multiplyThenDivide(AssetAmount numerator, AssetAmount denominator) {
        assetSameAsset(numerator, denominator);
        return new AssetAmount(asset, amount * numerator.amount / denominator.amount);
    }

    public AssetAmount negate() {
        return new AssetAmount(asset, 0.0 - amount);
    }

    @Override
    public String toString() {
        BigDecimal bd = new BigDecimal(amount);
        bd = bd.round(new MathContext(8));
        return asset.getAssetCode() + " " + bd;
    }

    public static AssetAmount min(AssetAmount aa1, AssetAmount aa2) {
        assetSameAsset(aa1, aa2);
        return new AssetAmount(aa1.asset, Math.min(aa1.amount, aa2.amount));
    }

    public static AssetAmount makeZeroFor(Asset asset) {
        return new AssetAmount(asset, 0.0);
    }

    private static void assetSameAsset(AssetAmount aa1, AssetAmount aa2) {
        if (!aa1.asset.equals(aa2.asset)) {
            throw new IllegalArgumentException("attempted to mix " + aa1.asset + " and " + aa2.asset);
        }
    }
}
