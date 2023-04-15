package cz.fit.cvut.tjv.game_store.business;


import cz.fit.cvut.tjv.game_store.buisness.OrderService;
import cz.fit.cvut.tjv.game_store.buisness.UserService;
import cz.fit.cvut.tjv.game_store.buisness.exceptions.EntityDoesNotExistsException;
import cz.fit.cvut.tjv.game_store.buisness.exceptions.OrderIsClosedException;
import cz.fit.cvut.tjv.game_store.dao.jpa.OrderJpaRepository;
import cz.fit.cvut.tjv.game_store.domain.Game;
import cz.fit.cvut.tjv.game_store.domain.Order;
import cz.fit.cvut.tjv.game_store.domain.User;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@SpringBootTest
public class OrderServiceTest {
    @Autowired
    private OrderService orderService;

    @MockBean
    private OrderJpaRepository orderJpaRepository;

    @MockBean
    private UserService userService;
    private Game game1;
    private Game game2;
    private Game game3;
    private LocalDate localDate;

    @BeforeEach
    void setUp() {
        game1 = new Game(1L, "Test Game 1", 10f, "");
        game2 = new Game(2L, "Test Game 2", 20f, "");
        game3 = new Game(3L, "Test Game 3", 30f, "");


        localDate = LocalDate.now();

    }
    @Test
    void createOrderTest() {
        var user = new User("test_user", "Test", "User", "test@mail.ru", "CZ", 100f);

        var order = new Order(1L, user, localDate, null, new HashSet<>(Arrays.asList(game1, game2)), false);
        var orderResult = new Order(1L, user, localDate, 30f, new HashSet<>(Arrays.asList(game1, game2)), false);
        var userResult = new User("test_user", "Test", "User", "test@mail.ru", "CZ", user.getBalance() - orderResult.getOrderPrice());


        orderService.create(order);
        Mockito.verify(userService, Mockito.times(1)).update(userResult);
        Mockito.verify(orderJpaRepository, Mockito.times(1)).save(orderResult);
    }

    @Test
    void deleteGameTest() {
        var user = new User("test_user", "Test", "User", "test@mail.ru", "CZ", 100f);
        var order = new Order(1L, user, localDate, game2.getPrice() + game1.getPrice(),  new HashSet<>(Arrays.asList(game1, game2)), true);
        Mockito.when(orderJpaRepository.existsById(order.getID())).thenReturn(true);

        Assertions.assertThrows(OrderIsClosedException.class, () -> orderService.deleteGame(order, game1));
        order.setCompleted(false);
        Assertions.assertThrows(EntityDoesNotExistsException.class, () -> orderService.deleteGame(order, game3));
        var resultUser = new User("test_user", "Test", "User", "test@mail.ru", "CZ", user.getBalance() + game1.getPrice());

        var orderResult = new Order(1L, user, localDate, game2.getPrice(), new HashSet<>(Collections.singletonList(game2)), true);

        orderService.deleteGame(order, game1);


        Mockito.verify(orderJpaRepository, Mockito.times(1)).save(orderResult);
        Mockito.verify(userService, Mockito.times(1)).update(resultUser);
    }

    @Test
    void deleteGameTestOnDelete(){
        var user = new User("test_user", "Test", "User", "test@mail.ru", "CZ", 100f);
        var order = new Order(1L, user, localDate, game2.getPrice() + game1.getPrice(),  new HashSet<>(Collections.singletonList(game1)), false);
        Mockito.when(orderJpaRepository.existsById(order.getID())).thenReturn(true);

        orderService.deleteGame(order, game1);
        Mockito.verify(orderJpaRepository, Mockito.times(1)).deleteById(order.getID());
    }



}
