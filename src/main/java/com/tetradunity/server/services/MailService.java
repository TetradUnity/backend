package com.tetradunity.server.services;

import com.tetradunity.server.entities.UserEntity;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import freemarker.template.Configuration;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
@AllArgsConstructor
public class MailService {

    private final Configuration configuration;
    private final JavaMailSender mailSender;

    @SneakyThrows
    public void sendAuthEmail(final UserEntity user, final String subject_title) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                false,
                "UTF-8");
        helper.setSubject("Thank you for registration," + user.getFirst_name());
        helper.setTo(user.getEmail());
        String emailContent = getReminderEmailContent(user, subject_title);
        helper.setText(emailContent, true);
        mailSender.send(mimeMessage);
    }

    @SneakyThrows
    private String getReminderEmailContent(final UserEntity user, String subject_title) {
        StringWriter writer = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("first_name", user.getFirst_name());
        model.put("last_name", user.getLast_name());
        model.put("subject_title", subject_title);
        model.put("password", user.getPassword());
        configuration.getTemplate("reminder.ftlh")
                .process(model, writer);
        return writer.getBuffer().toString();
    }

}
