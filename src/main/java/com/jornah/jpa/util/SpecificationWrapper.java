package com.jornah.jpa.util;

import org.apache.ibatis.reflection.ReflectionException;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author licong
 * @date 2022/11/2 21:55
 */
public class SpecificationWrapper<T> {
    List<SqlCondition> conditionList = new ArrayList<>();


    public Specification<T> build() {
        return (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            conditionList.forEach(condition -> {
                Path<Object> path = root.get(condition.getColumnName());
                SqlKeyword keyword = condition.getKeyword();
                Predicate predicate = null;
                switch (keyword) {
                    case EQ:
                        predicate = cb.equal(path, condition.getValue());
                        break;
                }
                predicateList.add(predicate);
            });
            return query.where(predicateList.toArray(new Predicate[]{})).getRestriction();
        };
    }


    public SpecificationWrapper<T> eq(boolean condition, SFunction<T, ?> myFun, Object val) {

        String methodName = getLambdaMethodName(myFun);
        String fieldName = methodToProperty(methodName);
        conditionList.add(SqlCondition.of(SqlKeyword.EQ, fieldName, val));
        return this;
    }

    public SpecificationWrapper<T> eq(SFunction<T, ?> myFun, Object val) throws Exception {
        return eq(true, myFun, val);
    }

    private String getLambdaMethodName(SFunction<T, ?> myFun) {
        try {
            Method writeReplace = myFun.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(myFun);
            return serializedLambda.getImplMethodName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static String methodToProperty(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else if (name.startsWith("get") || name.startsWith("set")) {
            name = name.substring(3);
        } else {
            throw new ReflectionException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
        }

        if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }

        return name;
    }

}
