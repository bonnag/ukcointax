package com.bonnag.ukcointax.loading.importers;

import com.bonnag.ukcointax.domain.Asset;
import com.bonnag.ukcointax.domain.AssetAmount;
import com.bonnag.ukcointax.domain.TradeDirection;
import com.bonnag.ukcointax.domain.TradeId;
import com.bonnag.ukcointax.loading.CsvDecoder;
import com.bonnag.ukcointax.loading.CsvRecogniser;
import com.bonnag.ukcointax.loading.DecoderSink;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.Optional;

public class BinanceTradeRecogniser implements CsvRecogniser {

    private static final String[] expectedHeaderFields = "Date(UTC),Market,Type,Price,Amount,Total,Fee,Fee Coin".split(",");

    @Override
    public Optional<CsvDecoder> maybeCreateDecoder(String filename, String[] headerFields) {
        if (!Arrays.equals(headerFields, expectedHeaderFields)) {
            return Optional.empty();
        }
        return Optional.of(new BinanceTradeDecoder());
    }

    private static class BinanceTradeDecoder implements CsvDecoder {
        @Override
        public void process(String[] record, DecoderSink sink) {
            // e.g.
            //Date(UTC),Market,Type,Price,Amount,Total,Fee,Fee Coin
            //2017-12-29 23:56:46,KNCETH,SELL,0.0032556,160,0.520896,0.0005209,ETH
            //2017-12-29 23:56:46,KNCETH,SELL,0.0033336,230,0.766728,0.00076673,ETH
            DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd HH:mm:ss")
                    .toFormatter().withZone(ZoneId.of("UTC"));
            String venue = "binance";
            String dateStr = record[0];
            Instant tradedAt = Instant.from(dateTimeFormatter.parse(dateStr));
            String theirPair = record[1];
            String assetBase;
            String assetQuoted;
            if (theirPair.endsWith("ETH")) {
                assetQuoted = "ETH";
            } else if (theirPair.endsWith("BTC")) {
                assetQuoted = "BTC";
            } else {
                throw new IllegalArgumentException("unexpected pair " + theirPair);
            }
            assetBase = theirPair.substring(0, theirPair.length() - assetQuoted.length());
            String theirSide = record[2];
            double grossAmountBase = Double.parseDouble(record[4]);
            double grossAmountQuoted = Double.parseDouble(record[5]);
            double feeAmount = Double.parseDouble(record[6]);
            String feeTheirAsset = record[7];
            TradeDirection tradeSide;
            double amountBase;
            double amountQuoted;
            switch (theirSide) {
                case "BUY":
                    tradeSide = TradeDirection.Buy;
                    break;
                case "SELL":
                    tradeSide = TradeDirection.Sell;
                    break;
                default:
                    throw new IllegalArgumentException("unknown trade side " + theirSide);
            }
            if (feeTheirAsset.equals(assetBase)) {
                amountBase = grossAmountBase - feeAmount;
                amountQuoted = grossAmountQuoted;
            } else if (feeTheirAsset.equals(assetQuoted)) {
                amountBase = grossAmountBase;
                amountQuoted = grossAmountQuoted - feeAmount;
            } else if (feeTheirAsset.equals("BNB")) {
                //System.err.println("warning - ignoring BNB fee");
                amountBase = grossAmountBase;
                amountQuoted = grossAmountQuoted;
            } else {
                throw new IllegalArgumentException("unexpected fee currency " + feeTheirAsset);
            }
            String tradeId = DigestUtils.md5Hex(String.join(",", record));
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
