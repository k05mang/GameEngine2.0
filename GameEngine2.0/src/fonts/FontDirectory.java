package fonts;

import java.util.Hashtable;

public class FontDirectory {

	protected Hashtable<String, FontTableAttr> tables;
	
	/**
	 * Constructs a FontDirectory with the given number of tables
	 * 
	 * @param numTables Number of tables the font directory contains from the font file
	 */
	public FontDirectory(int numTables) {
		tables = new Hashtable<String, FontTableAttr>(numTables);
	}

	/**
	 * Adds the given table data to the font directory.
	 * 
	 * @param tag Tag name of the table represented as an integer that is converted to a set of unicode chars
	 * @param chcksum Checksum value for determining table integrity
	 * @param offset Offset from the beginning of the file where this table begins
	 * @param length Length of the table in bytes from the offset point
	 */
	public void add(int tag, int chcksum, int offset, int length){
		//pass the other data to the font table attribute class
//		System.out.println("Tag: "+convert(tag));
		tables.put(convert(tag), new FontTableAttr(chcksum, offset, length));
	}
	
	/**
	 * Gets the FontTableAttr for the given {@code tag} name of the table
	 * 
	 * @param tag Name of the table to retrieve data from
	 * @return FontTableAttr containing data about the font table it represents
	 */
	public FontTableAttr get(String tag){
		//convert the tag to a string
		return tables.get(tag.toLowerCase());
	}
	
	/**
	 * Gets the FontTableAttr for the given {@code table} name of the table
	 * 
	 * @param table FontTable
	 * @return FontTableAttr containing data about the font table it represents
	 */
	public FontTableAttr get(FontTable table){
		//convert the tag to a string
		return tables.get(table.name);
	}
	
	/**
	 * Converts the given integer value to a string by decomposing the integer
	 * into a byte array which is then converted to individual unicode values to
	 * form the final string
	 * 
	 * @param value Integer to convert
	 * 
	 * @return String representing the 4 character unicode value of the integer {@code value}
	 */
	public static String convert(int value){
		return new String(
				new byte[]{
						(byte)(value >> 24),
						(byte)(value >> 16),
						(byte)(value >> 8),
						(byte)value
				}
			).toLowerCase();
	}
}
