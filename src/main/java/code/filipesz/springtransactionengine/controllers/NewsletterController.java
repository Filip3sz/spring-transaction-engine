package code.filipesz.springtransactionengine.controllers;

import code.filipesz.springtransactionengine.dto.NewsletterRequest;
import code.filipesz.springtransactionengine.dto.NewsletterSendRequest;
import code.filipesz.springtransactionengine.entities.Newsletter;
import code.filipesz.springtransactionengine.services.NewsletterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/newslettter")
public class NewsletterController {

    private final NewsletterService newsletterService;
    // private final JavaMailSender mailSender;

    @PostMapping
    public ResponseEntity<Newsletter> addNewsletter(@RequestBody @Valid NewsletterRequest request) {
        Newsletter newsletter = newsletterService.addNewsletter(request);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("company@gmail.com");
        message.setTo(newsletter.getEmail());
        message.setSubject("Dziękujemy za subskrybcję (newsletter)");
        message.setText("Będziesz teraz otrzymywał ekskluzywne oferty jako pierwszy!");
        // mailSender.send(message);

        return new ResponseEntity<>(newsletter, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNewsletter(@PathVariable Long id) {
        newsletterService.deleteNewsletter(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendNewsletter(@Valid @RequestBody NewsletterSendRequest request) {
        newsletterService.sendNewsletter(request);
        return ResponseEntity.ok().build();
    }
}
