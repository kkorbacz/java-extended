package com.bluecatcode.common.base;

/**
 * @see com.bluecatcode.common.base.Block
 * @see com.bluecatcode.common.base.Consumer
 * @see com.google.common.base.Function
 * @see com.google.common.base.Predicate
 * @see com.google.common.base.Supplier
 * @see java.util.concurrent.Callable
 */
public interface Effect {

    /**
     * Performs this operation for side effect.
     *
     * @throws RuntimeException if unable to compute
     */
    void cause();
}