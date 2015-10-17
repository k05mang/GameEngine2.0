package gldata;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public enum AttribType {
	BYTE(1, GL_BYTE),
	UBYTE(1, GL_UNSIGNED_BYTE),
	SHORT(1, GL_SHORT),
	USHORT(1, GL_UNSIGNED_SHORT),
	INT(1, GL_INT),
	UINT(1, GL_UNSIGNED_INT),
	HALF_FLOAT(1, GL_HALF_FLOAT),
	FLOAT(1, GL_FLOAT),
	DOUBLE(1, GL_DOUBLE),
	
	VEC2(2, GL_FLOAT),
	VEC3(3, GL_FLOAT),
	VEC4(4, GL_FLOAT),
	
	DVEC2(2, GL_DOUBLE),
	DVEC3(3, GL_DOUBLE),
	DVEC4(4, GL_DOUBLE),

	MAT2(4, GL_FLOAT),
	MAT2x3(6, GL_FLOAT),
	MAT2x4(8, GL_FLOAT),

	MAT3(9, GL_FLOAT),
	MAT3x2(6, GL_FLOAT),
	MAT3x4(12, GL_FLOAT),

	MAT4(16, GL_FLOAT),
	MAT4x2(8, GL_FLOAT),
	MAT4x3(12, GL_FLOAT),
	
	DMAT2(4, GL_DOUBLE),
	DMAT2x3(6, GL_DOUBLE),
	DMAT2x4(8, GL_DOUBLE),

	DMAT3(9, GL_DOUBLE),
	DMAT3x2(6, GL_DOUBLE),
	DMAT3x4(12, GL_DOUBLE),

	DMAT4(16, GL_DOUBLE),
	DMAT4x2(8, GL_DOUBLE),
	DMAT4x3(12, GL_DOUBLE);
	/*
	 * BYTE,
		UNSIGNED_BYTE, SHORT, UNSIGNED_SHORT, INT, UNSIGNED_INT, FLOAT,
		HALF_FLOAT, or DOUBLE
	 * */

	public final int size, type;
	
	private AttribType(int size, int type){
		this.size = size;
		this.type = type;
	}
}
