package com.jornah.jpa.util;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author licong
 * @date 2022/11/2 23:16
 */
public enum CompareType {
    GT,
    GE,
    EQ,
    LE,
    NE,
    LT,
    LIKE,
    IN,
    BETWEEN;
}
