package com.bonnag.ukcointax;

import com.bonnag.ukcointax.calculating.Calculator;
import com.bonnag.ukcointax.domain.Calculated;
import com.bonnag.ukcointax.domain.Loaded;
import com.bonnag.ukcointax.domain.Trade;
import com.bonnag.ukcointax.loading.Loader;
import com.bonnag.ukcointax.presenting.ReportsWriter;
import com.google.devtools.common.options.OptionsParser;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class App
{
    public static void main(String[] args) throws IOException {
        OptionsParser parser = OptionsParser.newOptionsParser(CommandLineOptions.class);
        parser.parseAndExitUponError(args);
        CommandLineOptions options = parser.getOptions(CommandLineOptions.class);
        if (options.input.isEmpty() || options.output.isEmpty()) {
            printUsage(parser);
            return;
        }
        Loaded loaded = new Loader().load(Paths.get(options.input));
        List<Trade> validTrades = loaded.getTrades().stream().filter(App::isValidTrade).collect(Collectors.toList());
        Calculated calculated = new Calculator().calculate(validTrades, loaded.getExchangeRates());
        new ReportsWriter().write(Paths.get(options.output), calculated);
    }

    private static boolean isValidTrade(Trade trade) {
        return trade.getBase().getAmount() > 1e-12 && trade.getQuoted().getAmount() > 1e-12;
    }

    private static void printUsage(OptionsParser parser) {
        System.out.println("Usage: java -jar ukcointax.jar OPTIONS");
        System.out.println(parser.describeOptions(Collections.emptyMap(), OptionsParser.HelpVerbosity.LONG));
    }
}
