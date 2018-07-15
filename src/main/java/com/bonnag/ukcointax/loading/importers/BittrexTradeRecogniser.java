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

public class BittrexTradeRecogniser implements CsvRecogniser {

    private static final String[] expectedHeaderFields = "OrderUuid,Exchange,Type,Quantity,Limit,CommissionPaid,Price,Opened,Closed".split(",");

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
            // Note that the column headings are really misleading!
            // e.g.
            // OrderUuid,Exchange,Type,Quantity,Limit,CommissionPaid,Price,Opened,Closed
            // 279cb651-6609-4459-84ae-6b0723711283,BTC-ETH,LIMIT_SELL,0.10000000,0.01198942,0.00000299,0.00119894,1/15/2017 8:01:23 PM,1/15/2017 8:02:00 PM
            // 141148df-af18-4f7d-908d-ea6c1c61c5cf,BTC-ETH,LIMIT_BUY,0.10000000,0.01198942,0.00000299,0.00119894,1/15/2017 8:01:29 PM,1/15/2017 8:01:29 PM
            // TODO - what zone are these?
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a").withZone(ZoneId.of("UTC"));
            String pair = record[1];
            // backwards - e.g. BTC-ETH
            String assetQuoted = pair.split("-", 2)[0];
            String assetBase = pair.split("-", 2)[1];
            String theirSide = record[2];
            double amountBase = Double.parseDouble(record[3]);
            double feeAmountQuoted = Double.parseDouble(record[5]);
            double grossAmountQuoted = Double.parseDouble(record[6]);
            Instant tradedAt = Instant.from(dateTimeFormatter.parse(record[8]));
            TradeDirection tradeSide;
            double amountQuoted;
            switch (theirSide) {
                case "LIMIT_BUY":
                    tradeSide = TradeDirection.Buy;
                    amountQuoted = grossAmountQuoted + feeAmountQuoted;
                    break;
                case "LIMIT_SELL":
                    tradeSide = TradeDirection.Sell;
                    amountQuoted = grossAmountQuoted - feeAmountQuoted;
                    break;
                default:
                    throw new IllegalArgumentException("unknown trade side " + theirSide);
            }
            String venue = "bittrex";
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
