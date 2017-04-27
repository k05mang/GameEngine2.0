package fonts;

public enum GlyphFlag {
	
	ON_CURVE(0x0000_0001),
	X_SHORT(0x0000_0010),
	Y_SHORT(0x0000_0100),
	REPEAT(0x0000_1000),
	X_SAME(0x0001_0000),
	Y_SAME(0x0010_0000),
//	RESERVED(0x0100_0000),
//	RESERVED(0x1000_0000)
	;
	
	public final int flag;
	
	private GlyphFlag(int value){
		flag = value;
	}
}
