package ua.kpi.mobiledev.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Created by Oleg on 06.11.2016.
 */
@Getter
@AllArgsConstructor
public class OrderDto {

    private Integer customerId;

    private LocalDateTime startTime;

    private String startPoint;

    private String endPoint;

    private Map<Integer, Integer> additionalRequestValueMap;
}
