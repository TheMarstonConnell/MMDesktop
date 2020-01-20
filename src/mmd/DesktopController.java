package mmd;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Timer;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

public class DesktopController {
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	float xPos = screenSize.width / 2;
	float yPos = screenSize.height / 2;
	int timesScrolled = 0;
		
	float sens = 1;

	public boolean run(ControllerManager controllers) {
		int[] move = {0,0,0,0,0,0,0};
		try {
			move = handleInput(controllers);
		} catch (AWTException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		xPos += Float.valueOf(move[0] / sens);
		yPos += Float.valueOf(move[1] / sens);

		if (xPos > screenSize.width)
			xPos = screenSize.width;
		if (xPos < 0)
			xPos = 0;

		if (yPos > screenSize.height)
			yPos = screenSize.height;
		if (yPos < 0)
			yPos = 0;

		// System.out.println(xPos + ":" + yPos);
		Point p = new Point();
		p.setLocation(xPos, yPos);
		moveMouse(p);
		if (move[2] == 1) {
			try {
				click();
			} catch (AWTException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		if (timesScrolled > 40) {
			try {
				scroll(move[3]);
			} catch (AWTException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			timesScrolled = 0;
		} else {
			timesScrolled++;
		}
		
		
		
		if(move[4] == 1) {
			moveMouse(new Point(20,(int) yPos)); 
			try {
				click();
			} catch (AWTException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		if(move[5] == 1) {
			moveMouse(new Point(screenSize.width - 20,(int) yPos)); 
			try {
				click();
			} catch (AWTException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		if(move[6] == 1) {
			return true;
		}
		
		
		return false;
	}
	
	public DesktopController(ControllerManager controllers) {


		
		
	}

	public void click() throws AWTException {
		Robot bot = new Robot();
		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

	public void scroll(int amt) throws AWTException {
		Robot bot = new Robot();
		bot.mouseWheel(amt);
	}

	public void moveMouse(Point p) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();

		// Search the devices for the one that draws the specified point.
		for (GraphicsDevice device : gs) {
			GraphicsConfiguration[] configurations = device.getConfigurations();
			for (GraphicsConfiguration config : configurations) {
				Rectangle bounds = config.getBounds();
				if (bounds.contains(p)) {
					// Set point to screen coordinates.
					Point b = bounds.getLocation();
					Point s = new Point(p.x - b.x, p.y - b.y);

					try {
						Robot r = new Robot(device);
						r.mouseMove(s.x, s.y);
					} catch (AWTException e) {
						e.printStackTrace();
					}

					return;
				}
			}
		}
		// Couldn't move to the point, it may be off screen.
		return;
	}

	private int[] handleInput(ControllerManager controllers) throws AWTException {
		ControllerState cstate = controllers.getState(0);
		
		
		// left, up, a, scroll, lbump, rbump
		int[] changes = { 0, 0, 0, 0, 0 ,0, 0 };

		if (cstate.isConnected) {

			if (cstate.leftStickY > 0.2) {
				changes[1] = -1;
			} else if (cstate.leftStickY < -0.2) {
				changes[1] = 1;

			}
			if (cstate.leftStickX > 0.2) {
				changes[0] = 1;

			} else if (cstate.leftStickX < -0.2) {
				changes[0] = -1;

			}

//			if (cstate.backJustPressed) {
//				timer.stop();
//			}

			if (cstate.aJustPressed) {
				changes[2] = 1;
			}

			if (cstate.rightStickY > 0.2) {
				changes[3] = -1;

			} else if (cstate.rightStickY < -0.2) {
				changes[3] = 1;
			}
			
			if(cstate.lbJustPressed) {
				changes[4] = 1;
			} 
			
			if(cstate.rbJustPressed) {
				changes[5] = 1;
			}
			
			if(cstate.back && cstate.start) {
				Robot r = new Robot();
				r.keyPress(KeyEvent.VK_ALT);
				r.keyPress(KeyEvent.VK_F4);

				r.keyRelease(KeyEvent.VK_ALT);
				r.keyRelease(KeyEvent.VK_F4);
				changes[6] = 1;

			}

		}

		return changes;
	}
}
