package org.example.pizzeria.entity.benefits;

import jakarta.persistence.*;
import lombok.*;
import org.example.pizzeria.entity.user.UserApp;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "userApp")
@EqualsAndHashCode(exclude = "userApp")
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "comment")
    private String comment;

    @Column(name = "grade")
    private Integer grade;

    @CreationTimestamp
    @Column(name = "review_date")
    private LocalDateTime reviewDate;

    @ManyToOne
    private UserApp userApp;

}
