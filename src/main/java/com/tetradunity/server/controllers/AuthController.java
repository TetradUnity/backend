package com.tetradunity.server.controllers;

import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.models.Role;
import com.tetradunity.server.models.UserWithTokens;
import com.tetradunity.server.repositories.UserRepository;
import com.tetradunity.server.services.CheckValidService;
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

@RestController
@RequestMapping("/authorization")
public class AuthController {

    @Autowired
    UserRepository userRepository;

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

            String validData = CheckValidService.checkUser(email, password, first_name, last_name, true);

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
}