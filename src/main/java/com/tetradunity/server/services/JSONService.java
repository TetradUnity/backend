package com.tetradunity.server.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.regex.Pattern;

public class JSONService {
    public static int getTime(String exam) {
        try {
            JSONArray questions = new JSONArray(exam);
            return questions.getJSONObject(0).getInt("time");
        } catch (JSONException ex) {
            return 0;
        }
    }

    public static int getPassing_grade(String exam) {
        try {
            JSONArray questions = new JSONArray(exam);
            return questions.getJSONObject(0).getInt("passing_grade");
        } catch (JSONException ex) {
            return 0;
        }
    }

    public static boolean isViewing_correct_answers(String exam) {
        try {
            JSONArray questions = new JSONArray(exam);
            return questions.getJSONObject(0).getBoolean("viewing_correct_answers");
        } catch (JSONException ex) {
            return false;
        }
    }

    public static int getCount_attempts(String exam) {
        try {
            JSONArray questions = new JSONArray(exam);
            return questions.getJSONObject(0).getInt("count_attempts");
        } catch (JSONException ex) {
            return 1;
        }
    }

    public static int getAmountQuestion(String test){
        try{
            return new JSONArray(test).length() - 1;
        }catch(RuntimeException e){return 0;}
    }

    public static String getQuestions(String testStr, boolean withGeneralInfo) throws RuntimeException {
        if (testStr == null) {
            return null;
        }
        JSONArray test = new JSONArray(testStr);
        JSONArray questions = new JSONArray();
        if (withGeneralInfo) {
            questions.put(test.getJSONObject(0));
        }
        JSONObject question;
        JSONObject temp;
        JSONArray answers;
        JSONArray answersProcessed;
        String type;

        for (int i = 1; i < test.length(); i++) {
            temp = test.getJSONObject(i);
            type = temp.getString("type");
            question = new JSONObject();
            question.put("type", type);
            question.put("title", temp.getString("title"));
            switch (type) {
                case "MULTI_ANSWER":
                case "ONE_ANSWER":
                    answers = temp.getJSONArray("answers");
                    answersProcessed = new JSONArray();
                    for (int j = 0; j < answers.length(); j++) {
                        answersProcessed.put(answers.getJSONObject(j).getString("content"));
                    }
                    question.put("answers", answersProcessed);
                case "TEXT":
                    break;
                default:
                    throw new RuntimeException();

            }
            questions.put(question);
        }

        return questions.toString();
    }

    public static String getQuestionsWithYourAnswers(String testStr, String answersStr) {
        try{
            JSONArray questions = new JSONArray(getQuestions(testStr, false));
            JSONArray answers = new JSONArray(answersStr);
        JSONArray result = new JSONArray();
        JSONObject question;
        JSONArray answer;

        int length = questions.length();

        if (length != answers.length()) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < length; i++) {
            question = questions.getJSONObject(i);
            answer = answers.getJSONArray(i);
            question.put("your_answer", answer);
            result.put(question);
        }
        return result.toString();

        } catch(RuntimeException e){return null;}
    }

    public static String getQuestionsWithYourAnswersRight(String testStr, String answersStr) {
        try {
            JSONArray questions = new JSONArray(getQuestionsWithRightAnswers(testStr));
            JSONArray answers = new JSONArray(answersStr);
            JSONArray result = new JSONArray();
            JSONObject question;
            JSONArray answer;

            int length = questions.length();

            if (length != answers.length()) {
                throw new IllegalArgumentException();
            }

            for (int i = 0; i < length; i++) {
                question = questions.getJSONObject(i);
                answer = answers.getJSONArray(i);
                question.put("your_answer", answer);
                result.put(question);
            }
            return result.toString();
        }catch(RuntimeException e){return null;}
    }

    public static double checkAnswers(String examStr, String answersStr) throws RuntimeException {
        if (answersStr == null || examStr == null) {
            throw new RuntimeException();
        }

        JSONArray exam = new JSONArray(examStr);
        JSONArray answers = new JSONArray(answersStr);

        double result = 0;

        if (exam.length() != answers.length() + 1) {
            throw new RuntimeException();
        }

        JSONArray answer;
        JSONObject generalQuestion;
        String type;

        List<Integer> rightAnswers;

        Set<Integer> setRightAnswers;

        int selectedRightAnswers;
        int selectedIncorrectAnswers;
        int amountAnswers;
        double amountRightAnswers;

        int index;

        for (int i = 0; i < answers.length(); i++) {
            answer = answers.getJSONArray(i);
            generalQuestion = exam.getJSONObject(i + 1);
            type = generalQuestion.getString("type");

            amountAnswers = answer.length();

            if(amountAnswers == 0){
                continue;
            }

            switch (type) {
                case "ONE_ANSWER":
                    if (amountAnswers > 1) {
                        throw new RuntimeException();
                    }
                case "MULTI_ANSWER":
                    rightAnswers = getRightAnswers(generalQuestion.getJSONArray("answers"));
                    amountRightAnswers = rightAnswers.size();
                    setRightAnswers = new TreeSet<>();
                    selectedRightAnswers = 0;
                    selectedIncorrectAnswers = 0;
                    for (int j = 0; j < answer.length(); j++) {
                        if (setRightAnswers.contains(index = answer.getInt(j))) {
                            continue;
                        }
                        setRightAnswers.add(index);
                        if (rightAnswers.contains(index)) {
                            selectedRightAnswers++;
                        } else {
                            selectedIncorrectAnswers++;
                        }
                    }
                    result += Math.max(0, (selectedRightAnswers - selectedIncorrectAnswers) / amountRightAnswers);
                    break;
                case "TEXT":
                    if (consistValue(answer.getString(0), generalQuestion.getJSONArray("answers"))) {
                        result++;
                    }
                    break;
            }
        }
        return 100 * result / answers.length();
    }

    private static boolean consistValue(String value, JSONArray list) {
        int length = list.length();
        for (int i = 0; i < length; i++) {
            if (list.getJSONObject(i).getString("content").equals(value)) {
                return true;
            }
        }
        return false;
    }

    private static List<Integer> getRightAnswers(JSONArray answers) throws RuntimeException {
        JSONObject answer;
        List<Integer> rightAnswers = new ArrayList<>();

        for (int i = 0; i < answers.length(); i++) {
            answer = answers.getJSONObject(i);
            if (answer.getBoolean("isCorrect")) {
                rightAnswers.add(i);
            }
        }

        return rightAnswers;
    }

    private static final Pattern patternFile = Pattern.compile("^[a-zA-Z0-9-]{1,40}\\.[a-zA-Z0-9]{3,}$");

    public static boolean checkFiles(String json) {
        try {
            JSONArray files = new JSONArray(json);
            for (int i = 0; i < files.length(); i++) {
                System.out.println("Checking file: " + files.getString(i));
                if (!patternFile.matcher(files.getString(i)).matches()) {
                    System.out.println(1);
                    return false;
                }
                System.out.println(2);
            }
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    public static List<String> getFiles(String json) {
        try {
            List<String> files = new ArrayList<>();
            JSONArray JSONFiles = new JSONArray(json);
            int length = JSONFiles.length();
            String file;
            for (int i = 0; i < length; i++) {
                if (!patternFile.matcher(file = JSONFiles.getString(i)).matches()) {
                    files.add(file);
                }
            }
            return files;
        } catch (RuntimeException ex) {
            return new ArrayList<>();
        }
    }

    public static String getQuestionsWithRightAnswers(String testStr) {
        try {
            if (testStr == null) {
                return null;
            }
            JSONArray test = new JSONArray(testStr);
            JSONArray questions = new JSONArray();
            JSONObject question;
            JSONObject temp;
            String type;

            for (int i = 1; i < test.length(); i++) {
                temp = test.getJSONObject(i);
                type = temp.getString("type");
                question = new JSONObject();
                question.put("type", type);
                question.put("title", temp.getString("title"));
                switch (type) {
                    case "MULTI_ANSWER":
                    case "ONE_ANSWER":
                    case "TEXT":
                        question.put("answers", temp.getJSONArray("answers"));
                        break;
                    default:
                        throw new RuntimeException();

                }
                questions.put(question);
            }

            return questions.toString();
        }
        catch (RuntimeException e){
            return null;
        }
    }

    public static String checkTest(String test) throws RuntimeException {
        if (test == null) {
            throw new RuntimeException();
        }
        JSONArray questions = new JSONArray(test);
        if(questions.length() < 2){
            throw new RuntimeException();
        }
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
        boolean temp;
        JSONObject GeneralInfo = questions.getJSONObject(0);
        int val;
        for (Iterator<String> it = GeneralInfo.keys(); it.hasNext(); ) {
            String key = it.next();
            switch (key) {
                case "time":
                    val = GeneralInfo.getInt(key);
                    if (val <= 0 || val >= 18_000_000) {
                        GeneralInfo.put(key, -1);
                    }
                    break;
                case "passing_grade":
                    val = GeneralInfo.getInt(key);
                    if (val < 0 || val > 100) {
                        throw new RuntimeException();
                    }
                    break;
                case "viewing_correct_answers":
                    GeneralInfo.getBoolean(key);
                    break;
                case "count_attempts":
                    val = GeneralInfo.getInt(key);
                    if (val < 0 || val > 3) {
                        throw new RuntimeException();
                    }
                    break;
                default:
                    GeneralInfo.remove(key);
            }
        }
        proccessedQuestions.put(GeneralInfo);
        for (int q = 1; q < questions.length(); q++) {
            question = questions.getJSONObject(q);
            title = question.getString("title");
            if (title.trim().equals("")) {
                throw new RuntimeException();
            }
            answers = question.getJSONArray("answers");
            (proccessedQuestion = new JSONObject()).put("title", title);
            proccessedQuestion.put("type", type = question.getString("type"));
            switch (type) {
                case "ONE_ANSWER":
                    existsCorrect = false;
                    set = new TreeSet<>();
                    if(answers.length() < 2){
                        throw new RuntimeException();
                    }
                    for (int i = 0; i < answers.length(); i++) {
                        answer = answers.getJSONObject(i);
                        if ((text = answer.getString("content")).trim().isEmpty()) {
                            throw new RuntimeException();
                        }
                        if (set.contains(text)) {
                            answers.remove(i--);
                        } else {
                            set.add(text);
                        }
                        if ((temp = answer.getBoolean("isCorrect")) & existsCorrect) {
                            throw new RuntimeException();
                        }
                        existsCorrect |= temp;
                    }
                    break;
                case "MULTI_ANSWER":
                    set = new TreeSet<>();
                    if(answers.length() < 2){
                        throw new RuntimeException();
                    }
                    for (int i = 0; i < answers.length(); i++) {
                        answer = answers.getJSONObject(i);
                        if ((text = answer.getString("content")).trim().isEmpty()) {
                            throw new RuntimeException();
                        }
                        if (set.contains(text)) {
                            answers.remove(i--);
                        } else {
                            set.add(text);
                        }
                        answer.getBoolean("isCorrect");
                    }
                    break;
                case "TEXT":
                    set = new TreeSet<>();
                    if(answers.length() == 0){
                        throw new RuntimeException();
                    }
                    for (int i = 0; i < answers.length(); i++) {
                        answer = answers.getJSONObject(i);
                        if ((text = answer.getString("content")).trim().equals("")) {
                            throw new RuntimeException();
                        }
                        if (set.contains(text)) {
                            answers.remove(i--);
                        } else {
                            set.add(text);
                        }
                        for (Iterator<String> it = GeneralInfo.keys(); it.hasNext(); ) {
                            String key = it.next();

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
