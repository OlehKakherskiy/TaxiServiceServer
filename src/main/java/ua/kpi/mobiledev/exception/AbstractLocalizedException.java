package ua.kpi.mobiledev.exception;

import lombok.Getter;

@Getter
public class AbstractLocalizedException extends RuntimeException {

    private ErrorCode errorCode;
    private Object[] params;

    public AbstractLocalizedException(ErrorCode errorCode, Object... params) {
        this.errorCode = errorCode;
        this.params = params;
    }

    public AbstractLocalizedException(ErrorCode errorCode, Throwable cause, Object... params) {
        super(cause);
        this.errorCode = errorCode;
        this.params = params;
    }
}
