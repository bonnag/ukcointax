package com.bonnag.ukcointax.calculating;

import com.bonnag.ukcointax.domain.ExchangeRate;

import java.time.Instant;
import java.util.*;

public class ExchangeRateHistory {
    private final List<ExchangeRate> sortedExchangeRates;
    private final Comparator<ExchangeRate> quotedAtComparator = Comparator.comparing(ExchangeRate::getQuotedAt);

    public ExchangeRateHistory(List<ExchangeRate> exchangeRates) {
        if (exchangeRates.isEmpty()) {
            throw new IllegalArgumentException("must be at least one exchange rate");
        }
        if (exchangeRates.stream().map(ExchangeRate::getAssetPair).distinct().count() > 1) {
            throw new IllegalArgumentException("all exchange rates must be on same pair");
        }
        this.sortedExchangeRates = new ArrayList<>(exchangeRates);
        sortedExchangeRates.sort(quotedAtComparator);
    }

    public Optional<ExchangeRate> lookup(AssetPair assetPair, Instant quoteCutoff) {
        if (sortedExchangeRates.isEmpty()) {
            return Optional.empty();
        }
        ExchangeRate exampleRate = sortedExchangeRates.get(0);
        if (!exampleRate.getAssetPair().equals(assetPair)) {
            throw new IllegalArgumentException("incorrect pair " + assetPair);
        }
        // Construct a fictitious exchange rate with the quotedAt = quoteCutoff so
        // we can ask where in the sorted list of exchange rates it should appear.
        ExchangeRate fictitiousRate = new ExchangeRate(
                quoteCutoff, exampleRate.getBase(), exampleRate.getQuoted(),
                exampleRate.getPrice(), "fictitious");
        int result = Collections.binarySearch(sortedExchangeRates, fictitiousRate, quotedAtComparator);
        if (result >= 0) {
            // There really is an exchange rate with exactly the same cut-off.
            return Optional.of(sortedExchangeRates.get(result));
        } else {
            // There isn't an exact match, but we know where our fictitious rate
            // would be inserted - albeit in a rather confusing way:
            //     result == (-(insertion point) - 1)
            // ==> result + 1 = (-(insertion point))
            // ==> -(result + 1) = insertion point
            int insertionPoint = -(result + 1);
            if (insertionPoint == 0) {
                // If our fictitious one belongs at the start, that's no good - we
                // want an earlier rate.
                return Optional.empty();
            }
            int previousPoint = insertionPoint - 1;
            return Optional.of(sortedExchangeRates.get(previousPoint));
        }
    }
}
