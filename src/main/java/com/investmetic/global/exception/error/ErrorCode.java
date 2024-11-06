package com.investmetic.global.exception.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT) // JSON으로 변환 시 객체 형식 유지
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(400, 1001, "잘못된 입력 값"),
    METHOD_NOT_ALLOWED(405, 1002, "허용되지 않은 메서드"),
    ENTITY_NOT_FOUND(400, 1003, "엔티티 찾을 수 없음"),
    INTERNAL_SERVER_ERROR(500, 1004, "서버 오류"),
    INVALID_TYPE_VALUE(400, 1005, "잘못된 유형 값"),
    HANDLE_ACCESS_DENIED(403, 1006, "액세스가 거부됨"),
    FORBIDDEN_ACCESS(403, 1007, "비정상적 접근"),

    // 수정 요청
    // METHOD_NOT_ALLOWED(405, 1002, "허용되지 않은 메서드입니다."),
    // ENTITY_NOT_FOUND(400, 1003, "엔티티를 찾을 수 없습니다."),
    // INTERNAL_SERVER_ERROR(500, 1004, "서버 오류가 발생했습니다."),
    // INVALID_TYPE_VALUE(400, 1005, "잘못된 유형 값입니다."),
    // ACCESS_DENIED(403, 1006, "접근 권한이 없습니다."), // 통합된 권한 오류

    // 전략 관련 오류
    PROPOSAL_NOT_FOUND(404, 3001, "해당 제안서를 찾을 수 없습니다."),
    STRATEGY_REGISTER_FAILED(400, 3002, "전략 등록에 실패했습니다."),
    STRATEGY_NOT_FOUND(404, 3003, "전략을 찾을 수 없습니다."),
    STRATEGY_QUERY_ERROR(400, 3005, "전략 조회 중 오류가 발생했습니다."),
    PROPOSAL_UPLOAD_FAILED(400, 3006, "제안서 업로드에 실패했습니다."),
    SUBSCRIPTION_CANCEL_FAILED(400, 3007, "구독 취소에 실패했습니다."),
    FAVORITE_QUERY_FAILED(400, 3008, "관심 전략 조회에 실패했습니다."),
    VISIBILITY_UPDATE_FAILED(400, 3009, "전략 공개 여부 수정에 실패했습니다."),
    DAILY_ANALYSIS_QUERY_FAILED(400, 3010, "전략 일간 분석 조회에 실패했습니다."),
    DAILY_ANALYSIS_UPDATE_FAILED(400, 3011, "전략 일간 분석 등록/수정에 실패했습니다."),
    DAILY_ANALYSIS_DELETE_FAILED(400, 3012, "전략 일간 분석 삭제에 실패했습니다."),
    DAILY_ANALYSIS_EXCEL_UPLOAD_FAILED(400, 3013, "일간 분석 엑셀 업로드에 실패했습니다."),
    DAILY_ANALYSIS_REGISTER_FAILED(400, 3014, "해당 날짜 데이터 삭제를 실패했습니다."),
    ACCOUNT_IMAGE_QUERY_FAILED(400, 3015, "실계좌 이미지 조회에 실패했습니다."),
    ACCOUNT_IMAGE_SAVE_FAILED(400, 3016, "실계좌 이미지 등록/수정에 실패했습니다."),
    ACCOUNT_IMAGE_DELETE_FAILED(400, 3017, "실계좌 이미지 삭제에 실패했습니다.");

    private final int status; // HTTP 상태 코드
    private final int code;   // 에러 고유 코드
    private final String message; // 에러 메시지

    ErrorCode(final int status, final int code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}