package com.bonnag.ukcointax.loading.importers;

import com.bonnag.ukcointax.domain.Asset;
import com.bonnag.ukcointax.domain.AssetAmount;
import com.bonnag.ukcointax.domain.TradeDirection;
import com.bonnag.ukcointax.domain.TradeId;
import com.bonnag.ukcointax.loading.CsvDecoder;
import com.bonnag.ukcointax.loading.CsvRecogniser;
import com.bonnag.ukcointax.loading.DecoderSink;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Optional;

public class LiquiTradeRecogniser implements CsvRecogniser {

    private static final String[] expectedHeaderFields = "timestamp,pair,type,amount,rate,order_id,trade_id".split(",");

    @Override
    public Optional<CsvDecoder> maybeCreateDecoder(String filename, String[] headerFields) {
        if (!Arrays.equals(headerFields, expectedHeaderFields)) {
            return Optional.empty();
        }
        return Optional.of(new Decoder());
    }

    private static class Decoder implements CsvDecoder {
        @Override
        public void process(String[] record, DecoderSink sink) {
            // e.g.
            // timestamp,pair,type,amount,rate,order_id,trade_id
            // 1515365336,eos_eth,buy,106,0.0093,190710604,58696264
            // 1515365795,eos_eth,sell,29.74878769,0.009325,190741845,58699085
            String venue = "liqui";
            String dateStr = record[0];
            Instant tradedAt = Instant.ofEpochSecond(Long.parseLong(dateStr));
            String[] theirPair = record[1].split("_", 2);
            String assetBase = theirPair[0].toUpperCase();
            String assetQuoted = theirPair[1].toUpperCase();
            String theirSide = record[2];
            double amountBase = Double.parseDouble(record[3]);
            double rate = Double.parseDouble(record[4]);
            double amountQuoted = amountBase * rate;
            String tradeId = record[6];
            TradeDirection tradeSide;
            switch (theirSide) {
                case "buy":
                    tradeSide = TradeDirection.Buy;
                    break;
                case "sell":
                    tradeSide = TradeDirection.Sell;
                    break;
                default:
                    throw new IllegalArgumentException("unknown trade side " + theirSide);
            }
            //return new Trade(tradedAt, tradeSide, assetBase, amountBase, assetQuoted, amountQuoted, venue, tradeId);
            sink.addTrade(
                    new TradeId(venue, tradeId),
                    tradedAt,
                    tradeSide,
                    new AssetAmount(new Asset(assetBase), amountBase),
                    new AssetAmount(new Asset(assetQuoted), amountQuoted)
            );
        }
    }
}
