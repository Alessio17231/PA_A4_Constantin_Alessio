package org.example.homework;

public class ExampleClass {

    @MyAnnotation
    public void sayHello() {
        System.out.println("Salut");
    }

    @MyAnnotation
    public void printNumber(int number) {
        System.out.println("Numarul este: " + number);
    }

    public void normalMethod() {
        System.out.println("Metroda normala.");
    }
}