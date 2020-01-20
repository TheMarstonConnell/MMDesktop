package mmd;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;

import com.studiohartman.jamepad.ControllerManager;

public class IconButton extends JButton{
	
	File cmd;
	DesktopMenu dm;
	ControllerManager controllers;
	public BufferedImage baseImg;
	
	public IconButton(File cmd, DesktopMenu dm, ControllerManager controllers, BufferedImage image) {
		this.cmd = cmd;
		this.dm = dm;
		this.controllers = controllers;
		this.baseImg = image;
	
	}
	
	@Override
	public void doClick() {
		// TODO Auto-generated method stub
		System.out.println("cmdname = " + cmd.getName());
		BrowserThread bt;
		try {
			
			if(cmd.getName().trim().equals("Netflix.bat")) {
				System.out.println("Special case for netflix");
				dm.runningNetflix = true;
			}
			
			bt = new BrowserThread(cmd, controllers, dm.dc, dm);
			bt.start();
			
			dm.runningNetflix = false;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
