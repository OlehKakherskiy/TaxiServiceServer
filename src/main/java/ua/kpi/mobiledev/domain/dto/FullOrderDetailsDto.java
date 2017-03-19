package ua.kpi.mobiledev.domain.dto;

import lombok.Getter;
import ua.kpi.mobiledev.domain.Order;

import java.util.List;
import java.util.Objects;

@Getter
public class FullOrderDetailsDto {

    public static FullOrderDetailsDto from(Order order) {
        FullOrderDetailsDto orderDetailsDto = new FullOrderDetailsDto();
        orderDetailsDto.orderId = order.getOrderId();
        orderDetailsDto.startTime = order.getStartTime().toString();
        orderDetailsDto.status = order.getOrderStatus().name();
        orderDetailsDto.customer = new UserSimpleDto(order.getCustomer().getId(), order.getCustomer().getName());
        if (Objects.nonNull(order.getTaxiDriver())) {
            orderDetailsDto.taxiDriver = new TaxiDriverSimpleDto(order.getTaxiDriver().getId(), order.getTaxiDriver().getName());
        }
        orderDetailsDto.price = order.getPrice();
        return orderDetailsDto;
    }

    private Long orderId;

    private String startTime;

    private String startPoint;

    private String endPoint;

    private String status;

    private UserSimpleDto customer;

    private TaxiDriverSimpleDto taxiDriver;

    private Double price;

    private List<AddReqSimpleDto> additionalRequirements;

}
