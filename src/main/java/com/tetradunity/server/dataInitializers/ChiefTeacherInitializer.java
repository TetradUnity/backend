package com.tetradunity.server.dataInitializers;

import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.models.general.Role;
import com.tetradunity.server.repositories.UserRepository;
import com.tetradunity.server.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ChiefTeacherInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @Value(value = "${first-chief-teacher.email}")
    private String email;
    @Value(value = "${first-chief-teacher.password}")
    private String password;
    @Value(value = "${first-chief-teacher.first-name}")
    private String first_name;
    @Value(value = "${first-chief-teacher.last-name}")
    private String last_name;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsChiefTeacher()) {
            UserEntity user = new UserEntity(email, passwordEncoder.encode(password), first_name, last_name, Role.CHIEF_TEACHER);
            userRepository.save(user);
        }
    }
}