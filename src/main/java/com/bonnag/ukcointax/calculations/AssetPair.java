package com.bonnag.ukcointax.calculations;

import com.bonnag.ukcointax.domain.Asset;

import java.util.Objects;

public class AssetPair {
    private final Asset base;
    private final Asset quoted;

    public AssetPair(Asset base, Asset quoted) {
        this.base = base;
        this.quoted = quoted;
    }

    public Asset getBase() {
        return base;
    }

    public Asset getQuoted() {
        return quoted;
    }

    public Asset[] asArray() {
        return new Asset[]{base, quoted};
    }

    public AssetPair inverted() {
        return new AssetPair(getQuoted(), getBase());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssetPair other = (AssetPair) o;
        return Objects.equals(base, other.base) &&
                Objects.equals(quoted, other.quoted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, quoted);
    }

    @Override
    public String toString() {
        return "AssetPair{" + "base=" + base +
                ", quoted=" + quoted +
                '}';
    }
}
