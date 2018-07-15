package com.bonnag.ukcointax.loading.importers;

import com.bonnag.ukcointax.domain.Asset;
import com.bonnag.ukcointax.domain.AssetAmount;
import com.bonnag.ukcointax.domain.TradeDirection;
import com.bonnag.ukcointax.domain.TradeId;
import com.bonnag.ukcointax.loading.CsvDecoder;
import com.bonnag.ukcointax.loading.CsvRecogniser;
import com.bonnag.ukcointax.loading.DecoderSink;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Optional;

public class KvotheTradeRecogniser implements CsvRecogniser {

    private static final String[] expectedHeaderFields = "exchange_id,ccy_base,ccy_quoted,exchange_trade_id,exchange_order_id,traded_at,bid_or_ask,trade_amount_base,trade_amount_quoted,trade_price,fee_amount_base,fee_amount_quoted".split(",");

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
            // exchange_id,ccy_base,ccy_quoted,exchange_trade_id,exchange_order_id,traded_at,bid_or_ask,trade_amount_base,trade_amount_quoted,trade_price,fee_amount_base,fee_amount_quoted
            // Yobit,ETH,BTC,46755568,14000175002173,2016-12-29 10:28:41+00,Bid,0.020000000000,0.000171636800,0.008581840000,0.000000000000,0.000000343274
            // Yobit,ETH,BTC,47159926,24000175610034,2016-12-30 19:39:29+00,Ask,0.020000000000,0.000170370400,0.008518520000,0.000000000000,0.000000340741
            DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd HH:mm:ss")
                    .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true)
                    .appendPattern("x")
                    .toFormatter();
            String venue = record[0].toLowerCase();
            String assetBase = record[1];
            String assetQuoted = record[2];
            String tradeId = venue + "-" + record[3];
            String dateStr = record[5];
            Instant tradedAt = Instant.from(dateTimeFormatter.parse(dateStr));
            String theirSide = record[6];
            double grossAmountBase = Double.parseDouble(record[7]);
            double grossAmountQuoted = Double.parseDouble(record[8]);
            double feeAmountBase = Double.parseDouble(record[10]);
            double feeAmountQuoted = Double.parseDouble(record[11]);
            TradeDirection tradeSide;
            double amountBase;
            double amountQuoted;
            switch (theirSide) {
                case "Bid":
                    tradeSide = TradeDirection.Buy;
                    amountBase = grossAmountBase - feeAmountBase;
                    amountQuoted = grossAmountQuoted + feeAmountQuoted;
                    break;
                case "Ask":
                    tradeSide = TradeDirection.Sell;
                    amountBase = grossAmountBase + feeAmountBase;
                    amountQuoted = grossAmountQuoted - feeAmountQuoted;
                    break;
                default:
                    throw new IllegalArgumentException("unknown trade side " + theirSide);
            }
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
