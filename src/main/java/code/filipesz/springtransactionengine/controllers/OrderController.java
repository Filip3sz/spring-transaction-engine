package code.filipesz.springtransactionengine.controllers;

import code.filipesz.springtransactionengine.dto.OrderRequest;
import code.filipesz.springtransactionengine.entities.Order;
import code.filipesz.springtransactionengine.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    // private final JavaMailSender mailSender;

    @GetMapping("/pending")
    public List<Order> orderListInPending() {
        return orderService.orderListInPending();
    }

    @GetMapping("/paid")
    public List<Order> orderPaidLists() {
        return orderService.orderPaidList();
    }

    @GetMapping
    public List<Order> orderList() {
        return orderService.orderList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<Order> placeOrder(@Valid @RequestBody OrderRequest request) {
        return new ResponseEntity<>(orderService.placeOrder(request), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Order> payOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.payOrder(id));
    }

    @PostMapping("/{id}/sent")
    public ResponseEntity<Order> orderSent(@PathVariable UUID id) {
        Order order = orderService.getOrderById(id);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("company@gmail.com");
        message.setTo(order.getClient().getEmail());
        message.setSubject("Twoje zamówienie o numerze #" + order.getId() + " zostało wysłane!");
        message.setText("Twoje zamówienie zostało pomyślnie wysłane!");
        // mailSender.send(message);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/return-request")
    public ResponseEntity<Order> requestReturn(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.returnOrderRequest(id));
    }

    @PostMapping("/{id}/return-success")
    public ResponseEntity<Order> processReturnSuccessful(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.returnOrderSuccessful(id));
    }
}