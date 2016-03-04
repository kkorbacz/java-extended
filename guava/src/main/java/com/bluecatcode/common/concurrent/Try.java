package com.bluecatcode.common.concurrent;

import com.bluecatcode.common.base.Either;
import com.bluecatcode.common.exceptions.WrappedException;
import com.bluecatcode.common.functions.CheckedFunction;
import com.bluecatcode.common.functions.Effect;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;

import javax.annotation.CheckReturnValue;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.bluecatcode.common.base.Eithers.either;
import static com.bluecatcode.common.contract.Postconditions.ensure;
import static com.bluecatcode.common.contract.Preconditions.require;
import static com.bluecatcode.common.exceptions.Exceptions.uncheckedException;
import static com.bluecatcode.common.exceptions.Exceptions.unwrapToUnchckedException;

@CheckReturnValue
public class Try {

    public static <T> T tryWith(Callable<T> callable) {
        return either(callable).orThrow(unwrapToUnchckedException());
    }

    public static <T> T tryWith(Callable<T> callable, Function<WrappedException, RuntimeException> exceptionFunction) {
        return either(callable).orThrow(exceptionFunction);
    }

    public static void tryWith(Effect effect) {
        try {
            effect.cause();
        } catch (Exception e) {
            throw uncheckedException().apply(e);
        }
    }

    public static <T> T tryWith(long timeoutDuration, TimeUnit timeoutUnit, Callable<T> callable) {
        try {
            TimeLimiter limiter = new SimpleTimeLimiter();
            //noinspection unchecked
            Callable<T> proxy = limiter.newProxy(callable, Callable.class, timeoutDuration, timeoutUnit);
            return proxy.call();
        } catch (Exception e) {
            throw uncheckedException().apply(e);
        }
    }

    public static void tryWith(long timeoutDuration, TimeUnit timeoutUnit, Effect effect) {
        try {
            TimeLimiter limiter = new SimpleTimeLimiter();
            //noinspection unchecked
            Effect proxy = limiter.newProxy(effect, Effect.class, timeoutDuration, timeoutUnit);
            proxy.cause();
        } catch (Exception e) {
            throw uncheckedException().apply(e);
        }
    }

    public interface Decorator<T> extends Function<T, T> {
    }

    public interface Limiter<T, R> extends Decorator<Function<T, R>> {
    }

    public interface LimitStep<R> {
        R limit(long timeoutDuration, TimeUnit timeoutUnit);
    }

    public interface TryStep<T, R, E extends Exception> {
        R tryA(T function) throws E;
    }

    public interface EitherFunction<T, R> extends Function<T, Either<WrappedException, R>> {}

    public interface TryCheckedFunction<T, R, ER, E extends Exception> extends
            TryStep<CheckedFunction<T, R, E>, ER, E> {}

    public static abstract class WithReference<T, R, ER, E extends Exception> implements
            TryCheckedFunction<T, R, ER, E>,
            LimitStep<TryCheckedFunction<T, R, ER, E>> {

        protected final Supplier<T> supplier;

        protected Limiter<CheckedFunction<T, R, E>, ER> limiter;

        public WithReference(Supplier<T> supplier) {
            this.supplier = supplier;
            this.limiter = f -> f;
        }

        protected abstract Function<CheckedFunction<T, R, E>, ER> limiteeFunction();

        @Override
        public TryCheckedFunction<T, R, ER, E> limit(long timeoutDuration, TimeUnit timeoutUnit) {
            this.limiter = function -> {
                try {
                    TimeLimiter limiter = new SimpleTimeLimiter();
                    //noinspection unchecked
                    Function result = limiter.newProxy(function, Function.class, timeoutDuration, timeoutUnit);
                    ensure(result != null);
                    return result;
                } catch (Exception e) {
                    throw uncheckedException().apply(e);
                }
            };
            return this;
        }
    }

    public static class WithCloseable<R, E extends Exception> extends WithReference<Closeable, R, Either<WrappedException, R>, E> {
        public WithCloseable(Supplier<Closeable> supplier) {
            super(supplier);
        }

        @Override
        public Function<CheckedFunction<Closeable, R, E>, Either<WrappedException, R>> limiteeFunction() {
            return f -> {
                try (Closeable c = supplier.get()) {
                    require(c != null);
                    Either<WrappedException, R> result = either(f).apply(c);
                    ensure(result != null);
                    return result;
                } catch (IOException e) {
                    throw uncheckedException().apply(e);
                }
            };
        }

        @Override
        public Either<WrappedException, R> tryA(CheckedFunction<Closeable, R, E> function) throws E {
            ensure(function.apply(() -> {}) != null); // FIXME remove
            return limiter.apply(limiteeFunction()).apply(function);
        }
    }

    public static <R, E extends Exception> WithCloseable<R, E> with(Closeable closeable, Class<R> returnType, Class<E> throws1) {
        return new WithCloseable<>(() -> closeable);
    }

    public static <R, E extends Exception> WithCloseable<R, E> with(Closeable closeable, Class<E> throws1) {
        return new WithCloseable<>(() -> closeable);
    }

    public enum Unit {
        INSTANCE
    }

    private Try() {
        throw new UnsupportedOperationException();
    }
}
