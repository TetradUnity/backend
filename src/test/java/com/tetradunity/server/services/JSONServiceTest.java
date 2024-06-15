package com.tetradunity.server.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.tetradunity.server.services.JSONService;

public class JSONServiceTest {

    @Test
    public void shouldReturnCheckAnswers(){
        double result = JSONService.checkAnswers("""
                    [{time = 1200000, passing_grade = 70},
                    {title = "question", type="MULTI_ANSWER",
                    answers = [{content = "1 option", isCorrect = true}, {content = "2 option", isCorrect = false},
                    {content = "3 option", isCorrect = true}]},
                    {title = "text_question", type = "TEXT", answers = [{content = "ro"}, {content = "n"}]}]
                """, """
                [[0, 1, 2], ["ro"]]""");

        Assertions.assertEquals(75, result);
    }

    @Test
    public void shouldReturnCheckTest(){
        try{
            String result = JSONService.checkTest("""
                    [{time = 1200000, passing_grade = 70},
                    {title = "question", type="MULTI_ANSWER",
                    answers = [{content = "1 option", isCorrect = true}, {content = "2 option", isCorrect = false},
                    {content = "3 option", isCorrect = true}]},
                    {title = "text_question", type = "TEXT", answers = [{content = "ro"}, {content = "n"}]}]
                """);
            System.out.println(result);
            Assertions.assertEquals(true, true);
        }catch(RuntimeException e){
            Assertions.assertEquals(true, false);
        }
    }

    @Test
    public void shouldReturnCheckTime(){
        int time = JSONService.getTime("""
                    [{time = 1200000, passing_grade = 70}]
                """);
        Assertions.assertEquals(1_200_000, time);

        time = JSONService.getTime("""
                    [{passing_grade = 70}]
                """);
        Assertions.assertEquals(0, time);
    }
}