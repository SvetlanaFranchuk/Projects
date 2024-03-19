package org.example.pizzeria.service.user;

import lombok.RequiredArgsConstructor;
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
import org.example.pizzeria.exeption.*;
import org.example.pizzeria.mapper.benefits.ReviewMapper;
import org.example.pizzeria.mapper.user.UserMapper;
import org.example.pizzeria.repository.benefits.FavoritesRepository;
import org.example.pizzeria.repository.benefits.ReviewRepository;
import org.example.pizzeria.repository.order.BasketRepository;
import org.example.pizzeria.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public UserRepository userRepository;
    public BasketRepository basketRepository;
    public FavoritesRepository favoritesRepository;
    public ReviewRepository reviewRepository;

    public UserMapper userMapper;
    public ReviewMapper reviewMapper;

    @Override
    @Transactional
    public UserResponseDto save(UserRegisterRequestDto candidate) {
        validateUserNotExist(candidate.userName(), candidate.email());
        UserApp newUser = userMapper.toUserApp(candidate.userName(), candidate.password(),
                candidate.email(), candidate.birthDate(), new Address(candidate.addressCity(),
                        candidate.addressStreetName(), candidate.addressHouseNumber(),
                        candidate.addressApartmentNumber()), new ContactInformation(candidate.phoneNumber()),
                false, Role.CLIENT, new Bonus(0, 0.0));
        newUser = userRepository.save(newUser);
        Basket basket = new Basket();
        basket.setUserApp(newUser);
        basketRepository.save(basket);
        newUser.setBasket(basket);
        Favorites favorites = new Favorites();
        favorites.setUserApp(newUser);
        favoritesRepository.save(favorites);
        newUser.setFavorites(favorites);
        return userMapper.toUserResponseDto(newUser);
    }

    private void validateUserNotExist(String userName, String email) {
        List<UserApp> users = userRepository.findAllByUserNameAndEmail(userName, email);
        if (!users.isEmpty()) {
            throw new UserCreateError(ErrorMessage.USER_ALREADY_EXIST);
        }
    }

    @Override
    public UserResponseDto getUser(Long id) {
        return userMapper.toUserResponseDto(userRepository.getReferenceById(id));
    }

    @Override
    @Transactional
    public UserResponseDto update(Long id, UserRequestDto userRequestDto) {
        UserApp userApp = userRepository.getReferenceById(id);
        if (userApp == null) {
            throw new UserNotFoundException(ErrorMessage.USER_NOT_FOUND);
        }
        Address address = new Address(userRequestDto.addressCity(), userRequestDto.addressStreetName(),
                userRequestDto.addressHouseNumber(), userRequestDto.addressApartmentNumber());
        ContactInformation phoneNumber = new ContactInformation(userRequestDto.phoneNumber());
        if (!(Objects.equals(userApp.getPassword(), userRequestDto.password())))
            userApp.setPassword(userRequestDto.password());
        if (!(Objects.equals(userApp.getEmail(), userRequestDto.email()))) userApp.setEmail(userRequestDto.email());
        if (!(userApp.getBirthDate() == userRequestDto.birthDate())) userApp.setBirthDate(userRequestDto.birthDate());
        if (!(Objects.equals(userApp.getAddress(), address))) userApp.setAddress(address);
        if (!(Objects.equals(userApp.getPhoneNumber(), phoneNumber))) userApp.setPhoneNumber(phoneNumber);
        userApp = userRepository.save(userApp);
        return userMapper.toUserResponseDto(userApp);
    }

    @Override
    public List<UserResponseDto> getUsersByBirthday(LocalDate date) {
        if (date == null) throw new DateIsNullException(ErrorMessage.DATA_IS_NULL);
        return userRepository.findUserAppByBirthDate(date).stream()
                .map(u -> userMapper.toUserResponseDto(u)).toList();
    }

    @Override
    public List<UserResponseDto> getUserByClientRole() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole().equals(Role.CLIENT))
                .map(u -> userMapper.toUserResponseDto(u)).toList();
    }

    @Override
    public List<UserBlockedResponseDto> getUserBlocked() {
        List<UserApp> userApps = userRepository.findAll().stream()
                .filter(UserApp::isBlocked).toList();
        return userApps.stream()
                .map(u -> userMapper.toUserBlockedResponseDto(u.getId(), u.getUserName(), u.isBlocked(),
                        Objects.requireNonNull(reviewRepository.findAllByUserApp(u).stream()
                                        .max(Comparator.comparing(Review::getReviewDate)).orElse(null))
                                .getReviewDate())).toList();
    }

    @Override
    @Transactional
    public UserBlockedResponseDto changeBlockingUser(Long id, boolean isBlocked) {
        UserApp userApp = userRepository.getReferenceById(id);
        if (userApp == null)
            throw new UserNotFoundException(ErrorMessage.USER_NOT_FOUND);
        Review review = reviewRepository.findAllByUserApp(userApp).stream()
                .max(Comparator.comparing(Review::getReviewDate)).orElseThrow(() ->
                        new UserReviewNotFoundException(ErrorMessage.USER_REVIEW_NOT_FOUND));
        userApp.setBlocked(isBlocked);
        userApp = userRepository.save(userApp);
        return userMapper.toUserBlockedResponseDto(id, userApp.getUserName(), isBlocked, review.getReviewDate());
    }


    @Override
    public UserBonusDto getUserBonus(Long userId) {
        UserApp userApp = userRepository.getReferenceById(userId);
        if (userApp == null)
            throw new UserNotFoundException(ErrorMessage.USER_NOT_FOUND);
        return userMapper.toUserBonusDto(userApp.getBonus().getCountOrders(),
                userApp.getBonus().getSumOrders());
    }

    @Override
    @Transactional
    public UserBonusDto updateUserBonus(Long userId, int count, double sum) {
        UserApp userApp = userRepository.getReferenceById(userId);
        if (userApp == null)
            throw new UserNotFoundException(ErrorMessage.USER_NOT_FOUND);
        Bonus newBonus = userApp.getBonus();
        newBonus.setCountOrders(newBonus.getCountOrders() + count);
        newBonus.setSumOrders(newBonus.getSumOrders() + sum);
        userApp.setBonus(newBonus);
        userApp = userRepository.save(userApp);
        return userMapper.toUserBonusDto(userApp.getBonus().getCountOrders(),
                userApp.getBonus().getSumOrders());
    }

    @Override
    @Transactional
    public ReviewResponseDto addReview(ReviewRequestDto reviewRequestDto) {
        if (userRepository.getReferenceById(reviewRequestDto.UserId()).isBlocked())
            throw new UserBlockedException(ErrorMessage.USER_BLOCKED);
        return reviewMapper.toReviewResponseDto(reviewRepository.save(reviewMapper.toReview(reviewRequestDto.comment(),
                reviewRequestDto.grade(), userRepository.getReferenceById(reviewRequestDto.UserId()))));
    }

    @Override
    @Transactional
    public ReviewResponseDto updateReview(Long id, ReviewRequestDto reviewRequestDto) {
        UserApp userApp = userRepository.getReferenceById(reviewRequestDto.UserId());
        if (userApp.isBlocked())
            throw new UserBlockedException(ErrorMessage.USER_BLOCKED);
        Review review = reviewRepository.getReferenceById(id);
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
        UserApp userApp = userRepository.getReferenceById(userId);
        if (userApp == null)
            throw new UserNotFoundException(ErrorMessage.USER_NOT_FOUND);
        return reviewRepository.findAllByUserApp(userApp).stream()
                .map(r -> reviewMapper.toReviewResponseDto(r)).toList();
    }

    @Override
    public List<ReviewResponseDto> getAllReviewByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return reviewRepository.findAllByReviewDateBetween(startDate, endDate).stream()
                .map(r -> reviewMapper.toReviewResponseDto(r)).toList();
    }
}
