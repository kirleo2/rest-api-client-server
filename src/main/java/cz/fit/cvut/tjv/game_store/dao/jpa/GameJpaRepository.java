package cz.fit.cvut.tjv.game_store.dao.jpa;

import cz.fit.cvut.tjv.game_store.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface GameJpaRepository extends JpaRepository<Game, Long> {
    @Query("SELECT g from Game g where size(g.orders) >= :count")
    Collection<Game> findBySellAmountGreaterThan(@Param("count") int count);
}
