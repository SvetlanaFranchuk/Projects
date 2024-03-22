package org.example.pizzeria.repository.benefits;

import org.example.pizzeria.entity.benefits.Bonus;
import org.example.pizzeria.entity.benefits.Favorites;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.entity.user.Role;
import org.example.pizzeria.entity.user.UserApp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class FavoritesRepositoryTest {
//
//    private static final UserApp USER_APP = UserApp.builder()
//            .id(1L)
//            .userName("IvanAdmin")
//            .password("12345")
//            .email("iv.admin@pizzeria.com")
//            .birthDate(LocalDate.of(2000, 1, 15))
//            .dateRegistration(LocalDate.now())
//            .isBlocked(false)
//            .role(Role.CLIENT)
//            .bonus(new Bonus(0,0.0))
//            .favorites(new Favorites())
//            .build();
//
//    @Autowired
//    private FavoritesRepository favoritesRepository;
//
//private static Stream<UserApp> provideFindByUserAppTestData() {
//    return Stream.of(null, USER_APP);
//}
//@ParameterizedTest
//@MethodSource("provideFindByUserAppTestData")
//void findByUserApp(UserApp userApp) {
//    Optional<Favorites> favorites = favoritesRepository.findByUserApp(userApp);
//    assertTrue(favorites.isPresent());
//    assertEquals(USER_APP, favorites.get().getUserApp());
//}
//
//    @Test
//    void save() {
//        Favorites favorites = new Favorites(null, List.of(new Pizza()), USER_APP);;
//        Favorites actual = favoritesRepository.save(favorites);
//        assertNotNull(actual);
//        assertNotNull(actual.getId());
//        assertTrue(actual.getId() > 0);
//    }

}