package shaders;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL41.glProgramParameteri;
import static org.lwjgl.opengl.GL41.glCreateShaderProgramv;
import glMath.matrices.Mat2;
import glMath.matrices.Mat3;
import glMath.matrices.Matrix;
import glMath.vectors.Vec2;
import glMath.vectors.Vec3;
import glMath.vectors.Vec4;
import glMath.vectors.Vector;
import gldata.BufferObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import shaders.data.ShaderData;

public class ShaderProgram {
	private int programId, stageBits;
	private ArrayList<Shader> shaders;
	private boolean isSeparable;
	private ShaderData shaderData;

	public ShaderProgram(String file, ShaderStage stage){
		this(new File(file), stage);
	}
	
	public ShaderProgram(File file, ShaderStage stage){
		shaders = new ArrayList<Shader>();
		programId = glCreateShaderProgramv(stage.type, Shader.getSource(file));
		shaderData = new ShaderData(programId);
		shaderData.introspect();
		isSeparable = true;
		stageBits = stage.getUseBit();
	}
	
	/**
	 * Creates a shader program object on the GPU
	 */
	public ShaderProgram(){
		shaders = new ArrayList<Shader>();
		programId = glCreateProgram();
		shaderData = new ShaderData(programId);
		isSeparable = false;
		stageBits = 0;
	}
	
	/**
	 * Binds this shader program to the opengl context
	 */
	public void bind(){
		glUseProgram(programId);
	}
	
	/**
	 * Unbinds this shader program from the opengl context
	 */
	public void unbind(){
		glUseProgram(0);
	}
	
	/**
	 * Deletes this shader program object from the GPU
	 */
	public void delete(){
		glUseProgram(0);
		glDeleteProgram(programId);
	}
	
	/**
	 * Attaches the specified shader object to this shader program if the shader has been successfully compiled
	 * 
	 * @param shader Shader object to attach to this shader program
	 * @return True if the shader was successfully attached to this shader program
	 */
	public boolean attach(Shader shader){
		if (shader.isCompiled()) {
			shader.attach(programId);
			shaders.add(shader);
			return true;
		}
		return false;
	}
	
	/**
	 * Detaches the specified shader object from this shader program
	 * 
	 * @param shader Shader object to detach
	 */
	public void detach(Shader shader){
		shader.detach(programId);
		shaders.remove(shader);
	}
	
	/**
	 * Sets the parameter specified by {@code param} to the given boolean {@code value}
	 * 
	 * @param param Parameter of the shader program to set
	 * @param value Value to set the parameter to
	 */
	public void setParam(ProgramParam param, boolean value){
		glProgramParameteri(programId, param.value, value ? GL_TRUE : GL_FALSE);
		//check to make sure separable can be set
		if(param == ProgramParam.SEPARABLE && glGetProgrami(programId, GL_LINK_STATUS) != GL_TRUE){
			isSeparable = value;
		}
	}
	
	/**
	 * Gets whether of not this shader program is separable and therefore compatible with shader pipelines
	 * 
	 * @return True if the shader has been set to separable, false otherwise
	 */
	public boolean isSeparable(){
		return isSeparable;
	}
	
	/**
	 * Gets the shader stages of this program as a bit mask for use with program pipelines
	 * 
	 * @return Integer representing a bit mask of shader stages this shader program is linked for use with
	 */
	public int getShaderStages(){
		return stageBits;
	}
	
	public boolean isLinked(){
		return glGetProgrami(programId, GL_LINK_STATUS) == GL_TRUE;
	}
	
	/**
	 * Links this shader program on the GPU and generates the binary executables for the GPU to run.
	 * Additionally any shaders are detached from the program after linking is successful
	 * 
	 * @return True if the linking was successful, false otherwise
	 */
	public boolean link(){
		glLinkProgram(programId);
		//if we linked successfully add the uniforms to the map and fix any that were unknown while parsing a single shader file
		if(glGetProgrami(programId, GL_LINK_STATUS) == GL_TRUE){
			//iterate and add uniforms
			for(Shader shader : shaders){
				stageBits |= shader.getShaderStage().getUseBit();
				shader.detach(programId);
			}
			shaders.clear();
			shaderData.introspect();
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Gets the info log associated with this shader program, if the info log is empty this
	 * will return an empty String
	 * 
	 * @return Info log for this shader program
	 */
	public String getInfoLog(){
		return glGetProgramInfoLog(programId);
	}
	
	public int getId(){
		return programId;
	}
	
	/**
	 * Sets the uniform specified by the given name, and sets it to the value of data,
	 * if the uniform cannot be found then this function returns false
	 * 
	 * @param uniformName Name of the uniform to modify
	 * @param data Buffer containing the data to set the uniform to
	 * @return True if the uniform was found and set to the given data
	 */
//	public boolean setUniform(String uniformName, ByteBuffer data){
//		Uniform found = shaderData.getUniform(uniformName);
//		if(found != null){
//			return found.set(programId, false, data);
//		}else{
//			return false;
//		}
//	}
	
	/**
	 * Sets the uniform specified by the given name, and sets it to the value of variables.
	 * if the uniform cannot be found then this function returns false.
	 * 
	 * if the data given is smaller than the actual size of the uniform being set then the
	 * data is padded with values of 0.
	 * 
	 * if the data given is larger than the size of the uniform being set then the data is
	 * truncated before being passed to the GPU.
	 * 
	 * @param uniformName Name of the uniform to modify
	 * @param variables Arbitrarily sized data to set the uniform to
	 * @return True if the uniform was found and set to the given data
	 */
	public boolean setUniform(String uniformName, float... variables){
		Uniform found = shaderData.getUniform(uniformName);
		if(found != null){
			return found.set(programId, false, variables);
		}else{
			return false;
		}
	}

	/**
	 * Sets the uniform specified by the given name, and sets it to the value of variables.
	 * if the uniform cannot be found then this function returns false.
	 * 
	 * if the data given is smaller than the actual size of the uniform being set then the
	 * data is padded with values of 0.
	 * 
	 * if the data given is larger than the size of the uniform being set then the data is
	 * truncated before being passed to the GPU.
	 * 
	 * @param uniformName Name of the uniform to modify
	 * @param variables Arbitrarily sized data to set the uniform to
	 * @return True if the uniform was found and set to the given data
	 */
	public boolean setUniform(String uniformName, int... variables){
		Uniform found = shaderData.getUniform(uniformName);
		if(found != null){
			return found.set(programId, false, variables);
		}else{
			return false;
		}
	}

	/**
	 * Sets the uniform specified by the given name, and sets it to the value of variables.
	 * if the uniform cannot be found then this function returns false.
	 * 
	 * if the data given is smaller than the actual size of the uniform being set then the
	 * data is padded with values of 0.
	 * 
	 * if the data given is larger than the size of the uniform being set then the data is
	 * truncated before being passed to the GPU.
	 * 
	 * @param uniformName Name of the uniform to modify
	 * @param variables Arbitrarily sized data to set the uniform to
	 * @return True if the uniform was found and set to the given data
	 */
	public boolean setUniform(String uniformName, boolean... variables){
		Uniform found = shaderData.getUniform(uniformName);
		if(found != null){
			return found.set(programId, variables);
		}else{
			return false;
		}
	}
	
	/**
	 * Sets a vector uniform to the given vector
	 * 
	 * @param uniformName Name of uniform to modify
	 * @param value Vector to set the uniform with
	 * @return True if the uniform was found and set to the given data
	 */
	public boolean setUniform(String uniformName, Vector value){
		Uniform found = shaderData.getUniform(uniformName);
		if(found != null){
			if(value instanceof Vec2){
				Vec2 castVal = (Vec2) value;
				return found.set(programId, false, castVal.x, castVal.y);
			}else if(value instanceof Vec3){
				Vec3 castVal = (Vec3) value;
				return found.set(programId, false, castVal.x, castVal.y, castVal.z);
			}else{
				Vec4 castVal = (Vec4) value;
				return found.set(programId, false, castVal.x, castVal.y, castVal.z, castVal.w);
			}
		}else{
			return false;
		}
	}
	
	/**
	 * Sets a matrix uniform specified by the given name
	 * 
	 * @param uniformName Name of uniform to modify
	 * @param value Matrix to set the uniform to
	 * @return True if the uniform was found and set to the given data
	 */
	public boolean setUniform(String uniformName, Matrix value){
		Uniform found = shaderData.getUniform(uniformName);
		if(found != null){
			if(value instanceof Mat2){
				return found.set(programId, false, 
						value.valueAt(0), value.valueAt(1),
						value.valueAt(2), value.valueAt(3)
						);
			}else if(value instanceof Mat3){
				return found.set(programId, false, 
						value.valueAt(0), value.valueAt(1), value.valueAt(2), 
						value.valueAt(3), value.valueAt(4), value.valueAt(5), 
						value.valueAt(6), value.valueAt(7), value.valueAt(8)
						);
			}else{
				return found.set(programId, false, 
						value.valueAt(0), value.valueAt(1), value.valueAt(2), value.valueAt(3),
						value.valueAt(4), value.valueAt(5), value.valueAt(6), value.valueAt(7),
						value.valueAt(8), value.valueAt(9), value.valueAt(10), value.valueAt(11),
						value.valueAt(12), value.valueAt(13), value.valueAt(14), value.valueAt(15)
						);
			}
		}else{
			return false;
		}
	}
	
	/**
	 * Sets a matrix uniform specified by the given name
	 * 
	 * @param uniformName Name of uniform to modify
	 * @param transpose Whether to transpose the data as it is being passed to the GPU
	 * @param value Matrix to set the uniform to
	 * @return True if the uniform was found and set to the given data
	 */
	public boolean setUniform(String uniformName, boolean transpose, Matrix value){
		Uniform found = shaderData.getUniform(uniformName);
		if(found != null){
			if(value instanceof Mat2){
				return found.set(programId, transpose, 
						value.valueAt(0), value.valueAt(1),
						value.valueAt(2), value.valueAt(3)
						);
			}else if(value instanceof Mat3){
				return found.set(programId, transpose, 
						value.valueAt(0), value.valueAt(1), value.valueAt(2), 
						value.valueAt(3), value.valueAt(4), value.valueAt(5), 
						value.valueAt(6), value.valueAt(7), value.valueAt(8)
						);
			}else{
				return found.set(programId, transpose, 
						value.valueAt(0), value.valueAt(1), value.valueAt(2), value.valueAt(3),
						value.valueAt(4), value.valueAt(5), value.valueAt(6), value.valueAt(7),
						value.valueAt(8), value.valueAt(9), value.valueAt(10), value.valueAt(11),
						value.valueAt(12), value.valueAt(13), value.valueAt(14), value.valueAt(15)
						);
			}
		}else{
			return false;
		}
	}
	
	/**
	 * 
	 * @param uniformName Name of uniform to bind this buffer to
	 * @param buffer Buffer to bind to the uniform
	 * @return True if successfully bound, false otherwise
	 */
//	public boolean bindBuffer(String uniformName, BufferObject buffer){
//		Uniform found = shaderData.getUniform(uniformName);
//		if(found != null){
//			return found.bindBuffer(buffer);
//		}else{
//			return false;
//		}
//	}
//	
//	public boolean bindBuffer(String uniformName, BufferObject buffer, int startOffset){
//		Uniform found = shaderData.getUniform(uniformName);
//		if(found != null){
//			return found.bindBuffer(buffer, startOffset);
//		}else{
//			return false;
//		}
//	}
//	
//	public boolean bindBuffer(String uniformName, BufferObject buffer, int startOffset, int readSize){
//		Uniform found = shaderData.getUniform(uniformName);
//		if(found != null){
//			return found.bindBuffer(buffer, startOffset, readSize);
//		}else{
//			return false;
//		}
//	}
}
