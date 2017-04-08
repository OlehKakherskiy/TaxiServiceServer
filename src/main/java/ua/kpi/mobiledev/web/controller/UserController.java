package ua.kpi.mobiledev.web.controller;

import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.domain.dto.UserDto;
import ua.kpi.mobiledev.service.UserService;
import ua.kpi.mobiledev.web.converter.CustomConverter;

import javax.annotation.Resource;

@RestController
@Setter
public class UserController {

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "userConverter")
    private CustomConverter<User, UserDto> userConverter;

    @RequestMapping(path = "/user/register", method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void registerUser(@RequestBody /*@Valid*/ UserDto userDto) {
        User user = isCustomer(userDto.getUserType()) ? new User() : new TaxiDriver();
        userConverter.reverseConvert(userDto, user);

        userService.register(user, userDto.getPassword());
    }

    private boolean isCustomer(User.UserType userType) {
        return userType == User.UserType.CUSTOMER;
    }


//    @RequestMapping(path = "/user", method = RequestMethod.PUT, consumes = "application/json")
//    @PreAuthorize("#userDto.userId == authentication.details.id")
//    public void updateUserProfile(@Valid @RequestBody FullUserDto userDto) {
//        userService.update(FullUserDto.toUser(userDto));
//    }

}
