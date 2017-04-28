package ua.kpi.mobiledev.web;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class CustomAcceptHeaderLocaleResolver implements LocaleResolver {

    private static final String ACCEPT_LANGUAGE = "Accept-Language";
    private static final String DELIMITER = "_";
    private static final int LOCALE_ELEMENT_COUNT = 2;
    private static final int LANGUAGE_INDEX = 0;
    private static final int COUNTRY_INDEX = 1;

    private Locale defaultLocale;

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String locale = request.getHeader(ACCEPT_LANGUAGE);
        if (StringUtils.isEmpty(locale)) {
            return getDefaultLocale();
        }
        String[] localeParts = locale.split(DELIMITER);
        if (localeParts.length != LOCALE_ELEMENT_COUNT) {
            return getDefaultLocale();
        }
        return new Locale(localeParts[LANGUAGE_INDEX], localeParts[COUNTRY_INDEX]);
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
    }

    private Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }
}
