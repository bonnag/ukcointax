package com.bonnag.ukcointax.loading.importers;

import com.bonnag.ukcointax.domain.Asset;
import com.bonnag.ukcointax.domain.AssetAmount;
import com.bonnag.ukcointax.domain.TradeDirection;
import com.bonnag.ukcointax.domain.TradeId;
import com.bonnag.ukcointax.loading.CsvDecoder;
import com.bonnag.ukcointax.loading.CsvRecogniser;
import com.bonnag.ukcointax.loading.DecoderSink;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

public class NativeTradeRecogniser implements CsvRecogniser {

    private static final String[] expectedHeaderFields = new String[]{
            "TradedAt", // 0
            "TradeSide", // 1
            "AssetBase", // 2
            "AmountBase", // 3
            "AssetQuoted", // 4
            "AmountQuoted", // 5
            "Venue",  // 6
            "TradeId" // 7
    };

    @Override
    public Optional<CsvDecoder> maybeCreateDecoder(String filename, String[] headerFields) {
        if (!Arrays.equals(headerFields, expectedHeaderFields)) {
            return Optional.empty();
        }
        return Optional.of(new NativeTradeDecoder());
    }

    private static class NativeTradeDecoder implements CsvDecoder {
        @Override
        public void process(String[] record, DecoderSink sink) {
            sink.addTrade(
                    new TradeId(record[6], record[7]),
                    Instant.parse(record[0]),
                    TradeDirection.valueOf(record[1]),
                    new AssetAmount(new Asset(record[2]), Double.parseDouble(record[3])),
                    new AssetAmount(new Asset(record[4]), Double.parseDouble(record[5]))
            );
        }
    }
}
