package cz.fit.cvut.tjv.game_store.api.model.converter;

import cz.fit.cvut.tjv.game_store.api.model.OrderDto;
import cz.fit.cvut.tjv.game_store.buisness.GameService;
import cz.fit.cvut.tjv.game_store.buisness.UserService;
import cz.fit.cvut.tjv.game_store.buisness.exceptions.InvalidEntityStateException;
import cz.fit.cvut.tjv.game_store.domain.Game;
import cz.fit.cvut.tjv.game_store.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

@Component
public class OrderToEntityConverter implements Function<OrderDto, Order> {
    private final UserService userService;
    private final GameService gameService;

    public OrderToEntityConverter(UserService userService,
                                  GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }
    @Override
    public Order apply(OrderDto orderDto) throws NullPointerException, NoSuchElementException {
        Set<Game> soldGames = new HashSet<>();
        for (Long id : Objects.requireNonNull(orderDto.getSoldGames())) {
            soldGames.add(gameService.readByID(id).orElseThrow());
        }
        return new Order(orderDto.getId(), userService.readByID(orderDto.getUsername()).orElseThrow(), orderDto.getOrderDate(),
                orderDto.getOrderPrice(), soldGames, orderDto.getCompleted());
    }
}
