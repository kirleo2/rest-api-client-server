package cz.fit.cvut.tjv.game_store.api.model.converter;

import cz.fit.cvut.tjv.game_store.api.model.UserDto;
import cz.fit.cvut.tjv.game_store.domain.User;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UserToEntityConverter implements Function<UserDto, User> {
    @Override
    public User apply(UserDto userDto) throws NullPointerException{
        return new User(userDto.getUsername(), userDto.getFirstName(), userDto.getLastName(),
                userDto.getEmail(), userDto.getCountry(), userDto.getBalance());
    }
}
