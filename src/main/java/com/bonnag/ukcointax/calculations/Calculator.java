package com.bonnag.ukcointax.calculations;

import com.bonnag.ukcointax.calculations.*;
import com.bonnag.ukcointax.domain.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Calculator {
    public Calculated calculate(List<Trade> trades, List<ExchangeRate> exchangeRates) {
        DayMapper dayMapper = new DayMapper();
        Bounds bounds = new Bounds(trades, dayMapper);
        List<ValuedTrade> valuedTrades = new TradeValuer(dayMapper, exchangeRates).appraise(trades);
        DailyAcquisitionsAndDisposals dailyAcquisitionsAndDisposals = new DailyAcquisitionsAndDisposals(valuedTrades);
        Identifications identifications = new Identifications();
        HoldingPools holdingPools = new HoldingPools();
        IdentificationRule[] identificationRules = new IdentificationRule[]{
                new SameDayIdentificationRule(),
                new BedAndBreakfastIdentificationRule(),
                new Section104PoolIdentificationRule(holdingPools)
        };
        for (IdentificationRule identificationRule : identificationRules) {
            for (LocalDate day : bounds.getDaysForTaxYears()) {
                for (Asset asset : bounds.getShareLikeAssets()) {
                    AssetDay assetDay = new AssetDay(asset, day);
                    identificationRule.apply(assetDay, dailyAcquisitionsAndDisposals, identifications);
                }
            }
        }
        LinkedHashMap<TaxYear,TaxSummary> taxYearSummaries = new LinkedHashMap<>();
        for (TaxYear taxYear : bounds.getTaxYears()) {
            TaxSummary taxSummary = identifications.getIdentifiedDayDisposalsDuring(taxYear).stream().collect(
                    Collectors.collectingAndThen(Collectors.toList(), TaxSummary::fromIdentifiedDayDisposals));
            taxYearSummaries.put(taxYear, taxSummary);
        }
        return new Calculated(bounds, valuedTrades, dailyAcquisitionsAndDisposals, identifications, taxYearSummaries);
    }
}
