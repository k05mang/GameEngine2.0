package fonts;

import java.util.Hashtable;

public class FontDirectory {

	private Hashtable<Integer, FontTableAttr> tables;
	
	public FontDirectory(int numTables) {
		tables = new Hashtable<Integer, FontTableAttr>(numTables);
	}

	public void add(int tag, int chcksum, int offset, int length){
		//pass the other data to the font table attribute class
//		System.out.println("Tag: "+convert(tag));
		tables.put(tag, new FontTableAttr(chcksum, offset, length));
	}
	
	/**
	 * Gets the FontTableAttr for the given tag value represented by the given 
	 * @param tag
	 * @return
	 */
	public FontTableAttr get(int tag){
		//convert the tag to a string
		return tables.get(tag);
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
//	public static String convert(int value){
//		return new String(
//				new byte[]{
//						(byte)(value >> 24),
//						(byte)(value >> 16),
//						(byte)(value >> 8),
//						(byte)value
//				}
//			);
//	}
}
