package org.hxzon.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class BigDecimalUtil {
    public static final BigDecimal Int0 = new BigDecimal(0);
    public static final BigDecimal Int1 = new BigDecimal(1);
    public static final BigDecimal Int10 = new BigDecimal(10);
    public static final BigDecimal Int100 = new BigDecimal(100);
    public static final Map<Integer, BigDecimal> integerCache = new HashMap<Integer, BigDecimal>();
    static {
        integerCache.put(0, Int0);
        integerCache.put(1, Int1);
        integerCache.put(10, Int10);
        integerCache.put(100, Int100);
    }

    public static BigDecimal getBigDecimal(int i) {
        BigDecimal cache = integerCache.get(i);
        if (cache == null) {
            cache = new BigDecimal(i);
            integerCache.put(i, cache);
        }
        return cache;
    }

    //add
    public static BigDecimal add(BigDecimal d1, BigDecimal d2) {
        return d1.add(d2);
    }

    public static BigDecimal add(BigDecimal d1, int d2) {
        return add(d1, getBigDecimal(d2));
    }

    //subtract
    public static BigDecimal subtract(BigDecimal d1, BigDecimal d2) {
        return d1.subtract(d2);
    }

    public static BigDecimal subtract(BigDecimal d1, int d2) {
        return subtract(d1, getBigDecimal(d2));
    }

    public static BigDecimal subtract(int d1, int d2) {
        return subtract(getBigDecimal(d1), d2);
    }

    //multiply
    public static BigDecimal multiply(BigDecimal d1, BigDecimal d2) {
        return d1.multiply(d2);
    }

    public static BigDecimal multiply(BigDecimal d1, int d2) {
        return d1.multiply(getBigDecimal(d2));
    }

    public static BigDecimal multiply(BigDecimal d1, BigDecimal d2, int percent) {
        return divide(multiply(d1, d2), getBigDecimal(percent));
    }

    public static BigDecimal multiply(BigDecimal d1, int d2, int percent) {
        return multiply(d1, getBigDecimal(d2), percent);
    }

    //divide
    public static BigDecimal divide(BigDecimal d1, BigDecimal d2) {
        BigDecimal result = d1.divide(d2, BigDecimal.ROUND_HALF_EVEN);
        return result;
    }

    public static BigDecimal divide(BigDecimal d1, int d2) {
        return divide(d1, getBigDecimal(d2));
    }

    public static BigDecimal divide(int d1, BigDecimal d2) {
        return divide(getBigDecimal(d1), d2);
    }

    public static BigDecimal divide(BigDecimal d1, BigDecimal d2, int percent) {
        return divide(multiply(d1, percent), d2);
    }

    public static BigDecimal divide(BigDecimal d1, int d2, int percent) {
        return divide(d1, getBigDecimal(d2), percent);
    }

    public static BigDecimal divice(int d1, BigDecimal d2, int percent) {
        return divide(getBigDecimal(d1), d2, percent);
    }

    //remainder
    public static BigDecimal remainder(BigDecimal d1, BigDecimal d2) {
        return d1.remainder(d2);
    }

    public static BigDecimal remainder(BigDecimal d1, int d2) {
        return remainder(d1, getBigDecimal(d2));
    }

    //bigger than
    public static boolean biggerThan(BigDecimal d1, BigDecimal d2) {
        return d1.compareTo(d2) > 0;
    }

    public static boolean biggerThan(BigDecimal d1, int d2) {
        return biggerThan(d1, getBigDecimal(d2));
    }

    //not biggder than
    public static boolean notBiggerThan(BigDecimal d1, BigDecimal d2) {
        return !biggerThan(d1, d2);
    }

    public static boolean notBiggerThan(BigDecimal d1, int d2) {
        return notBiggerThan(d1, getBigDecimal(d2));
    }

    //less than
    public static boolean lessThan(BigDecimal d1, BigDecimal d2) {
        return d1.compareTo(d2) < 0;
    }

    public static boolean lessThan(BigDecimal d1, int d2) {
        return lessThan(d1, getBigDecimal(d2));
    }

    //not less than
    public static boolean notLessThan(BigDecimal d1, BigDecimal d2) {
        return !lessThan(d1, d2);
    }

    public static boolean notLessThan(BigDecimal d1, int d2) {
        return notLessThan(d1, getBigDecimal(d2));
    }

    //equals
    public static boolean equals(BigDecimal d1, BigDecimal d2) {
        return d1.compareTo(d2) == 0;
    }

    public static boolean equals(BigDecimal d1, int i) {
        return equals(d1, getBigDecimal(i));
    }

    //not equals
    public static boolean notEquals(BigDecimal d1, BigDecimal d2) {
        return !equals(d1, d2);
    }

    public static boolean notEquals(BigDecimal d1, int i) {
        return !equals(d1, i);
    }
}
