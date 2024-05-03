package com.example.server.services;

import com.example.server.entities.UserEntity;
import com.example.server.repositories.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class CheckValidDataService {

	private static UserEntityRepository userEntityRepository;

	@Autowired
	CheckValidDataService(UserEntityRepository userEntityRepository){
		CheckValidDataService.userEntityRepository = userEntityRepository;
	}

	public static String checkUser(UserEntity userEntity, boolean checkExists){
		return checkUser(userEntity.getEmail(), userEntity.getPassword(), userEntity.getFirst_name(),
				userEntity.getLast_name(), checkExists);
	}

	public static String checkUser(String email, String password, String first_name, String last_name,
	                               boolean checkExists){
		if(email == null || password  == null || first_name == null || last_name == null){
			return "incorrect_data";
		}
		if(checkExists && !userEntityRepository.findByEmail(email).isEmpty()){
			return "user_already_exist";
		}
		Pattern pattern = Pattern.compile("^[a-zA-Z][a-zA-Z0-9._%+-]{4,}@[a-zA-Z][a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
		if(!pattern.matcher(email).matches()){
			return "incorrect_mail_format";
		}

		if(password.length() < 6){
			return "password_very_short";
		}
		if(password.length() > 32){
			return "password_very_long";
		}

		pattern = Pattern.compile("^[\\S]+$");

		if(!pattern.matcher(password).matches()){
			return "incorrect_password";
		}

		pattern = Pattern.compile("^[A-Z][a-z]{1,}$");
		if(!pattern.matcher(first_name).matches() || !pattern.matcher(last_name).matches()){
			pattern = Pattern.compile("^[А-Я][а-я]{1,}$");
			if(!pattern.matcher(first_name).matches() || !pattern.matcher(last_name).matches()){
				return "incorrect_name";
			}
		}
		return "ok";
	}
}
