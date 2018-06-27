package com.bonnag.ukcointax.presenting;

import com.bonnag.ukcointax.domain.ExchangeRate;
import com.bonnag.ukcointax.domain.Trade;
import com.bonnag.ukcointax.domain.ValuedTrade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValuedTradeFormatter implements ItemFormatter<ValuedTrade> {
    @Override
    public String[] getHeader() {
        return new String[] {
                "TradedAt",
                "TradeDirection",
                "AssetBase",
                "AmountBase",
                "AssetQuoted",
                "AmountQuoted",
                "Venue",
                "TradeId",
                "SterlingValue",
                "Day",
                "Rate1Pair",
                "Rate1Price",
                "Rate1QuotedAt",
                "Rate1Source",
                "Rate2Pair",
                "Rate2Price",
                "Rate2QuotedAt",
                "Rate2Source",
        };
    }

    @Override
    public String[] format(ValuedTrade item) {
        Trade trade = item.getTrade();
        List<String> fields = new ArrayList<String>(Arrays.asList(new String[] {
                trade.getTradedAt().toString(),
                trade.getTradeDirection().toString(),
                trade.getBase().getAsset().toString(),
                trade.getBase().getAmountAsString(),
                trade.getQuoted().getAsset().toString(),
                trade.getQuoted().getAmountAsString(),
                trade.getTradeId().getVenueCode(),
                trade.getTradeId().getId(),
                item.getValue().getAmountAsString(),
                item.getDay().toString(),
        }));
        List<ExchangeRate> exchangeRatesUsed = item.getExchangeRatesUsed();
        for (int i = 0; i < 2; i++) {
            if (i < exchangeRatesUsed.size()) {
                ExchangeRate exchangeRate = exchangeRatesUsed.get(i);
                fields.add(exchangeRate.getAssetPair().toString());
                fields.add(exchangeRate.getPriceAsString());
                fields.add(exchangeRate.getQuotedAt().toString());
                fields.add(exchangeRate.getSourceCode());
            } else {
                fields.add("n/a");
                fields.add("n/a");
                fields.add("n/a");
                fields.add("n/a");
            }
        }
        return fields.toArray(new String[0]);
    }
}
