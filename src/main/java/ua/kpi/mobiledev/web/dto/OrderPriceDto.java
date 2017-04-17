package ua.kpi.mobiledev.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import java.util.List;

import static java.util.Collections.emptyList;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OrderPriceDto {

    public interface PriceInfoCheck extends RoutePointDto.FullRoutePointCheck, AddReqSimpleDto.AdditionalRequirementCheck {
    }

    @Valid
    private List<RoutePointDto> routePoints = emptyList();
    @Valid
    private List<AddReqSimpleDto> additionalRequirements = emptyList();

    private Double price = 0.0;

    public OrderPriceDto(Double price) {
        this.price = price;
    }
}
