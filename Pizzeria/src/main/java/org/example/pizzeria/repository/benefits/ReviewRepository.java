package org.example.pizzeria.repository.benefits;

import org.example.pizzeria.entity.benefits.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

}
