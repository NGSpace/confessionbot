package io.github.ngspace.confessionbot;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JOptionPane;

import net.dv8tion.jda.api.exceptions.InvalidTokenException;

public class Main {
	
	public static void main(String[] args) {
		File tokenfile = new File("token.txt");
		try {
			String TOKEN = Files.readString(tokenfile.toPath());
			
			new ConfessionBot(TOKEN);
		} catch (InvalidTokenException | IOException e) {
			e.printStackTrace();
			if (!tokenfile.exists()) {
				try {
					if (!tokenfile.createNewFile()) {
						System.err.println("Failed to create token.txt!");
						if (!GraphicsEnvironment.isHeadless()) {
							JOptionPane.showMessageDialog(null, "Failed to create token.txt!");
						}
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (!GraphicsEnvironment.isHeadless()) {
				JOptionPane.showMessageDialog(null, "Invalid token in token.txt!");
			}
		}
	}
}
