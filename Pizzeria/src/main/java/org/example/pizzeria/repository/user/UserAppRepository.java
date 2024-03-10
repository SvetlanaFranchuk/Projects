package org.example.pizzeria.repository.user;

import org.example.pizzeria.entity.user.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserAppRepository extends JpaRepository<UserApp, Long> {
    Optional<UserApp> findUserAppByLogin (String login);
    Optional<UserApp> findUserAppByEmail (String email);
    List<UserApp> findUserAppByBirthDate (LocalDate birthdate);
    Boolean existsUserAppByLogin(String login);
    Boolean existsUserAppByEmail(String email);
}
