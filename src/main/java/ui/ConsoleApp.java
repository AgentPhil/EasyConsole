package ui;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import logging.Logger;
import logging.LoggerFactory;

public abstract class ConsoleApp {
	
	protected Scanner scanner;
	
	private PrintStream out;
	
	private Map<String, ConsoleInputHandler> handlers;
	
	private Map<String, String> descriptions;
	
	protected boolean exited;
	
	private Logger logger = LoggerFactory.getInstance().getLogger(this.getClass());
	
	public ConsoleApp(Scanner scanner, PrintStream outputStream) {
		this.scanner = scanner;
		out = outputStream;
		handlers = new HashMap<>();
		descriptions = new HashMap<>();
		addCommand("exit", (a) -> exited = true);
		addCommand("help", (a) -> {
			for (String command : handlers.keySet()) {
				if (command.isEmpty()) {
					continue;
				}
				out.print(command);
				if (descriptions.containsKey(command)) {
					out.print(": " + descriptions.get(command));
				}
				out.println();
			}
		});
	}
	
	public void addCommand(String command, ConsoleInputHandler handler) {
		handlers.put(command.toUpperCase(), handler);
	}
	
	public void addCommand(String command, String description, ConsoleInputHandler handler) {
		addCommand(command, handler);
		descriptions.put(command.toUpperCase(), description);
	}	
	public void launch() {
		exited = false;
		while (!exited) {
			synchronized (out) {				
				out.print(getPrompt());				
			}
			if (!scanner.hasNextLine()) {
				continue;
			}
			String line = scanner.nextLine().trim();
			line = updateInput(line);
			int firstSpace = line.indexOf(' ');
			String command;
			String arguments;
			if (firstSpace != -1) {
				command = line.substring(0, firstSpace);
				arguments = line.substring(firstSpace + 1);							
			} else {
				command = line;
				arguments = "";
			}
			command = command.toUpperCase();
			if (handlers.containsKey(command)) {
				try {
					handlers.get(command).handle(arguments);					
				} catch (Exception e) {
					print("Error");
					logger.error("Error for command " + command, e);
				}
			} else {
				print("Unknown command");
			}
		}
	}
	
	protected String getPrompt() {
		return ">";
	}
	
	protected String updateInput(String input) {
		return input;
	}
	
	protected void print(String output) {
		out.println(output);
	}
	
	protected File chooseFile(File file) {
		return new ConsoleFileChooser(scanner, out, file).getFile();
	}
}
