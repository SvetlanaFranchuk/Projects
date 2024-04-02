package org.example.pizzeria.service.user;

import org.example.pizzeria.dto.benefits.ReviewRequestDto;
import org.example.pizzeria.dto.benefits.ReviewResponseDto;
import org.example.pizzeria.dto.user.*;
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
import org.example.pizzeria.exception.InvalidIDException;
import org.example.pizzeria.exception.user.StatusAlreadyExistsException;
import org.example.pizzeria.exception.user.UpdateReviewException;
import org.example.pizzeria.exception.user.UserBlockedException;
import org.example.pizzeria.exception.user.UserCreateException;
import org.example.pizzeria.mapper.benefits.ReviewMapper;
import org.example.pizzeria.mapper.user.UserMapper;
import org.example.pizzeria.repository.benefits.FavoritesRepository;
import org.example.pizzeria.repository.benefits.ReviewRepository;
import org.example.pizzeria.repository.order.BasketRepository;
import org.example.pizzeria.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public ReviewMapper reviewMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BasketRepository basketRepository, FavoritesRepository favoritesRepository, ReviewRepository reviewRepository, UserMapper userMapper, ReviewMapper reviewMapper) {
        this.userRepository = userRepository;
        this.basketRepository = basketRepository;
        this.favoritesRepository = favoritesRepository;
        this.reviewRepository = reviewRepository;
        this.userMapper = userMapper;
        this.reviewMapper = reviewMapper;
    }

    @Override
    @Transactional
    public UserResponseDto save(UserRegisterRequestDto candidate) {
        validateUserNotExist(candidate.userName(), candidate.email());
        UserApp newUser = userMapper.toUserApp(candidate);
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
    public UserResponseDto getUser(Long id) {
        try {
            return userMapper.toUserResponseDto(userRepository.getReferenceById(id));
        } catch (RuntimeException e) {
            throw new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public UserResponseDto update(Long id, UserRequestDto userRequestDto) {
        try {
            UserApp userApp = userRepository.getReferenceById(id);
            setDifferences(userApp, userRequestDto);
            userApp = userRepository.save(userApp);
            return userMapper.toUserResponseDto(userApp);
        } catch (RuntimeException e) {
            throw new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND);
        }
    }

    private void setDifferences(UserApp userApp, UserRequestDto userRequestDto) {
        Address address = new Address(userRequestDto.addressCity(), userRequestDto.addressStreetName(),
                userRequestDto.addressHouseNumber(), userRequestDto.addressApartmentNumber());
        ContactInformation phoneNumber = new ContactInformation(userRequestDto.phoneNumber());
        userApp.setPassword(Objects.equals(userApp.getPassword(), userRequestDto.password()) ?
                userApp.getPassword() : userRequestDto.password());
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
        List<UserApp> users = userRepository.findUserAppByBirthDate(date);
        if (users.isEmpty()) {
            throw new EntityInPizzeriaNotFoundException("Users", ErrorMessage.ENTITY_NOT_FOUND);
        }
        return users.stream()
                .map(userMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDto> getUserByClientRole() {
        return userRepository.findAllByRole(Role.CLIENT).stream()
                .map(u -> userMapper.toUserResponseDto(u)).toList();
    }

    @Override
    public List<UserBlockedResponseDto> getUserBlocked() {
        List<UserApp> userApps = userRepository.findAllByIsBlocked(true).stream()
                .toList();
        return userApps.stream()
                .map(u -> userMapper.toUserBlockedResponseDto(u.getId(), u.getUserName(), u.isBlocked(),
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
        return userMapper.toUserBlockedResponseDto(id, userApp.getUserName(), isBlocked, review.getReviewDate());
    }


    @Override
    public UserBonusDto getUserBonus(Long userId) {
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
    public UserBonusDto updateUserBonus(Long userId, int count, double sum) {
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

    @Override
    @Transactional
    public ReviewResponseDto addReview(ReviewRequestDto reviewRequestDto, Long userId) {
        UserApp userApp = userRepository.findById(userId)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND));
        if (userApp.isBlocked())
            throw new UserBlockedException(ErrorMessage.USER_BLOCKED);
        Review review = reviewMapper.toReview(reviewRequestDto.comment(), reviewRequestDto.grade());
        review.setUserApp(userApp);
        review = reviewRepository.save(review);
        userApp.addReview(review);
        userRepository.save(userApp);
        return reviewMapper.toReviewResponseDto(review);
    }

    @Override
    @Transactional
    public ReviewResponseDto updateReview(Long reviewId, ReviewRequestDto reviewRequestDto, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("Review", ErrorMessage.ENTITY_NOT_FOUND));
        UserApp userApp = userRepository.findById(userId)
                .orElseThrow(() -> new EntityInPizzeriaNotFoundException("User", ErrorMessage.ENTITY_NOT_FOUND));
        if (userApp.isBlocked()) {
            throw new UserBlockedException(ErrorMessage.USER_BLOCKED);
        }
        if (!Objects.equals(review.getUserApp().getId(), userApp.getId())) {
            throw new UpdateReviewException(ErrorMessage.CANT_REVIEW_UPDATED);
        }
        review.setComment(reviewRequestDto.comment());
        review.setGrade(reviewRequestDto.grade());
        return reviewMapper.toReviewResponseDto(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isPresent()) {
            reviewRepository.delete(review.get());
        } else {
            throw new InvalidIDException(ErrorMessage.INVALID_ID);
        }
    }

    @Override
    public List<ReviewResponseDto> getAllReview() {
        return reviewRepository.findAll().stream()
                .map(r -> reviewMapper.toReviewResponseDto(r)).toList();
    }

    @Override
    public List<ReviewResponseDto> getAllReviewByUser(Long userId) {
        return reviewRepository.findAllByUserApp_Id(userId).stream()
                .map(r -> reviewMapper.toReviewResponseDto(r)).toList();
    }

    @Override
    public List<ReviewResponseDto> getAllReviewByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return reviewRepository.findAllByReviewDateBetween(startDate, endDate).stream()
                .map(r -> reviewMapper.toReviewResponseDto(r)).toList();
    }
}
