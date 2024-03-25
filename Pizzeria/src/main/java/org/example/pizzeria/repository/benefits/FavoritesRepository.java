package org.example.pizzeria.repository.benefits;

import org.example.pizzeria.entity.benefits.Favorites;
import org.example.pizzeria.entity.user.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, Long>{
    Optional<Favorites> findByUserApp(UserApp userApp);
    Optional<Favorites> findByUserApp_Id(Long id);

}
