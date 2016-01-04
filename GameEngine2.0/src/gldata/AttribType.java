package gldata;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.*;

import java.util.ArrayList;

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
	DMAT4x3(12, 96, GL_DOUBLE),
	
	INT_2_10_10_10_REV(4, 4, GL_INT_2_10_10_10_REV),
	UINT_2_10_10_10_REV(4, 4, GL_UNSIGNED_INT_2_10_10_10_REV),
	UINT_10F_11F_11F_REV(3, 4, GL_UNSIGNED_INT_10F_11F_11F_REV);

	public final int bytes, size, type;
	
	private AttribType(int size, int bytes, int type){
		this.size = size;
		this.bytes = bytes;
		this.type = type;
	}
	
	/**
	 * Decomposes this AttribType into smaller AttribTypes if it is necessary, (i.e. Mat4 = 4 Vec4)
	 * 
	 * @param attributes ArrayList to add the decomposed attributes to
	 */
	public void decompose(ArrayList<AttribType> attributes){
		//check if the attribute is a matrix or double vector greater than 2 and decompose it into simpler types so that they can be passed to the GPU
		switch (this) {
			case DVEC3:
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DOUBLE);
				break;
			case DVEC4:
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				break;
			case MAT2:
				attributes.add(AttribType.VEC2);
				attributes.add(AttribType.VEC2);
				break;
			case MAT2x3:
				attributes.add(AttribType.VEC3);
				attributes.add(AttribType.VEC3);
				break;
			case MAT2x4:
				attributes.add(AttribType.VEC4);
				attributes.add(AttribType.VEC4);
				break;

			case MAT3:
				attributes.add(AttribType.VEC3);
				attributes.add(AttribType.VEC3);
				attributes.add(AttribType.VEC3);
				break;
			case MAT3x2:
				attributes.add(AttribType.VEC2);
				attributes.add(AttribType.VEC2);
				attributes.add(AttribType.VEC2);
				break;
			case MAT3x4:
				attributes.add(AttribType.VEC3);
				attributes.add(AttribType.VEC3);
				attributes.add(AttribType.VEC3);
				break;

			case MAT4:
				attributes.add(AttribType.VEC4);
				attributes.add(AttribType.VEC4);
				attributes.add(AttribType.VEC4);
				attributes.add(AttribType.VEC4);
				break;
			case MAT4x2:
				attributes.add(AttribType.VEC2);
				attributes.add(AttribType.VEC2);
				attributes.add(AttribType.VEC2);
				attributes.add(AttribType.VEC2);
				break;
			case MAT4x3:
				attributes.add(AttribType.VEC3);
				attributes.add(AttribType.VEC3);
				attributes.add(AttribType.VEC3);
				attributes.add(AttribType.VEC3);
				break;

			case DMAT2:
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				break;
			case DMAT2x3:
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DOUBLE);
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DOUBLE);
				break;
			case DMAT2x4:
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				break;

			case DMAT3:
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DOUBLE);

				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DOUBLE);

				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DOUBLE);
				break;
			case DMAT3x2:
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				break;
			case DMAT3x4:
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				break;

			case DMAT4:
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				break;
			case DMAT4x2:
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				break;
			case DMAT4x3:
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DOUBLE);
				
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DOUBLE);
				
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DOUBLE);
				
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DOUBLE);
				break;
			default:
				attributes.add(this);
				break;
		}
	}
	
	/**
	 * Determines whether this AttribType is a double type
	 * 
	 * @return True if this AttribType is a double type, false otherwise
	 */
	public boolean isDouble(){
		return this.type == GL_DOUBLE;
		
	}
	
	/**
	 * Determines whether this AttribType is a float type
	 * 
	 * @return True if this AttribType is a float type, false otherwise
	 */
	public boolean isFloat(){
		return this.type == GL_HALF_FLOAT ||
				this.type == GL_FLOAT ||
				this.type == GL_UNSIGNED_INT_10F_11F_11F_REV;
	}
	
	/**
	 * Determines whether this AttribType is a integer type
	 * 
	 * @return True if this AttribType is a integer type, false otherwise
	 */
	public boolean isInt(){
		return !isDouble() && !isFloat();
	}
}
