package com.bonnag.ukcointax.loading;

import com.bonnag.ukcointax.domain.*;

import java.time.Instant;

public class NativeTradeReader extends GeneralItemReader<Trade> {

    @Override
    protected String[] getColumnHeadings() {
        return new String[] {
                "TradedAt", // 0
                "TradeSide", // 1
                "AssetBase", // 2
                "AmountBase", // 3
                "AssetQuoted", // 4
                "AmountQuoted", // 5
                "Venue",  // 6
                "TradeId" // 7
        };
    }

    @Override
    protected Trade parse(String[] record, String baseFilename) throws IllegalArgumentException {
        return new Trade(
                new TradeId(record[6], record[7]),
                Instant.parse(record[0]),
                TradeDirection.valueOf(record[1]),
                new AssetAmount(new Asset(record[2]), Double.parseDouble(record[3])),
                new AssetAmount(new Asset(record[4]), Double.parseDouble(record[5]))
        );
    }
}
