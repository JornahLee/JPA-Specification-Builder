package com.jornah.jpa.util;

import org.springframework.cglib.core.ReflectUtils;
import org.springframework.util.ReflectionUtils;

import javax.persistence.Tuple;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * convert tuple to object
 * @author licong
 * @date 2022/11/6 10:04
 */
class DefaultResultTransformer<R> {

    private final List<Tuple> tupleList;
    private final Class<R> returnClass;
    private final Set<String> selectList;

    public DefaultResultTransformer(List<Tuple> tupleList, Class<R> returnClass, Set<String> selectList) {
        this.tupleList = tupleList;
        this.returnClass = returnClass;
        this.selectList = selectList;
    }

    public List<R> transform() {
        List<R> retList = new ArrayList<>();
        tupleList.forEach(tuple -> {
            R r = returnClass.cast(ReflectUtils.newInstance(returnClass));
            for (String sel : this.selectList) {
                Object columnValue = tuple.get(sel);
                String setterName = ColumnNameResolver.propertyToSetter(sel);
                Method setter = ReflectionUtils.findMethod(returnClass, setterName, columnValue.getClass());
                if (Objects.nonNull(setter)) {
                    ReflectionUtils.makeAccessible(setter);
                    ReflectionUtils.invokeMethod(setter, r, columnValue);
                }
            }
            retList.add(r);
        });
        return retList;
    }
}
