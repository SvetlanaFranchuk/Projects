package org.example.pizzeria.repository.user;

import org.example.pizzeria.entity.user.Role;
import org.example.pizzeria.entity.user.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserApp, Long> {

    Optional<UserApp> findByUserName(String userName);

    List<UserApp> findAllByBirthDate(LocalDate birthdate);

    Optional<UserApp> findByUserNameAndEmail(String UserName, String email);

    List<UserApp> findAllByRole(Role role);

    List<UserApp> findAllByIsBlocked(Boolean isBlocked);


}
