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

    public static int getCount_attempt(String exam) {
        try {
            JSONArray questions = new JSONArray(exam);
            return questions.getJSONObject(0).getInt("count_attempt");
        } catch (JSONException ex) {
            return 1;
        }
    }

    public static String getQuestions(String testStr) throws RuntimeException {
        if (testStr == null) {
            return null;
        }
        JSONArray test = new JSONArray(testStr);
        JSONArray questions = new JSONArray();
        questions.put(test.getJSONObject(0));
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
                case "MULTY_ANSWER":
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

    public static String getQuestionsWithYourAnswers(String testStr, String answersStr) throws RuntimeException {
        JSONArray questions = new JSONArray(getQuestions(testStr));
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
    }

    public static String getQuestionsWithYourAnswersRight(String testStr, String answersStr) throws RuntimeException {
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
        int amountRightAnswers;

        int index;

        for (int i = 0; i < answers.length(); i++) {
            answer = answers.getJSONArray(i);
            generalQuestion = exam.getJSONObject(i + 1);
            type = generalQuestion.getString("type");

            switch (type) {
                case "ONE_ANSWER":
                    if (answer.length() > 1) {
                        throw new RuntimeException();
                    }
                case "MULTY_ANSWER":
                    rightAnswers = getRightAnswers(generalQuestion.getJSONArray("answer"));
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
                    if (consistValue(answer.getString(0), generalQuestion.getJSONArray("answer"))) {
                        result++;
                    }
                    break;
            }
        }
        return 100 * result / exam.length();
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

    private static final Pattern patternFile = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}.[a-b0-9]{3,}$");

    public static boolean checkFiles(String json) {
        try {
            JSONArray files = new JSONArray(json);
            int length = files.length();
            for (int i = 0; i < length; i++) {
                if (!patternFile.matcher(files.getString(i)).matches()) {
                    return false;
                }
            }
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    public static String getQuestionsWithRightAnswers(String testStr) {
        if (testStr == null) {
            return null;
        }
        JSONArray test = new JSONArray(testStr);
        JSONArray questions = new JSONArray();
        questions.put(test.getJSONObject(0));
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
                case "MULTY_ANSWER":
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

    public static String checkTest(String test) throws RuntimeException {
        if (test == null) {
            throw new RuntimeException();
        }
        JSONArray questions = new JSONArray(test);
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
                    if (val <= 0) {
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
                case "count_attempt":
                    val = GeneralInfo.getInt(key);
                    if (val < 0 || val > 3) {
                        throw new RuntimeException();
                    }
                    break;
                default:
                    GeneralInfo.remove(key);
            }
        }
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
                        System.out.println("mihau");
                    }
                    break;
                case "MULTY_ANSWER":
                    set = new TreeSet<>();
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
