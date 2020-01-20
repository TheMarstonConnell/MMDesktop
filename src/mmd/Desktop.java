package mmd;

import java.awt.AWTException;
import java.io.IOException;

import com.studiohartman.jamepad.ControllerManager;

public class Desktop {

	private static ControllerManager controllers;

	public static void main(String[] args) throws AWTException {
		String folder;
		try {
			folder = args[0];

		} catch (Exception e) {
			folder = "C:\\Users\\marsa\\OneDrive\\Desktop\\men-u\\men-u";
		}

		controllers = new ControllerManager();

		DesktopMenu dm = new DesktopMenu(controllers, folder);

	}

}
