package com.bluecatcode.common.functions;

/**
 * @param <T> the result type
 * @see java.util.concurrent.Callable
 * @see Consumer
 * @see Effect
 * @see com.google.common.base.Function
 * @see com.google.common.base.Predicate
 * @see com.google.common.base.Supplier
 */
public interface CheckedBlock<T, E extends Exception> {

    /**
     * Performs this operation returning value.
     *
     * @throws E if unable to compute
     */
    T execute() throws E;
}
