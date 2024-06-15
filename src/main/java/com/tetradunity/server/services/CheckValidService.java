package com.tetradunity.server.services;

import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.json.*;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

@Service
public class CheckValidService {

    private static UserRepository userRepository;

    @Autowired
    CheckValidService(UserRepository userRepository) {
        CheckValidService.userRepository = userRepository;
    }

    public String checkUser(UserEntity userEntity, boolean checkExists) {
        return checkUser(userEntity.getEmail(), userEntity.getPassword(), userEntity.getFirst_name(),
                userEntity.getLast_name(), checkExists);
    }

    public String checkUser(String email, String first_name, String last_name) {
        String result;

        if(!(result = checkEmail(email, false)).equals("ok")){
            return result;
        }

        if(!(result = checkName(first_name, last_name)).equals("ok")){
            return result;
        }

        return "ok";
    }

    public String checkUser(String email, String password, String first_name, String last_name,
                                   boolean checkExists) {
        String result;

        if(!(result = checkEmail(email, checkExists)).equals("ok")){
            return result;
        }

        if(!(result = checkPassword(password)).equals("ok")){
            return result;
        }

        if(!(result = checkName(first_name, last_name)).equals("ok")){
            return result;
        }

        return "ok";
    }

    public String checkEmail(String email, boolean checkExists){
        if(email == null){
            return "incorrect_data";
        }

        if (checkExists && userRepository.findByEmail(email).isPresent()) {
            return "user_already_exist";
        }
        Pattern pattern = Pattern.compile("^[a-zA-Z][a-zA-Z0-9._%+-]{4,}@[a-zA-Z][a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        if (!pattern.matcher(email).matches()) {
            return "incorrect_mail_format";
        }
        return "ok";
    }

    public String checkPassword(String password){
        if(password == null){
            return "incorrect_data";
        }

        if (password.length() < 6) {
            return "password_very_short";
        }
        if (password.length() > 32) {
            return "password_very_long";
        }

        Pattern pattern = Pattern.compile("^[\\S]+$");

        if (!pattern.matcher(password).matches()) {
            return "incorrect_password";
        }

        return "ok";
    }

    public String checkName(String first_name, String last_name){
        if (first_name == null || last_name == null) {
            return "incorrect_data";
        }

        Pattern pattern = Pattern.compile("^[A-Z][a-z]{1,}$");
        if (!pattern.matcher(first_name).matches() || !pattern.matcher(last_name).matches()) {
            pattern = Pattern.compile("^[А-ЯІЇЄҐ][а-яіїєґ']{1,}$");
            if (!pattern.matcher(first_name).matches() || !pattern.matcher(last_name).matches()) {
                return "incorrect_name";
            }
        }
        return "ok";
    }
}
