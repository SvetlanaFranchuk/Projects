package org.example.pizzeria.service.user;

import lombok.RequiredArgsConstructor;
import org.example.pizzeria.dto.user.*;
import org.example.pizzeria.entity.benefits.Bonus;
import org.example.pizzeria.entity.benefits.Favorites;
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
import java.util.List;

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
                        false, Role.CLIENT, new Bonus(0,0));
        newUser = userRepository.save(newUser);
        Basket basket = new Basket();
        basket.setUserApp(newUser);
        basketRepository.save(basket);
        newUser.setBasket(basket);
        Favorites favorites = new Favorites();
        favorites.setUserApp(newUser);
        favoritesRepository.save(favorites);
        newUser.setFavorites(favorites);
        return  userMapper.toUserResponseDto(newUser);
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
        userApp = userRepository.updateUserAppByIdAndPasswordAndEmailAndBirthDateAndAddressAndPhoneNumber(userApp.getId(),
                userRequestDto.password(), userRequestDto.email(), userRequestDto.birthDate(),address,
                phoneNumber);
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
                .map(u-> userMapper.toUserBlockedResponseDto(u.getId(), u.getUserName(), u.isBlocked(),
                        reviewRepository.findLastMessageByUserId(u.getId()).getReviewDate()))
                .toList();
    }

    @Override
    @Transactional
    public UserBlockedResponseDto changeBlockingUser(Long id, boolean isBlocked) {
        UserApp userApp = userRepository.getReferenceById(id);
        if (userApp == null)
            throw new UserNotFoundException(ErrorMessage.USER_NOT_FOUND);
        LocalDateTime dateLastReview = reviewRepository.findLastMessageByUserId(userApp.getId()).getReviewDate();
        if (dateLastReview == null)
            throw new UserReviewNotFoundException(ErrorMessage.USER_REVIEW_NOT_FOUND);
        userRepository.updateUserBlockedStatus(id, isBlocked);
        userApp = userRepository.getReferenceById(id);
        return userMapper.toUserBlockedResponseDto(id, userApp.getUserName(), isBlocked, dateLastReview);
    }


    @Override
    public UserBonusDto getUserBonus(Long userId) {
        return userMapper.toUserBonusDto(userRepository.getReferenceById(userId).getBonus());
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
        userRepository.updateUserAppByBonus(userApp, newBonus);
        UserApp userAppNew = userRepository.getReferenceById(userId);
        return userMapper.toUserBonusDto(userAppNew.getBonus());
            }
//
//    @Override
//    @Transactional
//    public ReviewResponseDto addReview(Long id, ReviewRequestDto reviewRequestDto) {
//        return reviewMapper.toReviewResponseDto(reviewRepository.save(reviewMapper.toReview(id, reviewRequestDto)));
//    }
//
//    @Override
//    @Transactional
//    public ReviewResponseDto updateReview(Long id, ReviewRequestDto reviewRequestDto) {
//        return reviewMapper.toReviewResponseDto(reviewRepository.save(reviewMapper.toReview(id, reviewRequestDto)));
//    }
//
//    @Override
//    @Transactional
//    public void deleteReview(Long id) {
//        Optional<Review> review = reviewRepository.findById(id);
//        review.ifPresent(r -> {
//            reviewRepository.delete(r);
//        });
//    }
//
//    @Override
//    public List<ReviewResponseDto> getAllReview() {
//        return reviewRepository.findAll().stream()
//                .map(r -> reviewMapper.toReviewResponseDto(r)).toList();
//    }
//
//    @Override
//    public List<ReviewResponseDto> getAllReviewByUser(Long userId) {
//        return reviewRepository.findAllByUserApp(userRepository.getReferenceById(userId)).stream()
//                .map(r -> reviewMapper.toReviewResponseDto(r)).toList();
//    }
//
//    @Override
//    public List<ReviewResponseDto> getAllReviewByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
//        return reviewRepository.findAllByReviewDateBetween(startDate, endDate).stream()
//                .map(r -> reviewMapper.toReviewResponseDto(r)).toList();
//    }
}
