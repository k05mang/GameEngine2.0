package gldata;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public enum AttribType {
	BYTE(1, 1, GL_BYTE),
	UBYTE(1, 1, GL_UNSIGNED_BYTE),
	SHORT(1, 2, GL_SHORT),
	USHORT(1, 2, GL_UNSIGNED_SHORT),
	INT(1, 4, GL_INT),
	UINT(1, 4, GL_UNSIGNED_INT),
	HALF_FLOAT(1, 2, GL_HALF_FLOAT),
	FLOAT(1, 4, GL_FLOAT),
	DOUBLE(1, 8, GL_DOUBLE),
	
	VEC2(2, 8, GL_FLOAT),
	VEC3(3, 12, GL_FLOAT),
	VEC4(4, 16, GL_FLOAT),
	
	DVEC2(2, 16, GL_DOUBLE),
	DVEC3(3, 24, GL_DOUBLE),
	DVEC4(4, 32, GL_DOUBLE),

	MAT2(4, 16, GL_FLOAT),
	MAT2x3(6, 24, GL_FLOAT),
	MAT2x4(8, 32, GL_FLOAT),

	MAT3(9, 36, GL_FLOAT),
	MAT3x2(6, 24, GL_FLOAT),
	MAT3x4(12, 48, GL_FLOAT),

	MAT4(16, 64, GL_FLOAT),
	MAT4x2(8, 32, GL_FLOAT),
	MAT4x3(12, 48, GL_FLOAT),
	
	DMAT2(4, 32, GL_DOUBLE),
	DMAT2x3(6, 48, GL_DOUBLE),
	DMAT2x4(8, 64, GL_DOUBLE),

	DMAT3(9, 72, GL_DOUBLE),
	DMAT3x2(6, 48, GL_DOUBLE),
	DMAT3x4(12, 96, GL_DOUBLE),

	DMAT4(16, 128, GL_DOUBLE),
	DMAT4x2(8, 64, GL_DOUBLE),
	DMAT4x3(12, 96, GL_DOUBLE);
	/*
	 * BYTE,
		UNSIGNED_BYTE, SHORT, UNSIGNED_SHORT, INT, UNSIGNED_INT, FLOAT,
		HALF_FLOAT, or DOUBLE
	 * */

	public final int bytes, size, type;
	
	private AttribType(int size, int bytes, int type){
		this.size = size;
		this.bytes = bytes;
		this.type = type;
	}
}
