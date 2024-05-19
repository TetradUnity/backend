package com.tetradunity.server.services;

import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.props.MailProperties;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import freemarker.template.Configuration;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailService {

    private final Configuration configuration;
    private final JavaMailSender mailSender;
    private final String linkExam;

    @Autowired
    public MailService(Configuration configuration, JavaMailSender mailSender, @Value("${spring.mail.link-exam}") String linkExam) {
        this.configuration = configuration;
        this.mailSender = mailSender;
        this.linkExam = linkExam;
    }

    @SneakyThrows
    public void sendLinkToExam(final String first_name, final String last_name, final String uid, final String subject_title, final String email) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                false,
                "UTF-8");
        helper.setSubject("Link to exam");
        helper.setTo(email);
        String emailContent = getLetterStartExam(first_name, last_name, subject_title, linkExam + uid);
        helper.setText(emailContent, true);
        mailSender.send(mimeMessage);
    }

    @SneakyThrows
    private String getLetterStartExam(final String first_name, final String last_name, final String subject_title, final String link) {
        StringWriter writer = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("first_name", first_name);
        model.put("last_name", last_name);
        model.put("subject_title", subject_title);
        model.put("link", link);
        configuration.getTemplate("reminder.ftlh")
                .process(model, writer);
        return writer.getBuffer().toString();
    }

}
