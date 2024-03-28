package org.example.pizzeria;

import org.example.pizzeria.dto.benefits.ReviewRequestDto;
import org.example.pizzeria.dto.benefits.ReviewResponseDto;
import org.example.pizzeria.dto.product.dough.DoughCreateRequestDto;
import org.example.pizzeria.dto.product.dough.DoughResponseClientDto;
import org.example.pizzeria.dto.product.dough.DoughResponseDto;
import org.example.pizzeria.dto.product.dough.DoughUpdateRequestDto;
import org.example.pizzeria.dto.product.ingredient.IngredientRequestDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseClientDto;
import org.example.pizzeria.dto.product.ingredient.IngredientResponseDto;
import org.example.pizzeria.dto.product.pizza.PizzaRequestDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.dto.user.*;
import org.example.pizzeria.entity.benefits.Bonus;
import org.example.pizzeria.entity.benefits.Favorites;
import org.example.pizzeria.entity.order.*;
import org.example.pizzeria.entity.product.ingredient.Dough;
import org.example.pizzeria.entity.product.ingredient.GroupIngredient;
import org.example.pizzeria.entity.product.ingredient.Ingredient;
import org.example.pizzeria.entity.product.ingredient.TypeDough;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.entity.product.pizza.Styles;
import org.example.pizzeria.entity.product.pizza.ToppingsFillings;
import org.example.pizzeria.entity.product.pizza.TypeBySize;
import org.example.pizzeria.entity.user.Address;
import org.example.pizzeria.entity.user.ContactInformation;
import org.example.pizzeria.entity.user.Role;
import org.example.pizzeria.entity.user.UserApp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TestData {
    public static final Bonus BONUS = new Bonus(10, 125.0);
    public static final Bonus BONUS_NEW = new Bonus(30, 500.0);
    public static final Address ADDRESS = new Address("", "", "", "");
    public static final Address ADDRESS_NEW = new Address("", "test", "10", "");
    public static final ContactInformation CONTACT_INFORMATION = new ContactInformation("+490246562332");
    public static final ContactInformation CONTACT_INFORMATION_NEW = new ContactInformation("+480246562332");
    public static final DeliveryAddress DELIVERY_ADDRESS = new DeliveryAddress("", "", "", "");
    public static final DeliveryAddress DELIVERY_ADDRESS_NEW = new DeliveryAddress("", "test", "10b", "");

    public static final UserApp USER_APP = UserApp.builder()
            .id(1L)
            .userName("IvanAdmin")
            .password("12345")
            .email("iv.admin@pizzeria.com")
            .birthDate(LocalDate.of(2000, 1, 15))
            .address(ADDRESS)
            .isBlocked(false)
            .role(Role.ADMIN)
            .bonus(BONUS)
            .build();
    public static final UserApp USER_APP_2 = UserApp.builder()
            .id(2L)
            .userName("TestClient")
            .password("12345")
            .email("clientTest@pizzeria.com")
            .birthDate(LocalDate.of(2001, 10, 15))
            .dateRegistration(LocalDate.now())
            .address(ADDRESS)
            .phoneNumber(CONTACT_INFORMATION)
            .isBlocked(false)
            .role(Role.CLIENT)
            .bonus(new Bonus(0, 0.0))
            .build();
    public static final UserApp USER_APP_NOT_BLOCKED = UserApp.builder()
            .id(2L)
            .userName("TestClient")
            .password("12345")
            .email("clientTest@pizzeria.com")
            .birthDate(LocalDate.of(2001, 10, 15))
            .dateRegistration(LocalDate.now())
            .address(ADDRESS)
            .phoneNumber(CONTACT_INFORMATION)
            .isBlocked(false)
            .role(Role.CLIENT)
            .bonus(new Bonus(0, 0.0))
            .build();
    public static final UserApp USER_APP_BLOCKED = UserApp.builder()
            .id(2L)
            .userName("TestClient")
            .password("12345")
            .email("clientTest@pizzeria.com")
            .birthDate(LocalDate.of(2001, 10, 15))
            .dateRegistration(LocalDate.now())
            .address(ADDRESS)
            .phoneNumber(CONTACT_INFORMATION)
            .isBlocked(true)
            .role(Role.CLIENT)
            .bonus(new Bonus(0, 0.0))
            .build();
    public static final UserApp USER_APP_WITH_NEW_BONUS = UserApp.builder()
            .id(1L)
            .userName("IvanAdmin")
            .password("12345")
            .email("iv.admin@pizzeria.com")
            .address(ADDRESS)
            .isBlocked(false)
            .role(Role.ADMIN)
            .bonus(BONUS_NEW)
            .build();
    public static final UserApp USER_BLOCKED = UserApp.builder()
            .id(2L)
            .userName("TestClient")
            .password("12345")
            .email("client1@pizzeria.com")
            .birthDate(LocalDate.of(2002, 3, 15))
            .dateRegistration(LocalDate.now())
            .isBlocked(true)
            .role(Role.CLIENT)
            .build();

    public static final Basket BASKET = new Basket(1L, USER_APP, null);

    public static final Dough DOUGH_1 = new Dough(1, TypeDough.CLASSICA, 100, 120, 0.23);
    public static final Dough DOUGH_2 = new Dough(1, TypeDough.CORNMEAL, 130, 140, 0.23);
    public static final DoughCreateRequestDto DOUGH_REQUEST_DTO = new DoughCreateRequestDto(TypeDough.CLASSICA, 100, 120, 0.23);
    public static final DoughUpdateRequestDto DOUGH_UPDATE_REQUEST_DTO = new DoughUpdateRequestDto(1, 110, 110);
    public static final DoughResponseDto DOUGH_RESPONSE_DTO_NEW = new DoughResponseDto(1, TypeDough.CORNMEAL, 110, 110, 0.23);
    public static final DoughResponseDto DOUGH_RESPONSE_DTO = new DoughResponseDto(1, TypeDough.CLASSICA, 100, 120, 0.23);
    public static final DoughResponseClientDto DOUGH_RESPONSE_CLIENT_DTO = new DoughResponseClientDto(1,
            TypeDough.CLASSICA, 100, 120);

    public static final IngredientResponseClientDto INGREDIENT_RESPONSE_CLIENT_DTO_1 = new IngredientResponseClientDto(1L,
            "Tomato", 100, 90, GroupIngredient.BASIC);
    public static final IngredientResponseClientDto INGREDIENT_RESPONSE_CLIENT_DTO_2 = new IngredientResponseClientDto(2L,
            "Cheese", 20, 80, GroupIngredient.BASIC);
    public static final Ingredient INGREDIENT_1 = new Ingredient(1L, "Tomato", 100, 90, 0.2, GroupIngredient.BASIC, null);
    public static final Ingredient INGREDIENT_2 = new Ingredient(2L, "Cheese", 20, 80, 0.4, GroupIngredient.BASIC, null);
    public static final List<IngredientResponseClientDto> ingredientResponseClientBasicDtoList = List.of(INGREDIENT_RESPONSE_CLIENT_DTO_1, INGREDIENT_RESPONSE_CLIENT_DTO_2);
    public static final IngredientRequestDto INGREDIENT_REQUEST_DTO = new IngredientRequestDto("Tomato", 100, 120, 0.23, GroupIngredient.EXTRA);
    public static final IngredientResponseDto INGREDIENT_RESPONSE_DTO = new IngredientResponseDto(1L, "Tomato", 100, 120, 0.23, GroupIngredient.EXTRA);
    public static final IngredientRequestDto INGREDIENT_REQUEST_DTO_NEW = new IngredientRequestDto("Tomato", 110, 120, 0.25, GroupIngredient.EXTRA);
    public static final IngredientResponseDto INGREDIENT_RESPONSE_DTO_NEW = new IngredientResponseDto(1L, "Tomato", 110, 120, 0.25, GroupIngredient.EXTRA);

    public static final Pizza PIZZA = new Pizza(1L, "Margarita",
            "Description for pizza Margaritta", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.MEDIUM, true, (0.2 + 0.4 + 0.23) * 1.3, 377, DOUGH_1, List.of(INGREDIENT_1, INGREDIENT_2));
    public static final PizzaResponseDto PIZZA_RESPONSE_DTO = new PizzaResponseDto(1L, "Margarita",
            "Description for pizza Margaritta", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.MEDIUM, DOUGH_RESPONSE_CLIENT_DTO, ingredientResponseClientBasicDtoList, (0.2 + 0.4 + 0.23) * 1.3, 377);
    public static final Order ORDER = new Order(1L, LocalDateTime.now(), (0.2 + 0.4 + 0.23) * 1.3 * 2, StatusOrder.NEW,
            DELIVERY_ADDRESS, null, USER_APP);
    public static final OrderDetails ORDER_DETAILS = new OrderDetails(1L, LocalDateTime.now().plusHours(1), null, ORDER,
            List.of(PIZZA, PIZZA));
    public static final PizzaRequestDto PIZZA_REQUEST_DTO = new PizzaRequestDto("Margarita",
            "Description for pizza Margaritta", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.MEDIUM, 1, new ArrayList<>(), List.of(1L, 2L),
            new ArrayList<>());
    public static final PizzaRequestDto PIZZA_REQUEST_DTO_NEW = new PizzaRequestDto("Margarita",
            "Description for pizza Margaritta", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.LARGE, 1, new ArrayList<>(), List.of(1L, 2L),
            new ArrayList<>());
    public static final Pizza PIZZA_NEW = new Pizza(1L, "Margarita",
            "Description for pizza Margaritta", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.LARGE, true, (0.2 + 0.4 + 0.23) * 1.7, 493, DOUGH_1, List.of(INGREDIENT_1, INGREDIENT_2));

    public static final PizzaResponseDto PIZZA_RESPONSE_DTO_NEW = new PizzaResponseDto(1L, "Margarita",
            "Description for pizza Margaritta", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.LARGE, DOUGH_RESPONSE_CLIENT_DTO, ingredientResponseClientBasicDtoList, (0.2 + 0.4 + 0.23) * 1.7, 493);
    public static final Pizza PIZZA_2 = new Pizza(2L, "Carbonara",
            "Description for pizza Carbonara", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.LARGE, true, (0.2 + 0.4 + 0.23) * 1.7, 493, DOUGH_1, List.of(INGREDIENT_1, INGREDIENT_2));
    public static final Pizza PIZZA_3 = new Pizza(3L, "Test3Carbonara",
            "Description for pizza Carbonara", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.LARGE, true, (0.2 + 0.4 + 0.23) * 1.7, 493, DOUGH_1, List.of(INGREDIENT_1, INGREDIENT_2));
    public static final PizzaResponseDto PIZZA_RESPONSE_DTO_2 = new PizzaResponseDto(2L, "Carbonara",
            "Description for pizza Carbonara", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.LARGE, DOUGH_RESPONSE_CLIENT_DTO, ingredientResponseClientBasicDtoList, (0.2 + 0.4 + 0.23) * 1.7, 493);
    public static final PizzaResponseDto PIZZA_RESPONSE_DTO_3 = new PizzaResponseDto(3L, "Test3Carbonara",
            "Description for pizza Carbonara", Styles.CLASSIC_ITALIAN, ToppingsFillings.CHEESE,
            TypeBySize.LARGE, DOUGH_RESPONSE_CLIENT_DTO, ingredientResponseClientBasicDtoList, (0.2 + 0.4 + 0.23) * 1.7, 493);

    public static final Favorites FAVORITES = new Favorites(1L, List.of(PIZZA, PIZZA_2), USER_APP);

    public static final UserRegisterRequestDto USER_REGISTER_REQUEST_DTO = new UserRegisterRequestDto("IvanAdmin",
            "Qwerty", "iv.admin@pizzeria.com", LocalDate.of(2000, 1, 15),
            "", "", "", "", "+490246562332");
    public static final UserRequestDto USER_REQUEST_DTO = new UserRequestDto("IvanAdmin",
            "Qwerty", "iv.admin@pizzeria.com", LocalDate.of(2000, 1, 15),
            "", "test", "10", "", "+480246562332");
    public static final UserResponseDto USER_RESPONSE_DTO = new UserResponseDto("IvanAdmin",
            "iv.admin@pizzeria.com", LocalDate.of(2000, 1, 15), ADDRESS,
            CONTACT_INFORMATION);
    public static final UserResponseDto USER_RESPONSE_DTO_2 = new UserResponseDto("TestClient",
            "clientTest@pizzeria.com", LocalDate.of(2001, 10, 15), ADDRESS,
            CONTACT_INFORMATION);
    public static final UserBlockedResponseDto USER_BLOCKED_RESPONSE_DTO = new UserBlockedResponseDto(2L,
            "TestClient", true, LocalDateTime.now());
    public static final ReviewRequestDto REVIEW_REQUEST_DTO = new ReviewRequestDto( "Good pizza", 10);
    public static final ReviewRequestDto REVIEW_REQUEST_DTO_2 = new ReviewRequestDto( "Super", 10);
    public static final ReviewRequestDto REVIEW_REQUEST_DTO_3 = new ReviewRequestDto("Super", 10);
    public static final ReviewResponseDto REVIEW_RESPONSE_DTO = new ReviewResponseDto("Good pizza",
            10, "IvanAdmin", LocalDateTime.now());
    public static final ReviewResponseDto REVIEW_RESPONSE_DTO_NEW = new ReviewResponseDto("Super",
            8, "IvanAdmin", LocalDateTime.now());
    public static final UserBonusDto USER_BONUS_DTO = new UserBonusDto(10, 125.0);
    public static final UserBonusDto USER_NEW_BONUS_DTO = new UserBonusDto(30, 500.0);

}
