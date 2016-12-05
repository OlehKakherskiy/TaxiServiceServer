package ua.kpi.mobiledev.domain.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class AddReqSimpleDto {

    private Integer reqId;

    private Integer reqValueId;
}
