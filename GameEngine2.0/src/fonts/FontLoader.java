package fonts;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FontLoader {
	private File fontFile;
	private RandomAccessFile stream;
	private FontDirectory tableDir;
	private int numGlyphs;
	
	protected FontLoader(File file) throws FileNotFoundException{
		fontFile = file;
		stream = new RandomAccessFile(file, "r");
	}

	/**
	 * Parses the font file and generates a Font object for use by a Font renderer
	 * 
	 * @return Font object containing all the renderable data for the passed Font file, or null if the loading failed
	 * 
	 * @throws IOException
	 */
	public Font load(/*possibly add a value here to determine if we want to load the file faster using the offset table*/) throws IOException{
		//begin parsing the file
		//all True type font files must start with the font directory table
		loadFontDirectory();
		//begin parsing the required tables
		//start by parsing the header table
		
		//parse the maximum profile table to get the number of glyphs in the glyph table
		parseMaxp();
		//the number of glyphs will be determined by parsing the max profile table
		Font font = new Font(numGlyphs);
		//next parse the glyph table
		parseGlyph(font);
		//close the input stream
		stream.close();
		return font;
	}
	
	/**
	 * Loads the font directory from the file. This table is the first table and lays out the contents of the file, including
	 * tables in the file, offsets to the tables, the size of the tables, check sums for each table to test data integrity, and
	 * the offset table which can be used for quick lookup of relevant tables for fast loading.
	 * <br>
	 * The table is formatted as follows:
	 * <dl>
		  <dt>Offset table</dt>
		  <dd>Table containing the number of tables as well as the means to quickly search the font file for needed tables to speed up loading
		  		<dl>
		  			<dt>Version number</dt>
				  	<dd>The version of the font file</dd>
				  	
				  	<dt>Table count, specified by a 4 bytes</dt>
				  	<dd>Number of tables in this font file</dd>
				  	
				  	<dt>Search range, specified by a 2 bytes</dt>
				  	<dd>Range limit on searching the following font table</dd>
				  	
				  	<dt>Entry selector</dt>
				  	<dd>Not sure, specified by a 2 bytes</dd>
				  	
				  	<dt>Range shift</dt>
				  	<dd>Not sure, specified by a 2 bytes</dd>
		  		</dl>
		  </dd>
		  
		  <dt>Table directory</dt>
		  <dd>Directory containing information about each of the tables in the font file, each entry is defined as follows:
			  <dl>
				  <dt>Tag name</dt>
				  <dd>Name of the table represented by 4 bytes</dd>
				  
				  <dt>Check sum</dt>
				  <dd>Check sum value to determine data integrity, specified by 4 bytes</dd>
				  
				  <dt>Offset</dt>
				  <dd>Offset of the start of the table from the beginning of the file, specified by 4 bytes</dd>
				  
				  <dt>Length</dt>
				  <dd>Length of the table in bytes, specified by 4 bytes</dd>
			  </dl>
		  </dd>
	  </dl>
	 * 
	 * @throws IOException
	 */
	private void loadFontDirectory() throws IOException{
		//font directory starts with the offset table
		//this contains information on searching the file to obtain specific data
		//this can be useful for fast loading by looking only at relevant rendering data
		
		//version number
		stream.skipBytes(4);//skip
		
		//get the number of tables
		int numTables = stream.readUnsignedShort();
		//initialize the font directory object
		tableDir = new FontDirectory(numTables);
		
		//for now skip these entries, which is 48 bits or 6 bytes
		stream.skipBytes(6);
		
		//next is the table directory, this table lists the different tables the font file 
		//contains
		
		//iterate through the number of tables to construct the Font directory
		for(int curTblDir = 0; curTblDir < numTables; curTblDir++){
			//4-byte tag identifier
			int tag = stream.readInt();
			//checksum for this table
			int chcksum = stream.readInt();
			//offset from beginning of the file
			int offset = stream.readInt();
			//length of this table in byte (actual length not padded length)
			int length = stream.readInt();
			tableDir.add(tag, chcksum, offset, length);
		}
	}
	
	/**
	 * Parses the max profile table for gathering information about the glyphs
	 * is table establishes the memory requirements for this font. 
	 * Fonts with CFF data must use Version 0.5 of this table, specifying only the numGlyphs field. 
	 * Fonts with TrueType outlines must use Version 1.0 of this table, where all data is required.
		Version 0.5
		Fixed	Table version number	0x00005000 for version 0.5 
		(Note the difference in the representation of a non-zero fractional part, in Fixed numbers.)
		uint16	numGlyphs	The number of glyphs in the font.
		
		Version 1.0
		Fixed	Table version number	0x00010000 for version 1.0.
		uint16	numGlyphs	The number of glyphs in the font.
		uint16	maxPoints	Maximum points in a non-composite glyph.
		uint16	maxContours	Maximum contours in a non-composite glyph.
		uint16	maxCompositePoints	Maximum points in a composite glyph.
		uint16	maxCompositeContours	Maximum contours in a composite glyph.
		uint16	maxZones	1 if instructions do not use the twilight zone (Z0), or 2 if instructions do use Z0; should be set to 2 in most cases.
		uint16	maxTwilightPoints	Maximum points used in Z0.
		uint16	maxStorage	Number of Storage Area locations.
		uint16	maxFunctionDefs	Number of FDEFs, equals to the highest function number + 1.
		uint16	maxInstructionDefs	Number of IDEFs.
		uint16	maxStackElements	Maximum stack depth2.
		uint16	maxSizeOfInstructions	Maximum byte count for glyph instructions.
		uint16	maxComponentElements	Maximum number of components referenced at “top level” for any composite glyph.
		uint16	maxComponentDepth	Maximum levels of recursion; 1 for simple components.
	 * 
	 * @throws IOException
	 */
	private void parseMaxp() throws IOException{
		//set the file position to the start of the table
		stream.seek(tableDir.get(FontTable.MAXP).offset);
		//begin
		//check which table version it is
		int version = stream.readInt();
		//version number of .5
		if(version == 0x00005000){
			//next read the number of glpyhs
			numGlyphs = stream.readUnsignedShort();
		}else{//otherwise it is version 1.0
			//next read the number of glpyhs
			numGlyphs = stream.readUnsignedShort();
			//TODO expand on this section if needed, most likely will
		}
	}
	
	/**
	 * Parses the Glyf table which contains all the relevant rendering data for the fonts glyphs.
	 * int16	numberOfContours	If the number of contours is greater than or equal to zero, this is a single glyph; if negative, this is a composite glyph.
		int16	xMin	Minimum x for coordinate data.
		int16	yMin	Minimum y for coordinate data.
		int16	xMax	Maximum x for coordinate data.
		int16	yMax	Maximum y for coordinate data.
	 * 
	 *  @param font Font to store the parsed glyphs into
	 */
	private void parseGlyph(Font font) throws IOException{
		//set the file position to the start of the table
		stream.seek(tableDir.get(FontTable.GLYPH).offset);
		short numCurves = 0;
		//begin parsing the table
		for(int curGlyph = 0; curGlyph < numGlyphs; curGlyph++){
			//get the number of contours that the glyph might have
			numCurves = stream.readShort();	
			
			//determine whether the glyph is composite or not
			if(numCurves < 0){
				parseCompositeGlyph(font, numCurves);
			}else{
				parseGlyph(font, numCurves);
			}
		}
	}
	
	/**
	 * Parses the data from the glyf table for a single non-composite glyph and generates a glyph object
	 * 
	 * 
uint16	endPtsOfContours[n]	Array of last points of each contour; n is the number of contours.
uint16	instructionLength	Total number of bytes for instructions.
uint8	instructions[n]	Array of instructions for each glyph; n is the number of instructions.
uint8	flags[n]	Array of flags for each coordinate in outline; n is the number of flags.
uint8 or int16	xCoordinates[ ]	First coordinates relative to (0,0); others are relative to previous point.
uint8 or int16	yCoordinates[ ]	First coordinates relative to (0,0); others are relative to previous point.
Note: In the glyf table, the position of a point is not stored in absolute terms but as a vector relative to the previous point. The delta-x and delta-y vectors represent these (often small) changes in position.
Each flag is a single bit. Their meanings are shown below.
	 * 
	 * @param font Font to add the generated glyph to
	 * @param numCurves Number of curves or contours the glyph contains
	 * @throws IOException
	 */
	private void parseGlyph(Font font, short numCurves) throws IOException{
		short xMin, yMin, xMax, yMax;
		//get the minimum x for coordinate data.
		xMin = stream.readShort();
		//get the minimum y for coordinate data.
		yMin = stream.readShort();
		//get the maximum x for coordinate data.
		xMax = stream.readShort();
		//get the maximum y for coordinate data.
		yMax = stream.readShort();
		System.out.printf("Number of curves: %d%nXmin: %d%nYmin: %d%nXmax: %d%nXmax: %d%n", numCurves, xMin, yMin, xMax, yMax);
		
		//parse the flag value
		
		//If set, the point is on the curve; otherwise, it is off the curve
		
		//If set, the corresponding x-coordinate is a (uint8) else it's a (int16)
		
		//If set, the corresponding y-coordinate is a (uint8) else it's a (int16)
		
		//If set, the next byte (read as unsigned) specifies the number of additional times this set of flags is to be repeated
		
		//If x-Short Vector is set, this bit describes the sign of the value, with 1 equalling positive and 0 negative. 
		//If the x-Short Vector bit is not set and this bit is set, then the current x-coordinate is the same as the previous x-coordinate. 
		//If the x-Short Vector bit is not set and this bit is also not set, the current x-coordinate is a signed 16-bit delta vector.
	}
	
	private void parseCompositeGlyph(Font font, short numCurves) throws IOException{
		
	}
}
