package com.tetradunity.server.services;

public class CheckValidTestService {

    public static String check(String exam) throws RuntimeException{
        JSONArray questions = new JSONArray(exam);
        JSONArray proccessedQuestions = new JSONARrray();
        JSONObject proccessedQuestion;
        String title; 
        for(JSONObject question : questions){ 
            proccessedQuestion = new JSONObject(); 
            if((title = question.getString("title")).trim().equals("")){ 
                throw new RuntimeException(); 
            } 
            proccessedQuestion.put("title", title);
            switch (question.getString("type")) { 
                case "ONE_ANSWER": 
                    JSONArray answers = question.getJSONArray("answer"); 
                    Set<JSONObject> setAnswers = new TreeSet<>(); 
                    JSONObject answer; 
                    for(int i = 0; i < answers.length; i++){ 
                        answer = answers.getJSONObject(i); 
                        if(answer.has("text")){ 
                            if(answer.getString("text").trim().equals("")){
                                throw new RuntimeException();
                            } 
                        } 
                        if(answer.has("imgSource")){ 
                            if(answer.getString("imgSource").trim().equals("")){
                                throw new RuntimeException();
                            } 
                        } 
                        else{ 
                            if(!answer.has("text")){ 
                                throw new RuntimeException(); 
                            } 
                        } 
                        if(setAnswers.contains(answer)){ 
                            answers.remove(i--); 
                        } 
                        else{ 
                            setAnswers.add(answer); 
                        } 
                    } 
                    if(answers.length() <= 1){ 
                        throw new RuntimeException(); 
                    } 
                    proccessedQuestion.put("answers", answers);
                    JSONArray rightAnswers = question.getJSONArray("rightAnswers"); 
                    Set<Integer> setRightAnswers = new TreeSet<>(); 
                    int rightAnswer; 
                    for(int i = 0; i < rightAnswers.length; i++){ 
                        if((rightAnswer = rightAnswers.get(i)).trim().equals("")){
                            rightAnswers.remove(i--); 
                            continue;
                        }
                        if(setRightAnswers.contains(rightAnswer)){ 
                            rightAnswers.remove(i--); 
                        } 
                        else{ 
                            setRightAnswers.add(rightAnswer); 
                        } 
                    } 
                    if(rightAnswers.length() != 1){ 
                        throw new RuntimeException(); 
                    }
                    break; 
                case "MULTY_ANSWER": 
                    JSONArray answers = question.getJSONArray("answer"); 
                    Set<JSONObject> setAnswers = new TreeSet<>(); 
                    JSONObject answer; 
                    for(int i = 0; i < answers.length; i++){ 
                        answer = answers.getJSONObject(i); 
                        if(answer.has("text")){ 
                            if(answer.getString("text").trim().equals("")){
                                throw new RuntimeException();
                            } 
                        } 
                        if(answer.has("imgSource")){ 
                            if(answer.getString("imgSource").trim().equals("")){
                                throw new RuntimeException();
                            } 
                        } 
                        else{ 
                            if(!answer.has("text")){ 
                                throw new RuntimeException(); 
                            } 
                        } 
                        if(setAnswers.contains(answer)){ 
                            answers.remove(i--); 
                        } 
                        else{ 
                            setAnswers.add(answer); 
                        } 
                    } 
                    if(answers.length() <= 1){ 
                        throw new RuntimeException(); 
                    } 
                    proccessedQuestion.put("answers", answers);
                    JSONArray rightAnswers = question.getJSONArray("rightAnswers"); 
                    Set<Integer> setRightAnswers = new TreeSet<>(); 
                    int rightAnswer; 
                    for(int i = 0; i < rightAnswers.length; i++){ 
                        if((rightAnswer = rightAnswers.get(i)).trim().equals("")){
                            rightAnswers.remove(i--); 
                            continue;
                        }
                        if(setRightAnswers.contains(rightAnswer)){ 
                            rightAnswers.remove(i--); 
                        } 
                        else{ 
                            setRightAnswers.add(rightAnswer); 
                        } 
                    } 
                    if(rightAnswers.length() <= 1 || rightAnswers.length() > answers.length()){ 
                        throw new RuntimeException(); 
                    }
                    break; 
                case "TEXT": 
                    JSONArray rightAnswers = question.getJSONArray("rightAnswer"); 
                    Set<String> setAnswers = new TreeSet<>(); 
                    String answer; 
                    for(int i = 0; i < rightAnswers.length(); i++){ 
                        answer = rightAnswers.getString(i); 
                        if(answer.trim().equals("")){ 
                            rightAnswers.remove(i); 
                        } 
                        else{ 
                            if(setAnswers.contains(answer)){
                                rightAnswers.remove(i--); 
                            } 
                            else{ 
                                setAnswers.add(rightAnswers); 
                            } 
                        } 
                    } 
                                if(rightAnswers.length < 1){ 
                                    throw new RuntimeException(); 
                                } 
                                break;   
                            default: 
                                throw new RuntimeException(); 
                                break; 
                        } 
        } 
    }
}