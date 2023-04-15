package cz.fit.cvut.tjv.game_store.buisness;

import cz.fit.cvut.tjv.game_store.dao.jpa.UserJpaRepository;
import cz.fit.cvut.tjv.game_store.domain.User;
import org.springframework.stereotype.Service;

@Service
public class UserService extends AbstractCrudService<User, String>{

    public UserService(UserJpaRepository userRepository) {
        super(userRepository);
    }

    @Override
    public User create(User user) {
        if (user.getBalance() == null) user.setBalance(0f);
        return super.create(user);
    }
    public User increaseUserBalance(User user, Float amount) {
        user.setBalance(amount + user.getBalance());
        return super.update(user);
    }

}
