package com.tetradunity.server.services;

import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.json.*;

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

@Component
public class CheckValidDataService {

	private static UserRepository userRepository;

	@Autowired
	CheckValidDataService(UserRepository userRepository){
		CheckValidDataService.userRepository = userRepository;
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
		if(checkExists && !userRepository.findByEmail(email).isEmpty()){
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

	public static String check(String exam) throws RuntimeException {
		JSONArray questions = new JSONArray(exam);
		JSONArray proccessedQuestions = new JSONArray();
		JSONObject proccessedQuestion;
		JSONArray answers;
		JSONObject answer;
		String title;
		String text;
		String type;
		JSONObject question;
		Set<String> set;
		boolean existsCorrect;
		for (Object temp : questions) {
			question = (JSONObject) temp;
			title = question.getString("title");
			if(title.trim().equals("")){
				throw new RuntimeException();
			}
			answers = question.getJSONArray("answers");
			(proccessedQuestion = new JSONObject()).put("title", title);
			proccessedQuestion.put("type", type = question.getString("type"));
			switch(type){
				case "ONE_ANSWER":
					existsCorrect = false;
					set = new TreeSet<>();
					for(int i = 0; i < answers.length(); i++){
						answer = answers.getJSONObject(i);
						if((text = answer.getString("content")).trim().equals("")) {
							throw new RuntimeException();
						}
						if(set.contains(text)){
							answers.remove(i--);
						}
						else{
							set.add(text);
						}
						if(existsCorrect & (existsCorrect |= answer.getBoolean("isCorrect"))){
							throw new RuntimeException();
						}
					}
					break;
				case "MULTY_ANSWER":
					set = new TreeSet<>();
					for(int i = 0; i < answers.length(); i++){
						answer = answers.getJSONObject(i);
						if((text = answer.getString("content")).trim().equals("")) {
							throw new RuntimeException();
						}
						if(set.contains(text)){
							answers.remove(i--);
						}
						else{
							set.add(text);
						}
						answer.getBoolean("isCorrect");
					}
					break;
				case "TEXT":
					set = new TreeSet<>();
					for(int i = 0; i < answers.length(); i++) {
						answer = answers.getJSONObject(i);
						if ((text = answer.getString("content")).trim().equals("")) {
							throw new RuntimeException();
						}
						if (set.contains(text)) {
							answers.remove(i--);
						} else {
							set.add(text);
						}
					}
					break;
				default:
					throw new RuntimeException();
			}
			proccessedQuestion.put("answers", answers);
			proccessedQuestions.put(proccessedQuestion);
		}
		return proccessedQuestions.toString();
	}
}
