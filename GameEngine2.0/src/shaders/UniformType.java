package shaders;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL42.*;

//TODO add support for double types
public enum UniformType {
	FLOAT(GL_FLOAT, 1),
	VEC2(GL_FLOAT_VEC2, 2),
	VEC3(GL_FLOAT_VEC3, 3),
	VEC4(GL_FLOAT_VEC4, 4),
	DOUBLE(GL_DOUBLE, 2),
	DVEC2(GL_DOUBLE_VEC2, 4),
	DVEC3(GL_DOUBLE_VEC3, 6),
	DVEC4(GL_DOUBLE_VEC4, 8),
	INT(GL_INT, 1),
	IVEC2(GL_INT_VEC2, 2),
	IVEC3(GL_INT_VEC3, 3),
	IVEC4(GL_INT_VEC4, 4),
	UINT(GL_UNSIGNED_INT, 1),
	UVEC2(GL_UNSIGNED_INT_VEC2, 2),
	UVEC3(GL_UNSIGNED_INT_VEC3, 3),
	UVEC4(GL_UNSIGNED_INT_VEC4, 4),
	BOOL(GL_BOOL, 1),
	BVEC2(GL_BOOL_VEC2, 2),
	BVEC3(GL_BOOL_VEC3, 3),
	BVEC4(GL_BOOL_VEC4, 4),
	MAT2(GL_FLOAT_MAT2, 4),
	MAT3(GL_FLOAT_MAT3, 9),
	MAT4(GL_FLOAT_MAT4, 16),
	MAT2X3(GL_FLOAT_MAT2x3, 6),
	MAT2X4(GL_FLOAT_MAT2x4, 8),
	MAT3X2(GL_FLOAT_MAT3x2, 6),
	MAT3X4(GL_FLOAT_MAT3x4, 12),
	MAT4X2(GL_FLOAT_MAT4x2, 8),
	MAT4X3(GL_FLOAT_MAT4x3, 12),
	DMAT2(GL_DOUBLE_MAT2, 4),
	DMAT3(GL_DOUBLE_MAT3, 9),
	DMAT4(GL_DOUBLE_MAT4, 16),
	DMAT2X3(GL_DOUBLE_MAT2x3, 6),
	DMAT2X4(GL_DOUBLE_MAT2x4, 8),
	DMAT3X2(GL_DOUBLE_MAT3x2, 6),
	DMAT3X4(GL_DOUBLE_MAT3x4, 12),
	DMAT4X2(GL_DOUBLE_MAT4x2, 8),
	DMAT4X3(GL_DOUBLE_MAT4x3, 12),
	SAMPLER_1D(GL_SAMPLER_1D, 1),
	SAMPLER_2D(GL_SAMPLER_2D, 1),
	SAMPLER_3D(GL_SAMPLER_3D, 1),
	SAMPLER_CUBE(GL_SAMPLER_CUBE, 1),
	SAMPLER_1DSHADOW(GL_SAMPLER_1D_SHADOW, 1),
	SAMPLER_2DSHADOW(GL_SAMPLER_2D_SHADOW, 1),
	SAMPLER_1DARRAY(GL_SAMPLER_1D_ARRAY, 1),
	SAMPLER_2DARRAY(GL_SAMPLER_2D_ARRAY, 1),
	SAMPLER_1DARRAYSHADOW(GL_SAMPLER_1D_ARRAY_SHADOW, 1),
	SAMPLER_2DARRAYSHADOW(GL_SAMPLER_2D_ARRAY_SHADOW, 1),
	SAMPLER_2DMS(GL_SAMPLER_2D_MULTISAMPLE, 1),
	SAMPLER_2DMSARRAY(GL_SAMPLER_2D_MULTISAMPLE_ARRAY, 1),
	SAMPLER_CUBESHADOW(GL_SAMPLER_CUBE_SHADOW, 1),
	SAMPLER_BUFFER(GL_SAMPLER_BUFFER, 1),
	SAMPLER_2DRECT(GL_SAMPLER_2D_RECT, 1),
	SAMPLER_2DRECTSHADOW(GL_SAMPLER_2D_RECT_SHADOW, 1),
	ISAMPLER_1D(GL_INT_SAMPLER_1D, 1),
	ISAMPLER_2D(GL_INT_SAMPLER_2D, 1),
	ISAMPLER_3D(GL_INT_SAMPLER_3D, 1),
	ISAMPLER_CUBE(GL_INT_SAMPLER_CUBE, 1),
	ISAMPLER_1DARRAY(GL_INT_SAMPLER_1D_ARRAY, 1),
	ISAMPLER_2DARRAY(GL_INT_SAMPLER_2D_ARRAY, 1),
	ISAMPLER_2DMS(GL_INT_SAMPLER_2D_MULTISAMPLE, 1),
	ISAMPLER_2DMSARRAY(GL_INT_SAMPLER_2D_MULTISAMPLE_ARRAY, 1),
	ISAMPLER_BUFFER(GL_INT_SAMPLER_BUFFER, 1),
	ISAMPLER_2DRECT(GL_INT_SAMPLER_2D_RECT, 1),
	USAMPLER_1D(GL_UNSIGNED_INT_SAMPLER_1D, 1),
	USAMPLER_2D(GL_UNSIGNED_INT_SAMPLER_2D, 1),
	USAMPLER_3D(GL_UNSIGNED_INT_SAMPLER_3D, 1),
	USAMPLER_CUBE(GL_UNSIGNED_INT_SAMPLER_CUBE, 1),
	USAMPLER_1DARRAY(GL_UNSIGNED_INT_SAMPLER_1D_ARRAY, 1),
	USAMPLER_2DARRAY(GL_UNSIGNED_INT_SAMPLER_2D_ARRAY, 1),
	USAMPLER_2DMS(GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE, 1),
	USAMPLER_2DMSARRAY(GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY, 1),
	USAMPLER_BUFFER(GL_UNSIGNED_INT_SAMPLER_BUFFER, 1),
	USAMPLER_2DRECT(GL_UNSIGNED_INT_SAMPLER_2D_RECT, 1),
	IMAGE_1D(GL_IMAGE_1D, 1),
	IMAGE_2D(GL_IMAGE_2D, 1),
	IMAGE_3D(GL_IMAGE_3D, 1),
	IMAGE_2DRECT(GL_IMAGE_2D_RECT, 1),
	IMAGE_CUBE(GL_IMAGE_CUBE, 1),
	IMAGE_BUFFER(GL_IMAGE_BUFFER, 1),
	IMAGE_1DARRAY(GL_IMAGE_1D_ARRAY, 1),
	IMAGE_2DARRAY(GL_IMAGE_2D_ARRAY, 1),
	IMAGE_2DMS(GL_IMAGE_2D_MULTISAMPLE, 1),
	IMAGE_2DMSARRAY(GL_IMAGE_2D_MULTISAMPLE_ARRAY, 1),
	IIMAGE_1D(GL_INT_IMAGE_1D, 1),
	IIMAGE_2D(GL_INT_IMAGE_2D, 1),
	IIMAGE_3D(GL_INT_IMAGE_3D, 1),
	IIMAGE_2DRECT(GL_INT_IMAGE_2D_RECT, 1),
	IIMAGE_CUBE(GL_INT_IMAGE_CUBE, 1),
	IIMAGE_BUFFER(GL_INT_IMAGE_BUFFER, 1),
	IIMAGE_1DARRAY(GL_INT_IMAGE_1D_ARRAY, 1),
	IIMAGE_2DARRAY(GL_INT_IMAGE_2D_ARRAY, 1),
	IIMAGE_2DMS(GL_INT_IMAGE_2D_MULTISAMPLE, 1),
	IIMAGE_2DMSARRAY(GL_INT_IMAGE_2D_MULTISAMPLE_ARRAY, 1),
	UIMAGE_1D(GL_UNSIGNED_INT_IMAGE_1D, 1),
	UIMAGE_2D(GL_UNSIGNED_INT_IMAGE_2D, 1),
	UIMAGE_3D(GL_UNSIGNED_INT_IMAGE_3D, 1),
	UIMAGE_2DRECT(GL_UNSIGNED_INT_IMAGE_2D_RECT, 1),
	UIMAGE_CUBE(GL_UNSIGNED_INT_IMAGE_CUBE, 1),
	UIMAGE_BUFFER(GL_UNSIGNED_INT_IMAGE_BUFFER, 1),
	UIMAGE_1DARRAY(GL_UNSIGNED_INT_IMAGE_1D_ARRAY, 1),
	UIMAGE_2DARRAY(GL_UNSIGNED_INT_IMAGE_2D_ARRAY, 1),
	UIMAGE_2DMS(GL_UNSIGNED_INT_IMAGE_2D_MULTISAMPLE, 1),
	UIMAGE_2DMSARRAY(GL_UNSIGNED_INT_IMAGE_2D_MULTISAMPLE_ARRAY, 1),
	ATOMIC_UINT(GL_UNSIGNED_INT_ATOMIC_COUNTER, 1);

	public final int value, size;
	
	private UniformType(int type, int size){
		value = type;
		this.size = size;
	}
	
	/**
	 * Gets the UniformType enum based on the given integer returned by calls to the GL
	 * 
	 * @param type Integer representing a uniform value from the GL
	 * @return UniformType representing the given type
	 */
	public static UniformType getType(int type){
		for(UniformType value : UniformType.values()){
			if(value.value == type){
				return value;
			}
		}
		return null;
	}
	
	/**
	 * Determines whether the this UniformType is a float type
	 * 
	 * @return True if the uniform type is a float, false otherwise
	 */
	public boolean isFloat(){
		return
			this == FLOAT ||
			this == VEC2 ||
			this == VEC3 ||
			this == VEC4;
	}

	/**
	 * Determines whether the this UniformType is an integer type
	 * 
	 * @return True if the uniform type is an integer, false otherwise
	 */
	public boolean isInt(){
		return
			this == INT ||
			this == IVEC2 ||
			this == IVEC3 ||
			this == IVEC4 ||
			this == UINT ||
			this == UVEC2 ||
			this == UVEC3 ||
			this == UVEC4 ||
			this == BOOL ||
			this == BVEC2 ||
			this == BVEC3 ||
			this == BVEC4 ||
			this == SAMPLER_1D ||
			this == SAMPLER_2D ||
			this == SAMPLER_3D ||
			this == SAMPLER_CUBE ||
			this == SAMPLER_1DSHADOW ||
			this == SAMPLER_2DSHADOW ||
			this == SAMPLER_1DARRAY ||
			this == SAMPLER_2DARRAY ||
			this == SAMPLER_1DARRAYSHADOW ||
			this == SAMPLER_2DARRAYSHADOW ||
			this == SAMPLER_2DMS ||
			this == SAMPLER_2DMSARRAY ||
			this == SAMPLER_CUBESHADOW ||
			this == SAMPLER_BUFFER ||
			this == SAMPLER_2DRECT ||
			this == SAMPLER_2DRECTSHADOW ||
			this == ISAMPLER_1D ||
			this == ISAMPLER_2D ||
			this == ISAMPLER_3D ||
			this == ISAMPLER_CUBE ||
			this == ISAMPLER_1DARRAY ||
			this == ISAMPLER_2DARRAY ||
			this == ISAMPLER_2DMS ||
			this == ISAMPLER_2DMSARRAY ||
			this == ISAMPLER_BUFFER ||
			this == ISAMPLER_2DRECT ||
			this == USAMPLER_1D ||
			this == USAMPLER_2D ||
			this == USAMPLER_3D ||
			this == USAMPLER_CUBE ||
			this == USAMPLER_1DARRAY ||
			this == USAMPLER_2DARRAY ||
			this == USAMPLER_2DMS ||
			this == USAMPLER_2DMSARRAY ||
			this == USAMPLER_BUFFER ||
			this == USAMPLER_2DRECT ||
			this == IMAGE_1D ||
			this == IMAGE_2D ||
			this == IMAGE_3D ||
			this == IMAGE_2DRECT ||
			this == IMAGE_CUBE ||
			this == IMAGE_BUFFER ||
			this == IMAGE_1DARRAY ||
			this == IMAGE_2DARRAY ||
			this == IMAGE_2DMS ||
			this == IMAGE_2DMSARRAY ||
			this == IIMAGE_1D ||
			this == IIMAGE_2D ||
			this == IIMAGE_3D ||
			this == IIMAGE_2DRECT ||
			this == IIMAGE_CUBE ||
			this == IIMAGE_BUFFER ||
			this == IIMAGE_1DARRAY ||
			this == IIMAGE_2DARRAY ||
			this == IIMAGE_2DMS ||
			this == IIMAGE_2DMSARRAY ||
			this == UIMAGE_1D ||
			this == UIMAGE_2D ||
			this == UIMAGE_3D ||
			this == UIMAGE_2DRECT ||
			this == UIMAGE_CUBE ||
			this == UIMAGE_BUFFER ||
			this == UIMAGE_1DARRAY ||
			this == UIMAGE_2DARRAY ||
			this == UIMAGE_2DMS ||
			this == UIMAGE_2DMSARRAY;	
	}

	/**
	 * Determines whether the this UniformType is a matrix type
	 * 
	 * @return True if the uniform type is a matrix, false otherwise
	 */
	public boolean isMatrix(){
		return
			this == MAT2 ||
			this == MAT3 ||
			this == MAT4 ||
			this == MAT2X3 ||
			this == MAT2X4 ||
			this == MAT3X2 ||
			this == MAT3X4 ||
			this == MAT4X2 ||
			this == MAT4X3 ||
			this == DMAT2 ||
			this == DMAT3 ||
			this == DMAT4 ||
			this == DMAT2X3 ||
			this == DMAT2X4 ||
			this == DMAT3X2 ||
			this == DMAT3X4 ||
			this == DMAT4X2 ||
			this == DMAT4X3;
	}

	/**
	 * Determines whether the this UniformType is a sampler type
	 * 
	 * @return True if the uniform type is a sampler, false otherwise
	 */
	public boolean isSampler(){
		return
			this == SAMPLER_1D ||
			this == SAMPLER_2D ||
			this == SAMPLER_3D ||
			this == SAMPLER_CUBE ||
			this == SAMPLER_1DSHADOW ||
			this == SAMPLER_2DSHADOW ||
			this == SAMPLER_1DARRAY ||
			this == SAMPLER_2DARRAY ||
			this == SAMPLER_1DARRAYSHADOW ||
			this == SAMPLER_2DARRAYSHADOW ||
			this == SAMPLER_2DMS ||
			this == SAMPLER_2DMSARRAY ||
			this == SAMPLER_CUBESHADOW ||
			this == SAMPLER_BUFFER ||
			this == SAMPLER_2DRECT ||
			this == SAMPLER_2DRECTSHADOW ||
			this == ISAMPLER_1D ||
			this == ISAMPLER_2D ||
			this == ISAMPLER_3D ||
			this == ISAMPLER_CUBE ||
			this == ISAMPLER_1DARRAY ||
			this == ISAMPLER_2DARRAY ||
			this == ISAMPLER_2DMS ||
			this == ISAMPLER_2DMSARRAY ||
			this == ISAMPLER_BUFFER ||
			this == ISAMPLER_2DRECT ||
			this == USAMPLER_1D ||
			this == USAMPLER_2D ||
			this == USAMPLER_3D ||
			this == USAMPLER_CUBE ||
			this == USAMPLER_1DARRAY ||
			this == USAMPLER_2DARRAY ||
			this == USAMPLER_2DMS ||
			this == USAMPLER_2DMSARRAY ||
			this == USAMPLER_BUFFER ||
			this == USAMPLER_2DRECT;
	}

	/**
	 * Determines whether the this UniformType is an image type
	 * 
	 * @return True if the uniform type is an image, false otherwise
	 */
	public boolean isImage(){
		return
			this == IMAGE_1D ||
			this == IMAGE_2D ||
			this == IMAGE_3D ||
			this == IMAGE_2DRECT ||
			this == IMAGE_CUBE ||
			this == IMAGE_BUFFER ||
			this == IMAGE_1DARRAY ||
			this == IMAGE_2DARRAY ||
			this == IMAGE_2DMS ||
			this == IMAGE_2DMSARRAY ||
			this == IIMAGE_1D ||
			this == IIMAGE_2D ||
			this == IIMAGE_3D ||
			this == IIMAGE_2DRECT ||
			this == IIMAGE_CUBE ||
			this == IIMAGE_BUFFER ||
			this == IIMAGE_1DARRAY ||
			this == IIMAGE_2DARRAY ||
			this == IIMAGE_2DMS ||
			this == IIMAGE_2DMSARRAY ||
			this == UIMAGE_1D ||
			this == UIMAGE_2D ||
			this == UIMAGE_3D ||
			this == UIMAGE_2DRECT ||
			this == UIMAGE_CUBE ||
			this == UIMAGE_BUFFER ||
			this == UIMAGE_1DARRAY ||
			this == UIMAGE_2DARRAY ||
			this == UIMAGE_2DMS ||
			this == UIMAGE_2DMSARRAY;	
	}
}
