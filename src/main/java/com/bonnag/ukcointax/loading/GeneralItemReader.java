package com.bonnag.ukcointax.loading;

import com.opencsv.*;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class GeneralItemReader<T> {

    public List<T> read(Path directory) throws IOException {
        List<T> allItems = new ArrayList<>();
        try (
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory, "*.csv")
        ) {
            for (Path entry : directoryStream) {
                File file = entry.toFile();
                String baseFilename = FilenameUtils.removeExtension(file.getName());
                try (
                    InputStream fileStream = new FileInputStream(file)
                ) {
                    allItems.addAll(read(fileStream, baseFilename));
                }
            }
        }
        return allItems;
    }

    public List<T> read(InputStream inputStream, String baseFilename) throws IOException, IllegalArgumentException {
        try (
            InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
            CSVReader csvReader = new CSVReaderBuilder(streamReader).withCSVParser(getCsvParser()).build()
        ) {
            if (!validateHeader(csvReader, baseFilename)) {
                return Collections.emptyList();
            }
            List<T> items = new ArrayList<>();
            for (String[] record : csvReader) {
                try {
                    items.add(parse(record, baseFilename));
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                            "failed to parse near line " + (csvReader.getLinesRead() - 1) +
                            " of " + baseFilename +
                            "due to " + e.getMessage(),
                            e);
                }
            }
            return items;
        }
    }

    private boolean validateHeader(CSVReader csvReader, String baseFilename) throws IOException {
        final String[] expectedColumnHeadings = getColumnHeadings();
        String[] firstRecord = csvReader.readNext();
        return Arrays.deepEquals(expectedColumnHeadings, firstRecord);
    }

    private ICSVParser getCsvParser() {
         return new CSVParserBuilder().withSeparator(getCsvSeparator()).build();
    }

    protected char getCsvSeparator() {
        return ',';
    }

    protected abstract String[] getColumnHeadings();
    protected abstract T parse(String[] record, String baseFilename) throws IllegalArgumentException;
}
