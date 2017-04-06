package ua.kpi.mobiledev.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class FullOrderDetailsDto {

    private Long orderId;

    private String startTime;

    private String status;

    private UserSimpleDto customer;

    private TaxiDriverSimpleDto taxiDriver;

    private Double price;

    private List<RoutePointDto> addresses;

    private List<AddReqSimpleDto> additionalRequirements;

}
