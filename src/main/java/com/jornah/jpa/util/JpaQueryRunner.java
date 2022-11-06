package com.jornah.jpa.util;

import org.hibernate.query.criteria.internal.path.PluralAttributePath;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author licong
 * @date 2022/11/2 21:55
 */
public class JpaQueryRunner<T, R> implements Compare<JpaQueryRunner<T, R>, SFunction<T, ?>> {
    private final List<SqlCondition> conditionList = new ArrayList<>();
    private final EntityManager em;
    private final Class<R> returnClass;
    private final Class<T> entityClass;
    private final Set<String> selectList = new HashSet<>();

    public JpaQueryRunner(EntityManager em, Class<T> entityClass, Class<R> returnClass) {
        this.em = em;
        this.returnClass = returnClass;
        this.entityClass = entityClass;
    }


    public List<R> executeQuery() {
        CriteriaQuery<Tuple> criteriaQuery = createCriteriaQuery();

        List<Tuple> tupleResultList = em.createQuery(criteriaQuery)
                .getResultList();

        DefaultResultTransformer<R> resultTransformer =
                new DefaultResultTransformer<>(tupleResultList, returnClass, selectList);
        return resultTransformer.transform();
    }

    private CriteriaQuery<Tuple> createCriteriaQuery() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
        Root<T> root = query.from(this.entityClass);

        List<Predicate> predicateList = new ArrayList<>();
        conditionList.forEach(condition -> {
            Predicate predicate = generatePredicateBy(criteriaBuilder, root, condition);
            predicateList.add(predicate);
        });
        List<Selection<?>> selectionList = selectList.stream()
                .map(sel -> root.get(sel).alias(sel))
                .collect(Collectors.toList());

        return query.multiselect(selectionList)
                .where(predicateList.toArray(new Predicate[]{}));
    }


    private Predicate generatePredicateBy(CriteriaBuilder criteriaBuilder, Root<T> root, SqlCondition condition) {
        Path<String> stringPath = root.get(condition.getColumnName());
        Path<Number> numberPath = root.get(condition.getColumnName());
        Path<Object> objectPath = root.get(condition.getColumnName());
        CompareType keyword = condition.getKeyword();
        Object[] valueArray = condition.getValueArray();
        Object columnValue = valueArray[0];
        Predicate predicate = null;
        switch (keyword) {
            case EQ:
                predicate = criteriaBuilder.equal(objectPath, columnValue);
                break;
            case IN:
                predicate = criteriaBuilder.in(objectPath).in(valueArray);
                break;
            case GT:
                predicate = criteriaBuilder.gt(numberPath, (Number) columnValue);
                break;
            case GE:
                predicate = criteriaBuilder.ge(numberPath, (Number) columnValue);
                break;
            case LE:
                predicate = criteriaBuilder.le(numberPath, (Number) columnValue);
                break;
            case NE:
                predicate = criteriaBuilder.notEqual(numberPath, columnValue);
                break;
            case LT:
                predicate = criteriaBuilder.lt(numberPath, (Number) columnValue);
                break;
            case LIKE:
                predicate = criteriaBuilder.like(stringPath, (String) columnValue);
                break;
            case BETWEEN:
                predicate = criteriaBuilder.between(stringPath, (String)valueArray[0],(String) valueArray[1]);
                break;
        }
        return predicate;

    }


    @SafeVarargs
    public final JpaQueryRunner<T, R> select(SFunction<T, ?>... columns) {
        for (SFunction<T, ?> column : columns) {
            String methodName = ColumnNameResolver.getLambdaMethodName(column);
            String fieldName = ColumnNameResolver.methodToProperty(methodName);
            this.selectList.add(fieldName);
        }
        return this;
    }

    @Override
    public JpaQueryRunner<T, R> eq(boolean condition, SFunction<T, ?> column, Object val) {
        addQueryCondition(column, val, CompareType.EQ);
        return this;
    }


    @Override
    public JpaQueryRunner<T, R> ne(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public JpaQueryRunner<T, R> gt(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public JpaQueryRunner<T, R> ge(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public JpaQueryRunner<T, R> lt(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public JpaQueryRunner<T, R> le(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public JpaQueryRunner<T, R> between(boolean condition, SFunction<T, ?> column, Object val1, Object val2) {
        return null;
    }

    @Override
    public JpaQueryRunner<T, R> like(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    @Override
    public JpaQueryRunner<T, R> in(boolean condition, SFunction<T, ?> column, Object val) {
        return null;
    }

    private void addQueryCondition(SFunction<T, ?> column, Object val, CompareType compareType) {
        String methodName = ColumnNameResolver.getLambdaMethodName(column);
        String fieldName = ColumnNameResolver.methodToProperty(methodName);
        conditionList.add(SqlCondition.of(compareType, fieldName, val));
    }

}
