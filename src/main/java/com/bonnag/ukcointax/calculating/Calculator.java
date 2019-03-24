package com.bonnag.ukcointax.calculating;

import com.bonnag.ukcointax.domain.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Calculator {
    public Calculated calculate(List<Trade> trades, List<ExchangeRate> exchangeRates) {
        Collections.sort(trades, Comparator.comparing(t -> t.getTradedAt()));
        Collections.sort(exchangeRates, Comparator.comparing(t -> t.getQuotedAt()));
        DayMapper dayMapper = new DayMapper();
        Bounds bounds = new Bounds(trades, dayMapper);
        InferredBalancesHistory inferredBalancesHistory = new InferredBalancesHistory(trades, bounds, dayMapper);
        List<ValuedTrade> valuedTrades = new TradeValuer(dayMapper, exchangeRates).appraise(trades);
        DailyAcquisitionsAndDisposals dailyAcquisitionsAndDisposals = new DailyAcquisitionsAndDisposals(valuedTrades);
        Identifications identifications = new Identifications();
        HoldingPools holdingPools = new HoldingPools();
        IdentificationRule[] identificationRules = new IdentificationRule[]{
                new SameDayIdentificationRule(),
                new BedAndBreakfastIdentificationRule(),
                new Section104PoolIdentificationRule(holdingPools),
                new ValidatingIdentificationRule()
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
        /*
        TaxSummary overallTaxSummary = taxYearSummaries.get(new TaxYear("2018/19"));
        System.out.println("Overall " + overallTaxSummary + ":");
        System.out.println("totalNumDisposals=" + overallTaxSummary.getNumberOfDisposals());
        System.out.println("totalProceeds=" + overallTaxSummary.getDisposalProceeds().getAmountAsString());
        System.out.println("totalCosts=" + overallTaxSummary.getAllowableCosts().getAmountAsString());
        System.out.println("totalGains=" + overallTaxSummary.getGainsBeforeLosses().getAmountAsString());
        System.out.println("totalLosses=" + overallTaxSummary.getLosses().getAmountAsString());
        System.out.println("netGains=" + (overallTaxSummary.getGainsBeforeLosses().subtract(overallTaxSummary.getLosses())).getAmountAsString());
        */
        return new Calculated(bounds, valuedTrades, dailyAcquisitionsAndDisposals, identifications, taxYearSummaries, inferredBalancesHistory);
    }
}
