package org.example.pizzeria.repository.benefits;

import org.example.pizzeria.entity.benefits.Review;
import org.example.pizzeria.entity.user.UserApp;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByUserApp(UserApp userApp);
    List<Review> findAllByReviewDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    default Review findLastMessageByUserId(Long userId) {
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC,
                "reviewDate"));
        List<Review> lastMessages = findAllByUserId(userId, pageable);
        return lastMessages.isEmpty() ? null : lastMessages.get(0);
    }
    List<Review> findAllByUserId(Long userId, Pageable pageable);

}

