package cz.fit.cvut.tjv.game_store.dao.jpa;

import cz.fit.cvut.tjv.game_store.dao.UserRepository;
import cz.fit.cvut.tjv.game_store.domain.User;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<User, String> {
}
