package fonts;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import glMath.vectors.Vec3;
import mesh.curve.BezierCurve;
import mesh.curve.BezierPath;
import mesh.curve.Continuity;

public class FontLoader {
	private File fontFile;
	private RandomAccessFile stream;
	private FontDirectory tableDir;
	private int numGlyphs;
	short indexToLocFormat;
	private long[] glyphOffsets;
	
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
		
		parseHead();
		//parse the maximum profile table to get the number of glyphs in the glyph table
		parseMaxp();
		parseLoca();
		//the number of glyphs will be determined by parsing the max profile table
		Font font = new Font(numGlyphs);
		//next parse the glyph table
		parseGlyphs(font);
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
	 * Parses the head table which contains various information about the font file, currently this method only reads in the 
	 * loca table type value
	 * 
	 * @throws IOException
	 */
	private void parseHead() throws IOException{
		stream.seek(tableDir.get(FontTable.HEAD).offset+tableDir.get(FontTable.HEAD).length-4);
		
		//get the loca table format
		indexToLocFormat = stream.readShort();
	}
	
	/**
	 * Parses the Loca table which contains byte offsets for each of the glyphs defined in the glyf table
	 * 
	 * @throws IOException
	 */
	private void parseLoca() throws IOException{
		stream.seek(tableDir.get(FontTable.LOCA).offset);
		glyphOffsets = new long[numGlyphs+1];
		//get the offsets
		for(int curGlyph = 0; curGlyph < numGlyphs+1; curGlyph++){
			if(indexToLocFormat == 0){
				glyphOffsets[curGlyph] = stream.readUnsignedShort() << 1;//the actual value is divided by 2 when stored, so shift to get *2
			}else{
				glyphOffsets[curGlyph] = Integer.toUnsignedLong(stream.readInt());
			}
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
	private void parseGlyphs(Font font) throws IOException{
		//set the file position to the start of the table
//		stream.seek(tableDir.get(FontTable.GLYPH).offset);
		short numContours = 0;
		//begin parsing the table
		//TODO make sure the 4 is removed from this for loop
		for(int curGlyph = 0; curGlyph < numGlyphs/6; curGlyph++){
			stream.seek(tableDir.get(FontTable.GLYPH).offset+glyphOffsets[curGlyph]);
			//get the number of contours that the glyph might have
			numContours = stream.readShort();	

//			if(curGlyph == 19){
//				System.out.println();
//			}
			//determine whether the glyph is composite or not
			if(numContours < 0){
				parseCompositeGlyph(font, numContours);
//				System.out.println("We Have a composite");
			}else{
				parseGlyph(font, numContours);
//				System.out.println("glyph: "+font.glyphIndices.size());
			}
//			System.out.println("glyph: "+curGlyph);
//			System.out.println(stream.getFilePointer());
//			System.out.println(tableDir.get(FontTable.GLYPH).offset+glyphOffsets[curGlyph+1]);
//			System.out.println(stream.getFilePointer() == (tableDir.get(FontTable.GLYPH).offset+glyphOffsets[curGlyph+1]));
		}
//		long fileOffset = stream.getFilePointer();
//		assert fileOffset < tableDir.get(FontTable.GLYPH).offset+tableDir.get(FontTable.GLYPH).length : "We exceeded the table size";
	}
	
	/**
	 * Parses the data from the glyf table for a single non-composite glyph and generates a glyph object
	 * 
	 * 
uint16	endPtsOfContours[n]	Array of last point indices of each contour; n is the number of contours.
uint16	instructionLength	Total number of bytes for instructions.
uint8	instructions[n]	Array of instructions for each glyph; n is the number of instructions.
uint8	flags[n]	Array of flags for each coordinate in outline; n is the number of flags.
uint8 or int16	xCoordinates[ ]	First coordinates relative to (0,0); others are relative to previous point.
uint8 or int16	yCoordinates[ ]	First coordinates relative to (0,0); others are relative to previous point.
Note: In the glyf table, the position of a point is not stored in absolute terms but as a vector relative to the previous point. The delta-x and delta-y vectors represent these (often small) changes in position.
Each flag is a single bit. Their meanings are shown below.
	 * 
	 * @param font Font to add the generated glyph to
	 * @param numContours Number of curves or contours the glyph contains
	 * @throws IOException
	 */
	private void parseGlyph(Font font, short numContours) throws IOException{
		short xMin, yMin, xMax, yMax;
		//get the minimum x for coordinate data.
		xMin = stream.readShort();
		//get the minimum y for coordinate data.
		yMin = stream.readShort();
		//get the maximum x for coordinate data.
		xMax = stream.readShort();
		//get the maximum y for coordinate data.
		yMax = stream.readShort();
		
		int[] endPoints = new int[numContours];
		int numPoints = 0;
		//read in the indices of the end points of the contours
		for(int curEnd = 0; curEnd < numContours; curEnd++){
			int endPoint = stream.readUnsignedShort();
			endPoints[curEnd] = endPoint;//add the point to the list
			//determine if this point is the max index in the list
			numPoints = Math.max(numPoints, endPoint);//TODO this might not be necessary if the values are sequentially greater than the last
		}
		numPoints++;

		//get the length of the instruction set
		int instLength = stream.readUnsignedShort();
		//skip the instructions
		stream.skipBytes(instLength);
		
		//create the point list
		ArrayList<Vec3> points = new ArrayList<Vec3>(numPoints);
		Glyph glyph = new Glyph(numContours);
		//make a list to store the bytes with the bit flags for reading the values
		byte[] flags = new byte[numPoints];
		//get the flags for each of the points
		for(int curFlag = 0; curFlag < numPoints; curFlag++){
			//parse the flag value
			byte flag = stream.readByte();
			
			//determine if the flag is repeating
			int repeatTimes = 0;
			//if we repeat then read the next byte as an unsigned value for the number of times repeated
			if(GlyphFlag.REPEAT.isSet(flag)){
				repeatTimes = stream.readUnsignedByte();
			}
			//repeat these flags n times
			for(int recurr = 0; recurr < repeatTimes+1; recurr++){
				flags[curFlag+recurr] = flag;//store the flag
				//create empty points now
				points.add(new Vec3());
			}
			
			curFlag += repeatTimes;
		}
		
//		int curContour = 0;
//		boolean isFirst = true;//tracks whether this is the first\
		BezierPath contour = null;
		BezierCurve curve = null;
		//read each of the coordinate values
		readCoords(points, flags, true);//start with x
		readCoords(points, flags, false);//the read y
		
		//loop through each contour
		for(int curContour = 0; curContour < endPoints.length; curContour++){
			int basePoint = curContour == 0 ? 0 : endPoints[curContour-1]+1;//cap to 0 in case of -1 
			contour = new BezierPath(Continuity.C1);
			curve = new BezierCurve();
			//for each point in that contour construct curves
			for(int curPoint = basePoint; curPoint < endPoints[curContour]+1; curPoint++){
				//If set, the point is on the curve; otherwise, it is off the curve
				boolean onCurve = GlyphFlag.ON_CURVE.isSet(flags[curPoint]);
				//store the point
				curve.add(points.get(curPoint));
				
				//if we have an on curve point and are not the first point of the contour
				if(onCurve && curPoint != basePoint){
					//then we need to end the curve and start a new one
					contour.add(curve);
					
					//create new curve using current point as base point for new curve
					curve = new BezierCurve(points.get(curPoint));

					if(curPoint == endPoints[curContour]){
						//then we need a loop back curve to end the contour
						curve.add(points.get(basePoint));
						contour.add(curve);
					}
				}else if(curPoint == endPoints[curContour]){//check if we are on the last point for the contour
					//then we need a loop back curve to end the contour
					curve.add(points.get(basePoint));
					contour.add(curve);
				}
			}
			glyph.add(contour);
		}
		//lastly add the glyph to the font
		font.add(glyph);
	}
	
	private void readCoords(ArrayList<Vec3> points, byte[] flags, boolean isX) throws IOException{
		//create a central value to store point data in
		float value = 0;
		for(int curPoint = 0; curPoint < points.size(); curPoint++){
			//parse the flag value
			byte flag = flags[curPoint];
			
			//determine how to read the bit flag and get the values
			boolean 
			coorSame = isX ? GlyphFlag.X_SAME.isSet(flag) : GlyphFlag.Y_SAME.isSet(flag), 
			coorIsByte = isX ? GlyphFlag.X_IS_BYTE.isSet(flag) : GlyphFlag.Y_IS_BYTE.isSet(flag);
			//read the coordinate data
			if(coorIsByte){
				//check if the value is negative or positive
				if(coorSame){
					//if we have the short bit and same bit then the value is just read as a byte
					value += stream.readUnsignedByte();
				}else{
					//if we have the short bit but not the same bit then the value is a negative byte
					value -= stream.readUnsignedByte();
				}
			}else if(!coorSame){
				//else the value is read a short
				value += stream.readShort();
			}//otherwise we just use the previous value for x
			
			//store the value in the appropriate coordinate in the vector in points
			points.get(curPoint).set(isX ? 0 : 1, value);//since x is index 0 we can determine where to put the value with one line
		}
	}
	
	private void parseCompositeGlyph(Font font, short numContours) throws IOException{
		
	}
}
