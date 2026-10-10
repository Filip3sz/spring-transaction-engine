package code.filipesz.springtransactionengine.repositories;

import code.filipesz.springtransactionengine.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByOrderId(UUID orderId);
}
