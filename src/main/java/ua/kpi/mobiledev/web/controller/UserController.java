package ua.kpi.mobiledev.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.kpi.mobiledev.domain.dto.FullUserDto;
import ua.kpi.mobiledev.domain.dto.RegistrationUserDto;
import ua.kpi.mobiledev.service.UserService;

import javax.validation.Valid;

@RestController
public class UserController {

    private UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @RequestMapping(path = "/user/register", method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void registerUser(@RequestBody @Valid RegistrationUserDto registrationUserDto) {
        userService.register(RegistrationUserDto.toUser(registrationUserDto), registrationUserDto.getPassword());
    }

    @RequestMapping(path = "/user/{userId}", method = RequestMethod.GET)
    public FullUserDto getUserProfile(@PathVariable("userId") Integer userId) {
        return FullUserDto.toUserDto(userService.getById(userId));
    }


    @RequestMapping(path = "/user", method = RequestMethod.PUT, consumes = "application/json")
    @PreAuthorize("#userDto.userId == authentication.details.id")
    public void updateUserProfile(@Valid @RequestBody FullUserDto userDto) {
        userService.update(FullUserDto.toUser(userDto));
    }

}
