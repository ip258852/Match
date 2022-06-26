package org.match.enums;

public enum ErrorCode {

    STOCK_NOT_ENOUGH("0123","stock not enough"),
    ORDER_PRICE_MUST_POSITIVE("0124","order price must positive"),


    SQL_INSERT_CNT_VALID("9123","SQL insert cnt valid.")

    ;

    private String code;
    private String msg;

    ErrorCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
