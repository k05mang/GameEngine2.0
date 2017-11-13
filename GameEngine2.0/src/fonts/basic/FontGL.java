package fonts.basic;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

public class FontGL {
	
	private Font fontData;
	
	/**
	 * Constructs a new FontGL object binding the given font object to the instance
	 * 
	 * @param font Font to bind to this instance
	 */
	public FontGL(Font font) {
		fontData = font;
	}
	
	/**
	 * Loads all the data from the provided font file into renderable objects for OpenGL
	 */
	public void load(){
		
	}
	
	public static FontGL load(String file){
		return load(new File(file));
	}

	public static FontGL load(File font){
		FontGL result = null;
		try {
			Font data = Font.createFont(Font.TRUETYPE_FONT, font);
			result = new FontGL(data);
			result.load();
		}catch(Exception ex){
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		return result;
	}
}
