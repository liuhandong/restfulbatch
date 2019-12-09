package com.app.it.java.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

//import com.zheng.demo.DataUtil;
//import com.zheng.demo.model.LoginUser;

public class FunctionTest {
    @Test
    public void BiFunction_concat_two_String() {
        BiFunction<String, String, String> concat = (a, b) -> a + b;
        String combinedStr = concat.apply("Today is", " a wonderful day");
        assertEquals("Today is a wonderful day", combinedStr);
    }

    @Test
    public void BiFunction_multiple_two_int() {
        BiFunction<Integer, Integer, Integer> concat = (a, b) -> a * b;
        Integer product = concat.apply(3, 4);
        assertEquals(12, product.intValue());
    }

    @Test
    public void DoubleFunction_convertDoubleToString_via_lambda() {
        DoubleFunction<String> doubleToString = num -> Double.toString(num);

        assertEquals("123.456", doubleToString.apply(123.456));
    }

    @Test
    public void DoubleToIntFunction_convertDoubleToInt_via_lambda() {
        DoubleToIntFunction doubleToInt = num -> (int) num;

        assertEquals(123, doubleToInt.applyAsInt(123.456));
    }

    @Test
    public void DoubleToLongFunction_convertDoubleToLong_via_lambda() {
        DoubleToLongFunction doubleToLongFunc = num -> (long) num;

        assertEquals(123789008080l, doubleToLongFunc.applyAsLong(123789008080.456));
    }

    @Test
    public void Function_combine_TwoFunctions() {
        /*
        Function<LoginUser, String> getUser = LoginUser::getUsertName;
        Function<String, String> toUpper = String::toUpperCase;

        Function<LoginUser, String> userNameMustBeUppercase = getUser.andThen(toUpper);

        assertEquals("MARY", userNameMustBeUppercase.apply( DataUtil.buildLoginUser("Mary", "pwd123")));
        */
    }

    @Test
    public void Function_convertStringToInteger_via_methodReference() {
        Function<String, Integer> convertToWordCount = String::length;
        List<String> words = Arrays.asList("The", "That", "John", "Thanks");

        List<Integer> wordsCounts = words.stream().map(convertToWordCount).collect(Collectors.toList());

        assertEquals(3, wordsCounts.get(0).intValue());
        assertEquals(4, wordsCounts.get(1).intValue());
        assertEquals(4, wordsCounts.get(2).intValue());
        assertEquals(6, wordsCounts.get(3).intValue());
    }

    @Test
    public void IntFunction_convertIntegerToString_via_lambda() {
        IntFunction<String> intToString = num -> Integer.toString(num);

        assertEquals("123", intToString.apply(123));
    }

    @Test
    public void IntFunction_via_lambda() {
        IntFunction<Integer> powerValue = num -> num * num;

        assertEquals(9, powerValue.apply(3).intValue());
    }

    @Test
    public void IntToDoubleFunction_convertIntToDouble_via_lambda() {
        IntToDoubleFunction intToDoubleFunc = num -> (double) num;

        assertEquals(123, intToDoubleFunc.applyAsDouble(123), 0.1);
    }

    @Test
    public void IntToLongFunction_convertIntToLong_via_lambda() {
        IntToLongFunction intToLongFunc = num -> (long) num;

        assertEquals(123456, intToLongFunc.applyAsLong(123456));
    }

    @Test
    public void LongToDoubleFunction_convertLongToDouble_via_lambda() {
        LongToDoubleFunction longToDoubleFunc = num -> (double) num;

        assertEquals(123456, longToDoubleFunc.applyAsDouble(123456), 0.1);
    }

    @Test
    public void LongToIntFunction_convertLongToInt_via_lambda() {
        LongToIntFunction longToIntFun = num -> (int) num;

        assertEquals(123456, longToIntFun.applyAsInt(123456));
    }
    @Test
    public void stream_map_via_methodReference() {
        Map<String, List<String>> awards = new HashMap<>();
        awards.put("Mary", Arrays.asList("Math", "Spelling Bee"));
        awards.put("Tom", Arrays.asList("Basketball", "Spelling Bee"));
        awards.put("Allen", Arrays.asList("English", "Spelling Bee"));

        Function<String, String> convertKeyToUppercase = String::toUpperCase;

        List<String> uppercaseKeys = awards.entrySet().stream().map(e -> convertKeyToUppercase.apply(e.getKey()))
                .collect(Collectors.toList());

        assertTrue(uppercaseKeys.contains("MARY"));
        assertTrue(uppercaseKeys.contains("TOM"));
        assertTrue(uppercaseKeys.contains("ALLEN"));
    }

    @Test
    public void stream_map_with_lambda() {
        List<String> collected = Stream.of("Java", "Rocks").map(string -> string.toUpperCase())
                .collect(Collectors.toList());

        assertTrue(collected.contains("JAVA"));
        assertTrue(collected.contains("ROCKS"));
    }

    @Test
    public void ToDoubleBiFunction_power_two_int() {
        ToDoubleBiFunction<Integer, Integer> concat = (a, b) -> Math.pow(a, b);
        double powerRet = concat.applyAsDouble(5, 3);
        assertEquals(125.0, powerRet, 0.1);
    }

    @Test
    public void ToIntBiFunction_multiple_two_int() {
        ToIntBiFunction<Integer, Integer> concat = (a, b) -> a * b;
        Integer product = concat.applyAsInt(3, 4);
        assertEquals(12, product.intValue());
    }

    @Test
    public void ToLongBiFunction_power_two_int() {
        ToLongBiFunction<Integer, Integer> concat = (a, b) -> (long) Math.pow(a, b);
        Long powerRet = concat.applyAsLong(5, 3);
        assertEquals(125, powerRet.intValue());
    }
}
