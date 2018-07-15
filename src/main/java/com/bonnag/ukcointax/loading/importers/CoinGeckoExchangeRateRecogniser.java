package com.bonnag.ukcointax.loading.importers;

import com.bonnag.ukcointax.domain.Asset;
import com.bonnag.ukcointax.loading.CsvDecoder;
import com.bonnag.ukcointax.loading.CsvRecogniser;
import com.bonnag.ukcointax.loading.DecoderSink;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

public class CoinGeckoExchangeRateRecogniser implements CsvRecogniser {

    private static final String[] expectedHeaderFields = new String[] {
            "snapped_at",
            "price",
            "market_cap",
            "total_volume"
    };

    @Override
    public Optional<CsvDecoder> maybeCreateDecoder(String filename, String[] headerFields) {
        if (!Arrays.equals(headerFields, expectedHeaderFields)) {
            return Optional.empty();
        }
        String[] parts = filename.split("-", 4);
        if (parts.length != 3) {
            throw new IllegalArgumentException("expected baseFile '" + filename + "' to contain asset names");
        }
        return Optional.of(new Decoder(decodeBaseAsset(parts[0]), decodeQuotedAsset(parts[1])));
    }

    private Asset decodeBaseAsset(String foreignAssetCode) {
        if (foreignAssetCode.equals("eth")) {
            return new Asset("ETH");
        }
        if (foreignAssetCode.equals("btc")) {
            return new Asset("BTC");
        }
        throw new IllegalArgumentException("unknown base asset " + foreignAssetCode);
    }

    @SuppressWarnings("SameReturnValue")
    private Asset decodeQuotedAsset(String foreignAssetCode) {
        if (foreignAssetCode.equals("gbp")) {
            return new Asset("GBP");
        }
        throw new IllegalArgumentException("unknown base asset " + foreignAssetCode);
    }

    private static class Decoder implements CsvDecoder {
        private final Asset baseAsset;
        private final Asset quotedAsset;
        public Decoder(Asset baseAsset, Asset quotedAsset) {
            this.baseAsset = baseAsset;
            this.quotedAsset = quotedAsset;
        }

        @Override
        public void process(String[] record, DecoderSink sink) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
            sink.addExchangeRate(
                    Instant.from(dateTimeFormatter.parse(record[0])),
                    baseAsset,
                    quotedAsset,
                    Double.parseDouble(record[1]),
                    "coingecko"
            );
        }
    }
}
