package com.bonnag.ukcointax.domain;

import com.bonnag.ukcointax.calculations.AssetPair;

import java.time.Instant;
import java.util.Objects;

public class Trade {
    private final TradeId tradeId;
    private final Instant tradedAt;
    private final TradeDirection tradeDirection;
    private final AssetAmount base;
    private final AssetAmount quoted;

    public Trade(TradeId tradeId, Instant tradedAt, TradeDirection tradeDirection, AssetAmount base, AssetAmount quoted) {
        this.tradeId = tradeId;
        this.tradedAt = tradedAt;
        this.tradeDirection = tradeDirection;
        this.base = base;
        this.quoted = quoted;
    }

    public TradeId getTradeId() {
        return tradeId;
    }

    public Instant getTradedAt() {
        return tradedAt;
    }

    public TradeDirection getTradeDirection() {
        return tradeDirection;
    }

    public AssetAmount getBase() {
        return base;
    }

    public AssetAmount getQuoted() {
        return quoted;
    }

    public AssetAmount getBought() {
        switch (tradeDirection) {
            case Buy:
                return getBase();
            case Sell:
                return getQuoted();
            default:
                throw new IllegalArgumentException("unknown direction");
        }
    }

    public AssetAmount getSold() {
        switch (tradeDirection) {
            case Buy:
                return getQuoted();
            case Sell:
                return getBase();
            default:
                throw new IllegalArgumentException("unknown direction");
        }
    }

    public AssetPair getAssetPair() {
        return new AssetPair(getBase().getAsset(), getQuoted().getAsset());
    }

    public AssetAmount getAmount(Asset asset) {
        if (asset.equals(getBase().getAsset())) {
            return getBase();
        } else if (asset.equals(getQuoted().getAsset())) {
            return getQuoted();
        } else {
            throw new IllegalArgumentException("trade " + this + " does not have " + asset + " as base or quoted");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trade trade = (Trade) o;
        return Objects.equals(tradeId, trade.tradeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradeId);
    }

    @Override
    public String toString() {
        return "Trade{" + "tradeId=" + tradeId +
                ", tradedAt=" + tradedAt +
                ", tradeDirection=" + tradeDirection +
                ", base=" + base +
                ", quoted=" + quoted +
                '}';
    }
}
