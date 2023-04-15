package cz.fit.cvut.tjv.game_store.buisness;

import cz.fit.cvut.tjv.game_store.buisness.exceptions.EntityDoesNotExistsException;
import cz.fit.cvut.tjv.game_store.buisness.exceptions.NotEnoughFundsException;
import cz.fit.cvut.tjv.game_store.buisness.exceptions.OrderIsClosedException;
import cz.fit.cvut.tjv.game_store.dao.jpa.OrderJpaRepository;
import cz.fit.cvut.tjv.game_store.domain.Game;
import cz.fit.cvut.tjv.game_store.domain.Order;
import cz.fit.cvut.tjv.game_store.domain.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class OrderService extends AbstractCrudService<Order, Long>{
    private final UserService userService;
    public OrderService (OrderJpaRepository repository, UserService userService) {
        super(repository);
        this.userService = userService;
    }
    @Override
    public Order create(Order order) throws NotEnoughFundsException {
        User buyer = order.getCustomer();

        Float price = 0f;
        for (var game : order.getSoldGames()) {
            price += game.getPrice();
        }
        if (buyer.getBalance() < price) {
            throw new NotEnoughFundsException();
        }
        buyer.setBalance(buyer.getBalance() - price);
        order.setOrderDate(LocalDate.now());
        order.setOrderPrice(price);
        order.setCompleted(false);
        userService.update(buyer);
        return super.create(order);
    }
    public void deleteGame(Order order, Game game) {
        User customer = order.getCustomer();
        if (order.getCompleted()) throw new OrderIsClosedException();

        if (!order.getSoldGames().contains(game)) throw new EntityDoesNotExistsException();

        order.getSoldGames().remove(game);
        customer.setBalance(customer.getBalance() + game.getPrice());
        userService.update(customer);
        order.setOrderPrice(order.getOrderPrice() - game.getPrice());
        if (order.getSoldGames().isEmpty()) super.deleteByID(order.getID());
        else super.update(order);
    }

}
