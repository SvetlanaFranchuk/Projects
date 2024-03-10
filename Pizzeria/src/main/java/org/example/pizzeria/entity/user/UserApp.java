package org.example.pizzeria.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pizzeria.entity.benefits.Bonus;
import org.example.pizzeria.entity.benefits.Favorites;
import org.example.pizzeria.entity.benefits.Review;
import org.example.pizzeria.entity.order.Basket;
import org.example.pizzeria.entity.order.Order;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class UserApp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "birthdate")
    private LocalDate birthDate;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_registration")
    private LocalDateTime dateRegistration;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "is_Blocked")
    private boolean isBlocked;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Embedded
    private Bonus bonus;

    @OneToOne(cascade = CascadeType.REMOVE)
    private Basket basket;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Favorites> favorites = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Review> reviews = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Order> orders = new HashSet<>();


    public void addFavorites(Favorites favorite) {
        favorites.add(favorite);
        favorite.setUserApp(this);
    }

    public void removeFavorites(Favorites favorite) {
        favorites.remove(favorite);
        favorite.setUserApp(null);
    }

    public void addReview(Review review) {
        reviews.add(review);
        review.setUserApp(this);
    }

    public void removeReview(Review review) {
        reviews.remove(review);
        review.setUserApp(null);
    }

    public void addOrder(Order order) {
        orders.add(order);
        order.setUserApp(this);
    }

    public void removeOrder(Order order) {
        orders.remove(order);
        order.setUserApp(null);
    }
}
