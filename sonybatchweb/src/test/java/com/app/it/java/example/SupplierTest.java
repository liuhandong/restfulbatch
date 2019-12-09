package com.app.it.java.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import org.junit.Test;

public class SupplierTest {

    @Test
    public void BooleanSupplier_getAsBoolean() {
        BooleanSupplier booleanSupplier = () -> true;
        assertTrue(booleanSupplier.getAsBoolean());
    }

    @Test
    public void DoubleSupplier_getAsDouble() {
        DoubleSupplier pi = () -> Math.PI;
        assertEquals(3.14, pi.getAsDouble(), 0.01);
    }

    @Test
    public void IntSupplier_getAsInt() {
        IntSupplier maxInteger = () -> Integer.MAX_VALUE;
        assertEquals(2147483647, maxInteger.getAsInt());
    }

    @Test
    public void LongSupplier_getAsLong() {
        LongSupplier maxLongValue = () -> Long.MAX_VALUE;
        assertEquals(9223372036854775807l, maxLongValue.getAsLong());
    }

    @Test
    public void Supplier_AString() {
        Supplier<String> message = () -> "Mary is fun";
        assertEquals("Mary is fun", message.get());
    }
}
