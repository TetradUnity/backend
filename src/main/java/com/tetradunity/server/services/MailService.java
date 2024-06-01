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
    public void sendLinkToExam(String first_name, String last_name, String uid, String subject_title, String email) {
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
    private String getLetterStartExam(String first_name, String last_name, String subject_title, String link) {
        Map<String, Object> model = new HashMap<>();
        model.put("first_name", first_name);
        model.put("last_name", last_name);
        model.put("subject_title", subject_title);
        model.put("link_exam", link);
        model.put("additional_content", "Ваш додатковий контент тут");

        StringWriter writer = new StringWriter();
        configuration.getTemplate("startExam.ftlh")
                .process(model, writer);
        return writer.toString();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @SneakyThrows
    public void sendExamComplete(String first_name, String last_name, String subject_title, String email) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                false,
                "UTF-8");
        helper.setSubject("Complete exam!");
        helper.setTo(email);
        String emailContent = getExamComplete(first_name, last_name, subject_title);
        helper.setText(emailContent, true);
        mailSender.send(mimeMessage);
    }

    @SneakyThrows
    private String getExamComplete(String first_name, String last_name, String subject_title) {
        Map<String, Object> model = new HashMap<>();
        model.put("first_name", first_name);
        model.put("last_name", last_name);
        model.put("subject_title", subject_title);
        model.put("additional_content", "Ваш додатковий контент тут");

        StringWriter writer = new StringWriter();
        configuration.getTemplate("examComplete.ftlh")
                .process(model, writer);
        return writer.toString();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @SneakyThrows
    public void sendExamFail(String first_name, String last_name, String subject_title, String email) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                false,
                "UTF-8");
        helper.setSubject("Complete exam!");
        helper.setTo(email);
        String emailContent = getExamFail(first_name, last_name, subject_title);
        helper.setText(emailContent, true);
        mailSender.send(mimeMessage);
    }

    @SneakyThrows
    private String getExamFail(String first_name, String last_name, String subject_title) {
        Map<String, Object> model = new HashMap<>();
        model.put("first_name", first_name);
        model.put("last_name", last_name);
        model.put("subject_title", subject_title);
        model.put("additional_content", "Ваш додатковий контент тут");

        StringWriter writer = new StringWriter();
        configuration.getTemplate("examFail.ftlh")
                .process(model, writer);
        return writer.toString();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @SneakyThrows
    public void sendAuth(String first_name, String last_name, String subject_title, String password, String email) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                false,
                "UTF-8");
        helper.setSubject("Complete exam!");
        helper.setTo(email);
        String emailContent = getAuth(first_name, last_name, subject_title, password);
        helper.setText(emailContent, true);
        mailSender.send(mimeMessage);
    }

    @SneakyThrows
    private String getAuth(String first_name, String last_name, String subject_title, String password) {
        Map<String, Object> model = new HashMap<>();
        model.put("first_name", first_name);
        model.put("last_name", last_name);
        model.put("subject_title", subject_title);
        model.put("password", password);
        model.put("additional_content", "Ваш додатковий контент тут");

        StringWriter writer = new StringWriter();
        configuration.getTemplate("auth.ftlh")
                .process(model, writer);
        return writer.toString();
    }
}
