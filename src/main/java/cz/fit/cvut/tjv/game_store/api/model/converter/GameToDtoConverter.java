package cz.fit.cvut.tjv.game_store.api.model.converter;

import cz.fit.cvut.tjv.game_store.api.model.GameDto;
import cz.fit.cvut.tjv.game_store.domain.Game;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class GameToDtoConverter implements Function<Game, GameDto> {
    @Override
    public GameDto apply(Game game) {
        var gameDto = new GameDto();
        gameDto.setId(game.getId());
        gameDto.setDescription(game.getDescription());
        gameDto.setPrice(game.getPrice());
        gameDto.setTitle(game.getTitle());
        return gameDto;
    }
}
