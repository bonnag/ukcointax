package com.bonnag.ukcointax.loading.importers;

import com.bonnag.ukcointax.domain.Asset;
import com.bonnag.ukcointax.domain.AssetAmount;
import com.bonnag.ukcointax.domain.TradeDirection;
import com.bonnag.ukcointax.domain.TradeId;
import com.bonnag.ukcointax.loading.CsvDecoder;
import com.bonnag.ukcointax.loading.CsvRecogniser;
import com.bonnag.ukcointax.loading.DecoderSink;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.Optional;

public class DeltaBalancesTradeRecogniser implements CsvRecogniser {

    private static final String[] expectedHeaderFields = "Type,Trade,Token,Amount,Price,BaseCurrency,Total,Date,Block,Transaction Hash,Buyer,Seller,Fee,FeeToken,Token Contract,BaseCurrency Contract,Exchange".split(",");

    @Override
    public Optional<CsvDecoder> maybeCreateDecoder(String filename, String[] headerFields) {
        if (!Arrays.equals(headerFields, expectedHeaderFields)) {
            return Optional.empty();
        }
        return Optional.of(new Decoder());
    }

    private static class Decoder implements CsvDecoder {
        @Override
        public void process(String[] record, DecoderSink sink) {
            // e.g.
            // Maker,Buy,SALT,22.301,0.011210465,ETH,0.250004579965,2017-11-19T00:16:27+00:00,
            // 4578901,0xa0fe175c725c90a390afe7280955c38f15ed314aabdfa1c38583f4e8a71b59c8,
            // 0x81528e544ca84525d7644e624800684229bf4bb5, 0xb4ba5143c19ec3ff293d50d826ad99505d788f91,
            // 0,ETH,
            // 0x4156d3342d5c385a87d264f90653733592000581,0x0000000000000000000000000000000000000000,EtherDelta
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            // Instant tradedAt, String assetBought, double amountBought, String assetSold, double amountSold, String venue, String tradeId
            String dateStr = record[7];
            String blockNumber = record[8];
            // deltabalances seems to have trouble getting the date sometimes - not orphaned hopefully?
            Instant tradedAt = (dateStr.equals("??")) ? getBlockTime(Integer.parseInt(blockNumber)) : Instant.from(dateTimeFormatter.parse(dateStr));
            String venue = record[16].trim();
            String tradeId = DigestUtils.md5Hex(String.join(",", record));
            // yes, etherdelta use base backwards here!
            String token = record[2];
            double tokenAmount = Double.parseDouble(record[3]);
            String currency = record[5];
            double currencyAmount = Double.parseDouble(record[6]);
            TradeDirection tradeSide = TradeDirection.valueOf(record[1]);
            sink.addTrade(
                    new TradeId(venue, tradeId),
                    tradedAt,
                    tradeSide,
                    new AssetAmount(new Asset(token), tokenAmount),
                    new AssetAmount(new Asset(currency), currencyAmount)
            );
        }

        private Instant getBlockTime(int blockNumber) {
            // etherscan.io has a weird format
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM-dd-yyyy hh:mm:ss a").withZone(ZoneId.of("UTC"));
            if (blockNumber == 5207920) {
                return Instant.from(dateTimeFormatter.parse("Mar-06-2018 05:26:48 PM"));
            }
            if (blockNumber == 5207303) {
                return Instant.from(dateTimeFormatter.parse("Mar-06-2018 03:00:45 PM"));
            }
            if (blockNumber == 5208312) {
                return Instant.from(dateTimeFormatter.parse("Mar-06-2018 07:00:01 PM"));
            }
            if (blockNumber == 5282651) {
                return Instant.from(dateTimeFormatter.parse("Mar-19-2018 10:01:03 AM"));
            }
            if (blockNumber == 5307325) {
                return Instant.from(dateTimeFormatter.parse("Mar-23-2018 01:07:13 PM"));
            }
            throw new IllegalArgumentException("do not know time for block " + blockNumber);
        }
    }
}
