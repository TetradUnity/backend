package com.tetradunity.server.controllers;

import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.models.users.EditedUser;
import com.tetradunity.server.models.general.Role;
import com.tetradunity.server.models.users.ShortInfoOptionUser;
import com.tetradunity.server.models.users.User;
import com.tetradunity.server.repositories.UserRepository;
import com.tetradunity.server.services.CheckValidService;
import com.tetradunity.server.services.ResponseService;
import com.tetradunity.server.services.StorageService;
import com.tetradunity.server.utils.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StorageService storageService;
    @Autowired
    private CheckValidService checkValidService;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @GetMapping("/check-authorized")
    public ResponseEntity<Object> checkAuthorized(HttpServletRequest req){
        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.unauthorized();
        }
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/exists")
    public ResponseEntity<Object> getInfo(@RequestParam String email) {
        if (email == null) {
            return ResponseService.failed();
        }

        UserEntity user = userRepository.findByEmail(email).orElse(null);

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);

        if (user == null) {
            response.put("message", "no_exists");
            return ResponseEntity.ok().body(response);
        }

        if (user.getRole() == Role.STUDENT) {
            response.put("message", "is_student");
        } else {
            response.put("message", "is_teacher");
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/get")
    public ResponseEntity<Object> getInfo(HttpServletRequest req, @RequestParam(required = false, defaultValue = "0") long id) {
        UserEntity user = userRepository.findById(id).orElse(null);

        if(user == null){
            user = AuthUtil.authorizedUser(req);
            if(user == null){
                return ResponseService.notFound();
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("user", new User(user));
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteUser(HttpServletRequest req, @RequestBody(required = false) long id) {
        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.unauthorized();
        }

        UserEntity userInfo = (userRepository.findById(id)).orElse(null);

        if (userInfo == null) {
            return ResponseService.failed();
        }

        if (user.getRole() == Role.CHIEF_TEACHER && userInfo.getRole().ordinal() < 2) {
            userRepository.delete(userInfo);
            Map<String, Object> response = new HashMap<>();
            response.put("ok", true);
            return ResponseEntity.ok().body(response);
        }
        return ResponseService.failed("no_permission");
    }

    @PutMapping("/edit")
    public ResponseEntity<Object> editProfile(HttpServletRequest req, @RequestBody(required = false) EditedUser editedUser) {
        UserEntity user = AuthUtil.authorizedUser(req);

        if (user == null) {
            return ResponseService.unauthorized();
        }

        if (editedUser == null) {
            return ResponseService.failed();
        }

        String email = editedUser.getEmail();
        String first_name = editedUser.getFirst_name();
        String last_name = editedUser.getLast_name();
        String password = editedUser.getPassword();
        String oldPassword = editedUser.getOldPassword();
        String avatar = editedUser.getAvatar();

        String result;

        if (password != null) {
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                if((result = checkValidService.checkPassword(password)).equals("ok")){
                    user.setPassword(passwordEncoder.encode(password));
                }
                else{
                    return ResponseService.failed(result);
                }
            } else {
                return ResponseService.failed("incorrect_password");
            }
        }

        if ((result = checkValidService.checkEmail(email, true)).equals("ok")) {
            user.setEmail(email);
        }
        else{
            return ResponseService.failed(result);
        }

        first_name = first_name == null ? user.getFirst_name() : first_name;
        last_name = last_name == null ? user.getLast_name() : last_name;

        if((result = checkValidService.checkName(first_name, last_name)).equals("ok")){
            return ResponseService.failed(result);
        }

        if (avatar != null && !avatar.isBlank()) {
            storageService.deleteFile("avatars/" + user.getAvatar());
            user.setAvatar(avatar);
        }

        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("find-users")
    public ResponseEntity<Object> getOptions(HttpServletRequest req, @RequestParam(required = false) String email,
                                             @RequestParam(required = false) String first_name, @RequestParam(required = false) String last_name,
                                             @RequestParam Role role,
                                             @RequestParam(required = false, defaultValue = "3") int limit,
                                             @RequestParam(required = false, defaultValue = "1") int page) {
        UserEntity me = AuthUtil.authorizedUser(req);

        if (me == null) {
            return ResponseService.unauthorized();
        }

        int pos = (page - 1) * limit;

        List<UserEntity> users;

        Map<String, Object> response = new HashMap<>();

        if(email == null){
            if(first_name == null || last_name == null){
                return ResponseService.failed("incorrect_data");
            }
            users = userRepository.findByNameAndRole(first_name, last_name, role.name(), limit, pos);
        }
        else{
            if(email.length() < 2) {
                return ResponseService.failed("very_short");
            }

            if (me.getRole() != Role.CHIEF_TEACHER) {
                return ResponseService.failed("no_permission");
            }

            users = userRepository.findByEmailPrefixAndRole(email, role.name(), limit, pos);
        }



        response.put("ok", true);
        response.put("users", users
                .stream()
                .map(ShortInfoOptionUser::new)
                .collect(Collectors.toList()));
        return ResponseEntity.ok().body(response);
    }
}