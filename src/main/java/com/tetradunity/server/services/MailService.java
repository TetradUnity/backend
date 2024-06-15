package com.tetradunity.server.services;

import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.props.MailProperties;
import jakarta.mail.MessagingException;
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
    private final String linkRecoveryPassword;

    @Autowired
    public MailService(Configuration configuration, JavaMailSender mailSender,
                       @Value("${spring.mail.link-exam}") String linkExam, @Value("${spring.mail.link-recovery-password}") String linkRecoveryPassword) {
        this.configuration = configuration;
        this.mailSender = mailSender;
        this.linkExam = linkExam;
        this.linkRecoveryPassword = linkRecoveryPassword;
    }

    public void sendLinkToExam(String email, String first_name, String last_name, String uid, String subject_title) {
        new Thread(() -> {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = null;
            try {
                helper = new MimeMessageHelper(mimeMessage,
                    false,
                    "UTF-8");
                helper.setSubject("Посилання на екзамен");
                helper.setTo(email);
                String emailContent = getLetterStartExam(first_name, last_name, subject_title, linkExam + uid);
                helper.setText(emailContent, true);
            } catch (MessagingException e) {}
            mailSender.send(mimeMessage);
        }).start();
    }

    @SneakyThrows
    private String getLetterStartExam(String first_name, String last_name, String subject_title, String link) {
        Map<String, Object> model = new HashMap<>();
        model.put("first_name", first_name);
        model.put("last_name", last_name);
        model.put("subject_title", subject_title);
        model.put("link_exam", link);

        StringWriter writer = new StringWriter();
        configuration.getTemplate("letters/startExam.ftlh")
                .process(model, writer);
        return writer.toString();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @SneakyThrows
    public void sendExamComplete(String first_name, String last_name, String subject_title, String email) {
        new Thread(() -> {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = null;
            try {
                helper = new MimeMessageHelper(mimeMessage,
                    false,
                    "UTF-8");
                helper.setSubject("Успішна здача екзамену");
                helper.setTo(email);
                String emailContent = getExamComplete(first_name, last_name, subject_title);
                helper.setText(emailContent, true);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            mailSender.send(mimeMessage);
        }).start();
    }

    @SneakyThrows
    private String getExamComplete(String first_name, String last_name, String subject_title) {
        Map<String, Object> model = new HashMap<>();
        model.put("first_name", first_name);
        model.put("last_name", last_name);
        model.put("subject_title", subject_title);

        StringWriter writer = new StringWriter();
        configuration.getTemplate("letters/examComplete.ftlh")
                .process(model, writer);
        return writer.toString();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @SneakyThrows
    public void sendExamFail(String first_name, String last_name, String subject_title, String email) {
        new Thread(() -> {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = null;
            try {
                helper = new MimeMessageHelper(mimeMessage,
                    false,
                    "UTF-8");
                helper.setSubject("Провал екзамену");
                helper.setTo(email);
                String emailContent = getExamFail(first_name, last_name, subject_title);
                helper.setText(emailContent, true);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            mailSender.send(mimeMessage);
        }).start();
    }

    @SneakyThrows
    private String getExamFail(String first_name, String last_name, String subject_title) {
        Map<String, Object> model = new HashMap<>();
        model.put("first_name", first_name);
        model.put("last_name", last_name);
        model.put("subject_title", subject_title);

        StringWriter writer = new StringWriter();
        configuration.getTemplate("letters/examFail.ftlh")
                .process(model, writer);
        return writer.toString();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @SneakyThrows
    public void sendAuth(String first_name, String last_name, String subject_title, String password, String email) {
        new Thread(() -> {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = null;
            try {
                helper = new MimeMessageHelper(mimeMessage,
                        false,
                        "UTF-8");
                helper.setTo(email);
                String emailContent = getAuth(first_name, last_name, subject_title, password);
                helper.setText(emailContent, true);
                helper.setSubject("Авторизація");
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            mailSender.send(mimeMessage);
        }).start();
    }

    @SneakyThrows
    private String getAuth(String first_name, String last_name, String subject_title, String password) {
        Map<String, Object> model = new HashMap<>();
        model.put("first_name", first_name);
        model.put("last_name", last_name);
        model.put("subject_title", subject_title);
        model.put("password", password);

        StringWriter writer = new StringWriter();
        configuration.getTemplate("letters/auth.ftlh")
                .process(model, writer);
        return writer.toString();
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @SneakyThrows
    public void sendRecoveryPassword(String email, String first_name, String uid) {
        new Thread(() -> {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = null;
            try {
                helper = new MimeMessageHelper(mimeMessage,
                    false,
                    "UTF-8");
                helper.setTo(email);
                String emailContent = getRecoveryPassword(first_name, linkRecoveryPassword + uid);
                helper.setText(emailContent, true);
                helper.setSubject("Скидання пароля");
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            mailSender.send(mimeMessage);
        }).start();
    }

    @SneakyThrows
    private String getRecoveryPassword(String first_name, String link) {
        Map<String, Object> model = new HashMap<>();
        model.put("first_name", first_name);
        model.put("link", link);

        StringWriter writer = new StringWriter();
        configuration.getTemplate("letters/recoveryPassword.ftlh")
                .process(model, writer);
        return writer.toString();
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @SneakyThrows
    public void sendApplicationSubmitted(String email, String first_name, String last_name) {
        new Thread(() -> {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = null;
            try {
                helper = new MimeMessageHelper(mimeMessage,
                    false,
                    "UTF-8");
                helper.setSubject("Заявка прийнята");
                helper.setTo(email);
                String emailContent = getApplicationSubmitted(first_name, last_name);
                helper.setText(emailContent, true);
            } catch (MessagingException e) {}
            mailSender.send(mimeMessage);
        }).start();
    }

    @SneakyThrows
    private String getApplicationSubmitted(String first_name, String last_name) {
        Map<String, Object> model = new HashMap<>();
        model.put("first_name", first_name);
        model.put("last_name", last_name);

        StringWriter writer = new StringWriter();
        configuration.getTemplate("letters/applicationSubmitted.ftlh")
                .process(model, writer);
        return writer.toString();
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @SneakyThrows
    public void sendStartSubjectRemind(String email, String first_name, String subject_title) {
        new Thread(() -> {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = null;
            try {
                helper = new MimeMessageHelper(mimeMessage,
                    false,
                    "UTF-8");
                helper.setSubject("Предмет розпочато");
                helper.setTo(email);
                String emailContent = getStartSubjectRemind(first_name, subject_title);
                helper.setText(emailContent, true);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        mailSender.send(mimeMessage);

        }).start();
    }

    @SneakyThrows
    private String getStartSubjectRemind(String first_name, String last_name) {
        Map<String, Object> model = new HashMap<>();
        model.put("first_name", first_name);
        model.put("subject_title", last_name);

        StringWriter writer = new StringWriter();
        configuration.getTemplate("letters/startSubjectRemind.ftlh")
                .process(model, writer);
        return writer.toString();
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @SneakyThrows
    public void sendSubjectCanceled(String email, String first_name, String subject_title) {
        new Thread(() -> {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = null;
            try {
                helper = new MimeMessageHelper(mimeMessage,
                        false,
                        "UTF-8");
                helper.setSubject("Відміна предмету");
                helper.setTo(email);
                String emailContent = getSubjectCanceled(first_name, subject_title);
                helper.setText(emailContent, true);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            mailSender.send(mimeMessage);
        }).start();
    }

    @SneakyThrows
    private String getSubjectCanceled(String first_name, String subject_title) {
        Map<String, Object> model = new HashMap<>();
        model.put("first_name", first_name);
        model.put("subject_title", subject_title);

        StringWriter writer = new StringWriter();
        configuration.getTemplate("letters/subjectCanceled.ftlh")
                .process(model, writer);
        return writer.toString();
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @SneakyThrows
    public void sendConferenceRemind(String email, String subject_title) {
        new Thread(() -> {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = null;
            try {
                helper = new MimeMessageHelper(mimeMessage,
                        false,
                        "UTF-8");
                helper.setSubject("Незабаром конференція");
                helper.setTo(email);
                String emailContent = getConferenceRemind(subject_title);
                helper.setText(emailContent, true);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        mailSender.send(mimeMessage);
        }).start();
    }

    @SneakyThrows
    private String getConferenceRemind(String subject_title) {
        Map<String, Object> model = new HashMap<>();
        model.put("subject_title", subject_title);

        StringWriter writer = new StringWriter();
        configuration.getTemplate("letters/subjectCanceled.ftlh")
                .process(model, writer);
        return writer.toString();
    }
}