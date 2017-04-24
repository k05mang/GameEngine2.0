package fonts;

public enum FontTable {
	
	CMAP("cmap"),//character to glyph mapping table
	GLYPH("glyf"),//Glyph data
	HEAD("head"),//Font header
	HHEAD("hhea"),//Horizontal header
	HMTX("hmtx"),//Horizontal metrics
	MAXP("maxp"),//Maximum profile
	NAME("name"),//Naming table
	POST("post"),//PostScript information
	LOCA("loca"),//Index to location
	OS2("OS/2"),//OS/2 and Windows specific metrics (optional table)
	CVT("cvt"),//Control Value Table (optional table)
	FPGM("fpgm"),//Font program (optional table)
	PREP("prep"),//CVT Program (optional table)
	GASP("gasp"),//Grid-fitting/Scan-conversion (optional table)
	;
	
	public final String name;
	
	private FontTable(String value){
		name = value;
	}
	
	/**
	 * Determines if the enum represents a table that is required in an OpenType/TrueType font file
	 * 
	 * @return True if the enum represents a font table that is required, false otherwise
	 */
	public boolean isRequired(){
		return  name.equals("cmap") ||
				name.equals("cmap") ||
				name.equals("glyf") ||
				name.equals("head") ||
				name.equals("hhea") ||
				name.equals("hmtx") ||
				name.equals("maxp") ||
				name.equals("name") ||
				name.equals("post") ||
				name.equals("loca");
	}
}
