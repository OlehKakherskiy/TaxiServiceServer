package ua.kpi.mobiledev.service.gcm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.repository.NotificationTokenRepository;
import ua.kpi.mobiledev.service.NotificationService;
import ua.kpi.mobiledev.service.integration.HttpRequestHelper;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service("notificationService")
public class GcmNotificationService implements NotificationService {

    private static final String SEND_URL = "https://gcm-http.googleapis.com/gcm/send";
    private static final String ORDER_STATUS_KEY = "orderStatus";
    private static final String PLATE_NUMBER_KEY = "plateNumber";

    @Resource(name = "httpRequestHelper")
    private HttpRequestHelper httpRequestHelper;

    @Resource(name = "notificationTokenRepository")
    private NotificationTokenRepository notificationTokenRepository;

    @Value("${gcm.serverKey}")
    private String authToken;

    @Async
    @Override
    public void sendUpdateOrderNotification(Order order, User whoSend) {
        User notifyUser = isCustomer(whoSend) ? order.getTaxiDriver() : order.getCustomer();
        String notificationToken = notificationTokenRepository.getNotificationToken(notifyUser);
        if (!StringUtils.isEmpty(notificationToken)) {
            httpRequestHelper.processPostRequest(SEND_URL, prepareHeaders(), prepareBody(order, whoSend, notificationToken));
        }
    }

    private UpdateOrderStatusNotificationTemplate prepareBody(Order order, User whoSend, String notificationToken) {
        return isCustomer(whoSend) ? notifyDriver(order, notificationToken) : notifyCustomer(order, notificationToken);
    }

    private Map<String, String> prepareHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.put(HttpHeaders.AUTHORIZATION, "key=" + authToken);
        return headers;
    }

    private boolean isCustomer(User user) {
        return user.getUserType() == User.UserType.CUSTOMER;
    }

    private UpdateOrderStatusNotificationTemplate notifyDriver(Order order, String notificationToken) {
        return new UpdateOrderStatusNotificationTemplate()
                .appendTo(notificationToken)
                .appendData(ORDER_STATUS_KEY, order.getOrderStatus().name());
    }

    private UpdateOrderStatusNotificationTemplate notifyCustomer(Order order, String notificationToken) {
        UpdateOrderStatusNotificationTemplate result = new UpdateOrderStatusNotificationTemplate()
                .appendTo(notificationToken)
                .appendData(ORDER_STATUS_KEY, order.getOrderStatus().name());
        if (driverIsWaiting(order)) {
            result.appendData(PLATE_NUMBER_KEY, order.getTaxiDriver().getCar().getPlateNumber());
        }
        return result;
    }


    private boolean driverIsWaiting(Order order) {
        return order.getOrderStatus() == Order.OrderStatus.WAITING;
    }
}
