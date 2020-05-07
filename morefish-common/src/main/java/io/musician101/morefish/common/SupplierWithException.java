package io.musician101.morefish.common;

@FunctionalInterface
public interface SupplierWithException<T> {

    T get() throws RuntimeException;
}
