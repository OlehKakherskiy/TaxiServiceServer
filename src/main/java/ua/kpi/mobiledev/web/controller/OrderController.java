package ua.kpi.mobiledev.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.domain.dto.*;
import ua.kpi.mobiledev.service.OrderService;
import ua.kpi.mobiledev.web.security.model.UserContext;

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

    private static String VALID_ORDER_STATUSES = "NEW/ACCEPTED/DONE/CANCELLED/ALL";

    static {
        VALID_ORDER_STATUSES = Arrays.stream(Order.OrderStatus.values())
                .map(Order.OrderStatus::name)
                .collect(Collectors.joining(","));
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
    @Secured("ROLE_CUSTOMER")
    @PreAuthorize("#orderDto.customerId == authentication.details.id")
    public HttpStatus addOrder(@Valid @RequestBody OrderDto orderDto, BindingResult bindingResult) throws MethodArgumentNotValidException {
        checkIfValid(bindingResult);
        validate(orderPriceDtoValidatorForAdd, orderDto.getOrderPrice(), bindingResult);

        Order order = orderService.addOrder(orderDto);
        return (Objects.nonNull(order) && Objects.nonNull(order.getOrderId()))
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
        return new PriceDto(orderService.calculatePrice(orderPriceDto));
    }

    @RequestMapping(value = "/order/{orderId}/status", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void changeOrderStatus(@Valid @RequestBody OrderStatusDto orderStatusDto, @PathVariable("orderId") Long orderId) {
        orderService.changeOrderStatus(orderId, orderStatusDto.getUserId(), orderStatusDto.getOrderStatus());
    }

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<OrderSimpleDto> readAllOrders(@NotNull @RequestParam("orderStatus") String orderStatus) {
        String uppercasedOrderStatus = orderStatus.toUpperCase();
        return (uppercasedOrderStatus.equals("ALL"))
                ? mapToDto(orderService.getOrderList(null))
                : mapToDto(orderService.getOrderList(getFromName(uppercasedOrderStatus)));

    }

    private Order.OrderStatus getFromName(String uppercasedOrderStatus) {
        try {
            return Order.OrderStatus.valueOf(uppercasedOrderStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(MessageFormat.format("invalid order status. Can't be {0}. Valid ones: {1}", uppercasedOrderStatus, VALID_ORDER_STATUSES));
        }
    }

    private List<OrderSimpleDto> mapToDto(List<Order> orderList) {
        return orderList.stream().map(OrderSimpleDto::of).collect(Collectors.toList());
    }

    @RequestMapping(value = "/order/{orderId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public FullOrderDetailsDto getOrder(@NotNull @Min(0) @PathVariable("orderId") Long orderId) {
        return FullOrderDetailsDto.from(orderService.getOrder(orderId));
    }


    @RequestMapping(value = "/order/{orderId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteOrder(@NotNull @Min(0) @PathVariable("orderId") Long orderId, Authentication authentication) {
        UserContext userContext = (UserContext) authentication.getDetails();
        Integer userId = userContext.getId();
        checkUserType(userContext.getUserType());
        if (!orderService.deleteOrder(orderId, userId)) {
            throw new SecurityException("User with id = " + userId + " is not order owner.");
        }
    }

    private void checkUserType(User.UserType userType) {
        if (userType == User.UserType.TAXI_DRIVER) {
            throw new SecurityException("Taxi driver can't delete the order");
        }
    }

    @RequestMapping(value = "/order/{orderId}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @Secured("ROLE_CUSTOMER")
    public void updateOrder(@NotNull @Min(0) @PathVariable("orderId") Long orderId,
                            @RequestBody @Valid OrderDto orderDto, BindingResult bindingResult) throws MethodArgumentNotValidException {
        checkIfValid(bindingResult);
        validate(orderPriceDtoValidatorForAdd, orderDto.getOrderPrice(), bindingResult);
        orderService.updateOrder(orderId, orderDto);
    }
}