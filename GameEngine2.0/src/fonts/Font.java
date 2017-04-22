package fonts;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

public class Font {

	private Hashtable<Integer, Glyph> glyphs;
	
	/**
	 * Construct a Font object using the given Array list of Glyph objects to build the font
	 * 
	 * @param glyphs Glyphs to use in building the font
	 */
	protected Font() {
		this.glyphs = new Hashtable<Integer, Glyph>();
	}
	
	/**
	 * Adds the given glyph to this font mapping it to the given Glyph object
	 * 
	 * @param charCode Character code to map the glyph to
	 * @param glyph Glyph to map to the character code
	 */
	protected void add(int charCode, Glyph glyph){
		glyphs.put(charCode, glyph);
	}
	
	public static Font load(String fileName){
		return load(new File(fileName));
	}

	/**
	 * Attempts to load a font file.
	 * <br>
	 * Supported font files include:
	 * <ul>
	 * 	<li>True Type Font (.ttf)</li>
	 * </ul>
	 * 
	 * @param file File to be parsed and loaded, can only be of the supported font file types listed above
	 * 
	 * @return Renderable Font object containing the font defined by the given font object, or null if the file failed to load
	 */
	public static Font load(File file){
		//check to make sure the file exists
		if(file.exists()){
			try{
				//Open type is just a more robust true type, and shares the same format and file extension, so a single loader works
				return new FontLoader(file).load();
			}catch(IOException e){	
				e.printStackTrace();
				return null;
			}
		}else{
			return null;
		}
	}
}
