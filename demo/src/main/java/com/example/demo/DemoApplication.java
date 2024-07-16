package com.example.demo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

@SpringBootApplication
public class DemoApplication {
	private final Calculator calculator;

	public DemoApplication(@Qualifier("calculator") Calculator calculator) {
		this.calculator = calculator;
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public boolean outToConsole() throws Exception {
		Scanner in = new Scanner(System.in);

		System.out.println("Введите число a");
		double a = in.nextDouble();
		System.out.println("Введите число b");
		double b = in.nextDouble();
		System.out.println("Введите тип операции: " + calculator.getSupportedOperations());
		String operationType = in.next();

		calculator.calc(operationType, a, b);
		in.close();
		return true;
	}

}
