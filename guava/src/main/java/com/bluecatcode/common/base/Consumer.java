package com.bluecatcode.common.base;

/**
 * Represents an operation that accepts a single input argument and returns no result.
 * Unlike most other functional interfaces, Consumer is expected to operate via side-effects.
 *
 * @see <a href="http://docs.oracle.com/javase/8/docs/api/java/util/function/Consumer.html">Interface Consumer</a>
 *
 * @see com.bluecatcode.common.base.Block
 * @see com.bluecatcode.common.base.Effect
 * @see com.google.common.base.Function
 * @see com.google.common.base.Predicate
 * @see com.google.common.base.Supplier
 * @see java.util.concurrent.Callable
 * @param <T> the input type of method {@code accept}
 */
public interface Consumer<T> {

    /**
     * Performs this operation on the given argument.
     * @param input the input to consume
     * @throws RuntimeException if unable to compute
     */
    void accept(T input);
}
