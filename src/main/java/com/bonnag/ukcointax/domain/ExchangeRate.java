package com.bonnag.ukcointax.domain;

import com.bonnag.ukcointax.calculations.AssetPair;

import java.time.Instant;
import java.util.Objects;

public class ExchangeRate {
    private final Instant quotedAt;
    private final Asset base;
    private final Asset quoted;
    private final double price;
    private final String sourceCode;

    public ExchangeRate(Instant quotedAt, Asset base, Asset quoted, double price, String sourceCode) {
        this.quotedAt = quotedAt;
        this.base = base;
        this.quoted = quoted;
        this.price = price;
        this.sourceCode = sourceCode;
    }

    public Instant getQuotedAt() {
        return quotedAt;
    }

    public Asset getBase() {
        return base;
    }

    public Asset getQuoted() {
        return quoted;
    }

    public double getPrice() {
        return price;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public AssetPair getAssetPair() {
        return new AssetPair(getBase(), getQuoted());
    }

    public ExchangeRate inverted() {
        return new ExchangeRate(quotedAt, quoted, base, 1.0 / price, sourceCode);
    }

    public AssetAmount convert(AssetAmount assetAmount) {
        if (!assetAmount.getAsset().equals(getBase())) {
            throw new IllegalArgumentException("unable to convert asset " + assetAmount + " using rate " + this);
        }
        return new AssetAmount(getQuoted(), assetAmount.getAmount() * getPrice());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRate exchangeRate = (ExchangeRate) o;
        return Objects.equals(quotedAt, exchangeRate.quotedAt) &&
                Objects.equals(base, exchangeRate.base) &&
                Objects.equals(quoted, exchangeRate.quoted) &&
                Objects.equals(sourceCode, exchangeRate.sourceCode);
    }

    @Override
    public int hashCode() {
       return Objects.hash(quotedAt, base, quoted, sourceCode);
    }

    @Override
    public String toString() {
        return "ExchangeRate{" + "quotedAt=" + quotedAt +
                ", base=" + base +
                ", quoted=" + quoted +
                ", price=" + price +
                ", sourceCode='" + sourceCode + '\'' +
                '}';
    }
}
