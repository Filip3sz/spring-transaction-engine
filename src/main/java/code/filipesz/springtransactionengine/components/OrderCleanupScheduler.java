package code.filipesz.springtransactionengine.components;

import code.filipesz.springtransactionengine.entities.Order;
import code.filipesz.springtransactionengine.entities.OrderItem;
import code.filipesz.springtransactionengine.entities.OrderStatus;
import code.filipesz.springtransactionengine.entities.Product;
import code.filipesz.springtransactionengine.repositories.OrderRepository;
import code.filipesz.springtransactionengine.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCleanupScheduler {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void releaseExpiredReservations() {
        log.info("Uruchamianie mechanizmu sprawdzania przeterminowanych rezerwacji...");

        List<Order> expiredOrders = orderRepository.findAllByStatusAndExpiresAtBefore(
                OrderStatus.PENDING, LocalDateTime.now()
        );

        for (Order order : expiredOrders) {
            log.info("Anulowanie zamówienia: {} - czas na płatność minął.", order.getId());

            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);

            for (OrderItem item : order.getItems()) {
                if (item.getProduct() != null && item.getQuantity() != null) {
                    Product product = productRepository.findByIdForUpdate(item.getProduct().getId())
                            .orElse(null);

                    if (product != null) {
                        product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                        productRepository.save(product);
                        log.info("Zwrócono {} szt. produktu ID: {} na magazyn.", item.getQuantity(), product.getId());
                    }
                }
            }
        }
    }
}