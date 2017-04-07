package ua.kpi.mobiledev.web.converter;

public interface CustomConverter<S, T> {
    void convert(S source, T target);
    void reverseConvert(T source, S target);
}
