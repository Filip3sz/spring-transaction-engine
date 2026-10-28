package code.filipesz.springtransactionengine.controllers;

import code.filipesz.springtransactionengine.dto.TicketRequest;
import code.filipesz.springtransactionengine.entities.Ticket;
import code.filipesz.springtransactionengine.services.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    // private final JavaMailSender mailSender;

    @GetMapping
    public ResponseEntity<List<Ticket>> ticketList() {
        return ResponseEntity.ok(ticketService.ticketList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@Valid @RequestBody TicketRequest request) {
        Ticket ticket = ticketService.createTicket(request);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("company@gmail.com");
        message.setTo(ticket.getEmail());
        message.setSubject("Stworzyłeś ticket #" + ticket.getId());
        message.setText("Oczekuj na odpowiedź mailową.");
        // mailSender.send(message);

        return new ResponseEntity<>(ticket, HttpStatus.CREATED);
    }
}
