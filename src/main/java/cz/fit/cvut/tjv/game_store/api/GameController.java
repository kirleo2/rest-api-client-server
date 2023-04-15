package cz.fit.cvut.tjv.game_store.api;

import cz.fit.cvut.tjv.game_store.api.model.GameDto;
import cz.fit.cvut.tjv.game_store.api.model.converter.GameToDtoConverter;
import cz.fit.cvut.tjv.game_store.api.model.converter.GameToEntityConverter;
import cz.fit.cvut.tjv.game_store.buisness.GameService;
import cz.fit.cvut.tjv.game_store.domain.Game;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/games")
public class GameController extends AbstractCrudController<Game, GameDto, Long> {
    public GameController(GameService service, GameToDtoConverter toDtoConverter, GameToEntityConverter toEntityConverter) {
        super(service, toDtoConverter, toEntityConverter);
    }
    @GetMapping("/top")
    public Collection<GameDto> topOfGamesWithSellAmount(@RequestParam int amount) {
        return ((GameService) service).findGamesWithSellAmountEqualsOrGreaterThan(amount).stream().map(toDtoConverter).toList();
    }
}
