package ua.kpi.mobiledev.web.сontroller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.dto.FullOrderDetailsDto;
import ua.kpi.mobiledev.domain.dto.OrderDto;
import ua.kpi.mobiledev.domain.dto.OrderPriceDto;
import ua.kpi.mobiledev.domain.dto.OrderStatusDto;
import ua.kpi.mobiledev.service.OrderService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class OrderController {

    private static String VALID_ORDER_STATUSES;

    static {
        VALID_ORDER_STATUSES = Arrays.stream(Order.OrderStatus.values())
                .map(Order.OrderStatus::name)
                .collect(Collectors.joining(","));
    }

    private OrderService orderService;

    private Validator orderPriceDtoValidator;


    @Autowired
    public OrderController(OrderService orderService, Validator orderPriceDtoValidator) {
        this.orderService = orderService;
        this.orderPriceDtoValidator = orderPriceDtoValidator;
    }

    @InitBinder
    private void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.setValidator(orderPriceDtoValidator);
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public HttpStatus addOrder(@Valid OrderDto orderDto) {
        Order order = orderService.addOrder(orderDto);
        return (Objects.nonNull(order) && Objects.nonNull(order.getOrderId()))
                ? HttpStatus.OK
                : HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @RequestMapping(value = "/order/price", method = RequestMethod.GET)
    public ResponseEntity<Double> calculatePrice(@Validated OrderPriceDto orderPriceDto) {
        Double price = orderService.calculatePrice(orderPriceDto);
        return Objects.nonNull(price) ? ResponseEntity.ok(price) : new ResponseEntity<>(-1.0, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/order/{orderId}/status", method = RequestMethod.PUT)
    public HttpStatus changeOrderStatus(@Valid OrderStatusDto orderStatusDto, @PathVariable("orderId") Long orderId) {
        Order changedStatusOrder = orderService.changeOrderStatus(orderId, orderStatusDto.getUserId(), orderStatusDto.getOrderStatus());
        return Objects.nonNull(changedStatusOrder) ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public ResponseEntity<List<OrderDto>> readAllOrders(@NotNull @RequestParam("orderStatus") String orderStatus) {
        String uppercasedOrderStatus = orderStatus.toUpperCase();
        List<OrderDto> orders = (uppercasedOrderStatus.equals("ALL"))
                ? mapToDto(orderService.getOrderList(null))
                : mapToDto(orderService.getOrderList(getFromName(uppercasedOrderStatus)));

        return ResponseEntity.ok(orders);
    }

    private Order.OrderStatus getFromName(String uppercasedOrderStatus) {
        try {
            return Order.OrderStatus.valueOf(uppercasedOrderStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(MessageFormat.format("invalid order status. Can't be {0}. Valid ones: {1}", uppercasedOrderStatus, VALID_ORDER_STATUSES));
        }
    }

    private List<OrderDto> mapToDto(List<Order> orderList) {
        return orderList.stream().map(OrderDto::from).collect(Collectors.toList());
    }

    @RequestMapping(value = "/order/{orderId}", method = RequestMethod.GET)
    public ResponseEntity<FullOrderDetailsDto> getOrder(@NotNull @Min(0) @PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(FullOrderDetailsDto.from(orderService.getOrder(orderId)));
    }


//    @RequestMapping(value = "/order/{orderId}", method = RequestMethod.DELETE)
//    public HttpStatus deleteOrder(@NotNull @Min(0) @PathVariable("orderId") Long orderId){
//        orderService.deleteOrder()
//    } //TODO: implement delete order with security

    @RequestMapping(value = "/order/{orderId}", method = RequestMethod.POST)
    public HttpStatus updateOrder(@NotNull @Min(0) @PathVariable("orderId") Long orderId, OrderDto orderDto) {
        return Objects.isNull(orderService.updateOrder(orderId, orderDto)) ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK;
    }
}