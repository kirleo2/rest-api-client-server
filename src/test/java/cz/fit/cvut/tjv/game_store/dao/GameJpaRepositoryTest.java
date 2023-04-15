package cz.fit.cvut.tjv.game_store.dao;


import cz.fit.cvut.tjv.game_store.dao.jpa.GameJpaRepository;
import cz.fit.cvut.tjv.game_store.dao.jpa.OrderJpaRepository;
import cz.fit.cvut.tjv.game_store.dao.jpa.UserJpaRepository;
import cz.fit.cvut.tjv.game_store.domain.Game;
import cz.fit.cvut.tjv.game_store.domain.Order;
import cz.fit.cvut.tjv.game_store.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.*;

@DataJpaTest
public class GameJpaRepositoryTest {
    @Autowired
    private GameJpaRepository gameJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Test
    void testFindBySellAmountGreaterThan() {
        var game1 = new Game(1L, "Test Game 1", 10f, "");
        var game2 = new Game(2L, "Test Game 2", 20f, "");
        var game3 = new Game(3L, "Test Game 3", 30f, "");
        var game4 = new Game(4L, "Test Game 4", 50f, "");

        var user = new User("test_user", "Test", "User", "test@mail.ru", "CZ", 100f);
        userJpaRepository.save(user);
        gameJpaRepository.save(game1);
        gameJpaRepository.save(game2);
        gameJpaRepository.save(game3);
        gameJpaRepository.save(game4);

        var order = new Order(1L, user, LocalDate.now(), 10f, new HashSet<>(Arrays.asList(game1, game3)), false);
        orderJpaRepository.save(order);
        var ret = gameJpaRepository.findBySellAmountGreaterThan(1);
        Collection<Game> g = new ArrayList<>();
        g.add(game1);
        g.add(game3);
        Assertions.assertEquals(ret, g);
    }
}
