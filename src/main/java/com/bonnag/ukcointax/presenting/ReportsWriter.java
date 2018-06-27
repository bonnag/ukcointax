package com.bonnag.ukcointax.presenting;

import com.bonnag.ukcointax.domain.Calculated;
import com.bonnag.ukcointax.domain.IdentifiedDayDisposal;
import com.bonnag.ukcointax.domain.Trade;
import com.bonnag.ukcointax.domain.ValuedTrade;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class ReportsWriter {
    public void write(Path outputDirectory, Calculated calculated) throws IOException {
        writeItems(outputDirectory, "valued_trades", new ValuedTradeFormatter(), calculated.getValuedTrades());
/*
    public DailyAcquisitionsAndDisposals getDailyAcquisitionsAndDisposals() {
        return dailyAcquisitionsAndDisposals;
    }

    public Identifications getIdentifications() {
        return identifications;
    }


 */
        writeItems(outputDirectory, "identifications", new IdentificationFormatter(), calculated.getIdentifications().getIdentifications());
        writeItems(outputDirectory, "identified_day_disposals", new IdentifiedDayDisposalFormatter(), calculated.getIdentifications().getIdentifiedDayDisposals());
        writeItems(outputDirectory, "tax_year_summaries", new TaxYearSummaryFormatter(), calculated.getTaxYearSummaries().entrySet());
    }

    private <T> void writeItems(Path outputDirectory, String name, ItemFormatter<T> formatter, Collection<T> items) throws IOException {
        try (Writer writer = new FileWriter(outputDirectory.resolve(name + ".csv").toFile());
             CSVWriter csvWriter = new CSVWriter(writer)) {
            csvWriter.writeNext(formatter.getHeader());
            for (T item : items) {
                csvWriter.writeNext(formatter.format(item));
            }
        }
    }
}
