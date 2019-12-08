package com.app.it.java.example;

import static org.junit.Assert.assertEquals;

import java.util.function.DoubleUnaryOperator;
import java.util.function.IntUnaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.function.UnaryOperator;

import org.junit.Test;

public class UnaryOperatorTest {

    @Test
    public void UnaryOperator_convertToUppdercase_via_lamdba() {
        UnaryOperator<String> convertToUppercase = msg -> msg.toUpperCase();

        String uppString = convertToUppercase.apply("this will be all uppercase");

        assertEquals("THIS WILL BE ALL UPPERCASE", uppString);
    }

    @Test
    public void UnaryOperator_concatString_via_methodReference() {
        UnaryOperator<String> sayHi = "Hi, "::concat;

        String concatString = sayHi.apply("Mary");

        assertEquals("Hi, Mary", concatString);
    }

    @Test
    public void IntUnaryOperator_doubleIt() {
        IntUnaryOperator doubledIt = x -> x * 2;
        assertEquals(24, doubledIt.applyAsInt(12));
    }

    @Test
    public void LongUnaryOperator_squareIt() {
        LongUnaryOperator squareIt = x -> x * x;
        assertEquals(144, squareIt.applyAsLong(12));
    }

    @Test
    public void DoubleUnaryOperator_squareIt() {
        DoubleUnaryOperator squareIt = x -> x * x;
        assertEquals(144, squareIt.applyAsDouble(12), 0.1);
    }

}
