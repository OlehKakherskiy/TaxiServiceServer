package ua.kpi.mobiledev.web.controller;

import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.service.NotificationService;
import ua.kpi.mobiledev.service.ResetPasswordService;
import ua.kpi.mobiledev.service.UserService;
import ua.kpi.mobiledev.web.converter.CustomConverter;
import ua.kpi.mobiledev.web.dto.EmailDto;
import ua.kpi.mobiledev.web.dto.ResetPasswordDto;
import ua.kpi.mobiledev.web.dto.UserDto;
import ua.kpi.mobiledev.web.security.model.UserContext;

import javax.annotation.Resource;

import static java.util.Objects.isNull;

@RestController
@Setter
public class UserController {

    @Resource(name = "userService")
    private UserService userService;

    @Resource
    private NotificationService notificationService;

    @Resource(name = "userConverter")
    private CustomConverter<User, UserDto> userConverter;

    @Resource(name = "resetPasswordService")
    private ResetPasswordService resetPasswordService;

    @RequestMapping(path = "/user/register", method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void registerUser(@RequestBody @Validated({UserDto.AddUserCheck.class}) UserDto userDto) {
        User user = isCustomer(userDto.getUserType()) ? new User() : new TaxiDriver();
        userConverter.reverseConvert(userDto, user);

        userService.register(user, userDto.getPassword());
    }

    private boolean isCustomer(User.UserType userType) {
        return userType == User.UserType.CUSTOMER;
    }

    @RequestMapping(path = "/user/{userId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserProfile(@PathVariable Integer userId) {
        UserDto result = new UserDto();
        userConverter.convert(userService.getById(userId), result);
        return result;
    }

    @RequestMapping(path = "/user", method = RequestMethod.PATCH, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void updateUserProfile(@RequestBody @Validated({UserDto.UpdateUserCheck.class}) UserDto userDto, Authentication authentication) {
        UserContext userContext = (UserContext) authentication.getDetails();
        Integer userId = userContext.getUser().getId();
        User user = isCustomer(userContext.getUserType()) ? new User() : new TaxiDriver();
        user.setId(userId);
        userDto.setUserType(userContext.getUserType());
        userConverter.reverseConvert(userDto, user);
        user.setUserType(null); //to not update user type
        userService.update(user);
    }

    @RequestMapping(path = "/user/password", method = RequestMethod.POST, consumes = "application/json")
    public String resetPasswordRequest(@RequestBody @Validated EmailDto emailDto) {
        return resetPasswordService.resetPassword(emailDto.getEmail());
    }

    @RequestMapping(path = "/user/password/{passwordToken}", method = RequestMethod.POST, consumes = "application/json")
    public void resetPasswordAction(@RequestBody @Validated ResetPasswordDto resetPasswordDto, @PathVariable String passwordToken) {
        resetPasswordService.resetPassword(resetPasswordDto, passwordToken);
    }

    @RequestMapping(path = "/user/notifications", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void toggleNotifications(@RequestParam("toggle") Boolean switchOn, Authentication authentication){
        UserContext userContext = (UserContext) authentication.getDetails();
        notificationService.toggleNotifications(userContext.getUser(), isNull(switchOn) ? true : switchOn);
    }

    @RequestMapping(path = "/user/notifications/toggle-position", method = RequestMethod.GET)
    public Boolean getNotificationTokenTogglePosition(Authentication authentication) {
        UserContext userContext = (UserContext) authentication.getDetails();
        return notificationService.getNotificationTogglePosition(userContext.getUser());
    }

}
