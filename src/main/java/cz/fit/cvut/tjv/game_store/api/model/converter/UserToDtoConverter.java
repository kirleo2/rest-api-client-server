package cz.fit.cvut.tjv.game_store.api.model.converter;

import cz.fit.cvut.tjv.game_store.api.model.UserDto;
import cz.fit.cvut.tjv.game_store.domain.User;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UserToDtoConverter implements Function<User, UserDto> {
    @Override
    public UserDto apply(User user){
        var userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setUsername(user.getUsername());
        userDto.setBalance(user.getBalance());
        userDto.setCountry(user.getCountry());
        return userDto;
    }

}
