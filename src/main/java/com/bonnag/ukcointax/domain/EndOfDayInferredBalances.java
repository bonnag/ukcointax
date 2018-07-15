package com.bonnag.ukcointax.domain;

import java.time.LocalDate;
import java.util.List;

public class EndOfDayInferredBalances {
    public final LocalDate day;
    public final List<AssetAmount> balances;
    public EndOfDayInferredBalances(LocalDate day, List<AssetAmount> balances) {
        this.day = day;
        this.balances = balances;
    }
}
