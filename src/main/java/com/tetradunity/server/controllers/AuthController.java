package com.tetradunity.server.controllers;

import com.tetradunity.server.entities.PasswordRecoveryRequest;
import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.models.general.Role;
import com.tetradunity.server.models.general.StringModel;
import com.tetradunity.server.models.users.UserWithTokens;
import com.tetradunity.server.repositories.PasswordRecoveryRequestRepository;
import com.tetradunity.server.repositories.UserRepository;
import com.tetradunity.server.services.CheckValidService;
import com.tetradunity.server.services.MailService;
import com.tetradunity.server.services.ResponseService;
import com.tetradunity.server.utils.AuthUtil;
import com.tetradunity.server.utils.JwtUtil;
import com.tetradunity.server.utils.RefreshTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/authorization")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private PasswordRecoveryRequestRepository passwordRecoveryRequestRepository;
    @Autowired
    private CheckValidService checkValidService;

    private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @PostMapping("/refresh-authorized")
    public ResponseEntity<Object> refreshAuthorized(HttpServletRequest req) {
        UserWithTokens userWithTokens = AuthUtil.refreshAuthorizedUser(req);
        if (userWithTokens == null) {
            return ResponseService.unauthorized();
        }
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("accessToken", userWithTokens.getAccessToken());
        response.put("refreshToken", userWithTokens.getRefreshToken());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody(required = false) UserEntity userInfo) {
        if (userInfo == null) {
            return ResponseService.failed();
        }
        if (userInfo.getEmail() == null || userInfo.getPassword() == null) {
            return ResponseService.failed();
        }

        UserEntity user = userRepository.findByEmail(userInfo.getEmail()).orElse(null);

        if (user == null) {
            return ResponseService.failed("user_not_found", HttpStatus.NOT_FOUND);
        }

        if (passwordEncoder.matches(userInfo.getPassword(), user.getPassword())) {
            Map<String, Object> response = new HashMap<>();
            response.put("ok", true);
            response.put("accessToken", JwtUtil.generateToken(String.valueOf(user.getId())));
            response.put("refreshToken", RefreshTokenUtil.createRefreshToken(user).getToken());
            return ResponseEntity.ok().body(response);
        }

        return ResponseService.failed("incorrect_password");
    }

    @PostMapping("/create-user")
    public ResponseEntity<Object> createTeacher(HttpServletRequest req, @RequestBody(required = false) UserEntity newUserInfo) {
        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.unauthorized();
        }

        if (user.getRole() == Role.CHIEF_TEACHER) {
            String email = newUserInfo.getEmail();
            String password = newUserInfo.getPassword();
            String first_name = newUserInfo.getFirst_name();
            String last_name = newUserInfo.getLast_name();
            Role role = newUserInfo.getRole();

            if (role != Role.TEACHER && role != Role.STUDENT) {
                ResponseService.failed();
            }

            String validData = checkValidService.checkUser(email, password, first_name, last_name, true);

            if (!validData.equals("ok")) {
                return ResponseService.failed(validData);
            }

            UserEntity newUser = new UserEntity(
                    email, passwordEncoder.encode(password),
                    first_name, last_name, role
            );
            userRepository.save(newUser);
            Map<String, Object> response = new HashMap<>();
            response.put("ok", true);
            return ResponseEntity.ok().body(response);
        }
        return ResponseService.failed("no_permission");
    }
    @PostMapping("forgot-password")
    public ResponseEntity<Object> forgotPassword(@RequestParam String email){
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        if(user == null){
            return ResponseService.failed();
        }

        PasswordRecoveryRequest request = passwordRecoveryRequestRepository.findByEmail(email).orElse(null);

        if(request == null){
            request = passwordRecoveryRequestRepository.save(new PasswordRecoveryRequest(email));
        }
        else{
            passwordRecoveryRequestRepository.delete(request);
            request = passwordRecoveryRequestRepository.save(new PasswordRecoveryRequest(email));
        }

        mailService.sendRecoveryPassword(email, user.getFirst_name(), request.getUid().toString());

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("recovery-password/{uid}")
    public ResponseEntity<Object> recoveryPassword(@PathVariable String uid, @RequestBody StringModel new_password){
        PasswordRecoveryRequest request = passwordRecoveryRequestRepository.findByUid(UUID.fromString(uid)).orElse(null);

        if(request == null){
            return ResponseService.failed();
        }

        if(request.isActual()){
            UserEntity user = userRepository.findByEmail(request.getEmail()).orElse(null);

            if(user == null){
                return ResponseService.failed();
            }

            String password = new_password.getModel();

            if (password.length() < 6) {
                return ResponseService.failed("password_very_short");
            }
            if (password.length() > 32) {
                return ResponseService.failed("password_very_long");
            }

            Pattern pattern = Pattern.compile("^[\\S]+$");

            if (!pattern.matcher(password).matches()) {
                return ResponseService.failed("incorrect_password");
            }

            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        }

        passwordRecoveryRequestRepository.delete(request);

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        return ResponseEntity.ok().body(response);
    }
}