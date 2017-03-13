package ua.kpi.mobiledev.exception;

public class SystemException extends AbstractLocalizedException {

    public SystemException(ErrorCode errorCode, Object... params) {
        super(errorCode, params);
    }

    public SystemException(ErrorCode errorCode, Throwable cause, Object... params) {
        super(errorCode, cause, params);
    }
}
