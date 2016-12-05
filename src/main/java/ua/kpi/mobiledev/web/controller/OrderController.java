package ua.kpi.mobiledev.web.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.dto.*;
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

    private Validator futureTimeValidator;

    @Autowired
    public OrderController(OrderService orderService, Validator orderPriceDtoValidator, Validator futureTimeValidator) {
        this.orderService = orderService;
        this.orderPriceDtoValidator = orderPriceDtoValidator;
        this.futureTimeValidator = futureTimeValidator;
    }

    @InitBinder("orderPriceDto")
    private void initOrderPriceBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(orderPriceDtoValidator);
    }

    @InitBinder("orderDto")
    private void initStartTimeBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(futureTimeValidator);
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public HttpStatus addOrder(@Valid @RequestBody OrderDto orderDto) {
        Order order = orderService.addOrder(orderDto);
        return (Objects.nonNull(order) && Objects.nonNull(order.getOrderId()))
                ? HttpStatus.OK
                : HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @RequestMapping(value = "/order/price", method = RequestMethod.POST)
    public ResponseEntity<PriceDto> calculatePrice(@Validated @RequestBody OrderPriceDto orderPriceDto) {
        return ResponseEntity.ok(new PriceDto(orderService.calculatePrice(orderPriceDto)));
    }

    @RequestMapping(value = "/order/{orderId}/status", method = RequestMethod.PUT)
    public HttpStatus changeOrderStatus(@Valid @RequestBody OrderStatusDto orderStatusDto, @PathVariable("orderId") Long orderId) {
        orderService.changeOrderStatus(orderId, orderStatusDto.getUserId(), orderStatusDto.getOrderStatus());
        return HttpStatus.OK;
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
    public HttpStatus updateOrder(@NotNull @Min(0) @PathVariable("orderId") Long orderId,
                                  @RequestBody OrderDto orderDto, BindingResult bindingResult) throws MethodArgumentNotValidException {
        futureTimeValidator.validate(orderDto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new MethodArgumentNotValidException(null, bindingResult);
        } else {
            return updateAndOk(orderId, orderDto);
        }
    }

    private HttpStatus updateAndOk(Long orderId, OrderDto orderDto) {
        orderService.updateOrder(orderId, orderDto);
        return HttpStatus.OK;
    }
}