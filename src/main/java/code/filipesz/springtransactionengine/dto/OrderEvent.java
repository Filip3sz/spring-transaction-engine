package code.filipesz.springtransactionengine.dto;

import code.filipesz.springtransactionengine.entities.Order;
import code.filipesz.springtransactionengine.entities.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderEvent(
        UUID id,
        String clientEmail,
        OrderStatus status,
        BigDecimal totalAmount,
        Integer discount,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        String eventType
) {
    public static OrderEvent from(Order order, String eventType) {
        return new OrderEvent(
                order.getId(),
                order.getClient() != null ? order.getClient().getEmail() : null,
                order.getStatus(),
                order.getTotalAmount(),
                order.getDiscount(),
                order.getCreatedAt(),
                order.getExpiresAt(),
                eventType
        );
    }
}