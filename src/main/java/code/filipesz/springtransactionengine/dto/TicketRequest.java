package code.filipesz.springtransactionengine.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record TicketRequest(

        @NotBlank(message = "Numer zamówienia nie może być pusty.")
        UUID orderId,

        @NotBlank(message = "Email nie może być pusty.")
        @Email(message = "Niepoprawny format email.")
        String email,

        @NotBlank(message = "Tytuł nie może być pusty.")
        String title,

        @NotBlank(message = "Opis nie może być pusty.")
        String description
) {
}
