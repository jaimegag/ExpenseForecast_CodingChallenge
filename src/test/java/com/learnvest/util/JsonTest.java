package com.learnvest.util;

import org.junit.*;
import static org.junit.Assert.*;

import com.google.gson.reflect.TypeToken;

public class JsonTest {

    public static class Foo {
        String bar;
    }

    @Test
    public void fromJson() {
        assertTrue(Json.fromJson("{}").isJsonObject());
        assertTrue(Json.fromJson("[]").isJsonArray());

        Foo foo = Json.fromJson("{\"bar\":\"baz\"}", new TypeToken<Foo> () {});
        assertEquals("baz", foo.bar);
    }

}
