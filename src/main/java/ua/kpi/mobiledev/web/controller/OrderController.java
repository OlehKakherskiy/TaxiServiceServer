package ua.kpi.mobiledev.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.Order.OrderStatus;
import ua.kpi.mobiledev.domain.dto.*;
import ua.kpi.mobiledev.exception.ForbiddenOperationException;
import ua.kpi.mobiledev.exception.RequestException;
import ua.kpi.mobiledev.service.OrderService;
import ua.kpi.mobiledev.web.security.model.UserContext;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static java.util.Arrays.stream;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static ua.kpi.mobiledev.domain.User.UserType.TAXI_DRIVER;
import static ua.kpi.mobiledev.exception.ErrorCode.*;

@RestController
public class OrderController {

    private static String VALID_ORDER_STATUSES = "NEW/ACCEPTED/DONE/CANCELLED/ALL";

    static {
        VALID_ORDER_STATUSES = stream(OrderStatus.values())
                .map(OrderStatus::name)
                .collect(joining(","));
    }

    private OrderService orderService;

    private Validator orderPriceDtoValidatorForAdd;

    private Validator orderPriceDtoValidatorForCalculatePrice;

    private Validator futureTimeValidator;

    @Autowired
    public OrderController(OrderService orderService, Validator orderPriceDtoValidatorForAddOrder, Validator orderPriceDtoValidatorForCalculateOrderPrice, Validator futureTimeValidator) {
        this.orderService = orderService;
        this.orderPriceDtoValidatorForAdd = orderPriceDtoValidatorForAddOrder;
        this.orderPriceDtoValidatorForCalculatePrice = orderPriceDtoValidatorForCalculateOrderPrice;
        this.futureTimeValidator = futureTimeValidator;
    }

    @InitBinder("orderPriceDto")
    private void initOrderPriceBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(orderPriceDtoValidatorForCalculatePrice);
    }

    @InitBinder("orderDto")
    private void initStartTimeBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(futureTimeValidator);
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST, consumes = "application/json")
    public HttpStatus addOrder(@Valid @RequestBody OrderDto orderDto, BindingResult bindingResult, Authentication authentication) throws MethodArgumentNotValidException {
        checkIfValid(bindingResult);
        validate(orderPriceDtoValidatorForAdd, orderDto.getOrderPrice(), bindingResult);

        UserContext userContext = (UserContext) authentication.getDetails();
        if (userContext.getUserType() == TAXI_DRIVER) {
            throw new ForbiddenOperationException(TAXI_DRIVER_CANT_ADD_ORDER);
        }
        if (!userContext.getId().equals(orderDto.getCustomerId())) {
            throw new ForbiddenOperationException(USER_SHOULD_BE_ORDER_OWNER_WHEN_ADD, userContext.getId(), orderDto.getCustomerId());
        }

        Order order = orderService.addOrder(orderDto);
        return (nonNull(order) && nonNull(order.getOrderId()))
                ? HttpStatus.OK
                : HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private void validate(Validator validator, Object object, BindingResult bindingResult) throws MethodArgumentNotValidException {
        validator.validate(object, bindingResult);
        checkIfValid(bindingResult);
    }

    private void checkIfValid(BindingResult bindingResult) throws MethodArgumentNotValidException {
        if (bindingResult.hasErrors()) {
            throw new MethodArgumentNotValidException(null, bindingResult);
        }
    }

    @RequestMapping(value = "/order/price", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public PriceDto calculatePrice(@Valid @RequestBody OrderPriceDto orderPriceDto) {
        Double price = orderService.calculatePrice(orderPriceDto);
        Double roundedPrice = new BigDecimal(price).setScale(2, RoundingMode.UP).doubleValue();
        return new PriceDto(roundedPrice);
    }

    @RequestMapping(value = "/order/{orderId}/status", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void changeOrderStatus(@Valid @RequestBody OrderStatusDto orderStatusDto, @PathVariable("orderId") Long orderId) {
        orderService.changeOrderStatus(orderId, orderStatusDto.getUserId(), orderStatusDto.getOrderStatus());
    }

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<OrderSimpleDto> readAllOrders(@NotNull @RequestParam("orderStatus") String orderStatus) {
        String uppercaseOrderStatus = orderStatus.toUpperCase();
        return (uppercaseOrderStatus.equals("ALL"))
                ? mapToDto(orderService.getOrderList(null))
                : mapToDto(orderService.getOrderList(getFromName(uppercaseOrderStatus)));

    }

    private OrderStatus getFromName(String uppercaseOrderStatus) {
        try {
            return OrderStatus.valueOf(uppercaseOrderStatus);
        } catch (IllegalArgumentException e) {
            throw new RequestException(INVALID_ORDER_STATUS, uppercaseOrderStatus, VALID_ORDER_STATUSES);
        }
    }

    private List<OrderSimpleDto> mapToDto(List<Order> orderList) {
        return orderList.stream().map(OrderSimpleDto::of).collect(toList());
    }

    @RequestMapping(value = "/order/{orderId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public FullOrderDetailsDto getOrder(@NotNull @Min(0) @PathVariable("orderId") Long orderId) {
        return FullOrderDetailsDto.from(orderService.getOrder(orderId));
    }


    @RequestMapping(value = "/order/{orderId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @Secured("ROLE_CUSTOMER")
    public void deleteOrder(@NotNull @Min(0) @PathVariable("orderId") Long orderId, Authentication authentication) {

        orderService.deleteOrder(orderId, getUserId(authentication));
    }

    @RequestMapping(value = "/order/{orderId}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @Secured("ROLE_CUSTOMER")
    public void updateOrder(@NotNull @Min(0) @PathVariable("orderId") Long orderId,
                            @RequestBody @Valid OrderDto orderDto, BindingResult bindingResult, Authentication authentication) throws MethodArgumentNotValidException {
        checkIfValid(bindingResult);
        validate(orderPriceDtoValidatorForAdd, orderDto.getOrderPrice(), bindingResult);
        orderService.updateOrder(orderId, getUserId(authentication), orderDto);
    }

    private Integer getUserId(Authentication authentication) {
        UserContext userContext = (UserContext) authentication.getDetails();
        return userContext.getId();
    }
}