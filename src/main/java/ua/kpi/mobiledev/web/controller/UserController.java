package ua.kpi.mobiledev.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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

}
