package com.jornah.jpa.util;


import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Objects;

/**
 * @author licong
 * @date 2022/11/6 10:05
 */
public abstract class ColumnNameResolver {
    public static <T> String getLambdaMethodName(SFunction<T, ?> myFun) {
        try {
            Method writeReplace = myFun.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(myFun);
            return serializedLambda.getImplMethodName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String methodToProperty(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else if (name.startsWith("get") || name.startsWith("set")) {
            name = name.substring(3);
        } else {
            throw new RuntimeException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
        }
        if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }
        return name;
    }

    public static String propertyToSetter(String property) {
        if (Objects.nonNull(property) && property.length() > 0) {
            char[] chars = property.toCharArray();
            char upperCase = Character.toUpperCase(chars[0]);
            chars[0] = upperCase;
            StringBuilder sb = new StringBuilder();
            sb.append("set").append(chars);
            return sb.toString();
        }
        throw new UnsupportedOperationException("not support this property name: " + property);


    }
}
