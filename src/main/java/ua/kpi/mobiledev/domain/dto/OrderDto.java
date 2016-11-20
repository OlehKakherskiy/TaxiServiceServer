package ua.kpi.mobiledev.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderDto {

    private Integer customerId;

    private LocalDateTime startTime;

    private String startPoint;

    private String endPoint;

    private OrderPriceDto orderPrice;
}
