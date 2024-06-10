package com.tetradunity.server.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JSONServiceTest {

    @Test
    public void shouldReturnCheckAnswers1(){
        double result = JSONService.checkAnswers("""
                    [{time = 1200000, passing_grade = 70},
                    {title = "lala", type="ONE_ANSWER",
                    answers = [{content = "hi", isCorrect = true}, {content = "hello", isCorrect = false}]}]
                """, "[[1]]");

        Assertions.assertEquals(result, 100);
    }
}