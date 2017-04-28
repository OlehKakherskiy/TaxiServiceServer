package ua.kpi.mobiledev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kpi.mobiledev.domain.*;
import ua.kpi.mobiledev.exception.RequestException;
import ua.kpi.mobiledev.exception.ResourceNotFoundException;
import ua.kpi.mobiledev.exception.SystemException;
import ua.kpi.mobiledev.repository.UserRepository;
import ua.kpi.mobiledev.util.LazyInitializationUtil;
import ua.kpi.mobiledev.web.security.model.Role;
import ua.kpi.mobiledev.web.security.model.SecurityDetails;
import ua.kpi.mobiledev.web.security.service.CustomUserDetailsService;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static ua.kpi.mobiledev.exception.ErrorCode.*;

@Service
@Transactional(readOnly = true)
@Component("userService")
public class TransactionalUserService implements UserService {

    private static final Predicate<MobileNumber> IS_NEW_NUMBER = mobileNumber -> isNull(mobileNumber.getIdMobileNumber());
    private UserRepository userRepository;
    private CustomUserDetailsService securityDetailsRepository;
    private LazyInitializationUtil lazyInitializationUtil;

    @Autowired
    public TransactionalUserService(UserRepository userRepository, CustomUserDetailsService securityDetailsRepository,
                                    LazyInitializationUtil lazyInitializationUtil) {
        this.userRepository = userRepository;
        this.securityDetailsRepository = securityDetailsRepository;
        this.lazyInitializationUtil = lazyInitializationUtil;
    }

    @Override
    public User getById(Integer userId) {
        User user = userRepository.findOne(userId);
        if (isNull(user)) {
            throw new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID, userId);
        }
        lazyInitializationUtil.initMobileNumbers(user);
        if (isTaxiDriver(user)) {
            lazyInitializationUtil.initCar((TaxiDriver) user);
            lazyInitializationUtil.initDriverLicense((TaxiDriver) user);
        }
        return user;
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByEmail(username); //TODO: throw exception instead of return null
    }

    @Override
    @Transactional
    public User register(User user, String password) {
        checkIfUserExists(user);
        User resultUser = userRepository.save(user);
        checkIfUserSaved(resultUser);
        securityDetailsRepository.registerNewUser(prepareSecurityDetails(resultUser, password));
        return resultUser;
    }

    private void checkIfUserExists(User user) {
        if (nonNull(securityDetailsRepository.fullLoadWithAuthorities(user.getEmail()))) {
            throw new RequestException(USER_ALREADY_EXISTS, user.getEmail());
        }
    }

    private void checkIfUserSaved(User resultUser) {
        if (isNull(resultUser)) {
            throw new SystemException(REGISTRATION_GENERAL_SYSTEM_EXCEPTION);
        }
    }

    private SecurityDetails prepareSecurityDetails(User user, String password) {
        return new SecurityDetails(user.getEmail(), password, "", true,
                asList(new Role(new SimpleGrantedAuthority("ROLE_" + user.getUserType().name()))));
    }

    @Override
    @Transactional
    public User update(User userPrototype) {
        if (nonNull(userPrototype.getEmail())) {
            throw new RequestException(CANNOT_UPDATE_EMAIL);
        }
        if (nonNull(userPrototype.getUserType())) {
            throw new RequestException(CANNOT_UPDATE_USER_TYPE);
        }

        User actualUser = getById(userPrototype.getId());

        if (nonNull(userPrototype.getName())) {
            actualUser.setName(userPrototype.getName());
        }
        updateMobileNumbers(actualUser, userPrototype);
        if (isTaxiDriver(actualUser)) {
            TaxiDriver taxiDriver = (TaxiDriver) actualUser;
            TaxiDriver taxiDriverPrototype = (TaxiDriver) userPrototype;
            taxiDriver.setCar(updateCar(taxiDriver.getCar(), taxiDriverPrototype.getCar()));
            taxiDriver.setDriverLicense(updateDriverLicense(taxiDriver.getDriverLicense(), taxiDriverPrototype.getDriverLicense()));
        }

        return userRepository.save(actualUser);
    }

    private boolean isTaxiDriver(User user) {
        return user.getUserType() == User.UserType.TAXI_DRIVER;
    }

    private void updateMobileNumbers(User actualUser, User userPrototype) {
        if (isNull(userPrototype.getMobileNumbers())) {
            return;
        }

        Map<Integer, MobileNumber> mPhones = actualUser.getMobileNumbers().stream()
                .collect(LinkedHashMap::new,
                        (map, mobileNumber) -> map.put(mobileNumber.getIdMobileNumber(), mobileNumber),
                        HashMap::putAll);

        List<MobileNumber> numbersToUpdate = userPrototype.getMobileNumbers();
        List<MobileNumber> newNumbers = numbersToUpdate.stream()
                .filter(IS_NEW_NUMBER)
                .collect(toList());
        numbersToUpdate.removeIf(IS_NEW_NUMBER);

        for (MobileNumber mobileNumber : numbersToUpdate) {
            MobileNumber targetNumber = ofNullable(mPhones.get(mobileNumber.getIdMobileNumber()))
                    .orElseThrow(() -> new RequestException(INVALID_MOBILE_NUMBER_ID, mobileNumber.getIdMobileNumber()));

            if (mobileNumber.getMobileNumber() == null) { //delete
                mPhones.remove(targetNumber.getIdMobileNumber());
            } else {//update
                targetNumber.setMobileNumber(mobileNumber.getMobileNumber());
            }
        }

        List<MobileNumber> resultNumbers = mPhones.values().stream().collect(toList());
        resultNumbers.addAll(newNumbers);
        actualUser.setMobileNumbers(resultNumbers);
    }

    private Car updateCar(Car toUpdate, Car prototype) {
        if (isNull(prototype)) {
            return toUpdate;
        }
        if (nonNull(prototype.getManufacturer())) {
            toUpdate.setManufacturer(prototype.getManufacturer());
        }
        if (nonNull(prototype.getModel())) {
            toUpdate.setModel(prototype.getModel());
        }
        if (nonNull(prototype.getCarType())) {
            toUpdate.setCarType(prototype.getCarType());
        }
        if (nonNull(prototype.getPlateNumber())) {
            toUpdate.setPlateNumber(prototype.getPlateNumber());
        }
        if (nonNull(prototype.getSeatNumber())) {
            toUpdate.setSeatNumber(prototype.getSeatNumber());
        }
        return toUpdate;
    }

    private DriverLicense updateDriverLicense(DriverLicense toUpdate, DriverLicense prototype) {
        if (isNull(prototype)) {
            return toUpdate;
        }
        if (nonNull(prototype.getDriverLicense())) {
            toUpdate.setDriverLicense(prototype.getDriverLicense());
        }
        if (nonNull(prototype.getExpirationTime())) {
            toUpdate.setExpirationTime(prototype.getExpirationTime());
        }
        return toUpdate;
    }
}
