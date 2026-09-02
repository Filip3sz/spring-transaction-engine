package code.filipesz.springtransactionengine.dto;

import jakarta.validation.constraints.NotBlank;

public record NewsletterSendRequest(
        @NotBlank(message = "Tytuł jest wymagany.")
        String title,

        @NotBlank(message = "Wiadomość jest wymagana.")
        String message
) {
}
