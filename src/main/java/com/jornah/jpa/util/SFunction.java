package com.jornah.jpa.util;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author licong
 * @date 2022/11/2 21:48
 */

@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {
}

