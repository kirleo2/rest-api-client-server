package cz.fit.cvut.tjv.game_store.api.model.converter;

import cz.fit.cvut.tjv.game_store.api.model.OrderDto;
import cz.fit.cvut.tjv.game_store.domain.Game;
import cz.fit.cvut.tjv.game_store.domain.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.function.Function;

@Component
public class OrderToDtoConverter implements Function<Order, OrderDto> {
    @Override
    public OrderDto apply(Order order) {
        var orderDto = new OrderDto();

        orderDto.setId(order.getID());
        orderDto.setOrderPrice(order.getOrderPrice());
        orderDto.setOrderDate(order.getOrderDate());
        orderDto.setCompleted(order.getCompleted());
        HashSet<Long> soldGames = new HashSet<>();
        for (Game game : order.getSoldGames()) {
            soldGames.add(game.getID());
        }
        orderDto.setUsername(order.getCustomer().getUsername());
        orderDto.setSoldGames(soldGames);
        return orderDto;
    }
}
