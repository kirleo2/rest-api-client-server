package cz.fit.cvut.tjv.game_store.domain;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "orders")
public class Order implements Serializable, DomainEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_generator")
    @SequenceGenerator(name = "order_generator", sequenceName = "order_seq", allocationSize = 1)
    private Long id;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToMany
    @JoinTable(name = "soldGames",
            joinColumns = @JoinColumn(name= "order_id"),
            inverseJoinColumns = @JoinColumn(name="game_id")
    )
    private Set<Game> soldGames = new HashSet<>();

    @NotNull
    @ManyToOne
    private User customer;

    @NotNull
    private Boolean isCompleted;
    @NotNull
    private LocalDate orderDate;

    public Order() {
    }

    public Collection<Game> getSoldGames() {
        return soldGames;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public Float getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Float orderPrice) {
        this.orderPrice = orderPrice;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Float orderPrice;

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public Order(Long id, User customer, LocalDate orderDate, Float orderPrice, Set<Game> soldGames,
                 Boolean isCompleted) {
        this.id = id;
        this.customer = Objects.requireNonNull(customer, "Customer is Null");
        this.soldGames = Objects.requireNonNull(soldGames, "There's no games in order");
        this.orderDate = orderDate;
        this.orderPrice = orderPrice;
        this.isCompleted = isCompleted;
    }

    @Override
    public Long getID() {
        return id;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order post = (Order) o;

        return getID() != null ? getID().equals(post.getID()) : post.getID() == null;
    }
    @Override
    public int hashCode() {
        return getID() != null ? getID().hashCode() : 0;
    }

}
