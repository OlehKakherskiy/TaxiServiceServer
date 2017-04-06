package ua.kpi.mobiledev.domain.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

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
        return ofNullable(additionalRequirements).orElse(emptyList())
                .stream()
                .collect(toMap(AddReqSimpleDto::getReqId, AddReqSimpleDto::getReqValueId));
    }
}
