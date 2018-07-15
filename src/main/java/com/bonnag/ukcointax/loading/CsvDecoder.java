package com.bonnag.ukcointax.loading;

public interface CsvDecoder {
    void process(String[] record, DecoderSink sink);
}
