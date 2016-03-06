package com.bluecatcode.common.exceptions;

import com.bluecatcode.common.functions.CheckedFunction;
import com.google.common.base.Function;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static com.google.common.base.Preconditions.checkArgument;

public class Exceptions {

    private Exceptions() {
        throw new UnsupportedOperationException();
    }

    public static Function<Exception, RuntimeException> uncheckedException() {
        return e -> e instanceof RuntimeException ? (RuntimeException) e : new UncheckedException(e);
    }

    public static Function<WrappedException, RuntimeException> unwrapToUnchckedException() {
        return e -> uncheckedException().apply(e.unwrap());
    }

    public static <E extends Exception> E exception(Class<E> throwableType) {
        return throwable(throwableType);
    }

    public static <E extends Throwable> E throwable(Class<E> throwableType) {
        return throwable(throwableType, parameters(), arguments());
    }

    public static <E extends Exception> E exception(Class<E> throwableType,
                                                    CheckedFunction<Class<E>, Constructor<E>, ReflectiveOperationException> constructorSupplier,
                                                    CheckedFunction<Constructor<E>, E, ReflectiveOperationException> instanceSupplier) {
        return throwable(throwableType, constructorSupplier, instanceSupplier);
    }

    public static <E extends Throwable> E throwable(Class<E> throwableType,
                                                    CheckedFunction<Class<E>, Constructor<E>, ReflectiveOperationException> constructorSupplier,
                                                    CheckedFunction<Constructor<E>, E, ReflectiveOperationException> instanceSupplier) {
        checkArgument(!Modifier.isAbstract(throwableType.getModifiers()),
                "Expected non-abstract throwable type, got: '%s'", throwableType.getCanonicalName());
        final Constructor<E> constructor;
        try {
            constructor = constructorSupplier.apply(throwableType);
        } catch (Exception e) {
            throw new IllegalArgumentException("Expected a throwable with (String) constructor", e);
        }
        try {
            return instanceSupplier.apply(constructor);
        } catch (Exception e) {
            throw new IllegalArgumentException("Expected an instantiable throwable.", e);
        }
    }

    public static <T> CheckedFunction<Class<T>, Constructor<T>, ReflectiveOperationException> parameters(Class<?> parameterType) {
        return type -> type.getConstructor(parameterType);
    }

    public static <T> CheckedFunction<Class<T>, Constructor<T>, ReflectiveOperationException> parameters(Class<?>... parameterTypes) {
        return type -> type.getConstructor(parameterTypes);
    }

    public static <T> CheckedFunction<Constructor<T>, T, ReflectiveOperationException> arguments(Object arg) {
        return constructor -> constructor.newInstance(arg);
    }

    public static <T> CheckedFunction<Constructor<T>, T, ReflectiveOperationException> arguments(Object... args) {
        return constructor -> constructor.newInstance(args);
    }
}