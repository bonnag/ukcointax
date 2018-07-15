package com.bonnag.ukcointax.presenting;

import com.bonnag.ukcointax.domain.*;
import com.opencsv.CSVWriter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.*;

public class ReportsWriter {
    public void write(Path outputDirectory, Calculated calculated) throws IOException {
        writeItems(outputDirectory, "valued_trades", new ValuedTradeFormatter(), calculated.getValuedTrades());
/*
    public DailyAcquisitionsAndDisposals getDailyAcquisitionsAndDisposals() {
        return dailyAcquisitionsAndDisposals;
    }
 */
        writeItems(outputDirectory, "identifications", new IdentificationFormatter(), calculated.getIdentifications().getIdentifications());
        writeItems(outputDirectory, "identified_day_disposals", new IdentifiedDayDisposalFormatter(), calculated.getIdentifications().getIdentifiedDayDisposals());
        writeItems(outputDirectory, "tax_year_summaries", new TaxYearSummaryFormatter(), calculated.getTaxYearSummaries().entrySet());
        writeItems(outputDirectory, "inferred_balances", new InferredBalancesFormatter(calculated.getBounds()), calculated.getInferredBalancesHistory().getDailyBalancesSparse());
        writeDisposalComputations(outputDirectory, calculated.getBounds(), calculated.getIdentifications());
    }

    private void writeDisposalComputations(Path outputDirectory, Bounds bounds, Identifications identifications) throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
        cfg.setClassForTemplateLoading(ReportsWriter.class, "/views");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        for (TaxYear taxYear : bounds.getTaxYears()) {
            Map model = makeModel(identifications.getIdentifiedDayDisposalsDuring(taxYear));
            Path outputFile = outputDirectory.resolve("disposal-computations-" + taxYear.toSafeString() + ".html");
            try (Writer out = new FileWriter(outputFile.toFile())) {
                Template template = cfg.getTemplate("disposal-computations.ftlh");
                template.process(model, out);
            } catch (TemplateException e) {
                e.printStackTrace();
                throw new IOException(e);
            }
        }
    }

    private Map makeModel(List<IdentifiedDayDisposal> identifiedDayDisposals) {
        Map model = new HashMap();
        List disposalModels = new ArrayList();
        model.put("disposals", disposalModels);
        int disposalNumber = 1;
        for (IdentifiedDayDisposal identifiedDayDisposal : identifiedDayDisposals) {
            Map disposalModel = new HashMap();
            disposalModel.put("number", disposalNumber);
            disposalNumber++;
            disposalModel.put("date", identifiedDayDisposal.getAssetDay().getDay());
            disposalModel.put("numTrades", identifiedDayDisposal.getDayDisposal().getValuedTrades().size());
            Asset asset = identifiedDayDisposal.getAssetDay().getAsset();
            disposalModel.put("asset", asset);
            if (asset.isShareLike()) {
                disposalModel.put("assetType", "Crypto-currency");
            } else {
                throw new IllegalStateException("only expected share-like assets but got " + asset);
            }
            disposalModel.put("assetAmount", identifiedDayDisposal.getDayDisposal().getSold());
            disposalModel.put("proceeds", identifiedDayDisposal.getDayDisposal().getSterlingProceeds());
            disposalModel.put("allowableCost", identifiedDayDisposal.getAllowableCosts());
            List identificationModels = new ArrayList();
            disposalModel.put("identifications", identificationModels);
            for (Identification identification : identifiedDayDisposal.getIdentifications()) {
                Map identificationModel = new HashMap();
                identificationModel.put("rule", identification.getIdentificationRuleCode());
                identificationModel.put("acquisitionDate",
                        identification.getDayAcquisition()
                                .map(da -> da.getAssetDay().getDay().toString())
                                .orElse("n/a"));
                identificationModel.put("assetAmount", identification.getAmount());
                identificationModel.put("allowableCost", identification.getAllowableCostSterling());
                if (identification.getAllowableCostSterling().getAmount() >= 0.01) {
                    identificationModels.add(identificationModel);
                }
            }
            disposalModels.add(disposalModel);
        }
        return model;
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
