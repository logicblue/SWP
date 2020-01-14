package com.rkoliver.net.swp.test;

import org.testng.Assert;

import static org.testng.Assert.fail;

public class SWPTestUtils {


    public static void assertEquals(byte[] actual, int actualOffset, byte[] expected, int expectedOffset, int expectedLength) {

        if (expected == null && actual == null) {

            return;
        }

        if (expected == null && actual != null) {

            fail("Expected as null but actual was not.");
        }

        if (expected != null && actual == null) {

            fail("Expected was not null but actual was.");
        }

        for (int i = 0; i < expectedLength; i++) {

            Assert.assertEquals(actual[actualOffset + i], expected[expectedOffset + i]);
        }
    }


    public static void println(byte[] value) {

        if (value == null) {

            println("<null>");
        }

        print("byte[");
        print(value.length);
        print("] { ");
        for (int i = 0; i < value.length; i++) {

            if (i > 0) {

                print(", ");
            }

            print(value[i]);
        }
        println(" }");
    }


    private static void print(int value) {

        print(Integer.toString(value));
    }


    private static void print(byte value) {

        print(Byte.toString(value));
    }


    private static void print(String value) {

        System.out.print(value);
    }


    static void println(String value) {

        print(value);
        print("\n");
    }
}
