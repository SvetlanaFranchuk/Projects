package org.example.pizzeria.repository.user;

import org.example.pizzeria.entity.benefits.Bonus;
import org.example.pizzeria.entity.user.Address;
import org.example.pizzeria.entity.user.ContactInformation;
import org.example.pizzeria.entity.user.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserApp, Long> {

    Optional<UserApp> findUserAppByUserName (String userName);
    Optional<UserApp> findUserAppByEmail (String email);
    List<UserApp> findUserAppByBirthDate (LocalDate birthdate);
    Boolean existsUserAppByUserName(String userName);
    Boolean existsUserAppByEmail(String email);
    List<UserApp> findAllByUserNameAndEmail(String UserName, String email);

}
