package com.investmetic.domain.user.model;

public enum Role {
    TRADER,         // 트레이더
    INVESTOR,       // 투자자
    TRADER_ADMIN,   // 트레이더 관리자
    INVESTOR_ADMIN,  // 투자자 관리자
    SUPER_ADMIN;

    // admin일 경우 true;
    public static boolean isAdmin(Role role) {
        return switch (role) {
            case INVESTOR_ADMIN, TRADER_ADMIN, SUPER_ADMIN -> true;
            default -> false;
        };
    }
}
