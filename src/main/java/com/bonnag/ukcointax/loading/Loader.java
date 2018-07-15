package com.bonnag.ukcointax.loading;

import com.bonnag.ukcointax.domain.*;
import com.bonnag.ukcointax.loading.importers.*;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.ICSVParser;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Loader {
    private final CsvRecogniser[] recognisers;

    public Loader() {
        recognisers = new CsvRecogniser[] {
                new BinanceTradeRecogniser(),
                new BittrexTradeRecogniser(),
                new CoinGeckoExchangeRateRecogniser(),
                new DeltaBalancesTradeRecogniser(),
                new GdaxFillsTradeRecogniser(),
                new HitbtcTradeRecogniser(),
                new KvotheTradeRecogniser(),
                new LiquiTradeRecogniser(),
                new NativeExchangeRateRecogniser(),
                new NativeTradeRecogniser(),
                new PoloniexTradeRecogniser(),
                new QuandlEcbEurGbpExchangeRateRecogniser(),
        };
    }

    public Loaded load(Path inputDirectory) {
        LoaderSink sink = new LoaderSink();
        try (Stream<Path> paths = Files.walk(inputDirectory)) {
            paths
                    .filter(p -> p.getFileName().toString().endsWith(".csv"))
                    .forEach(p -> loadCsvFile(p.toFile(), sink));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sink.getResult();
    }

    private void loadCsvFile(File csvFile, LoaderSink sink) {
        char csvSeparator = sniffCsvSeparator(csvFile);
        ICSVParser csvParser = new CSVParserBuilder().withSeparator(csvSeparator).build();
        String baseFilename = FilenameUtils.removeExtension(csvFile.getName());
        try (
                FileInputStream inputStream = new FileInputStream(csvFile);
                InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
                CSVReader csvReader = new CSVReaderBuilder(streamReader).withCSVParser(csvParser).build()
        ) {
            String[] headerFields = csvReader.readNext();
            List<CsvDecoder> decoders =
                    Arrays.stream(recognisers)
                            .map(r -> r.maybeCreateDecoder(baseFilename, headerFields))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.toList());
            if (decoders.isEmpty()) {
                throw new Exception("no decoder found");
            } else if (decoders.size() > 1) {
                throw new Exception("unsure which decoder to use");
            } else {
                CsvDecoder decoder = decoders.get(0);
                for (String[] record : csvReader) {
                    decoder.process(record, sink);
                }
            }
        } catch (Exception e) {
            // TODO - handle properly
            System.err.println("Error on " + csvFile);
            e.printStackTrace();
        }
    }

    private char sniffCsvSeparator(File csvFile) {
        try (
                FileInputStream inputStream = new FileInputStream(csvFile);
                InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
                BufferedReader bufferedReader = new BufferedReader(streamReader)
        ) {
            String firstLine = bufferedReader.readLine();
            if (firstLine.contains("\t")) {
                return '\t';
            }
        } catch (Exception e) {
            // TODO - handle properly
            System.err.println("Error sniffing separator in " + csvFile);
            e.printStackTrace();
        }
        return ',';
    }

    private static class LoaderSink implements DecoderSink {

        private final ArrayList<Trade> trades;
        private final ArrayList<ExchangeRate> exchangeRates;

        public LoaderSink() {
            trades = new ArrayList<>();
            exchangeRates = new ArrayList<>();
        }

        @Override
        public void addTrade(TradeId tradeId, Instant tradedAt, TradeDirection tradeDirection, AssetAmount base, AssetAmount quoted) {
            trades.add(new Trade(tradeId, tradedAt, tradeDirection, base, quoted));
        }

        @Override
        public void addExchangeRate(Instant quotedAt, Asset base, Asset quoted, double price, String sourceCode) {
            exchangeRates.add(new ExchangeRate(quotedAt, base, quoted, price, sourceCode));
        }

        public Loaded getResult() {
            return new Loaded(trades, exchangeRates);
        }
    }
}
