package org.example;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectionRunner {
    public static void main(String[] args) throws Exception {
        Class<?> loadedClass = Class.forName("org.example.MyRunnableClass");

        Method runMethod = Arrays.stream(loadedClass.getDeclaredMethods())
                .filter(currentMethod -> currentMethod.getName().equals("run") && currentMethod.getParameterCount() == 0)
                .findFirst()
                .orElse(null);

        if (runMethod != null) {
            Object classInstance = loadedClass.getDeclaredConstructor().newInstance();
            runMethod.invoke(classInstance);
        } else {
            System.out.println("Metoda run nu exista.");
        }
    }
}