package fonts;

public enum GlyphFlag {
	
	ON_CURVE(0b0000_0001),
	X_IS_BYTE(0b0000_0010),
	Y_IS_BYTE(0b0000_0100),
	REPEAT(0b0000_1000),
	X_SAME(0b0001_0000),
	Y_SAME(0b0010_0000),
//	RESERVED(0x0100_0000),
//	RESERVED(0x1000_0000)
	;
	
	public final int flag;
	
	private GlyphFlag(int value){
		flag = value;
	}
	
	/**
	 * Determines if the given flags variable has the same flag set as the current enum type
	 * 
	 * @param flags Byte containing the bit flags to determine if the corresponding type was set
	 * 
	 * @return True if the bit flag this enum represents is set, false otherwise
	 */
	public boolean isSet(byte flags){
		return (flag & flags) > 0;
	}
}
