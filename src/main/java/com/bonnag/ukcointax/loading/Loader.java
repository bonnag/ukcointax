package com.bonnag.ukcointax.loading;

import com.bonnag.ukcointax.domain.Loaded;

import java.io.IOException;
import java.nio.file.Path;

public class Loader {
    public Loaded load(Path inputDirectory) throws IOException {
        return new Loaded(
                new NativeTradeReader().read(inputDirectory),
                new NativeExchangeRateReader().read(inputDirectory));
    }
}
