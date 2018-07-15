package com.bonnag.ukcointax.domain;

import com.bonnag.ukcointax.calculating.DayMapper;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

public class InferredBalancesHistory {

    private static class BalanceSnapshot {
        public final Instant snapshotAt;
        public final AssetAmount assetAmount;
        public BalanceSnapshot(Instant snapshotAt, AssetAmount assetAmount) {
            this.snapshotAt = snapshotAt;
            this.assetAmount = assetAmount;
        }

        @Override
        public String toString() {
            return "BalanceSnapshot{" + "snapshotAt=" + snapshotAt +
                    ", assetAmount=" + assetAmount +
                    '}';
        }
    }

    private final LinkedHashMap<Asset,List<BalanceSnapshot>> balanceHistories;
    private final List<LocalDate> interestingDays;
    private final LinkedHashMap<Asset,TreeMap<LocalDate,AssetAmount>> endOfDayBalances;

    public InferredBalancesHistory(List<Trade> sortedTrades, Bounds bounds, DayMapper dayMapper) {
        balanceHistories = new LinkedHashMap<>();
        endOfDayBalances = new LinkedHashMap<>();
        Instant startPoint = sortedTrades.get(0).getTradedAt().minusSeconds(1);
        for (Asset asset : bounds.getAssets()) {
            BalanceSnapshot initialSnapshot = new BalanceSnapshot(startPoint, AssetAmount.makeZeroFor(asset));
            List<BalanceSnapshot> initialHistory = new ArrayList<>();
            initialHistory.add(initialSnapshot);
            balanceHistories.put(asset, initialHistory);
            endOfDayBalances.put(asset, new TreeMap<>());
        }
        for (Trade trade : sortedTrades) {
            processTrade(trade);
        }
        Set<LocalDate> daysWithBalances = new HashSet<>();
        for (List<BalanceSnapshot> balanceHistory : balanceHistories.values()) {
            for (BalanceSnapshot balanceSnapshot : balanceHistory) {
                LocalDate day = dayMapper.dayOfTrade(balanceSnapshot.snapshotAt);
                Asset asset = balanceSnapshot.assetAmount.getAsset();
                TreeMap<LocalDate,AssetAmount> balanceByDay = endOfDayBalances.get(asset);
                balanceByDay.put(day, balanceSnapshot.assetAmount);
                daysWithBalances.add(day);
            }
        }
        interestingDays = new ArrayList<>(daysWithBalances);
        interestingDays.sort(Comparator.naturalOrder());
    }

    private void processTrade(Trade trade) {
        processTrade(trade.getTradedAt(), trade.getBought());
        processTrade(trade.getTradedAt(), trade.getSold().negate());
    }

    private void processTrade(Instant tradedAt, AssetAmount balanceChange) {
        Asset asset = balanceChange.getAsset();
        List<BalanceSnapshot> balanceHistory = balanceHistories.get(asset);
        BalanceSnapshot lastSnapshot = balanceHistory.get(balanceHistory.size() - 1);
        BalanceSnapshot newSnapshot = new BalanceSnapshot(tradedAt, lastSnapshot.assetAmount.add(balanceChange));
        if (newSnapshot.assetAmount.isProperlyNegative()) {
            if (asset.isSterling()) {
                // allow negative
            } else {
                throw new IllegalStateException("balance went negative at " + newSnapshot);
            }
        }
        balanceHistory.add(newSnapshot);
    }

    public List<EndOfDayInferredBalances> getDailyBalancesSparse() {
        List<EndOfDayInferredBalances> result = new ArrayList<>();
        for (LocalDate day : interestingDays) {
            List<AssetAmount> balances = new ArrayList<>();
            for (Asset asset : endOfDayBalances.keySet()) {
                TreeMap<LocalDate,AssetAmount> balanceByDay = endOfDayBalances.get(asset);
                Map.Entry<LocalDate,AssetAmount> lastBalance = balanceByDay.floorEntry(day);
                if (lastBalance == null) {
                    throw new IllegalStateException("no entry found before " + day + " for " + asset);
                }
                balances.add(lastBalance.getValue());
            }
            result.add(new EndOfDayInferredBalances(day, balances));
        }
        return result;
    }
}
