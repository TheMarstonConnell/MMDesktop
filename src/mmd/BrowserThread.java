package mmd;

import java.io.File;
import java.io.IOException;

import com.studiohartman.jamepad.ControllerManager;

public class BrowserThread extends Thread {

	private File cmd;
	DesktopController dc;
	ControllerManager controllers;
	
	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		super.start();
		try {
			runCmd(this.cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public BrowserThread(File cmd, ControllerManager controllers, DesktopController dc) throws IOException {
		this.cmd = cmd;
		this.controllers = controllers;
		this.dc = dc;
	}
	
	private void runCmd(File cmd) throws IOException {
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", cmd.getAbsolutePath());
		builder.redirectErrorStream(true);
		Process p = builder.start();
	}
	
    public void run(){
    	
    	while (cmd.getName().equals("Netflix.bat")) {
//			System.out.println("Currently in netflix");
			if(dc.run(controllers)) {
				break;
			}
		}
    }
  }
