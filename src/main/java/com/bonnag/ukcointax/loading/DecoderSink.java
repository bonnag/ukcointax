package com.bonnag.ukcointax.loading;

import com.bonnag.ukcointax.domain.Asset;
import com.bonnag.ukcointax.domain.AssetAmount;
import com.bonnag.ukcointax.domain.TradeDirection;
import com.bonnag.ukcointax.domain.TradeId;

import java.time.Instant;

public interface DecoderSink {
    void addTrade(TradeId tradeId, Instant tradedAt, TradeDirection tradeDirection, AssetAmount base, AssetAmount quoted);
    void addExchangeRate(Instant quotedAt, Asset base, Asset quoted, double price, String sourceCode);
}
