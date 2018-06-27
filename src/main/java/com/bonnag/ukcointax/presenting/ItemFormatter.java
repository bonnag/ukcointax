package com.bonnag.ukcointax.presenting;

public interface ItemFormatter<T> {
    String[] getHeader();
    String[] format(T item);
}
