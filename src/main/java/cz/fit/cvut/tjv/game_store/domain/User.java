package cz.fit.cvut.tjv.game_store.domain;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements Serializable, DomainEntity<String>{
    @Id
    private String username;
    @NotBlank
    private String email;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String country;
    @NotNull
    private Float balance;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE)
    private final Set<Order> orders = new HashSet<>();

    public User() {
    }

    @Override
    public String getID() {
        return getUsername();
    }
    public User(String username,
                String firstName,
                String lastName,
                String email,
                String country,
                Float balance
                ) {
        this.username = Objects.requireNonNull(username, "Blank username");
        this.email = Objects.requireNonNull(email, "Blank email");
        this.firstName = Objects.requireNonNull(firstName, "Blank First name");
        this.lastName = Objects.requireNonNull(lastName, "Blank Last name");
        this.country = country;
        this.balance = balance;
    }
    public void addOrder(Order order) {
        orders.add(order);
    }
    public Set<Order> getOrders() {
        return orders;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCountry() {
        return country;
    }

    public Float getBalance() {
        return balance;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return getUsername().hashCode();
    }
}
