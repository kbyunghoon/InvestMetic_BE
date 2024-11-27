package com.investmetic.domain.user.model;

public enum Role {
    TRADER("ROLE_TRADER"),         // 트레이더
    INVESTOR("ROLE_INVESTOR"),     // 투자자
    TRADER_ADMIN("ROLE_TRADER_ADMIN"),   // 트레이더 관리자
    INVESTOR_ADMIN("ROLE_INVESTOR_ADMIN"), // 투자자 관리자
    SUPER_ADMIN("ROLE_SUPER_ADMIN");     // 슈퍼 관리자

    private final String role;

    Role(String role) {
        this.role = role;
    }
    // admin일 경우 true;
    public static boolean isAdmin(Role role) {
        return switch (role) {
            case INVESTOR_ADMIN, TRADER_ADMIN, SUPER_ADMIN -> true;
            default -> false;
        };
    }

    // 트레이더일 경우
    public static boolean isTrader(Role role) {
        return switch (role) {
            case TRADER, TRADER_ADMIN -> true;
            default -> false;
        };
    }

    // 일반 투자자일 경우
    public static boolean isInvestor(Role role) {
        return switch (role) {
            case INVESTOR , INVESTOR_ADMIN -> true;
            default -> false;
        };
    }
}
