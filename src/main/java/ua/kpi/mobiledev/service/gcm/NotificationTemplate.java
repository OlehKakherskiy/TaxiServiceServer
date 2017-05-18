package ua.kpi.mobiledev.service.gcm;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class NotificationTemplate {
    private Map<String, String> data = new HashMap<>();
    private String to;

    public NotificationTemplate appendData(String key, String value) {
        data.put(key, value);
        return this;
    }

    public NotificationTemplate appendTo(String to) {
        this.to = to;
        return this;
    }

    public NotificationTemplate setData(Map<String, String> data){
        this.data = data;
        return this;
    }
}
