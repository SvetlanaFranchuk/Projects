package org.example.pizzeria.service.user;

import org.example.pizzeria.dto.user.*;
import org.example.pizzeria.dto.user.auth.UserRegisterRequestDto;
import org.example.pizzeria.entity.benefits.Bonus;
import org.example.pizzeria.entity.benefits.Favorites;
import org.example.pizzeria.entity.benefits.Review;
import org.example.pizzeria.entity.order.Basket;
import org.example.pizzeria.entity.user.Address;
import org.example.pizzeria.entity.user.ContactInformation;
import org.example.pizzeria.entity.user.Role;
import org.example.pizzeria.entity.user.UserApp;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.user.StatusAlreadyExistsException;
import org.example.pizzeria.exception.user.UserCreateException;
import org.example.pizzeria.mapper.user.UserMapper;
import org.example.pizzeria.repository.benefits.FavoritesRepository;
import org.example.pizzeria.repository.benefits.ReviewRepository;
import org.example.pizzeria.repository.order.BasketRepository;
import org.example.pizzeria.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    public UserRepository userRepository;
    public BasketRepository basketRepository;
    public FavoritesRepository favoritesRepository;
    public ReviewRepository reviewRepository;
    public UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BasketRepository basketRepository, FavoritesRepository favoritesRepository, ReviewRepository reviewRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.basketRepository = basketRepository;
        this.favoritesRepository = favoritesRepository;
        this.reviewRepository = reviewRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserResponseDto save(UserRegisterRequestDto candidate, String password) {
        validateUserNotExist(candidate.userName(), candidate.email());
        UserApp newUser = userMapper.toUserApp(candidate, password);
        newUser = userRepository.save(newUser);
        Basket basket = new Basket();
        basket.setUserApp(newUser);
        basketRepository.save(basket);
        newUser.setBasket(basket);
        Favorites favorites = new Favorites();
        favorites.setUserApp(newUser);
        favoritesRepository.save(favorites);
        newUser.setFavorites(favorites);
        newUser = userRepository.save(newUser);
        return userMapper.toUserResponseDto(newUser);
    }

    private void validateUserNotExist(String userName, String email) {
        Optional<UserApp> userOptional = userRepository.findByUserNameAndEmail(userName, email);
        if (userOptional.isPresent()) {
            throw new UserCreateException(ErrorMessage.USER_ALREADY_EXIST);
        }
    }

    @Override
    public UserApp getByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND));
    }

    @Override
    public UserDetailsService userDetailsService() {
        return this::getByUserName;
    }

    @Override
    public UserApp getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUserName(username);
    }

    @Override
    public UserResponseDto getUser(Long id) {
        try {
            return userMapper.toUserResponseDto(userRepository.getReferenceById(id));
        } catch (RuntimeException e) {
            throw new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public UserResponseDto update(Long id, UserRequestDto userRequestDto, String password) {
        try {
            UserApp userApp = userRepository.getReferenceById(id);
            setDifferences(userApp, userRequestDto, password);
            userApp = userRepository.save(userApp);
            return userMapper.toUserResponseDto(userApp);
        } catch (RuntimeException e) {
            throw new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND);
        }
    }

    private void setDifferences(UserApp userApp, UserRequestDto userRequestDto, String password) {
        Address address = new Address(userRequestDto.addressCity(), userRequestDto.addressStreetName(),
                userRequestDto.addressHouseNumber(), userRequestDto.addressApartmentNumber());
        ContactInformation phoneNumber = new ContactInformation(userRequestDto.phoneNumber());
        userApp.setPassword(Objects.equals(userApp.getPassword(), password) ?
                userApp.getPassword() : password);
        userApp.setEmail(Objects.equals(userApp.getEmail(), userRequestDto.email()) ?
                userApp.getEmail() : userRequestDto.email());
        userApp.setBirthDate(userApp.getBirthDate().equals(userRequestDto.birthDate()) ?
                userApp.getBirthDate() : userRequestDto.birthDate());
        userApp.setAddress(Objects.equals(userApp.getAddress(), address) ? userApp.getAddress() : address);
        userApp.setPhoneNumber(Objects.equals(userApp.getPhoneNumber(), phoneNumber) ?
                userApp.getPhoneNumber() : phoneNumber);
    }

    @Override
    public List<UserResponseDto> getUsersByBirthday(LocalDate date) {
        List<UserApp> users = userRepository.findAllByBirthDate(date);
        if (users.isEmpty()) {
            throw new EntityInPizzeriaNotFoundException("Users", ErrorMessage.ENTITY_NOT_FOUND);
        }
        return users.stream()
                .map(userMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDtoForAdmin> getUserByClientRole() {
        return userRepository.findAllByRole(Role.ROLE_CLIENT).stream()
                .map(u -> userMapper.toUserResponseDtoForAdmin(u)).toList();
    }

    @Override
    public List<UserBlockedResponseDto> getUserBlocked() {
        List<UserApp> userApps = userRepository.findAllByIsBlocked(true).stream()
                .toList();
        return userApps.stream()
                .map(u -> userMapper.toUserBlockedResponseDto(u.getId(), u.getUsername(), u.isBlocked(),
                        Objects.requireNonNull(reviewRepository.findAllByUserApp(u).stream()
                                        .max(Comparator.comparing(Review::getReviewDate)).orElse(null))
                                .getReviewDate())).toList();
    }

    @Override
    @Transactional
    public UserBlockedResponseDto changeUserBlocking(Long id, boolean isBlocked) {
        UserApp userApp = userRepository.getReferenceById(id);
        Review review = reviewRepository.findAllByUserApp_Id(id).stream()
                .max(Comparator.comparing(Review::getReviewDate)).orElseThrow(() ->
                        new EntityInPizzeriaNotFoundException("Review", ErrorMessage.ENTITY_NOT_FOUND));
        if (userApp.isBlocked() == isBlocked) {
            throw new StatusAlreadyExistsException("User is already " + (isBlocked ? "blocked" : "unblocked"));
        }
        userApp.setBlocked(isBlocked);
        userApp = userRepository.save(userApp);
        return userMapper.toUserBlockedResponseDto(id, userApp.getUsername(), isBlocked, review.getReviewDate());
    }


    @Override
    public UserBonusDto getBonus(Long userId) {
        try {
            UserApp userApp = userRepository.getReferenceById(userId);
            return userMapper.toUserBonusDto(userApp.getBonus().getCountOrders(),
                    userApp.getBonus().getSumOrders());
        } catch (RuntimeException e) {
            throw new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public UserBonusDto updateBonus(Long userId, int count, double sum) {
        try {
            UserApp userApp = userRepository.getReferenceById(userId);
            Bonus newBonus = userApp.getBonus();
            newBonus.setCountOrders(newBonus.getCountOrders() + count);
            newBonus.setSumOrders(newBonus.getSumOrders() + sum);
            userApp.setBonus(newBonus);
            userApp = userRepository.save(userApp);
            return userMapper.toUserBonusDto(userApp.getBonus().getCountOrders(),
                    userApp.getBonus().getSumOrders());
        } catch (RuntimeException e) {
            throw new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND);
        }
    }

}
