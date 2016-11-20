package ua.kpi.mobiledev.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class OrderPriceDto {

    private Double distance;

    private Map<Integer, Integer> additionalRequestValueMap;
}
