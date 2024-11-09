package com.investmetic.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * ErrorCode API 응답에서 발생할 수 있는 오류 코드와 메시지 enum
 * 각 오류 상황에 대한 HttpStatus, 오류 코드, 설명 메시지
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 공통(Common) 오류
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, 1001, "잘못된 입력 값"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, 1002, "허용되지 않은 메서드"),
    ENTITY_NOT_FOUND(HttpStatus.BAD_REQUEST, 1003, "엔티티 찾을 수 없음"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1004, "서버 오류"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, 1005, "잘못된 유형 값"),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, 1006, "액세스가 거부됨"),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, 1007, "비정상적 접근"),
    INVALID_DATE(HttpStatus.BAD_REQUEST, 1008, "날짜 형식을 확인해주세요."),
    EMPTY_PATH_VARIABLE(HttpStatus.BAD_REQUEST, 1008, "필수 경로 변수가 누락되었습니다. 요청 경로에 올바른 값을 입력해 주세요."),

    // 전략 관련 오류
    PROPOSAL_NOT_FOUND(HttpStatus.NOT_FOUND, 3001, "해당 제안서를 찾을 수 없습니다."),
    STRATEGY_REGISTER_FAILED(HttpStatus.BAD_REQUEST, 3002, "전략 등록에 실패했습니다."),
    STRATEGY_NOT_FOUND(HttpStatus.NOT_FOUND, 3003, "전략을 찾을 수 없습니다."),
    STRATEGY_QUERY_ERROR(HttpStatus.BAD_REQUEST, 3005, "전략 조회 중 오류가 발생했습니다."),
    PROPOSAL_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, 3006, "제안서 업로드에 실패했습니다."),
    SUBSCRIPTION_CANCEL_FAILED(HttpStatus.BAD_REQUEST, 3007, "구독 취소에 실패했습니다."),
    FAVORITE_QUERY_FAILED(HttpStatus.BAD_REQUEST, 3008, "관심 전략 조회에 실패했습니다."),
    VISIBILITY_UPDATE_FAILED(HttpStatus.BAD_REQUEST, 3009, "전략 공개 여부 수정에 실패했습니다."),
    DAILY_ANALYSIS_QUERY_FAILED(HttpStatus.BAD_REQUEST, 3010, "전략 일간 분석 조회에 실패했습니다."),
    DAILY_ANALYSIS_UPDATE_FAILED(HttpStatus.BAD_REQUEST, 3011, "전략 일간 분석 등록/수정에 실패했습니다."),
    DAILY_ANALYSIS_DELETE_FAILED(HttpStatus.BAD_REQUEST, 3012, "전략 일간 분석 삭제에 실패했습니다."),
    DAILY_ANALYSIS_EXCEL_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, 3013, "일간 분석 엑셀 업로드에 실패했습니다."),
    DAILY_ANALYSIS_REGISTER_FAILED(HttpStatus.BAD_REQUEST, 3014, "해당 날짜 데이터 삭제를 실패했습니다."),
    ACCOUNT_IMAGE_QUERY_FAILED(HttpStatus.BAD_REQUEST, 3015, "실계좌 이미지 조회에 실패했습니다."),
    ACCOUNT_IMAGE_SAVE_FAILED(HttpStatus.BAD_REQUEST, 3016, "실계좌 이미지 등록/수정에 실패했습니다."),
    ACCOUNT_IMAGE_DELETE_FAILED(HttpStatus.BAD_REQUEST, 3017, "실계좌 이미지 삭제에 실패했습니다."),


    // 전략리뷰 관련오류(3300번대 );
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND,3301,"해당 전략의 리뷰를 찾을 수 없습니다.");




    private final HttpStatus status; // HTTP 상태 코드
    private final int code; // 고유 오류 코드
    private final String message; // 오류 메시지

    public int getStatusCode() {
        return status.value();
    }
}