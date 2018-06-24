package com.bonnag.ukcointax;

import com.bonnag.ukcointax.calculations.Calculator;
import com.bonnag.ukcointax.domain.*;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void blockTaxUkExample() {
        List<Trade> trades = Arrays.asList(
                new Trade(
                        new TradeId("someVenue", "1"),
                        instantAtStartOfLondonDay(2017, Month.JANUARY, 1),
                        TradeDirection.Buy,
                        new AssetAmount("BTC", 0.25),
                        new AssetAmount("GBP", 1500 * 0.25)
                        ),
                new Trade(
                        new TradeId("someVenue", "2"),
                        instantAtStartOfLondonDay(2017, Month.MARCH, 1),
                        TradeDirection.Buy,
                        new AssetAmount("BTC", 0.25),
                        new AssetAmount("GBP", 2000 * 0.25)
                ),
                new Trade(
                        new TradeId("someVenue", "3"),
                        instantAtStartOfLondonDay(2017, Month.MAY, 1),
                        TradeDirection.Buy,
                        new AssetAmount("BTC", 0.25),
                        new AssetAmount("GBP", 2500 * 0.25)
                ),
                new Trade(
                        new TradeId("someVenue", "4"),
                        instantAtStartOfLondonDay(2017, Month.JULY, 1),
                        TradeDirection.Buy,
                        new AssetAmount("BTC", 0.25),
                        new AssetAmount("GBP", 3000 * 0.25)
                ),
                new Trade(
                        new TradeId("someVenue", "5"),
                        instantAtStartOfLondonDay(2017, Month.OCTOBER, 1),
                        TradeDirection.Buy,
                        new AssetAmount("BTC", 0.25),
                        new AssetAmount("GBP", 3500 * 0.25)
                ),
                new Trade(
                        new TradeId("someVenue", "6"),
                        instantAtStartOfLondonDay(2017, Month.OCTOBER, 1),
                        TradeDirection.Sell,
                        new AssetAmount("BTC", 0.6),
                        new AssetAmount("GBP", 3500 * 0.6)
                ),
                new Trade(
                        new TradeId("someVenue", "7"),
                        instantAtStartOfLondonDay(2017, Month.OCTOBER, 17),
                        TradeDirection.Buy,
                        new AssetAmount("BTC", 0.2),
                        new AssetAmount("GBP", 3600 * 0.2)
                ));
        Calculator calculator = new Calculator();
        Calculated calculated = calculator.calculate(trades, Collections.emptyList());
        Bounds bounds = calculated.getBounds();
        Assert.assertEquals(Arrays.asList(new Asset("BTC"), new Asset("GBP")), bounds.getAssets());
        Assert.assertEquals("2016/17", bounds.getFirstTaxYear().getName());
        Assert.assertEquals("2017/18", bounds.getLastTaxYear().getName());
        System.out.println(calculated);
    }

    private Instant instantAtStartOfLondonDay(int year, Month month, int dayOfMonth) {
        ZoneId zoneId = ZoneId.of("Europe/London");
        return LocalDate.of(year, month, dayOfMonth).atStartOfDay(zoneId).toInstant();
    }
}
