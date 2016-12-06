package ua.kpi.mobiledev.domain.dto;

import lombok.*;

import java.util.*;

@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderPriceDto {

    private Double distance;

    private List<AddReqSimpleDto> additionalRequirements;

    public Map<Integer, Integer> paramsToMap() {
        Map<Integer, Integer> map = new HashMap<>();
        Optional.ofNullable(additionalRequirements).orElse(Collections.emptyList())
                .forEach(req -> map.put(req.getReqId(), req.getReqValueId()));
        return map;
    }
}
