package com.tetradunity.server.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class JSONService {
    public static int getTime(String exam){
        try{
            JSONArray questions = new JSONArray(exam);
            return questions.getJSONObject(0).getInt("time");
        }catch(JSONException ex){
            return 0;
        }
    }

    public static String checkTest(String exam) throws RuntimeException {
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
        JSONObject GeneralInfo = questions.getJSONObject(0);
        for (Iterator<String> it = GeneralInfo.keys(); it.hasNext(); ) {
            String key = it.next();
            switch (key){
                case "time":
                    int val = GeneralInfo.getInt(key);
                    if(val <= 0){
                        GeneralInfo.put(key, 0);
                    }
                    break;
                default:
                    GeneralInfo.remove(key);
            }
        }

        for (int i = 1; i < questions.length(); i++) {
            question = questions.getJSONObject(i);
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
