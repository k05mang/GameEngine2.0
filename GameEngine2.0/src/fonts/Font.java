package fonts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class Font {

	private Hashtable<Integer, Glyph> glyphs;
	private ArrayList<Glyph> glyphIndices;
	
	/**
	 * Construct a Font object using the given Array list of Glyph objects to build the font
	 * 
	 * @param glyphs Glyphs to use in building the font
	 */
	protected Font(int numGlyphs) {
		glyphs = new Hashtable<Integer, Glyph>(numGlyphs);
		glyphIndices = new ArrayList<Glyph>(numGlyphs);
	}
	
	/**
	 * Maps the given character code to the given glyph at the index specified
	 * 
	 * @param charCode Character code to map the specified glyph to
	 * @param glyphIndex Index of the glyph to be mapped to the given character
	 */
	protected void map(int charCode, int glyphIndex){
		//check the range is valid
		if(glyphIndex > 0 && glyphIndex < glyphIndices.size()){
			//add the mapping to the glyph hashtable
			glyphs.put(charCode, glyphIndices.get(glyphIndex));
		}
	}
	
	/**
	 * Adds the given glyph to this font at the index following the previously added glyph
	 * 
	 * @param glyph Glyph to add to the font
	 */
	protected void add(Glyph glyph){
		glyphIndices.add(glyph);
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
				//Open type is just a more robust true type, and shares the same format and file extension
				//however collection files need to be differentiated
				if(file.getName().contains(".otc") || file.getName().contains(".ttc")){
					return null;//for now return null for collection files
				}else if(file.getName().contains(".otf") || file.getName().contains(".ttf")){
					return new FontLoader(file).load();
				}else{//otherwise the file given is not parsable by this function
					return null;
				}
			}catch(IOException e){	
				e.printStackTrace();
				return null;
			}
		}else{
			return null;
		}
	}
}
