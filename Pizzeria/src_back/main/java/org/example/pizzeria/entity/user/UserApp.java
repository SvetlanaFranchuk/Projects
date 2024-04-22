package org.example.pizzeria.entity.user;

import jakarta.persistence.*;
import lombok.*;
import org.example.pizzeria.entity.benefits.Bonus;
import org.example.pizzeria.entity.benefits.Favorites;
import org.example.pizzeria.entity.benefits.Review;
import org.example.pizzeria.entity.order.Basket;
import org.example.pizzeria.entity.order.Order;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"orders", "reviews", "basket", "favorites"})
@EqualsAndHashCode(exclude = {"orders", "reviews", "basket", "favorites"})
@Builder
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "userName"),
                @UniqueConstraint(columnNames = "email")
        })
public class UserApp implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Builder.Default
    @Column(name = "user_name")
    private String userName = "user";

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "birthdate")
    private LocalDate birthDate;

    @CreationTimestamp
    @Column(name = "date_registration")
    private LocalDate dateRegistration;

    @Embedded
    private Address address;

    @Embedded
    private ContactInformation phoneNumber;

    @Column(name = "is_Blocked")
    private boolean isBlocked;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Embedded
    private Bonus bonus;

    @OneToOne(cascade = CascadeType.REMOVE)
    private Basket basket;

    @OneToOne(cascade = CascadeType.REMOVE)
    private Favorites favorites;

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Review> reviews = new HashSet<>();

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Order> orders = new HashSet<>();


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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
