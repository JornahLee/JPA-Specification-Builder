package com.jornah.jpa.util;

import lombok.Data;

/**
 * @author licong
 * @date 2022/11/2 23:12
 */
@Data
public class SqlCondition {
    private String columnName;
    private SqlKeyword keyword;
    private Object value;

    public static SqlCondition of(SqlKeyword keyword, String columnName, Object value) {
        SqlCondition sqlCondition = new SqlCondition();
        sqlCondition.columnName=columnName;
        sqlCondition.keyword=keyword;
        sqlCondition.value=value;
        return sqlCondition;
    }

}
