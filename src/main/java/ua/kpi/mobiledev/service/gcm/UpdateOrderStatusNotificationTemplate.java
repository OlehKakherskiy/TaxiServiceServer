package ua.kpi.mobiledev.service.gcm;

import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Data
@Getter
public class UpdateOrderStatusNotificationTemplate {
    private Map<String, String> data = new HashMap<>();
    private String to;

    public UpdateOrderStatusNotificationTemplate appendData(String key, String value){
        data.put(key, value);
        return this;
    }

    public UpdateOrderStatusNotificationTemplate appendTo(String to){
        this.to = to;
        return this;
    }
}
