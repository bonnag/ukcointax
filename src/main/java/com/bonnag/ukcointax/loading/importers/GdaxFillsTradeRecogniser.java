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

public class GdaxFillsTradeRecogniser implements CsvRecogniser {

    private static final String[] expectedHeaderFields = "trade id,product,side,created at,size,size unit,price,fee,total,price/fee/total unit".split(",");

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
            String theirTradeId = record[0];
            String theirProduct = record[1];
            String theirSide = record[2];
            TradeDirection tradeSide;
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
            Instant tradedAt = Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(record[3]));
            double size = Math.abs(Double.parseDouble(record[4]));
            String theirSizeUnit = record[5];
            double total = Math.abs(Double.parseDouble(record[8]));
            String theirTotalUnit = record[9];
            String[] theirAssetCodes = theirProduct.split("-", 2);
            String theirBaseAssetCode = theirAssetCodes[0];
            String theirQuotedAssetCode = theirAssetCodes[1];
            if (!theirBaseAssetCode.equals(theirSizeUnit)) {
                throw new IllegalArgumentException("assumed size unit would always be base ccy");
            }
            if (!theirQuotedAssetCode.equals(theirTotalUnit)) {
                throw new IllegalArgumentException("assumed total unit would always be quoted ccy");
            }
            String venue = "gdax";
            String tradeId = venue + "-" + theirProduct + "-" + theirTradeId;
            sink.addTrade(
                    new TradeId(venue, tradeId),
                    tradedAt,
                    tradeSide,
                    new AssetAmount(new Asset(theirBaseAssetCode), size),
                    new AssetAmount(new Asset(theirQuotedAssetCode), total)
            );
        }
    }
}
