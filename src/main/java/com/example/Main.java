package com.example;

import com.example.service.CalculatorService;

public class Main {
    public static void main(String[] args) {
        System.out.println("Demo - Aspects\n");

        CalculatorService calc = new CalculatorService();

        // aspect will log it automatically
        int sum = calc.sum(5, 3);
        System.out.println("Result: " + sum);

        int mult = calc.multiply(4, 7);
        System.out.println("Result: " + mult);

        int divi = calc.divide(4, 2);
        System.out.println("result: " + divi);

        try {
            calc.divide(2,0);
        } catch (Exception err) {
            System.out.println("Exception: " + err.getMessage());
        }
    }
}
