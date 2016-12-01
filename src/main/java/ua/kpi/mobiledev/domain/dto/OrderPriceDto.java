package ua.kpi.mobiledev.domain.dto;

import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        additionalRequirements.forEach(req -> map.put(req.getReqId(), req.getReqValueId()));
        return map;
    }

    public Double getDistance() {
        return distance;
    }
}
