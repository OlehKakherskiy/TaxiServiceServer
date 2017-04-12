package ua.kpi.mobiledev.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class AbstractLocalizedException extends RuntimeException {

    private ErrorCode errorCode;
    private Object[] params = new Object[0];

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
