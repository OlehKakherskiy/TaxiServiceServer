package ua.kpi.mobiledev.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

import static java.util.Collections.emptyList;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OrderPriceDto {
    private List<RoutePointDto> routePoints = emptyList();
    private List<AddReqSimpleDto> additionalRequirements = emptyList();
    private Double price = 0.0;

    public OrderPriceDto(Double price) {
        this.price = price;
    }
}
