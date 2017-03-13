package ua.kpi.mobiledev.exception;

public class ResourceNotFoundException extends AbstractLocalizedException {

    public ResourceNotFoundException(ErrorCode errorCode, Object... params) {
        super(errorCode, params);
    }

}
