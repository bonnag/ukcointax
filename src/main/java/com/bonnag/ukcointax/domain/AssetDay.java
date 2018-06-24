package com.bonnag.ukcointax.domain;

import java.time.LocalDate;
import java.util.Objects;

public class AssetDay {
    private final Asset asset;
    private final LocalDate day;

    public AssetDay(Asset asset, LocalDate day) {
        this.asset = asset;
        this.day = day;
    }

    public Asset getAsset() {
        return asset;
    }

    public LocalDate getDay() {
        return day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssetDay assetDay = (AssetDay) o;
        return Objects.equals(asset, assetDay.asset) &&
                Objects.equals(day, assetDay.day);
    }

    @Override
    public int hashCode() {
        return Objects.hash(asset, day);
    }

    @Override
    public String toString() {
        return "AssetDay{" + "asset=" + asset +
                ", day=" + day +
                '}';
    }
}
