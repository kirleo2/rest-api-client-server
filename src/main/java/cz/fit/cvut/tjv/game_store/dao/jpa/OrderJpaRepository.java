package cz.fit.cvut.tjv.game_store.dao.jpa;

import cz.fit.cvut.tjv.game_store.dao.OrderRepository;
import cz.fit.cvut.tjv.game_store.domain.Order;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
