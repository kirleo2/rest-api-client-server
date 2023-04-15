package cz.fit.cvut.tjv.game_store.buisness;
import cz.fit.cvut.tjv.game_store.dao.jpa.GameJpaRepository;
import cz.fit.cvut.tjv.game_store.domain.Game;
import org.springframework.stereotype.Service;

import java.util.Collection;


@Service
public class GameService extends AbstractCrudService<Game, Long>{
    public GameService(GameJpaRepository gameRepository) {
        super(gameRepository);
    }

    public Collection<Game> findGamesWithSellAmountEqualsOrGreaterThan(int sellAmount) {
        return ((GameJpaRepository) repository).findBySellAmountGreaterThan(sellAmount);
    }
}
