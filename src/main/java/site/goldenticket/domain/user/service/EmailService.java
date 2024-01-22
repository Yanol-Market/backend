package site.goldenticket.domain.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendPasswordResetEmail(String to, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("goldenticket8282@gmail.com");
        message.setTo(to);
        message.setSubject("Password Reset");
        message.setText("Your new password is: " + newPassword);
        javaMailSender.send(message);
    }
}
