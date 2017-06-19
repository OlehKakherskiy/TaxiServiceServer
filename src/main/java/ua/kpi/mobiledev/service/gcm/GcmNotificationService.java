package ua.kpi.mobiledev.service.gcm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ua.kpi.mobiledev.domain.*;
import ua.kpi.mobiledev.repository.NotificationTokenRepository;
import ua.kpi.mobiledev.service.NotificationService;
import ua.kpi.mobiledev.service.integration.HttpRequestHelper;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

@Service("notificationService")
public class GcmNotificationService implements NotificationService {

    private static final String SEND_URL = "https://gcm-http.googleapis.com/gcm/send";
    private static final String ORDER_STATUS_KEY = "orderStatus";
    private static final String PLATE_NUMBER_KEY = "plateNumber";
    private static final String NAME_KEY = "name";
    private static final String ORDER_ID_KEY = "orderId";
    private static final String ROUTE_POINT_COORDINATES_FORMAT = "%s,%s";
    private static final String CAR_MODEL_KEY = "model";
    private static final String CAR_MANUFACTURER_KEY = "manufacturer";

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
        NotificationToken notificationToken = notificationTokenRepository.getNotificationToken(notifyUser);
        if (nonNull(notificationToken) && notificationToken.isSwitchOnNotification()) {
            httpRequestHelper.processPostRequest(SEND_URL, prepareHeaders(), prepareBody(order, whoSend, notificationToken.getToken()));
        }
    }

    @Override
    public void toggleNotifications(User user, boolean switchOn) {
        notificationTokenRepository.toggleNotificationToken(user, switchOn);
    }

    private NotificationTemplate prepareBody(Order order, User whoSend, String notificationToken) {
        return isCustomer(whoSend) ? notifyDriver(order, notificationToken) : notifyCustomer(order, (TaxiDriver) whoSend, notificationToken);
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

    private NotificationTemplate notifyDriver(Order order, String notificationToken) {
        return new NotificationTemplate()
                .appendTo(notificationToken)
                .appendData(ORDER_STATUS_KEY, order.getOrderStatus().name())
                .appendData(NAME_KEY, order.getCustomer().getName())
                .appendData(ORDER_ID_KEY, order.getOrderId().toString());
    }

    private NotificationTemplate notifyCustomer(Order order, TaxiDriver whoSend, String notificationToken) {
        NotificationTemplate result = new NotificationTemplate()
                .appendTo(notificationToken)
                .appendData(ORDER_STATUS_KEY, order.getOrderStatus().name())
                .appendData(NAME_KEY, whoSend.getName())
                .appendData(ORDER_ID_KEY, order.getOrderId().toString());
        if (driverIsWaiting(order)) {
            Car car = order.getTaxiDriver().getCar();
            result.appendData(PLATE_NUMBER_KEY, car.getPlateNumber());
            result.appendData(CAR_MODEL_KEY, car.getModel());
            result.appendData(CAR_MANUFACTURER_KEY, car.getManufacturer());
        }
        return result;
    }


    @Async
    @Override
    public void sendAddNewOrderNotification(Order order, User whoSend) {
        NotificationTemplate template = prepareAddNotificationTemplate(order);
        notificationTokenRepository.getDriverTokens()
                .forEach(notificationToken -> sendAddNewOrderNotification(template, notificationToken));
    }

    private boolean driverIsWaiting(Order order) {
        return order.getOrderStatus() == Order.OrderStatus.WAITING;
    }

    private NotificationTemplate prepareAddNotificationTemplate(Order order){
        List<RoutePoint> routePoints = order.getRoutePoints();
        int firstElementIndex = 0;
        int lastElementIndex = routePoints.size() - 1;
        RoutePoint startPoint = routePoints.get(firstElementIndex);
        RoutePoint endPoint = routePoints.get(lastElementIndex);

        return new NotificationTemplate()
                .appendData(ORDER_ID_KEY, order.getOrderId().toString())
                .appendData("startPoint", format(ROUTE_POINT_COORDINATES_FORMAT, startPoint.getLatitude(), startPoint.getLongtitude()))
                .appendData("endPoint", format(ROUTE_POINT_COORDINATES_FORMAT, endPoint.getLatitude(), endPoint.getLongtitude()))
                .appendData("startTime", order.getStartTime().toString())
                .appendData("price", order.getPrice().toString());

    }

    private void sendAddNewOrderNotification(NotificationTemplate notificationTemplate, NotificationToken sendTo) {
        NotificationTemplate completedTemplate = new NotificationTemplate().setData(notificationTemplate.getData()).appendTo(sendTo.getToken());
        httpRequestHelper.processPostRequest(SEND_URL, prepareHeaders(), completedTemplate);
    }

    @Override
    public boolean getNotificationTogglePosition(User user) {
        NotificationToken notificationToken = notificationTokenRepository.getNotificationToken(user);
        return nonNull(notificationToken) && notificationToken.isSwitchOnNotification();
    }
}
