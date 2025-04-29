package com.investmetic.global.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 소수점 반올림 하는 유틸 클래스
 */
public class RoundUtil {


    public static double roundToSecond(Double value) {
        return safeRound(value, 1);
    }

    public static double roundToFifth(Double value) {
        return safeRound(value, 4);
    }

    /**
     * 안전하게 반올림 수행 (null, NaN, Infinity 방어)
     */
    private static double safeRound(Double value, int scale) {
        if (value == null || !Double.isFinite(value)) {
            return 0.0;
        }

        try {
            return BigDecimal.valueOf(value)
                    .setScale(scale, RoundingMode.HALF_UP)
                    .doubleValue();
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

}
