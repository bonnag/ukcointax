package com.bonnag.ukcointax.loading.importers;

import com.bonnag.ukcointax.domain.Asset;
import com.bonnag.ukcointax.loading.CsvDecoder;
import com.bonnag.ukcointax.loading.CsvRecogniser;
import com.bonnag.ukcointax.loading.DecoderSink;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

public class NativeExchangeRateRecogniser implements CsvRecogniser {

    private static final String[] expectedHeaderFields = new String[]{
            "QuotedAt",
            "AssetBase",
            "AssetQuoted",
            "Price"
    };

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
            sink.addExchangeRate(
                    Instant.parse(record[0]),
                    new Asset(record[1]),
                    new Asset(record[2]),
                    Double.parseDouble(record[3]),
                    "unknown"
            );
        }
    }
}
