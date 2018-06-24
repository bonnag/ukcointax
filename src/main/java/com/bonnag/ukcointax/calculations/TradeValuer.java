package com.bonnag.ukcointax.calculations;

import com.bonnag.ukcointax.domain.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TradeValuer {
    private final DayMapper dayMapper;
    private final ExchangeRatePathChooser exchangeRatePathChooser;
    private final ExchangeRateHistories exchangeRateHistories;

    public TradeValuer(DayMapper dayMapper, List<ExchangeRate> exchangeRates) {
        this.dayMapper = dayMapper;
        this.exchangeRatePathChooser = new ExchangeRatePathChooser(exchangeRates);
        this.exchangeRateHistories = new ExchangeRateHistories(dayMapper, exchangeRates);
    }

    public List<ValuedTrade> appraise(List<Trade> trades) {
        return trades.stream().map(t -> appraise(t)).collect(Collectors.toList());
    }

    private ValuedTrade appraise(Trade trade) {
        LocalDate day = dayMapper.dayOfTrade(trade.getTradedAt());
        List<Asset> ratePath = exchangeRatePathChooser.chooseBestPathToSterlingFromAmong(trade.getAssetPair().asArray());
        if (ratePath.isEmpty()) {
            throw new IllegalArgumentException("unable to find exchange rates for trade " + trade);
        }
        List<ExchangeRate> exchangeRatesUsed = new ArrayList<>();
        AssetAmount workingAssetAmount = null;
        for (Asset asset : ratePath) {
            if (workingAssetAmount == null) {
                workingAssetAmount = trade.getAmount(asset);
            } else {
                ExchangeRate exchangeRate = exchangeRateHistories.lookup(workingAssetAmount.getAsset(), asset, day)
                        .orElseThrow(() -> new IllegalArgumentException("no exchange rate found for " + asset + " on " + day));
                exchangeRatesUsed.add(exchangeRate);
                workingAssetAmount = exchangeRate.convert(workingAssetAmount);
            }
        }
        return new ValuedTrade(trade, day, workingAssetAmount, exchangeRatesUsed);
    }
}
