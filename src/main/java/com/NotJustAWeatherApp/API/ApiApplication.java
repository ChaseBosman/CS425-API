package com.NotJustAWeatherApp.API;

import com.NotJustAWeatherApp.API.RouteController.URLConnectionReaderMultiple;
import com.NotJustAWeatherApp.API.SingleLocationController.URLConnectionReaderSingle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Scanner;


@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ApiApplication.class, args);

		Scanner myObj = new Scanner(System.in);
		String choice;
		System.out.println("Enter 'single' or 'multiple' to view routing. Enter anything else to exit");
		choice = myObj.nextLine();

		while(choice.equals("single") || choice.equals("multiple")) {
			if (choice.equals("single")) {
				URLConnectionReaderSingle.main(null);
			} else {
				URLConnectionReaderMultiple.main(null);
			}
			System.out.println("Enter 'single' or 'multiple' to view routing. Enter anything else to exit");
			choice = myObj.nextLine();
		}
		System.exit(0);
	}

}
