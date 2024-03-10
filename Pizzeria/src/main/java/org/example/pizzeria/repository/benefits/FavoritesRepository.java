package org.example.pizzeria.repository.benefits;

import org.example.pizzeria.entity.benefits.Favorites;
import org.example.pizzeria.entity.user.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, Long>{
    List<Favorites> findAllByUserApp(UserApp userApp);
}