package shaders;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.opengl.GL43.*;

public enum ShaderStage {
	COMPUTE(GL_COMPUTE_SHADER),
	VERTEX(GL_VERTEX_SHADER),
	TESS_CONTROL(GL_TESS_CONTROL_SHADER),
	TESS_EVAL(GL_TESS_EVALUATION_SHADER),
	GEO(GL_GEOMETRY_SHADER),
	FRAG(GL_FRAGMENT_SHADER);
	
	public final int type;
	
	private ShaderStage(int type){
		this.type = type;
	}
	
	public int getUseBit(){
		switch(this){
			case COMPUTE:
				return GL_COMPUTE_SHADER_BIT;
			case FRAG:
				return GL_FRAGMENT_SHADER_BIT;
			case GEO:
				return GL_GEOMETRY_SHADER_BIT;
			case TESS_CONTROL:
				return GL_TESS_CONTROL_SHADER_BIT;
			case TESS_EVAL:
				return GL_TESS_EVALUATION_SHADER_BIT;
			case VERTEX:
				return GL_VERTEX_SHADER_BIT;
			default:
				return GL_ALL_SHADER_BITS;
			
		}
	}
	
	/**
	 * Gets the shader stage enums associated with the bitmask {@code useBits}
	 * 
	 * @param useBits Bitmask of bits corresponding to different shader stages
	 * 
	 * @return Array containing all the shader stages specified by the bitmask useBits
	 */
//	public static ShaderStage[] getStages(int useBits){
//		
//	}
}
