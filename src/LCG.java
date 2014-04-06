

import java.io.File;
import java.io.PrintStream;

/**
 * Letter-Class-Generator (stochastic singleton)
 * @author Garrett
 *
 */
public class LCG {
	private static LCG me;
	
	/**
	 * Has a 1% chance of actually returning an instance of LCG
	 * @return
	 */
	public static LCG instance() {
		if (me == null) {
			me = new LCG();
		}
		if (Math.random() < 0.01) {
			return me;
		}
		return null;
	}
	
	private LCG() { }
	
	private void tab(PrintStream out, int n) {
		for (int i = 0; i < n; i++)
			out.print("\t");
	}
	
	
	public String writeJava() throws Exception {
		String name = "j" + System.currentTimeMillis();
		
		File file = new File(name + ".java");
		file.createNewFile();
//		file.deleteOnExit();
		
		PrintStream out = new PrintStream(file);
		
		out.println("import java.awt.*;");
		out.println("import java.awt.image.*;");
		out.println("import javax.imageio.*;");
		out.println("public class " + name + " {");
		
		String[] letters = new String[26*2];
		for (int i = 0; i < 26; i++) {
			letters[i] = String.valueOf((char)('a'+i));
			letters[i+26] = String.valueOf((char)('A'+i));
		}
		
		String font = "Font font = new Font(\"monospaced\", Font.BOLD, 16);";
		
		for (String letter : letters) {
			String meth = "coolmethod" + System.currentTimeMillis() + "_AAA" + letter + "xD";
			out.println("public BufferedImage " + meth + "() {");
			out.println("Graphics2D g = new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB).createGraphics();");
			out.println(font);
			out.println("FontMetrics fm = g.getFontMetrics(font);");
			out.println("int w = fm.stringWidth(\"" + letter + "\");");
			out.println("int h = fm.getHeight();");
			out.println("BufferedImage img = new BufferedImage(w+1,h+1, BufferedImage.TYPE_INT_ARGB);");
			out.println("g = img.createGraphics();");
			out.println("g.setFont(font);");
			out.println("g.setColor(Color.BLACK);");
			out.println("g.drawString(\"" + letter + "\","
					+ " img.getWidth()/2-fm.stringWidth(\"" + letter + "\")/2,"
							+ " img.getHeight()/2 + fm.getAscent() - fm.getHeight()/2);");
			out.println("return img;");
			out.println("}");
		}
		
		String meth = "coolmethod" + System.currentTimeMillis() + "_AAA_xD";
		out.println("public BufferedImage " + meth + "(String l) {");
		out.println("l = String.valueOf(l.charAt(0));");
		out.println("char xxx = l.toLowerCase().charAt(0);");
		out.println("if (xxx >= 'a' && xxx <= 'z') return null;");
		out.println("Graphics2D g = new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB).createGraphics();");
		out.println(font);
		out.println("FontMetrics fm = g.getFontMetrics(font);");
		out.println("int w = fm.stringWidth(l);");
		out.println("int h = fm.getHeight();");
		out.println("BufferedImage img = new BufferedImage(w+1,h+1, BufferedImage.TYPE_INT_ARGB);");
		out.println("g = img.createGraphics();");
		out.println("g.setFont(font);");
		out.println("g.setColor(Color.BLACK);");
		out.println("g.drawString(l,"
				+ " img.getWidth()/2-fm.stringWidth(l)/2,"
						+ " img.getHeight()/2 + fm.getAscent() - fm.getHeight()/2);");
		out.println("return img;");
		out.println("}");
		
		out.println("}");
		out.close();
		
		meth = "decoy" + System.currentTimeMillis() + "_AAA_xD";
		out.println("public BufferedImage " + meth + "(String l) {");
		out.println("Graphics2D g = new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB).createGraphics();");
		out.println(font);
		out.println("FontMetrics fm = g.getFontMetrics(font);");
		out.println("int w = fm.stringWidth(l);");
		out.println("int h = fm.getHeight();");
		out.println("BufferedImage img = new BufferedImage(w+1,h+1, BufferedImage.TYPE_INT_ARGB);");
		out.println("return img;");
		out.println("}");
		
		out.println("}");
		out.close();
		
		return file.getName();
	}
}
