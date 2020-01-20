package mmd;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

public class DesktopMenu extends JFrame {
	JPanel content;
	JScrollPane scroll;

	Timer timer;
	JLabel time;
	JLabel date;

	Image staticImg;

	Random rand = new Random();

	HashMap<Point, String> icons;

	Point selectedBox = new Point(0, 0);

	int currentPage = 0;
	int currentVal = 0;

	int rows = 4;
	int pages = 4;
	int columns = 4;

	final Color wiiColor = new Color(52, 190, 237);
	
	int lastStickState = 0;
	int moveTick = 0;

	double body = 0.85;

	Date currentTime;

	boolean runningNetflix = false;

	DesktopController dc;

	// init var
	File goodFile = null;

	Font clockFont;

	HashMap<Point, JComponent> buttons;

	public HashMap<String, HashMap<File, String>> loadFolder(String location) {
		HashMap<String, HashMap<File, String>> folder = new HashMap<String, HashMap<File, String>>();

		File f = new File(location);
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));

		for (File file : files) {
			if (!(file.getName().contains(".jpeg"))) {

				String fName = file.getAbsolutePath().substring(0, file.getAbsolutePath().indexOf(".")) + ".jpeg";

				HashMap<File, String> icon = new HashMap<File, String>();
				System.out.println(fName);
				icon.put(file, fName);

				folder.put(file.getName().substring(0, file.getName().indexOf(".")), icon);

			}
		}

		return folder;
	}

	private BufferedImage createStatic(Dimension screenSize, int tick) {
		// Creating Static
		int width = screenSize.width / columns - 40;
		int height = (int) (screenSize.height * body) / rows - 40;
		BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED);

		Graphics2D g2d = bimage.createGraphics();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int noise = (int) (rand.nextInt(255 / 2)) + 255 / 2;

				if ((tick + y) % 20 == 0 || (tick + y + 1) % 20 == 0 || (tick + y + 2) % 20 == 0) {
					noise -= 20;
				}

				g2d.setColor(new Color(noise, noise, noise));
				g2d.fillRect(x, y, 1, 1);

			}
		}

		g2d.dispose();
		return bimage;
	}

	private static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	private BufferedImage addTv(BufferedImage bimg, Dimension screenSize, int tick) {
		if (bimg != null) {
			BufferedImage bimage;

			// Creating Static
			int width = screenSize.width / columns - 40;
			int height = (int) (screenSize.height * body) / rows - 40;
			bimage = deepCopy(bimg);

			Graphics2D g2d = (Graphics2D) bimage.getGraphics();

			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if ((tick + y) % 20 == 0 || (tick + y + 1) % 20 == 0 || (tick + y + 2) % 20 == 0) {
						int noise = 20;
						g2d.setColor(new Color(noise, noise, noise, 20));
						g2d.fillRect(x, y, 1, 1);
					}

				}
			}

			g2d.dispose();
			return bimage;
		}
		return bimg;

	}

	private BufferedImage scaleImg(BufferedImage original, int newWidth, int newHeight) {
		BufferedImage resized = new BufferedImage(newWidth, newHeight, original.getType());
		Graphics2D g = resized.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(original, 0, 0, newWidth, newHeight, 0, 0, original.getWidth(), original.getHeight(), null);
		g.dispose();
		return resized;
	}

	private void createIcons(String folder, ControllerManager controllers, Dimension screenSize) {

		content.removeAll();

		HashMap<String, HashMap<File, String>> map = loadFolder(folder);
		Set<String> names = map.keySet();
		int page = 0;
		int x1 = 0;
		int y1 = 0;
		for (String s : names) {

			if (x1 >= columns) {
				x1 = 0;
				y1++;
			}
			if (y1 >= rows) {
				y1 = 0;
				page++;
			}
			System.out.println("Creating " + s + " at " + x1 + "," + y1);
			icons.put(new Point(page * columns + x1, y1), s);
			x1++;
		}

		buttons = new HashMap<Point, JComponent>();

		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < pages * columns; x++) {
				Point p = new Point(x, y);
				String s = icons.get(p);
				if (s != null) {
					System.out.println("Creating new button: " + s);

					// Image loading
					BufferedImage img = null;
					HashMap<File, String> imgs = map.get(s);
					Set<File> fs = imgs.keySet();
					for (File f : fs) {
						if (f.getName().indexOf(s) != -1) {
							goodFile = f;

							String s2 = f.getAbsolutePath().substring(0, f.getAbsolutePath().indexOf(".")) + ".jpeg";
							try {
								img = ImageIO.read(new File(s2));
							} catch (Exception e1) {
								System.out.println("There's no image associated with " + s + ", consider adding one!");
							}
						}
					}
					BufferedImage img2 = null;
					if (img != null) {
						img2 = scaleImg(img, screenSize.width / columns - 40,
								(int) (screenSize.height * body) / rows - 40);
					}
					IconButton b = new IconButton(goodFile, this, controllers, img2);

					try {
						b.setIcon(new ImageIcon(makeRoundedCorner(b.baseImg, 20)));

					} catch (Exception e) {
						b.setText(s);
					}
					// imgloading done

					System.out.println(goodFile.getName());

					b.setFont(clockFont.deriveFont(36f));
					b.setOpaque(false);

					buttons.put(p, b);
					content.add(b);
				} else {
					JButton holder = new JButton();
					Image img = null;
					

//					holder.setIcon(new ImageIcon(img.getScaledInstance(screenSize.width / columns - 40,
//							(int) (screenSize.height * body) / rows - 40, java.awt.Image.SCALE_SMOOTH)));
					holder.setOpaque(false);
					buttons.put(p, holder);
					content.add(holder);
				}
			}
		}
	}
	
	public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
	    int w = image.getWidth();
	    int h = image.getHeight();
	    BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2 = output.createGraphics();

	    // This is what we want, but it only does hard-clipping, i.e. aliasing
	    // g2.setClip(new RoundRectangle2D ...)

	    // so instead fake soft-clipping by first drawing the desired clip shape
	    // in fully opaque white with antialiasing enabled...
	    g2.setComposite(AlphaComposite.Src);
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g2.setColor(Color.WHITE);
	    g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

	    // ... then compositing the image on top,
	    // using the white shape from above as alpha source
	    g2.setComposite(AlphaComposite.SrcAtop);
	    g2.drawImage(image, 0, 0, null);

	    g2.dispose();

	    return output;
	}

	public void addNewIcon(String folder, ControllerManager controllers, Dimension screenSize) {

		FileDialog jfc = new FileDialog(this, "Choose a program", FileDialog.LOAD);
		jfc.setDirectory(folder);
		jfc.setFile("*.exe");
		jfc.setVisible(true);
		String filename = jfc.getFile();
		if (filename == null)
			System.out.println("You cancelled the choice");
		else
			try {
				Files.write(Paths.get(folder + "\\" + filename.substring(0, filename.indexOf(".")) + ".bat"),
						(jfc.getDirectory() + "\\" + filename).getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}

		createIcons(folder, controllers, screenSize);
	}

	public DesktopMenu(ControllerManager controllers, String folder) {
		super();
		// String folder = "C:\\Users\\marsa\\OneDrive\\Desktop\\wetop";

		try {
			// create the font to use. Specify the size!
			URL font = this.getClass().getResource("stm.ttf");
			clockFont = Font.createFont(Font.TRUETYPE_FONT, font.openStream()).deriveFont(72f);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			// register the font
			ge.registerFont(clockFont);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FontFormatException e) {
			e.printStackTrace();
		}

		currentTime = new Date();

		dc = new DesktopController(controllers);

		controllers.initSDLGamepad();

		icons = new HashMap<Point, String>();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		this.setSize(screenSize.width, screenSize.height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocation(0, 0);
		this.setUndecorated(true);

		this.setLayout(new BorderLayout());

		scroll = new JScrollPane();

		GridLayout grid = new GridLayout(0, pages * columns);

		content = new JPanel(grid);
		content.setPreferredSize(new Dimension(screenSize.width * 4, (int) (screenSize.height * body)));

		createIcons(folder, controllers, screenSize);

		scroll.setBounds(0, 0, screenSize.width, screenSize.height);
		scroll.setViewportView(content);
		scroll.createHorizontalScrollBar();
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		JPanel clockPane = new JPanel();
		clockPane.setPreferredSize(
				new Dimension((int) screenSize.getWidth(), (int) (screenSize.getHeight() * (1 - body))));

		scroll.setPreferredSize(new Dimension((int) screenSize.getWidth(), (int) (screenSize.getHeight() * body)));


		time = new JLabel("00:00");
		time.setAlignmentX(Component.CENTER_ALIGNMENT);
		date = new JLabel("Sun, January 19th");
		date.setAlignmentX(Component.CENTER_ALIGNMENT);
		time.setFont(clockFont.deriveFont(72f));
		date.setFont(clockFont.deriveFont(42f));

		clockPane.setLayout(new BoxLayout(clockPane, BoxLayout.Y_AXIS));

		clockPane.add(time);
		clockPane.add(date);

		
		
		
		scroll.setBorder(new EmptyBorder(0,0,0,0));
		
		clockPane.setBorder(new MatteBorder(0, 0 ,6 ,0, wiiColor));
		
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		this.add(scroll, BorderLayout.CENTER);
		this.add(clockPane, BorderLayout.PAGE_END);

		this.setVisible(true);

		
		
		timer = new Timer(1, new ActionListener() {

			private int staticTick = 0;

			@Override
			public void actionPerformed(ActionEvent e) {

				
				
				
				currentTime = new Date();

				SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

				time.setText(sdf.format(currentTime));

				sdf = new SimpleDateFormat("EEE d'/'M");
				date.setText(sdf.format(currentTime));

				if (isActive()) {

					int[] move = { 0, 0, 0, 0, 0, 0 };

					try {
						move = handleInput(controllers);
					} catch (AWTException e1) {
						e1.printStackTrace();
					}

					if (move[6] == 1) {
						addNewIcon(folder, controllers, screenSize);
					}

					if (staticTick % 2 == 0) {
						staticImg = makeRoundedCorner(createStatic(screenSize, staticTick), 20);

					}
					staticTick += 1;

					Set<Point> bs = buttons.keySet();
					for (Point p : bs) {
						if (p.x < (currentPage + 1) * columns) {

							JComponent jc = buttons.get(p);
							jc.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(20, 20, 20, 20),
									new RoundedBorder(20, 3, Color.gray)));
							if (jc instanceof JButton) {
								JButton b = (JButton) jc;
								b.setContentAreaFilled(false);
								b.setFocusable(false);
								b.setOpaque(false);
								b.setBackground(new Color(0,0,0,0));

								if (!(jc instanceof IconButton)) {
									b.setIcon(new ImageIcon(staticImg));
								} else {
									IconButton ib = (IconButton) b;
									BufferedImage bimg = ib.baseImg;
									

								}

							}
						}
					}
					

					if (selectedBox.x >= pages * columns) {
						selectedBox.x = pages * columns - 1;

					} else if (selectedBox.x < 0) {
						selectedBox.x = 0;
					}

					if (selectedBox.y < 0) {
						selectedBox.y = 0;

					} else if (selectedBox.y >= rows) {
						selectedBox.y = rows - 1;
					}

					JComponent b = buttons.get(selectedBox);

					if (b != null) {

						b.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(20, 20, 20, 20),
								new RoundedBorder(20, 4, wiiColor)));

					}

					currentPage = selectedBox.x / columns;

					moveTick++;
					if (moveTick > 1) {
						moveTick = 0;
					}
					if (move[0] == 1 && moveTick == 0) {
						selectedBox.x += 1;
						lastStickState = 1;
					}
					if (move[0] == -1 && moveTick == 0) {
						selectedBox.x -= 1;
						lastStickState = 2;
					}
					if (move[1] == -1 && moveTick == 0) {
						selectedBox.y -= 1;
						lastStickState = 3;
					}
					if (move[1] == 1 && moveTick == 0) {
						selectedBox.y += 1;
						lastStickState = 4;
					}

					if (move[5] == 1) {
						selectedBox.x += 4;
						currentPage += 1;
					}

					if (move[4] == 1) {
						selectedBox.x -= 4;
						currentPage -= 1;
					}

					if (currentPage > pages) {
						currentPage = pages;
					}
					if (currentPage < 0) {
						currentPage = 0;
					}

					if (currentVal > currentPage * screenSize.width) {
						currentVal -= screenSize.width / 20;
					} else if (currentVal < currentPage * screenSize.width) {
						currentVal += screenSize.width / 20;
					}

					scroll.getHorizontalScrollBar().setValue(currentVal);

					if (move[2] == 1) {
						if (buttons.get(selectedBox) instanceof JButton) {
							JButton butt = (JButton) buttons.get(selectedBox);
							butt.doClick();
						}
					}
				} else {

				}

			}

		});

		timer.start();

	}

	private int[] handleInput(ControllerManager controllers) throws AWTException {
		ControllerState cstate = controllers.getState(0);

		// left, up, a, scroll, lbump, rbump
		int[] changes = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };

		if (cstate.isConnected) {

			if (cstate.dpadLeftJustPressed) {
				selectedBox.x--;
			} else if (cstate.dpadRightJustPressed) {
				selectedBox.x++;
			}

			if (cstate.dpadUpJustPressed) {
				selectedBox.y--;
			} else if (cstate.dpadDownJustPressed) {
				selectedBox.y++;
			}

			if (selectedBox.x > pages * columns) {
				selectedBox.x = pages * columns;
			}
			if (selectedBox.x < 0) {
				selectedBox.x = 0;
			}

			float thresh = 0.4f;

			if (cstate.leftStickY > thresh) {
				changes[1] = -1;
			} else if (cstate.leftStickY < -thresh) {
				changes[1] = 1;

			}
			if (cstate.leftStickX > thresh) {
				changes[0] = 1;

			} else if (cstate.leftStickX < -thresh) {
				changes[0] = -1;

			}

			if (cstate.aJustPressed) {
				changes[2] = 1;
			}

			if (cstate.back && cstate.start) {
				System.exit(0);
			}

			if (cstate.startJustPressed && !cstate.back) {
				changes[6] = 1;
			}

			if (cstate.rightStickY > 0.2) {
				changes[3] = -1;

			} else if (cstate.rightStickY < -0.2) {
				changes[3] = 1;
			}

			if (cstate.lbJustPressed) {
				changes[4] = 1;
			}

			if (cstate.rbJustPressed) {
				changes[5] = 1;
			}

		}

		return changes;
	}

}
