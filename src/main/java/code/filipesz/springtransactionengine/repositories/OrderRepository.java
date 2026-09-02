package code.filipesz.springtransactionengine.repositories;

import code.filipesz.springtransactionengine.entities.Order;
import code.filipesz.springtransactionengine.entities.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllByStatusAndExpiresAtBefore(OrderStatus status, LocalDateTime dateTime);

    List<Order> findByStatus(OrderStatus status);
}
