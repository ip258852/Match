package org.match.exception;

import org.match.enums.ErrorCode;

public class MySelfException extends RuntimeException{

    private ErrorCode errorCode;

    public MySelfException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
