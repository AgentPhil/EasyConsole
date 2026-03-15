package ui;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class ConsoleFileChooser extends ConsoleApp {

	private File file;
	
	private boolean fileChosen = false;
	
	public ConsoleFileChooser(Scanner scanner, PrintStream outputStream, File file) {
		super(scanner, outputStream);
		try {
			this.file = file.getCanonicalFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		addCommand("cd", (a) -> {
			File newFile = new File(this.file, a);
			if (newFile.exists()) {
				try {
					this.file = newFile.getCanonicalFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				print("File not found");
			}
		});
		addCommand("ls", (a) -> {
			for (String fileName : this.file.list()) {
				print(fileName);
			}
		});
		addCommand("", "Choose current file", (a) -> {
			exited = true;
			fileChosen = true;
		});
		
	}
	
	public File getFile() {
		launch();
		if (fileChosen) {
			return file;
		} else {
			return null;
		}
	}
	
	@Override
	protected String getPrompt() {
		try {
			return file.getCanonicalPath() + ">";
		} catch (IOException e) {
			return e.getMessage() + ">";
		}
	}
}
