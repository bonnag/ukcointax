package com.bonnag.ukcointax.domain;

import java.util.*;

public class Asset implements Comparable<Asset> {
    private static final Set<String> popularFiatCodes =
            new LinkedHashSet<>(Arrays.asList(new String[] {
            "USD",
            "EUR",
            "JPY",
            "GBP",
            "AUD",
            "CAD",
            "HKD",
            "KRW",
            "RUB"}));

    public static final Asset Sterling = new Asset("GBP");

    private final String assetCode;

    public Asset(String assetCode) {
        if (!assetCode.equals(assetCode.toUpperCase())) {
            //throw new IllegalArgumentException("asset codes must be upper-case");
        }
        this.assetCode = assetCode;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public boolean isSterling() {
        return equals(Sterling);
    }

    public boolean isFiat() {
        return isSterling() || popularFiatCodes.contains(assetCode);
    }

    public boolean isShareLike() {
        return !isFiat();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset asset = (Asset) o;
        return Objects.equals(assetCode, asset.assetCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assetCode);
    }

    @Override
    public int compareTo(Asset o) {
        return assetCode.compareTo(o.assetCode);
    }

    @Override
    public String toString() {
        return assetCode;
    }
}
