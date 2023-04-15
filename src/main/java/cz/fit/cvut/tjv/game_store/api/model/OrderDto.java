package cz.fit.cvut.tjv.game_store.api.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderDto {
    private Long id;
    private LocalDate orderDate;
    private String username;
    private HashSet<Long> soldGames;
    private Float orderPrice;
    private Boolean isCompleted;

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Long> getSoldGames() {
        return soldGames;
    }

    public void setSoldGames(HashSet<Long> soldGames) {
        this.soldGames = soldGames;
    }

    public Float getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Float orderPrice) {
        this.orderPrice = orderPrice;
    }
}
