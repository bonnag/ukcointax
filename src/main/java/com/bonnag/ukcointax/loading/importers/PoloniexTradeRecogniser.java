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
import java.util.Arrays;
import java.util.Optional;

public class PoloniexTradeRecogniser implements CsvRecogniser {

    private static final String[] expectedHeaderFields = "Date,Market,Category,Type,Price,Amount,Total,Fee,Order Number,Base Total Less Fee,Quote Total Less Fee".split(",");

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
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"));
            Instant tradedAt = Instant.from(dateTimeFormatter.parse(record[0]));
            String theirPair = record[1];
            String theirSide = record[3];
            TradeDirection tradeSide;
            switch (theirSide) {
                case "Buy":
                    tradeSide = TradeDirection.Buy;
                    break;
                case "Sell":
                    tradeSide = TradeDirection.Sell;
                    break;
                default:
                    throw new IllegalArgumentException("unknown trade side " + theirSide);
            }
            // Yes, poloniex's terminology does seem to be totally backwards ???
            double amountBase = Math.abs(Double.parseDouble(record[10]));
            double amountQuoted = Math.abs(Double.parseDouble(record[9]));
            String[] theirAssetCodes = theirPair.split("/", 2);
            String theirBaseAssetCode = theirAssetCodes[0];
            String theirQuotedAssetCode = theirAssetCodes[1];
            String venue = "poloniex";
            String tradeId = DigestUtils.md5Hex(String.join(",", record));
            sink.addTrade(
                    new TradeId(venue, tradeId),
                    tradedAt,
                    tradeSide,
                    new AssetAmount(new Asset(theirBaseAssetCode), amountBase),
                    new AssetAmount(new Asset(theirQuotedAssetCode), amountQuoted)
            );
        }
    }
}
