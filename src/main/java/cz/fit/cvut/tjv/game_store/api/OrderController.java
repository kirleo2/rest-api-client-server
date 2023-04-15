package cz.fit.cvut.tjv.game_store.api;

import cz.fit.cvut.tjv.game_store.api.model.OrderDto;
import cz.fit.cvut.tjv.game_store.api.model.converter.OrderToDtoConverter;
import cz.fit.cvut.tjv.game_store.api.model.converter.OrderToEntityConverter;
import cz.fit.cvut.tjv.game_store.buisness.GameService;
import cz.fit.cvut.tjv.game_store.buisness.OrderService;
import cz.fit.cvut.tjv.game_store.buisness.exceptions.EntityDoesNotExistsException;
import cz.fit.cvut.tjv.game_store.domain.Game;
import cz.fit.cvut.tjv.game_store.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/orders")
public class OrderController extends AbstractCrudController<Order, OrderDto, Long>{
    private final GameService gameService;
    public OrderController(OrderService service, OrderToDtoConverter toDtoConverter, OrderToEntityConverter toEntityConverter, GameService gameService) {
        super(service, toDtoConverter, toEntityConverter);
        this.gameService = gameService;
    }
    @PatchMapping("/delete/{orderId}")
    public void deleteGameFromOrder(@PathVariable Long orderId, @RequestParam Long id) {
        Order order = service.readByID(orderId).orElseThrow(EntityDoesNotExistsException::new);
        Game game = gameService.readByID(id).orElseThrow(EntityDoesNotExistsException::new);
        ((OrderService) service).deleteGame(order, game);
    }

}
