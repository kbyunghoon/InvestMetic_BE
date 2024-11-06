package com.investmetic.global.exception.error;

import com.investmetic.global.exception.BusinessException;

public class EntityNotFoundException extends BusinessException {
    public EntityNotFoundException(String message) {
        super(message, ErrorCode.ENTITY_NOT_FOUND);
    }
}