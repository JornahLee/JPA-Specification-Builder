package com.jornah.jpa.util;

import org.apache.ibatis.reflection.ReflectionException;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.util.ReflectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author licong
 * @date 2022/11/2 21:55
 */
public class JpaQueryRunner<T, Q> {
    List<SqlCondition> conditionList = new ArrayList<>();

    EntityManager em;
    Class<Q> returnClass;
    Class<T> entityClass;
    Set<String> selectList = new HashSet<>();

    public JpaQueryRunner(EntityManager em, Class<T> entityClass, Class<Q> returnClass) {
        this.em = em;
        this.returnClass = returnClass;
        this.entityClass = entityClass;
    }

    public JpaQueryRunner select(SFunction<T, ?>... myFun) {
        for (SFunction<T, ?> fun : myFun) {
            String methodName = getLambdaMethodName(fun);
            String fieldName = methodToProperty(methodName);
            this.selectList.add(fieldName);
        }
        return this;
    }

    public List<Q> execute() {
//        Projections
//        Class<Object[]> aClass = Object[].class;
        List<Tuple> tupleList = query();
        return tupleCastObject(tupleList);
    }

    private List<Q> tupleCastObject(List<Tuple> tupleList) {
        List<Q> retList = new ArrayList<>();
        Field[] fields = returnClass.getDeclaredFields();
        tupleList.forEach(tuple -> {
            Q q = (Q) ReflectUtils.newInstance(returnClass);
            Arrays.stream(fields)
                    .filter(f -> selectList.contains(f.getName()))
                    .forEach(field -> {
                        ReflectionUtils.makeAccessible(field);
                        ReflectionUtils.setField(field,q, tuple.get(field.getName()));
                    });
            retList.add(q);
        });
        return retList;
    }

    private List<Tuple> query() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
        Root<T> root = query.from(this.entityClass);

        List<Predicate> predicateList = new ArrayList<>();
        conditionList.forEach(condition -> {
            Path<Object> path = root.get(condition.getColumnName());
            SqlKeyword keyword = condition.getKeyword();
            Predicate predicate = null;
            switch (keyword) {
                case EQ:
                    predicate = criteriaBuilder.in(path).value(1).value(2).value(3);
                    break;
            }
            predicateList.add(predicate);
        });
        List<Selection<?>> selectionList = selectList.stream()
                .map(sel -> root.get(sel).alias(sel))
                .collect(Collectors.toList());

        CriteriaQuery<Tuple> criteriaQuery = query.multiselect(selectionList)
                .where(predicateList.toArray(new Predicate[]{}));
        return em.createQuery(criteriaQuery).getResultList();
    }


    public JpaQueryRunner<T, Q> eq(boolean condition, SFunction<T, ?> myFun, Object val) {

        String methodName = getLambdaMethodName(myFun);
        String fieldName = methodToProperty(methodName);
        conditionList.add(SqlCondition.of(SqlKeyword.EQ, fieldName, val));
        return this;
    }

    public JpaQueryRunner<T, Q> eq(SFunction<T, ?> myFun, Object val) {
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
