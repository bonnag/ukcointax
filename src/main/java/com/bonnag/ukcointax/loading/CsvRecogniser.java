package com.bonnag.ukcointax.loading;

import java.util.Optional;

public interface CsvRecogniser {
    Optional<CsvDecoder> maybeCreateDecoder(String baseFilename, String[] headerFields);
}
