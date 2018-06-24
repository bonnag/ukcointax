package com.bonnag.ukcointax.calculations;

import com.bonnag.ukcointax.domain.Asset;
import com.bonnag.ukcointax.domain.ExchangeRate;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExchangeRateHistories {
    private final DayMapper dayMapper;
    private final Map<AssetPair, ExchangeRateHistory> exchangeRateHistoryByPair;

    public ExchangeRateHistories(DayMapper dayMapper, List<ExchangeRate> exchangeRates) {
        this.dayMapper = dayMapper;
        this.exchangeRateHistoryByPair = exchangeRates.stream()
                .collect(Collectors.groupingBy(ExchangeRate::getAssetPair,
                        Collectors.collectingAndThen(Collectors.toList(),
                                ers -> new ExchangeRateHistory(ers))));
    }

    public Optional<ExchangeRate> lookup(Asset assetBase, Asset assetQuoted, LocalDate day) {
        AssetPair assetPair = new AssetPair(assetBase, assetQuoted);
        Instant quoteCutoff = dayMapper.quoteCutoff(day);
        Optional<ExchangeRate> firstAttempt = lookup(assetPair, quoteCutoff);
        if (firstAttempt.isPresent()) {
            return firstAttempt;
        }
        return lookupInverse(assetPair, quoteCutoff);
    }

    private Optional<ExchangeRate> lookup(AssetPair assetPair, Instant quoteCutoff) {
        return Optional.ofNullable(exchangeRateHistoryByPair.get(assetPair))
                .flatMap(erh -> erh.lookup(assetPair, quoteCutoff));
    }

    private Optional<ExchangeRate> lookupInverse(AssetPair assetPair, Instant quoteCutoff) {
        return lookup(assetPair.inverted(), quoteCutoff).map(ExchangeRate::inverted);
    }
}
