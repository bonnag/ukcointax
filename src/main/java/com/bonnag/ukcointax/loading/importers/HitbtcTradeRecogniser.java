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

public class HitbtcTradeRecogniser implements CsvRecogniser {

    private static final String[] expectedHeaderFields = "Date (UTC),Instrument,Trade ID,Order ID,Side,Quantity,Price,Volume,Fee,Rebate,Total".split(",");

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
            // "Date (UTC)","Instrument","Trade ID","Order ID","Side","Quantity","Price","Volume","Fee","Rebate","Total"
            // "2018-03-09 23:51:14","ETH/BTC","222490402","20670049603","sell","0.050","0.078336","0.003916800","0.000000000","0.000000391","0.003917191"
            // "2018-03-09 23:51:11","ETH/BTC","222490354","20670049449","buy","0.050","0.078340","0.003917000","0.000000000","0.000000391","-0.003916609"
            DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd H:mm:ss")
                    .toFormatter().withZone(ZoneId.of("UTC"));
            String venue = "hitbtc";
            String dateStr = record[0];
            Instant tradedAt = Instant.from(dateTimeFormatter.parse(dateStr));
            String[] theirPair = record[1].split("/", 2);
            String assetBase = theirPair[0];
            String assetQuoted = theirPair[1];
            String tradeId = venue + "-" + record[2];
            String theirSide = record[4];
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
            double amountBase = Math.abs(Double.parseDouble(record[5]));
            double amountQuoted = Math.abs(Double.parseDouble(record[10]));
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
