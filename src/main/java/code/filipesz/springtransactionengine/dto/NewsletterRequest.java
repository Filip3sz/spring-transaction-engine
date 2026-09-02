package code.filipesz.springtransactionengine.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record NewsletterRequest(
        @NotBlank(message = "Email jest wymagany.")
        @Email(message = "Niepoprawny format email.")
        String email
) {
}
