package com.jornah.jpa.util;

import lombok.Data;
import org.springframework.util.Assert;

/**
 * @author licong
 * @date 2022/11/2 23:12
 */
@Data
public class SqlCondition {
    private String columnName;
    private CompareType keyword;
    private Object[] valueArray;

    public static SqlCondition of(CompareType keyword, String columnName, Object... valueArray) {
        Assert.isTrue(valueArray.length != 0, "should have at least 1 value");
        SqlCondition sqlCondition = new SqlCondition();
        sqlCondition.columnName = columnName;
        sqlCondition.keyword = keyword;
        sqlCondition.valueArray = valueArray;
        return sqlCondition;
    }


}
