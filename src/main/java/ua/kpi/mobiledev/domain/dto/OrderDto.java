package ua.kpi.mobiledev.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderDto {

    @NotNull
    @Min(0)
    private Integer customerId;

    @NotNull
    @Future
    private LocalDateTime startTime;

    @NotNull
    @Size(min = 1)
    private String startPoint;

    @NotNull
    @Size(min = 1)
    private String endPoint;

    @NotNull
    @Valid
    private OrderPriceDto orderPrice;
}
