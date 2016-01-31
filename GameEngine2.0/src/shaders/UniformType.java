package shaders;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL42.*;

public enum UniformType {
	FLOAT(GL_FLOAT),
	VEC2(GL_FLOAT_VEC2),
	VEC3(GL_FLOAT_VEC3),
	VEC4(GL_FLOAT_VEC4),
	DOUBLE(GL_DOUBLE),
	DVEC2(GL_DOUBLE_VEC2),
	DVEC3(GL_DOUBLE_VEC3),
	DVEC4(GL_DOUBLE_VEC4),
	INT(GL_INT),
	IVEC2(GL_INT_VEC2),
	IVEC3(GL_INT_VEC3),
	IVEC4(GL_INT_VEC4),
	UINT(GL_UNSIGNED_INT),
	UVEC2(GL_UNSIGNED_INT_VEC2),
	UVEC3(GL_UNSIGNED_INT_VEC3),
	UVEC4(GL_UNSIGNED_INT_VEC4),
	BOOL(GL_BOOL),
	BVEC2(GL_BOOL_VEC2),
	BVEC3(GL_BOOL_VEC3),
	BVEC4(GL_BOOL_VEC4),
	MAT2(GL_FLOAT_MAT2),
	MAT3(GL_FLOAT_MAT3),
	MAT4(GL_FLOAT_MAT4),
	MAT2X3(GL_FLOAT_MAT2x3),
	MAT2X4(GL_FLOAT_MAT2x4),
	MAT3X2(GL_FLOAT_MAT3x2),
	MAT3X4(GL_FLOAT_MAT3x4),
	MAT4X2(GL_FLOAT_MAT4x2),
	MAT4X3(GL_FLOAT_MAT4x3),
	DMAT2(GL_DOUBLE_MAT2),
	DMAT3(GL_DOUBLE_MAT3),
	DMAT4(GL_DOUBLE_MAT4),
	DMAT2X3(GL_DOUBLE_MAT2x3),
	DMAT2X4(GL_DOUBLE_MAT2x4),
	DMAT3X2(GL_DOUBLE_MAT3x2),
	DMAT3X4(GL_DOUBLE_MAT3x4),
	DMAT4X2(GL_DOUBLE_MAT4x2),
	DMAT4X3(GL_DOUBLE_MAT4x3),
	SAMPLER_1D(GL_SAMPLER_1D),
	SAMPLER_2D(GL_SAMPLER_2D),
	SAMPLER_3D(GL_SAMPLER_3D),
	SAMPLER_CUBE(GL_SAMPLER_CUBE),
	SAMPLER_1DSHADOW(GL_SAMPLER_1D_SHADOW),
	SAMPLER_2DSHADOW(GL_SAMPLER_2D_SHADOW),
	SAMPLER_1DARRAY(GL_SAMPLER_1D_ARRAY),
	SAMPLER_2DARRAY(GL_SAMPLER_2D_ARRAY),
	SAMPLER_1DARRAYSHADOW(GL_SAMPLER_1D_ARRAY_SHADOW),
	SAMPLER_2DARRAYSHADOW(GL_SAMPLER_2D_ARRAY_SHADOW),
	SAMPLER_2DMS(GL_SAMPLER_2D_MULTISAMPLE),
	SAMPLER_2DMSARRAY(GL_SAMPLER_2D_MULTISAMPLE_ARRAY),
	SAMPLER_CUBESHADOW(GL_SAMPLER_CUBE_SHADOW),
	SAMPLER_BUFFER(GL_SAMPLER_BUFFER),
	SAMPLER_2DRECT(GL_SAMPLER_2D_RECT),
	SAMPLER_2DRECTSHADOW(GL_SAMPLER_2D_RECT_SHADOW),
	ISAMPLER_1D(GL_INT_SAMPLER_1D),
	ISAMPLER_2D(GL_INT_SAMPLER_2D),
	ISAMPLER_3D(GL_INT_SAMPLER_3D),
	ISAMPLER_CUBE(GL_INT_SAMPLER_CUBE),
	ISAMPLER_1DARRAY(GL_INT_SAMPLER_1D_ARRAY),
	ISAMPLER_2DARRAY(GL_INT_SAMPLER_2D_ARRAY),
	ISAMPLER_2DMS(GL_INT_SAMPLER_2D_MULTISAMPLE),
	ISAMPLER_2DMSARRAY(GL_INT_SAMPLER_2D_MULTISAMPLE_ARRAY),
	ISAMPLER_BUFFER(GL_INT_SAMPLER_BUFFER),
	ISAMPLER_2DRECT(GL_INT_SAMPLER_2D_RECT),
	USAMPLER_1D(GL_UNSIGNED_INT_SAMPLER_1D),
	USAMPLER_2D(GL_UNSIGNED_INT_SAMPLER_2D),
	USAMPLER_3D(GL_UNSIGNED_INT_SAMPLER_3D),
	USAMPLER_CUBE(GL_UNSIGNED_INT_SAMPLER_CUBE),
	USAMPLER_1DARRAY(GL_UNSIGNED_INT_SAMPLER_1D_ARRAY),
	USAMPLER_2DARRAY(GL_UNSIGNED_INT_SAMPLER_2D_ARRAY),
	USAMPLER_2DMS(GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE),
	USAMPLER_2DMSARRAY(GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY),
	USAMPLER_BUFFER(GL_UNSIGNED_INT_SAMPLER_BUFFER),
	USAMPLER_2DRECT(GL_UNSIGNED_INT_SAMPLER_2D_RECT),
	IMAGE_1D(GL_IMAGE_1D),
	IMAGE_2D(GL_IMAGE_2D),
	IMAGE_3D(GL_IMAGE_3D),
	IMAGE_2DRECT(GL_IMAGE_2D_RECT),
	IMAGE_CUBE(GL_IMAGE_CUBE),
	IMAGE_BUFFER(GL_IMAGE_BUFFER),
	IMAGE_1DARRAY(GL_IMAGE_1D_ARRAY),
	IMAGE_2DARRAY(GL_IMAGE_2D_ARRAY),
	IMAGE_2DMS(GL_IMAGE_2D_MULTISAMPLE),
	IMAGE_2DMSARRAY(GL_IMAGE_2D_MULTISAMPLE_ARRAY),
	IIMAGE_1D(GL_INT_IMAGE_1D),
	IIMAGE_2D(GL_INT_IMAGE_2D),
	IIMAGE_3D(GL_INT_IMAGE_3D),
	IIMAGE_2DRECT(GL_INT_IMAGE_2D_RECT),
	IIMAGE_CUBE(GL_INT_IMAGE_CUBE),
	IIMAGE_BUFFER(GL_INT_IMAGE_BUFFER),
	IIMAGE_1DARRAY(GL_INT_IMAGE_1D_ARRAY),
	IIMAGE_2DARRAY(GL_INT_IMAGE_2D_ARRAY),
	IIMAGE_2DMS(GL_INT_IMAGE_2D_MULTISAMPLE),
	IIMAGE_2DMSARRAY(GL_INT_IMAGE_2D_MULTISAMPLE_ARRAY),
	UIMAGE_1D(GL_UNSIGNED_INT_IMAGE_1D),
	UIMAGE_2D(GL_UNSIGNED_INT_IMAGE_2D),
	UIMAGE_3D(GL_UNSIGNED_INT_IMAGE_3D),
	UIMAGE_2DRECT(GL_UNSIGNED_INT_IMAGE_2D_RECT),
	UIMAGE_CUBE(GL_UNSIGNED_INT_IMAGE_CUBE),
	UIMAGE_BUFFER(GL_UNSIGNED_INT_IMAGE_BUFFER),
	UIMAGE_1DARRAY(GL_UNSIGNED_INT_IMAGE_1D_ARRAY),
	UIMAGE_2DARRAY(GL_UNSIGNED_INT_IMAGE_2D_ARRAY),
	UIMAGE_2DMS(GL_UNSIGNED_INT_IMAGE_2D_MULTISAMPLE),
	UIMAGE_2DMSARRAY(GL_UNSIGNED_INT_IMAGE_2D_MULTISAMPLE_ARRAY),
	ATOMIC_UINT(GL_UNSIGNED_INT_ATOMIC_COUNTER);

	public final int value;
	
	private UniformType(int type){
		value = type;
	}
	
	public int getSize(){
		switch(this){
			case ATOMIC_UINT:
				return 1;
			case BOOL:
				return 1;
			case BVEC2:
				return 2;
			case BVEC3:
				return 3;
			case BVEC4:
				return 4;
			case DMAT2:
				return 4;
			case DMAT2X3:
				return 6;
			case DMAT2X4:
				return 8;
			case DMAT3:
				return 9;
			case DMAT3X2:
				return 6;
			case DMAT3X4:
				return 12;
			case DMAT4:
				return 16;
			case DMAT4X2:
				return 8;
			case DMAT4X3:
				return 12;
			case DOUBLE:
				return 1;
			case DVEC2:
				return 2;
			case DVEC3:
				return 3;
			case DVEC4:
				return 4;
			case FLOAT:
				return 1;
			case IIMAGE_1D:
				return 1;
			case IIMAGE_1DARRAY:
				return 1;
			case IIMAGE_2D:
				return 1;
			case IIMAGE_2DARRAY:
				return 1;
			case IIMAGE_2DMS:
				return 1;
			case IIMAGE_2DMSARRAY:
				return 1;
			case IIMAGE_2DRECT:
				return 1;
			case IIMAGE_3D:
				return 1;
			case IIMAGE_BUFFER:
				return 1;
			case IIMAGE_CUBE:
				return 1;
			case IMAGE_1D:
				return 1;
			case IMAGE_1DARRAY:
				return 1;
			case IMAGE_2D:
				return 1;
			case IMAGE_2DARRAY:
				return 1;
			case IMAGE_2DMS:
				return 1;
			case IMAGE_2DMSARRAY:
				return 1;
			case IMAGE_2DRECT:
				return 1;
			case IMAGE_3D:
				return 1;
			case IMAGE_BUFFER:
				return 1;
			case IMAGE_CUBE:
				return 1;
			case INT:
				return 1;
			case ISAMPLER_1D:
				return 1;
			case ISAMPLER_1DARRAY:
				return 1;
			case ISAMPLER_2D:
				return 1;
			case ISAMPLER_2DARRAY:
				return 1;
			case ISAMPLER_2DMS:
				return 1;
			case ISAMPLER_2DMSARRAY:
				return 1;
			case ISAMPLER_2DRECT:
				return 1;
			case ISAMPLER_3D:
				return 1;
			case ISAMPLER_BUFFER:
				return 1;
			case ISAMPLER_CUBE:
				return 1;
			case IVEC2:
				return 2;
			case IVEC3:
				return 3;
			case IVEC4:
				return 4;
			case MAT2:
				return 4;
			case MAT2X3:
				return 6;
			case MAT2X4:
				return 8;
			case MAT3:
				return 9;
			case MAT3X2:
				return 6;
			case MAT3X4:
				return 12;
			case MAT4:
				return 16;
			case MAT4X2:
				return 8;
			case MAT4X3:
				return 12;
			case SAMPLER_1D:
				return 1;
			case SAMPLER_1DARRAY:
				return 1;
			case SAMPLER_1DARRAYSHADOW:
				return 1;
			case SAMPLER_1DSHADOW:
				return 1;
			case SAMPLER_2D:
				return 1;
			case SAMPLER_2DARRAY:
				return 1;
			case SAMPLER_2DARRAYSHADOW:
				return 1;
			case SAMPLER_2DMS:
				return 1;
			case SAMPLER_2DMSARRAY:
				return 1;
			case SAMPLER_2DRECT:
				return 1;
			case SAMPLER_2DRECTSHADOW:
				return 1;
			case SAMPLER_2DSHADOW:
				return 1;
			case SAMPLER_3D:
				return 1;
			case SAMPLER_BUFFER:
				return 1;
			case SAMPLER_CUBE:
				return 1;
			case SAMPLER_CUBESHADOW:
				return 1;
			case UIMAGE_1D:
				return 1;
			case UIMAGE_1DARRAY:
				return 1;
			case UIMAGE_2D:
				return 1;
			case UIMAGE_2DARRAY:
				return 1;
			case UIMAGE_2DMS:
				return 1;
			case UIMAGE_2DMSARRAY:
				return 1;
			case UIMAGE_2DRECT:
				return 1;
			case UIMAGE_3D:
				return 1;
			case UIMAGE_BUFFER:
				return 1;
			case UIMAGE_CUBE:
				return 1;
			case UINT:
				return 1;
			case USAMPLER_1D:
				return 1;
			case USAMPLER_1DARRAY:
				return 1;
			case USAMPLER_2D:
				return 1;
			case USAMPLER_2DARRAY:
				return 1;
			case USAMPLER_2DMS:
				return 1;
			case USAMPLER_2DMSARRAY:
				return 1;
			case USAMPLER_2DRECT:
				return 1;
			case USAMPLER_3D:
				return 1;
			case USAMPLER_BUFFER:
				return 1;
			case USAMPLER_CUBE:
				return 1;
			case UVEC2:
				return 2;
			case UVEC3:
				return 3;
			case UVEC4:
				return 4;
			case VEC2:
				return 2;
			case VEC3:
				return 3;
			case VEC4:
				return 4;
			default:
				return 0;
		
		}
	}
	
	public static UniformType getType(int type){
		for(UniformType value : UniformType.values()){
			if(value.value == type){
				return value;
			}
		}
		return null;
	}
	
	public boolean isFloat(){
		return
			this == FLOAT ||
			this == VEC2 ||
			this == VEC3 ||
			this == VEC4;
	}
	
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
