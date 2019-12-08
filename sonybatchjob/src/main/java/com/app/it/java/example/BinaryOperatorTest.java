package com.app.it.java.example;

import static org.junit.Assert.assertEquals;

import java.util.function.BinaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.DoubleBinaryOperator;

import org.junit.Test;

public class BinaryOperatorTest {

    @Test
    public void BinaryOperator_add_via_lambda() {
        BinaryOperator<Integer> add = (a, b) -> a + b;

        Integer sum = add.apply(10, 12);

        assertEquals(22, sum.intValue());
    }

    @Test
    public void IntBinaryOperator_add_two_numbers() {
        IntBinaryOperator add2 = (a, b) -> a + b;

        int sum = add2.applyAsInt(10, 12);

        assertEquals(22, sum);
    }

    @Test
    public void LongBinaryOperator_mutiple_two_numbers() {
        LongBinaryOperator add2 = (a, b) -> a * b;

        long product = add2.applyAsLong(10, 12);

        assertEquals(120, product);
    }

    @Test
    public void DoubleBinaryOperator_power_two_number() {
        DoubleBinaryOperator add2 = (a, b) -> Math.pow(a, b);

        double powerRet = add2.applyAsDouble(10, 2);

        assertEquals(100, powerRet, 001);
    }

}