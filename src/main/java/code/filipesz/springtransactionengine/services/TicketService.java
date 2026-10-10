package code.filipesz.springtransactionengine.services;

import code.filipesz.springtransactionengine.dto.TicketRequest;
import code.filipesz.springtransactionengine.entities.Ticket;
import code.filipesz.springtransactionengine.repositories.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public List<Ticket> ticketList() {
        return ticketRepository.findAll();
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie ma ticketu o takim id."));
    }

    @Transactional
    public Ticket createTicket(TicketRequest request) {
        Ticket ticket = ticketRepository.findByOrderId(request.orderId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono zamówienia."));
        ticket.setOrderId(request.orderId());
        ticket.setEmail(request.email());
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());

        return ticketRepository.save(ticket);
    }
}