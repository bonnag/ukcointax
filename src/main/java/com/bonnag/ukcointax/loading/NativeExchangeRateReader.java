package com.bonnag.ukcointax.loading;

import com.bonnag.ukcointax.domain.Asset;
import com.bonnag.ukcointax.domain.ExchangeRate;

import java.time.Instant;

public class NativeExchangeRateReader extends GeneralItemReader<ExchangeRate> {

    @Override
    protected String[] getColumnHeadings() {
        return new String[] {
                "QuotedAt",
                "AssetBase",
                "AssetQuoted",
                "Price"
        };
    }

    @Override
    protected ExchangeRate parse(String[] record, String baseFilename) throws IllegalArgumentException {
        return new ExchangeRate(
            Instant.parse(record[0]),
            new Asset(record[1]),
            new Asset(record[2]),
            Double.parseDouble(record[3]),
                "unknown"
        );
    }
}
