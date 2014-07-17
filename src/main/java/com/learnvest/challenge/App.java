package com.learnvest.challenge;

import com.learnvest.util.Json;

public class App {

    public static void main(String...args) {
        System.out.println( Json.toJson( Json.loadFromFile(args[0]) ) );
    }

}

