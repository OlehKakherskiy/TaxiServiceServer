package ua.kpi.mobiledev.web.security.token.extractor;

public interface TokenExtractor {
    public String extract(String payload);
}
