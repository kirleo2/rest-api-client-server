package cz.fit.cvut.tjv.game_store.domain;

import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

@Entity
public class Game implements Serializable, DomainEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_generator")
    @SequenceGenerator(name = "game_generator", sequenceName = "game_seq", allocationSize = 1)
    private Long id;
    @NotBlank
    private String title;
    @NotNull
    private Float price;
    private String description;

//    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "soldGames")
//    private final Set<Order> wasSoldIn = new HashSet<>();

    public Game(Long id, String title, Float price, String description) {
        this.id = id;
        this.title = Objects.requireNonNull(title, "Blank Game Title");
        this.price = Objects.requireNonNull(price, "Blank Game Price");
        this.description = description;
    }

    public Game() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(id, game.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public Long getID() {
        return id;
    }

    @ManyToMany(mappedBy = "soldGames")
    private Set<Order> orders = new HashSet<>();

    public Collection<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }
}
