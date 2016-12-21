package ua.kpi.mobiledev.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapper {
    public static String toJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
