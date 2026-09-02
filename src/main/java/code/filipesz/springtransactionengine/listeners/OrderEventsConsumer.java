package code.filipesz.springtransactionengine.listeners;

import code.filipesz.springtransactionengine.config.KafkaTopicConfig;
import code.filipesz.springtransactionengine.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventsConsumer {

    // private final JavaMailSender mailSender;

    @KafkaListener(topics = KafkaTopicConfig.ORDER_EVENTS_TOPIC, groupId = "transaction-group")
    public void consumeOrderEvent(OrderEvent event) {
        log.info("Odebrano zdarzenie zamówienia ze sklepu [ID: {}, Typ: {}]", event.id(), event.eventType());

        if (event.eventType() == null) {
            return;
        }

        switch (event.eventType()) {
            case "ORDER_PLACED" -> handleOrderPlaced(event);
            case "ORDER_PAID" -> handleOrderPaid(event);
            case "RETURN_REQUESTED" -> handleReturnRequested(event);
            case "RETURN_PROCESSED" -> handleReturnProcessed(event);
            default -> log.warn("Nieznany typ zdarzenia: {}", event.eventType());
        }
    }

    private void handleOrderPlaced(OrderEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("company@gmail.com");
        message.setTo(event.clientEmail());
        message.setSubject("Rozpocząłeś realizację zamówienia #" + event.id());
        message.setText("Złożyłeś zamówienie, które należy opłacić.");
        // mailSender.send(message);

        log.info("[E-MAIL] Wysyłanie powiadomienia o złożeniu zamówienia #{} do: {}", event.id(), event.clientEmail());
    }

    private void handleOrderPaid(OrderEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("company@gmail.com");
        message.setTo(event.clientEmail());
        message.setSubject("Potwierdzenie płatności za zamówienie #" + event.id());
        message.setText("Twoje zamówienie zostało pomyślnie opłacone!");
        // mailSender.send(message);

        log.info("[E-MAIL] Wysyłanie potwierdzenia opłacenia zamówienia #{} do: {}", event.id(), event.clientEmail());
    }

    private void handleReturnRequested(OrderEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("company@gmail.com");
        message.setTo(event.clientEmail());
        message.setSubject("Rozpocząłeś procedurę zwrotu #" + event.id());
        message.setText("Instrukcja wysyłki: Zapakuj towar i prześlij na nasz adres.");
        // mailSender.send(message);

        log.info("[E-MAIL] Wysyłanie instrukcji zwrotu dla zamówienia #{} do: {}", event.id(), event.clientEmail());
    }

    private void handleReturnProcessed(OrderEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("company@gmail.com");
        message.setTo(event.clientEmail());
        message.setSubject("Zwrot zamówienia #" + event.id() + " został przetworzony");
        message.setText("Twój zwrot został pomyślnie przyjęty w magazynie i czeka na weryfikację.");
        // mailSender.send(message);

        log.info("[E-MAIL] Wysyłanie potwierdzenia przetworzenia zwrotu #{} do: {}", event.id(), event.clientEmail());
    }
}