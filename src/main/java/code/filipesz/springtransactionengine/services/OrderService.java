package code.filipesz.springtransactionengine.services;

import code.filipesz.springtransactionengine.config.KafkaTopicConfig;
import code.filipesz.springtransactionengine.dto.OrderEvent;
import code.filipesz.springtransactionengine.dto.OrderItemRequest;
import code.filipesz.springtransactionengine.dto.OrderRequest;
import code.filipesz.springtransactionengine.entities.*;
import code.filipesz.springtransactionengine.repositories.CodeRepository;
import code.filipesz.springtransactionengine.repositories.OrderRepository;
import code.filipesz.springtransactionengine.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CodeRepository codeRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public List<Order> orderListInPending() {
        return orderRepository.findByStatus(OrderStatus.PENDING);
    }

    public List<Order> orderPaidList() {
        return orderRepository.findByStatus(OrderStatus.PAID);
    }

    public List<Order> orderList() {
        return orderRepository.findAll();
    }

    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono zamówienia o takim id."));
    }

    @Transactional
    public Order placeOrder(OrderRequest request) {
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal basePriceTotal = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.items()) {
            Product product = productRepository.findByIdForUpdate(itemRequest.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Produkt o ID " + itemRequest.productId() + " nie istnieje."));

            if (product.getStockQuantity() < itemRequest.quantity()) {
                throw new IllegalStateException("Brak wystarczającej ilości produktu: " + product.getName() + " na magazynie.");
            }

            product.setStockQuantity(product.getStockQuantity() - itemRequest.quantity());

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
            basePriceTotal = basePriceTotal.add(itemTotal);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.quantity())
                    .price(product.getPrice())
                    .build();

            orderItems.add(orderItem);
        }

        BigDecimal finalAmount = basePriceTotal;
        Integer appliedDiscount = 0;

        if (request.code() != null && !request.code().isBlank()) {
            String cleanCode = request.code().trim().toUpperCase();

            Code promoCode = codeRepository.findById(cleanCode)
                    .orElseThrow(() -> new IllegalArgumentException("Podany kod rabatowy nie istnieje lub został wykorzystany."));

            appliedDiscount = promoCode.getDiscount();
            BigDecimal percentageLeft = BigDecimal.valueOf(100 - appliedDiscount);

            finalAmount = basePriceTotal.multiply(percentageLeft)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (promoCode.getUsesCount() <= 1) {
                codeRepository.delete(promoCode);
            } else {
                promoCode.setUsesCount(promoCode.getUsesCount() - 1);
                codeRepository.save(promoCode);
            }
        }

        Client client = Client.builder()
                .email(request.client().email())
                .phoneNumber(request.client().phoneNumber())
                .address(request.client().address())
                .firstName(request.client().firstName())
                .lastName(request.client().lastName())
                .build();

        Order order = Order.builder()
                .client(client)
                .items(orderItems)
                .status(OrderStatus.PENDING)
                .discount(appliedDiscount)
                .totalAmount(finalAmount)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        Order savedOrder = orderRepository.save(order);

        sendOrderEvent(savedOrder, "ORDER_PLACED");
        return savedOrder;
    }

    @Transactional
    public Order payOrder(UUID orderId) {
        Order order = getOrderById(orderId);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Zamówienie nie jest w stanie PENDING.");
        }

        if (order.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Czas na opłacenie zamówienia upłynął.");
        }

        order.setStatus(OrderStatus.PAID);

        sendOrderEvent(order, "ORDER_PAID");
        return order;
    }

    @Transactional
    public Order returnOrderRequest(UUID orderId) {
        Order order = getOrderById(orderId);

        if (order.getCreatedAt().isBefore(LocalDateTime.now().minusDays(14))) {
            throw new IllegalStateException("Czas na dokonanie zwrotu minął (ponad 14 dni).");
        }

        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("Można zwrócić tylko opłacone zamówienie.");
        }

        order.setStatus(OrderStatus.IN_RETURN);

        sendOrderEvent(order, "RETURN_REQUESTED");
        return order;
    }

    @Transactional
    public Order returnOrderSuccessful(UUID orderId) {
        Order order = getOrderById(orderId);

        if (order.getStatus() != OrderStatus.IN_RETURN) {
            throw new IllegalStateException("Zamówienie nie jest w trakcie procedury zwrotu.");
        }

        for (OrderItem item : order.getItems()) {
            if (item.getProduct() != null && item.getQuantity() != null) {
                Product product = productRepository.findByIdForUpdate(item.getProduct().getId())
                        .orElse(null);

                if (product != null) {
                    product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                    productRepository.save(product);
                }
            }
        }

        order.setStatus(OrderStatus.RETURNED);

        sendOrderEvent(order, "RETURN_PROCESSED");
        return order;
    }

    private void sendOrderEvent(Order order, String eventType) {
        OrderEvent event = OrderEvent.from(order, eventType);
        kafkaTemplate.send(KafkaTopicConfig.ORDER_EVENTS_TOPIC, order.getId().toString(), event);
    }
}