package ua.kpi.mobiledev.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.Order.OrderStatus;
import ua.kpi.mobiledev.exception.RequestException;
import ua.kpi.mobiledev.service.OrderService;
import ua.kpi.mobiledev.web.converter.CustomConverter;
import ua.kpi.mobiledev.web.dto.OrderDto;
import ua.kpi.mobiledev.web.dto.OrderPriceDto;
import ua.kpi.mobiledev.web.dto.OrderSimpleDto;
import ua.kpi.mobiledev.web.dto.OrderStatusDto;
import ua.kpi.mobiledev.web.security.model.UserContext;

import javax.annotation.Resource;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;
import static ua.kpi.mobiledev.exception.ErrorCode.INVALID_ORDER_STATUS;

@RestController
public class OrderController {

    private static String VALID_ORDER_STATUSES;

    static {
        VALID_ORDER_STATUSES = stream(OrderStatus.values())
                .map(OrderStatus::name)
                .collect(joining(","));
    }

    @Resource(name = "orderConverter")
    private CustomConverter<OrderDto, Order> orderConverter;

    @Resource(name = "orderPricePopulator")
    private CustomConverter<OrderPriceDto, Order> orderPricePopulator;

    @Resource(name = "simpleOrderDtoConverter")
    private CustomConverter<Order, OrderSimpleDto> orderToSimpleOrderDtoConverter;

    @Resource(name = "orderService")
    private OrderService orderService;

    @RequestMapping(value = "/order", method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(OK)
    public void addOrder(@RequestBody @Validated(OrderDto.AddOrderGroup.class) OrderDto orderDto, Authentication authentication) {
        UserContext userContext = (UserContext) authentication.getDetails();
        Order order = new Order();
        order.fillDefaultAdditionalParameters();
        order.setOrderStatus(OrderStatus.NEW);
        orderConverter.convert(orderDto, order);
        orderService.addOrder(order, userContext.getId());
    }

    @RequestMapping(value = "/order/price", method = RequestMethod.POST)
    @ResponseStatus(OK)
    public OrderPriceDto calculatePrice(@RequestBody @Validated OrderPriceDto orderPriceDto) {
        Order orderWithPriceData = new Order();
        orderPricePopulator.convert(orderPriceDto, orderWithPriceData);
        Double price = orderService.calculatePrice(orderWithPriceData);
        Double roundedPrice = new BigDecimal(price).setScale(2, RoundingMode.UP).doubleValue();
        return new OrderPriceDto(roundedPrice);
    }

    @RequestMapping(value = "/order/{orderId}/status", method = RequestMethod.PATCH)
    @ResponseStatus(OK)
    public void changeOrderStatus(@RequestBody @Validated OrderStatusDto orderStatusDto, @PathVariable("orderId") Long orderId) {
        orderService.changeOrderStatus(orderId, orderStatusDto.getUserId(), orderStatusDto.getOrderStatus());
    }

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    @ResponseStatus(OK)
    public List<OrderSimpleDto> readAllOrders(@NotNull @RequestParam("orderStatus") String orderStatus) {
        String uppercaseOrderStatus = orderStatus.toUpperCase();
        OrderStatus status = uppercaseOrderStatus.equals("ALL") ? null : getFromName(uppercaseOrderStatus);
        return mapToDto(orderService.getOrderList(status));
    }

    private List<OrderSimpleDto> mapToDto(List<Order> orderList) {
        return orderList.stream().map(this::convertToDto).collect(toList());
    }

    private OrderSimpleDto convertToDto(Order order) {
        OrderSimpleDto orderSimpleDto = new OrderSimpleDto();
        orderToSimpleOrderDtoConverter.convert(order, orderSimpleDto);
        return orderSimpleDto;
    }

    private OrderStatus getFromName(String uppercaseOrderStatus) {
        try {
            return OrderStatus.valueOf(uppercaseOrderStatus);
        } catch (IllegalArgumentException e) {
            throw new RequestException(INVALID_ORDER_STATUS, uppercaseOrderStatus, VALID_ORDER_STATUSES);
        }
    }

    @RequestMapping(value = "/order/{orderId}", method = RequestMethod.GET)
    @ResponseStatus(OK)
    public OrderDto getOrder(@NotNull @Min(0) @PathVariable("orderId") Long orderId) {
        OrderDto responseBody = new OrderDto();
        orderConverter.reverseConvert(orderService.getOrder(orderId), responseBody);
        return responseBody;
    }


    @RequestMapping(value = "/order/{orderId}", method = RequestMethod.DELETE)
    @ResponseStatus(OK)
    public void deleteOrder(@NotNull @Min(0) @PathVariable("orderId") Long orderId, Authentication authentication) {
        orderService.deleteOrder(orderId, getUserId(authentication));
    }

    @RequestMapping(value = "/order/{orderId}", method = RequestMethod.PATCH)
    @ResponseStatus(OK)
    public void updateOrder(@NotNull @Min(0) @PathVariable("orderId") Long orderId,
                            @RequestBody @Validated(OrderDto.UpdateOrderGroup.class) OrderDto orderDto, Authentication authentication) {
        Order orderToUpdate = new Order();
        orderToUpdate.setOrderId(orderId);
        orderConverter.convert(orderDto, orderToUpdate);
        orderService.updateOrder(orderToUpdate, getUserId(authentication));
    }

    private Integer getUserId(Authentication authentication) {
        UserContext userContext = (UserContext) authentication.getDetails();
        return userContext.getId();
    }
}