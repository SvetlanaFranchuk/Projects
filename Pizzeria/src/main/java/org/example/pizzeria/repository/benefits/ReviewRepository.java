package org.example.pizzeria.repository.benefits;

import org.example.pizzeria.entity.benefits.Review;
import org.example.pizzeria.entity.user.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByUserApp(UserApp userApp);
    List<Review> findAllByUserApp_Id(Long userId);

    List<Review> findAllByReviewDateBetween(LocalDateTime startDate, LocalDateTime endDate);

}

