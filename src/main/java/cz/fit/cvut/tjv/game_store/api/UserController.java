package cz.fit.cvut.tjv.game_store.api;

import cz.fit.cvut.tjv.game_store.api.model.OrderDto;
import cz.fit.cvut.tjv.game_store.api.model.UserDto;
import cz.fit.cvut.tjv.game_store.api.model.converter.OrderToDtoConverter;
import cz.fit.cvut.tjv.game_store.api.model.converter.UserToDtoConverter;
import cz.fit.cvut.tjv.game_store.api.model.converter.UserToEntityConverter;
import cz.fit.cvut.tjv.game_store.buisness.UserService;
import cz.fit.cvut.tjv.game_store.buisness.exceptions.EntityDoesNotExistsException;
import cz.fit.cvut.tjv.game_store.domain.User;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
@RestController
@RequestMapping("/users")
public class UserController extends AbstractCrudController<User, UserDto, String>{

    public UserController(UserService service, UserToDtoConverter toDtoConverter, UserToEntityConverter toEntityConverter) {
        super(service, toDtoConverter, toEntityConverter);
    }
    @GetMapping({"/{id}/orders"})
    public Collection<OrderDto> readAllUserOrders(@PathVariable String id) throws EntityDoesNotExistsException {
        User user = service.readByID(id).orElseThrow(EntityDoesNotExistsException::new);
        OrderToDtoConverter converter = new OrderToDtoConverter();
        return user.getOrders().stream().map(converter).toList();
    }
    @PostMapping("/{id}/charge")
    public UserDto chargeBalance(@PathVariable String id, @RequestParam Float amount) {
        User user = service.readByID(id).orElseThrow(EntityDoesNotExistsException::new);
        return toDtoConverter.apply(((UserService) service).increaseUserBalance(user, amount));
    }
}
