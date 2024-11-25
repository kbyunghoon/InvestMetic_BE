package com.investmetic.global.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 소수점 반올림 하는 유틸 클래스
 */
public class RoundUtil {

    // 소수점 첫째 자리에서 반올림
    public static double roundToFirst(double value) {
        return round(value, 0);
    }

    // 소수점 둘째 자리에서 반올림
    public static double roundToSecond(double value) {
        return round(value, 1);
    }

    // 소수점 셋째 자리에서 반올림
    public static double roundToThird(double value) {
        return round(value, 2);
    }

    // 소수점 넷째 자리에서 반올림
    public static double roundToFourth(double value) {
        return round(value, 3);
    }

    // 소수점 다섯째 자리에서 반올림
    public static double roundToFifth(double value) {
        return round(value, 4);
    }

    private static double round(double value, int idx) {
        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        return bigDecimal.setScale(idx, RoundingMode.HALF_UP).doubleValue();
    }

}
