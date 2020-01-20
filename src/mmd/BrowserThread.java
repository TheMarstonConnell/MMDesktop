package mmd;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.FloatControl;

import com.studiohartman.jamepad.ControllerManager;

public class BrowserThread extends Thread {

	private File cmd;
	DesktopController dc;
	ControllerManager controllers;
	DesktopMenu dm;

	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		super.start();
		try {
			
//			dm.fadeMusicOut();
			runCmd(this.cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public BrowserThread(File cmd, ControllerManager controllers, DesktopController dc, DesktopMenu dm)
			throws IOException {
		this.cmd = cmd;
		this.controllers = controllers;
		this.dc = dc;
		this.dm = dm;
	}

	private void runCmd(File cmd) throws IOException {
		dm.musicPlaying = false;
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", cmd.getAbsolutePath());
		builder.redirectErrorStream(true);
		Process p = builder.start();
		while (p.isAlive()) {
			FloatControl volume = (FloatControl) dm.music.getControl(FloatControl.Type.MASTER_GAIN);
			
	        volume.setValue(-80f);
		}
	}

	public void run() {
		while (cmd.getName().equals("Netflix.bat")) {
			// System.out.println("Currently in netflix");
			if (dc.run(controllers)) {
				break;
			}
		}
	}

}
