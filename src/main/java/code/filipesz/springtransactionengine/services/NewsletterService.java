package code.filipesz.springtransactionengine.services;

import code.filipesz.springtransactionengine.dto.NewsletterRequest;
import code.filipesz.springtransactionengine.dto.NewsletterSendRequest;
import code.filipesz.springtransactionengine.entities.Newsletter;
import code.filipesz.springtransactionengine.repositories.NewsletterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsletterService {

    private final NewsletterRepository newsletterRepository;

    // @Autowired
    // private JavaMailSender mailSender;

    @Transactional
    public Newsletter addNewsletter(NewsletterRequest request) {
        if (newsletterRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Newsletter jest już podpięty pod ten adres email.");
        }

        Newsletter newsletter = Newsletter.builder()
                .email(request.email())
                .build();

        return newsletterRepository.save(newsletter);
    }

    @Transactional
    public Newsletter deleteNewsletter(Long id) {
        Newsletter newsletter = newsletterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono newslettera o takim id."));

        newsletterRepository.delete(newsletter);
        return newsletter;
    }

    public List<Newsletter> sendNewsletter(NewsletterSendRequest request) {
        List<Newsletter> subscribers = newsletterRepository.findAll();

        for (Newsletter subscriber : subscribers) {

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("company@gmail.com");
            message.setTo(subscriber.getEmail());
            message.setSubject(request.title());
            message.setText(request.message());

            // mailSender.send(message);
        }
        return subscribers;
    }
}
