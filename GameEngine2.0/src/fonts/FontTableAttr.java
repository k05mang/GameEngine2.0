package fonts;

public class FontTableAttr {

	protected final long checkSum, offset, length;
	
	/**
	 * Constructs an container for a Font tables attributes which include:
	 * <ul>
	 * 	<li>Check sum value</li>
	 * 	<li>Offset location of the table from the start of the file</li>
	 *  <li>Length of the table in bytes</li>
	 * </ul>
	 * All values passed to this constructor are uints that will be stored into longs for use as unsigned values
	 * 
	 * @param chckSum Check sum for table validation
	 * @param offset Offset to the start of the table in the file
	 * @param length Length of the table from its start
	 */
	protected FontTableAttr(Integer chckSum, Integer offset, Integer length) {
		checkSum = Integer.toUnsignedLong(chckSum);
		this.offset = Integer.toUnsignedLong(offset);
		this.length = Integer.toUnsignedLong(length);

//		System.out.println("checkSum: "+checkSum);
//		System.out.println("Offset: "+this.offset);
//		System.out.println("Length: "+this.length);
	}
}
