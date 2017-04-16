package ua.kpi.mobiledev.web.validation.groupprovider;

import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.domain.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class UserDtoGroupProvider implements DefaultGroupSequenceProvider<UserDto> {

    @Override
    public List<Class<?>> getValidationGroups(UserDto userDto) {
        List<Class<?>> groups = new ArrayList<>();
        groups.add(UserDto.class);
        if (isNull(userDto)) {
            return groups;
        }
        if (nonNull(userDto.getName())) {
            groups.add(UserDto.NameCheck.class);
        }
        if (nonNull(userDto.getMobileNumbers()) && userDto.getMobileNumbers().size() > 0) {
            groups.add(UserDto.UpdateUserCheck.class);
        }

        if (userDto.getUserType() == User.UserType.TAXI_DRIVER) {
            groups.add(UserDto.DriverAddMandatoryParams.class);
        }
        return groups;
    }
}
