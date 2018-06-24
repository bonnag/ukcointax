package com.bonnag.ukcointax.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ValuedTrade {
    private final Trade trade;
    private final LocalDate day;
    private final AssetAmount value;
    private final List<ExchangeRate> exchangeRatesUsed;

    public ValuedTrade(Trade trade, LocalDate day, AssetAmount value, List<ExchangeRate> exchangeRatesUsed) {
        if (!value.getAsset().isSterling()) {
            throw new IllegalArgumentException("trades must be valued in sterling, not " + value.getAsset());
        }
        this.trade = trade;
        this.day = day;
        this.value = value;
        this.exchangeRatesUsed = Collections.unmodifiableList(new ArrayList<>(exchangeRatesUsed));
    }

    public Trade getTrade() {
        return trade;
    }

    public LocalDate getDay() {
        return day;
    }

    public AssetAmount getValue() {
        return value;
    }

    public List<ExchangeRate> getExchangeRatesUsed() {
        return exchangeRatesUsed;
    }

    public AssetDay getBoughtAssetDay() {
        return new AssetDay(trade.getBought().getAsset(), day);
    }

    public AssetDay getSoldAssetDay() {
        return new AssetDay(trade.getSold().getAsset(), day);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValuedTrade that = (ValuedTrade) o;
        return Objects.equals(trade, that.trade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trade);
    }

    @Override
    public String toString() {
        return "ValuedTrade{" + "trade=" + trade +
                ", day=" + day +
                ", value=" + value +
                ", exchangeRatesUsed=" + exchangeRatesUsed +
                '}';
    }
}
