package cz.fit.cvut.tjv.game_store.api.model.converter;

import cz.fit.cvut.tjv.game_store.api.model.GameDto;
import cz.fit.cvut.tjv.game_store.buisness.exceptions.InvalidEntityStateException;
import cz.fit.cvut.tjv.game_store.domain.Game;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class GameToEntityConverter implements Function<GameDto, Game> {
        @Override
        public Game apply(GameDto gameDto) throws InvalidEntityStateException {

                return new Game(gameDto.getId(), gameDto.getTitle(), gameDto.getPrice(), gameDto.getDescription());
        }
}
