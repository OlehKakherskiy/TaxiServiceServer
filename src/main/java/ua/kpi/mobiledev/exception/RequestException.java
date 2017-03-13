package ua.kpi.mobiledev.exception;

public class RequestException extends AbstractLocalizedException {

    public RequestException(ErrorCode errorCode, Object... params) {
        super(errorCode, params);
    }
}
