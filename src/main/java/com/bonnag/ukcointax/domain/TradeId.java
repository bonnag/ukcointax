package com.bonnag.ukcointax.domain;

import java.util.Objects;

public class TradeId {
    private final String venueCode;
    private final String id;

    public TradeId(String venueCode, String id) {
        this.venueCode = venueCode;
        this.id = id;
    }

    public String getVenueCode() {
        return venueCode;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TradeId tradeId = (TradeId) o;
        return Objects.equals(venueCode, tradeId.venueCode) &&
               Objects.equals(id, tradeId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(venueCode, id);
    }

    @Override
    public String toString() {
        return venueCode + ":" + id;
    }
}
