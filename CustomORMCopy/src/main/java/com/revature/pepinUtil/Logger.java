package com.revature.pepinUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

// singleton design: one instance of something

// - write logs to file using a specific format

public class Logger {

	private static Logger Logger;

	private Logger() {

	}

	public static synchronized Logger getLogger() {
		if (Logger == null) {
			Logger = new Logger();
		}
		return Logger;
	}

	public void log(String message, LoggingLevel level) {

		message = LocalDateTime.now().toString() + " --- " + message + ": " + level;
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/log.log", true))) {
			writer.write(message);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	// ----------------------------------------

	// MAIN

	public static void main(String[] args) {

		System.out.println("hello world!");
		
		Logger woo = new Logger();
		
		woo.log("Chicken Nuggets!", LoggingLevel.INFO);

	}

}
