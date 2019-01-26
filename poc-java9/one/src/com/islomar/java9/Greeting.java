package com.islomar.java9;

import java.util.logging.Logger;

import com.islomar.person.Person;

public class Greeting {
    private static final Logger LOGGER = Logger.getLogger(Greeting.class.getName());

    public static void main(String[] args) {
        System.out.println("Hello world");

        LOGGER.warning("Something happened!");

        System.out.println(new Person("Riley"));
    }
}
