package com.bonnag.ukcointax.domain;

import com.bonnag.ukcointax.domain.ExchangeRate;
import com.bonnag.ukcointax.domain.Trade;

import java.util.List;

public class Loaded {
    private final List<Trade> trades;
    private final List<ExchangeRate> exchangeRates;

    public Loaded(List<Trade> trades, List<ExchangeRate> exchangeRates) {
        this.trades = trades;
        this.exchangeRates = exchangeRates;
    }

    public List<Trade> getTrades() {
        return trades;
    }

    public List<ExchangeRate> getExchangeRates() {
        return exchangeRates;
    }

    @Override
    public String toString() {
        return "Loaded{" + "numberOfTrades=" + trades.size() +
                ", numberOfExchangeRates=" + exchangeRates.size() +
                '}';
    }
}
