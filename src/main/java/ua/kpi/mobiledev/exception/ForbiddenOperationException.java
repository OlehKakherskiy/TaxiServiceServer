package ua.kpi.mobiledev.exception;

public class ForbiddenOperationException extends AbstractLocalizedException {

    public ForbiddenOperationException(ErrorCode errorCode, Object... params) {
        super(errorCode, params);
    }
}
