

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * This requires javac.exe to be on the system path to function.
 * @author Garrett Malmquist
 *
 */
public class HelloWorld {
	private static BufferedImage REFERENCE;
	
	public static void main(String[] args) throws Exception {
		cleanup();
		
		LCG lcg = null;
		int attempts = 0;
		while (lcg == null) {
			System.out.println("Attemting to initialize LCG (attempt " + attempts++ + ")");
			lcg = LCG.instance();
		}
		
		String name = lcg.writeJava();
		
		Runtime.getRuntime().exec("javac " + name);

		String clz = name.substring(0, name.lastIndexOf('.'));

		File classFile = new File(clz + ".class");
		while (!classFile.exists()) {
			System.out.println("Waiting for class to exist...");
			try { Thread.sleep(1000); }
			catch (InterruptedException e) {}
		}
		
		System.out.println(classFile.exists() + ", " + classFile.toString());
		if (new File("./bin").exists()) {
			classFile.renameTo(new File("./bin/" + clz + ".class"));
		}
		

		ClassLoader loader = URLClassLoader.newInstance(new URL[] {
				new File(".").toURI().toURL()
		});
		loader = HelloWorld.class.getClassLoader();
		Class<?> letterTiler = loader.loadClass(clz);
		
		
		REFERENCE = ImageIO.read(HelloWorld.class.getResource("ref.png"));
		
		System.out.println(stringVector(letterTiler, "Hello, world!").utility());

		Climber climber = new Climber(stringVector(letterTiler, "").random());
		for (int j = 0; j < 100; j++) {
			double lastValue = -Double.MAX_VALUE;
			int staleness = 0;
			for (int i = 0; i < 10000; i++) {
				Instance best = climber.best();
				
				if (climber.current().utility() == lastValue) {
					// stalemate
					staleness++;
					if (staleness > 100) {
						break;
					}
				} else {
					staleness = 0;
				}
				lastValue = climber.current().utility();
				
				System.out.println("Iteration " + i + ": " + climber.current());
				climber.iterate();
				if (climber.best().utility() >= "             ".length()) {
					break;
				}
			}
			if (climber.best().utility() >= "             ".length()) {
				break;
			}
			climber.restart();
			System.out.println("Current Best: " + climber.best());
		}
		
		System.out.println("Final Value: " + climber.best());
		System.out.println(climber.best().name());
		
		cleanup();
	}
	
	private static char[] alphabet = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM ,!".toCharArray();
	private static Instance stringVector(final Class<?> LT, final String str) {
		return new Instance() {
			private String string = str;
			@Override
			public Instance copy() {
				return stringVector(LT, string);
			}

			private char r() {
//				return (char)(int)(32 + Math.random() * (126-32));
				return alphabet[(int)(Math.random() * alphabet.length)];
			}
			
			@Override
			public Instance neighbor() {
				// mutation
				char[] chars = string.toCharArray();
				chars[(int)(Math.random() * chars.length)] = r();
				return stringVector(LT, new String(chars));
			}

			@Override
			public Instance random() {
				int length = "             ".length();
				String str = "";
				for (int i = 0; i < length; i++) {
					str += r();
				}
				return stringVector(LT, str);
			}

			private volatile boolean hasUtility = false;
			private double util = 0;
			@Override
			public double utility() {
				if (hasUtility) {
					return util;
				}
				
				if (Math.random() > 0.001) {
					double u = 0;
					for (int i = 0; i < string.length(); i++) {
						if (string.charAt(i) == "Hello, world!".charAt(i)) {
							u++;
						}
					}
					return u;
				}
				
				try {
					BufferedImage A = REFERENCE;
					BufferedImage tiled = assemble(LT, string);
					BufferedImage B = new BufferedImage(A.getWidth(), A.getHeight(), 
							BufferedImage.TYPE_INT_RGB);
					Graphics2D g = B.createGraphics();
					g.setColor(Color.WHITE);
					g.fillRect(-1, -1, B.getWidth()+2, B.getHeight()+2);
					g.drawImage(tiled, 0, 0, null);
					g.dispose();
					
					
					
					double error = 0;
					
					for (int x = 0; x < A.getWidth(); x++) {
						for (int y = 0; y < A.getHeight(); y++) {
							double a = grey(A.getRGB(x, y));
							double b = grey(B.getRGB(x, y));
							
							double d = (b-a);
							if ((a > 0) == (b > 0) && a == 0) {
								
							} else {
								error++;
							}
						}
					}
					
					hasUtility = true;
					util = -error;
					return util;
				} catch (Exception e) {
					System.err.println("Utility exception : " + e);
					return 0;
				}
			}
			
			public String name() {
				return string;
			}
			
			@Override
			public String toString() {
				return string + ": " + utility();
			}
		};
	}
	
	private static double grey(int c) {
		Color k = new Color(c);
		return (k.getRed() + k.getGreen() + k.getBlue())/(3 * 256.0);
	}
	
	private static void cleanup() {
		// cleanup
		File[] files = new File(".").listFiles();
		for (File file : files) {
			if (file.getName().matches("^j\\d+\\.((class)|(java))$")) {
				file.deleteOnExit();
			}
		}
		files = new File("./bin").listFiles();
		if (files == null) {
			return;
		}
		for (File file : files) {
			if (file.getName().matches("^j\\d+\\.((class)|(java))$")) {
				file.deleteOnExit();
			}
		}		
	}
	
	private static HashMap<String, BufferedImage> tileMap = new HashMap<String, BufferedImage>();
	public static BufferedImage assemble(Class<?> tiler, String message) throws Exception {
		BufferedImage[] tiles = new BufferedImage[message.length()];
		Method[] methods = tiler.getMethods();
		for (int i = 0; i < message.length(); i++) {
			String c = String.valueOf(message.charAt(i));
			
			BufferedImage tile = tileMap.get(c);
			if (tile != null) {
				tiles[i] = tile;
				continue;
			}
			
			try {
				for (Method m : methods) {
					String re = "^coolmethod\\d+_AAA" + c + "xD$";
					if (m.getName().matches(re)) {
						tile = (BufferedImage) m.invoke(tiler.getConstructors()[0].newInstance());
						break;
					}
				}
			} catch (Exception ex) {
				// probably a special character.
				// rather than do validation, we'll just catch the exception.
			}
			
			if (tile == null) {
				for (Method m : methods) {
					if (m.getName().matches("^coolmethod\\d+_AAA_xD$")) {
						tile = (BufferedImage) m.invoke(tiler.getConstructors()[0].newInstance(), c);
						break;
					}
				}
			}
			
			tiles[i] = tile;
			tileMap.put(c, tile);
		}
		
		int width = 0;
		int height = 0;
		for (BufferedImage m : tiles) {
			width += m.getWidth();
			if (m.getHeight() > height)
				height = m.getHeight();
		}
		
		BufferedImage total = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int x = 0;
		Graphics2D g = total.createGraphics();
		for (BufferedImage m : tiles) {
			g.drawImage(m, x, 0, null);
			x += m.getWidth();
		}
		g.dispose();
		return total;
	}
}
