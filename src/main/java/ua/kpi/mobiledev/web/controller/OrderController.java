package ua.kpi.mobiledev.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.Order.OrderStatus;
import ua.kpi.mobiledev.domain.dto.OrderDto;
import ua.kpi.mobiledev.domain.dto.OrderPriceDto;
import ua.kpi.mobiledev.domain.dto.OrderSimpleDto;
import ua.kpi.mobiledev.domain.dto.OrderStatusDto;
import ua.kpi.mobiledev.domain.dto.PriceDto;
import ua.kpi.mobiledev.exception.RequestException;
import ua.kpi.mobiledev.service.OrderService;
import ua.kpi.mobiledev.web.converter.CustomConverter;
import ua.kpi.mobiledev.web.security.model.UserContext;

import javax.annotation.Resource;
import javax.validation.Valid;
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

    private static String VALID_ORDER_STATUSES = "NEW/ACCEPTED/DONE/CANCELLED/ALL";

    static {
        VALID_ORDER_STATUSES = stream(OrderStatus.values())
                .map(OrderStatus::name)
                .collect(joining(","));
    }

    @Resource(name = "orderConverter")
    private CustomConverter<OrderDto, Order> orderConverter;

    @Resource(name = "simpleOrderDtoConverter")
    private CustomConverter<Order, OrderSimpleDto> orderToSimpleOrderDtoConverter;

    @Resource(name = "orderPriceConverter")
    private CustomConverter<OrderPriceDto, Order> orderPriceConverter;

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
    @ResponseStatus(OK)
    public void addOrder(@Valid @RequestBody OrderDto orderDto, BindingResult bindingResult, Authentication authentication) throws MethodArgumentNotValidException {
        checkIfValid(bindingResult);
        validate(orderPriceDtoValidatorForAdd, orderDto.getOrderPrice(), bindingResult);

        UserContext userContext = (UserContext) authentication.getDetails();
        Order order = new Order();
        order.setOrderStatus(OrderStatus.NEW);
        orderConverter.convert(orderDto, order);
        orderService.addOrder(order, userContext.getId());
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
    @ResponseStatus(OK)
    public PriceDto calculatePrice(@Valid @RequestBody OrderPriceDto orderPriceDto) {
        Double price = orderService.calculatePrice(null);
        Double roundedPrice = new BigDecimal(price).setScale(2, RoundingMode.UP).doubleValue();
        return new PriceDto(roundedPrice);
    }

    @RequestMapping(value = "/order/{orderId}/status", method = RequestMethod.PUT)
    @ResponseStatus(OK)
    public void changeOrderStatus(@Valid @RequestBody OrderStatusDto orderStatusDto, @PathVariable("orderId") Long orderId) {
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
    @Secured("ROLE_CUSTOMER")
    public void deleteOrder(@NotNull @Min(0) @PathVariable("orderId") Long orderId, Authentication authentication) {

        orderService.deleteOrder(orderId, getUserId(authentication));
    }

    @RequestMapping(value = "/order/{orderId}", method = RequestMethod.PUT)
    @ResponseStatus(OK)
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