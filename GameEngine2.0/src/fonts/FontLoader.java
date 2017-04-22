package fonts;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class FontLoader {
	private File fontFile;
	private DataInputStream stream;
	private FontDirectory tableDir;
	
	protected FontLoader(File file) throws FileNotFoundException{
		fontFile = file;
		stream = new DataInputStream(new FileInputStream(file));
	}

	/**
	 * Parses the font file and generates a Font object for use by a Font renderer
	 * 
	 * @return Font object containing all the renderable data for the passed Font file, or null if the loading failed
	 * 
	 * @throws IOException
	 */
	public Font load(/*possibly add a value here to determine if we want to load the file faster using the offset table*/) throws IOException{
		Font font = new Font();
		//begin parsing the file
		//all True type font files must start with the font directory table
		loadFontDirectory();
		//close the input stream
		stream.close();
		return font;
	}
	
	private void loadFontDirectory() throws IOException{
		//font directory starts with the offset table
		//this contains information on searching the file to obtain specific data
		//this can be useful for fast loading by looking only at relevant rendering data
		
		//uint32	version number
		stream.skip(4);//skip the scaler
		
		//uint16	numTables	number of tables
		//get the number of tables
		int numTables = stream.readUnsignedShort();
		//initialize the font directory object
		tableDir = new FontDirectory(numTables);
		
		//uint16	searchRange	(maximum power of 2 <= numTables)*16
		//uint16	entrySelector	log2(maximum power of 2 <= numTables)
		//uint16	rangeShift	numTables*16-searchRange
		//for now skip these entries, which is 48 bits or 6 bytes
		stream.skipBytes(6);
		
		//next is the table directory, this table lists the different tables the font file 
		//contains
		
		//iterate through the number of tables to construct the Font directory
		for(int curTblDir = 0; curTblDir < numTables; curTblDir++){
			//uint32	4-byte tag identifier
			int tag = stream.readInt();
			//uint32	checksum for this table
			int chcksum = stream.readInt();
			//uint32	offset from beginning of the file
			int offset = stream.readInt();
			//uint32	length of this table in byte (actual length not padded length)
			int length = stream.readInt();
			tableDir.add(tag, chcksum, offset, length);
		}
	}
}
