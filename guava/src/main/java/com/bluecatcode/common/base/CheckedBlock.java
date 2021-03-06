package com.bluecatcode.common.base;

/**
 * @param <T> the result type
 * @see java.util.concurrent.Callable
 * @see Consumer
 * @see Effect
 * @see com.google.common.base.Function
 * @see com.google.common.base.Predicate
 * @see com.google.common.base.Supplier
 */
public interface CheckedBlock<T> {

    /**
     * Performs this operation returning value.
     *
     * @throws Exception if unable to compute
     */
    T execute() throws Exception;
}
