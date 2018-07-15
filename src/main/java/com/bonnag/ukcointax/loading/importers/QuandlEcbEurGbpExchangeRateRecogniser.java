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

public class QuandlEcbEurGbpExchangeRateRecogniser implements CsvRecogniser {

    private static final String[] expectedHeaderFields = new String[]{
            "Date",
            "Value"
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
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.of("CET"));
            sink.addExchangeRate(
                    Instant.from(dateTimeFormatter.parse(record[0] + "T15:00:00")),
                    new Asset("EUR"),
                    new Asset("GBP"),
                    Double.parseDouble(record[1]),
                    "QuandlEcb"
            );
        }
    }
}
