package shaders;

import static org.lwjgl.opengl.GL41.*;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

public class Uniform {
	private int location;
	public UniformType type;
	private ByteBuffer dataBuffer;
	
	/**
	 * Constructs a uniform with the given {@code type} and program {@code location}.
	 * 
	 * @param type UniformType indicating the type of uniform this will represent
	 * @param location Uniform location in the shader program
	 */
	public Uniform(UniformType type, int location){
		this.type = type;
		this.location = location;
		dataBuffer = BufferUtils.createByteBuffer(4*type.size);
	}
	
	/**
	 * Gets the program binding index for this uniform after the getBinding function has been called
	 * 
	 * @return The location of this shader uniform 
	 */
	public int getLocation(){
		return location;
	}
	
	/**
	 * Gets the type that this uniform is associated with
	 * 
	 * @return The integral constant representing this uniform variables type
	 */
	public UniformType getType(){
		return type;
	}
	
	/**
	 * Sets the uniform to the value set by one of the other {@code set} functions.
	 * This function buffers the uniform bytebuffer to the GPU for processing the uniform.
	 * 
	 * @param program Shader program to set the uniform value of with this uniform
	 * @param transpose Whether or not to transpose the data as it is sent to the GPU,
	 * this is used when calling a set on a matrix type
	 */
	private void set(int program, boolean transpose){
		//set the buffers limit to be it's capacity, this will avoid buffer size expectations from the GL
		dataBuffer.limit(dataBuffer.capacity());
		if(type.isFloat()){
			switch(type.size){
				case 1:
					glProgramUniform1fv(program, location, 1, dataBuffer);
					break;
				case 2:
					glProgramUniform2fv(program, location, 1, dataBuffer);
					break;
				case 3:
					glProgramUniform3fv(program, location, 1, dataBuffer);
					break;
				case 4:
					glProgramUniform4fv(program, location, 1, dataBuffer);
					break;
			}
		}else if(type.isInt()){
			switch(type.size){
				case 1:
					glProgramUniform1iv(program, location, 1, dataBuffer);
					break;
				case 2:
					glProgramUniform2iv(program, location, 1, dataBuffer);
					break;
				case 3:
					glProgramUniform3iv(program, location, 1, dataBuffer);
					break;
				case 4:
					glProgramUniform4iv(program, location, 1, dataBuffer);
					break;
			}
		}else if(type.isMatrix()){
			this.setMat(program, transpose);
		}
	}
	
	public void set(int program, boolean transpose, float... variables){
		dataBuffer.position(0);
		for (int insert = 0; insert < Math.min(type.size, variables.length); insert++) {
			//determine if the type of the uniform is mismatched and convert to the needed type
			if (type.isFloat() || type.isMatrix()){
				dataBuffer.putFloat(variables[insert]);
			}else{
				dataBuffer.putInt((int)variables[insert]);
			}
		}
		dataBuffer.flip();//move the writer position back to the start of the buffer for reads
		this.set(program, transpose);
	}

	public void set(int program, boolean transpose, int... variables){
		dataBuffer.position(0);
		for (int insert = 0; insert < Math.min(type.size, variables.length); insert++) {
			//determine if the type of the uniform is mismatched and convert to the needed type
			if (type.isFloat() || type.isMatrix()){
				dataBuffer.putFloat((float)variables[insert]);
			}else{
				dataBuffer.putInt(variables[insert]);
			}
		}
		dataBuffer.flip();//move the writer position back to the start of the buffer for reads
		this.set(program, transpose);
	}

	public void set(int program, boolean... variables){
		dataBuffer.position(0);
		for (int insert = 0; insert < Math.min(type.size, variables.length); insert++) {
			if (type.isInt()) {
				dataBuffer.putInt(variables[insert] ? 1 : 0);
			}else if(type.isFloat()){
				dataBuffer.putFloat(variables[insert] ? 1.0f : 0.0f);
			}
		}
		dataBuffer.flip();//move the writer position back to the start of the buffer for reads
		this.set(program, false);
	}
	
	/**
	 * Sets the value of this matrix uniform object on the GPU with the given data
	 * 
	 * @param transpose Boolean deciding whether the matrix data should be trasnposed as it is being buffered to the uniform
	 * @return True if the variable could be set false otherwise
	 */
	private boolean setMat(int program, boolean transpose){
		if(type.isMatrix()){
			switch (type) {
				case MAT2:
					glProgramUniformMatrix2fv(program, location, 1, transpose, dataBuffer);
					break;
				case MAT2X3:
					glProgramUniformMatrix2x3fv(program, location, 1, transpose, dataBuffer);
					break;
				case MAT2X4:
					glProgramUniformMatrix2x4fv(program, location, 1, transpose, dataBuffer);
					break;
				case MAT3:
					glProgramUniformMatrix3fv(program, location, 1, transpose, dataBuffer);
					break;
				case MAT3X2:
					glProgramUniformMatrix3x2fv(program, location, 1, transpose, dataBuffer);
					break;
				case MAT3X4:
					glProgramUniformMatrix3x4fv(program, location, 1, transpose, dataBuffer);
					break;
				case MAT4:
					glProgramUniformMatrix4fv(program, location, 1, transpose, dataBuffer);
					break;
				case MAT4X2:
					glProgramUniformMatrix4x2fv(program, location, 1, transpose, dataBuffer);
					break;
				case MAT4X3:
					glProgramUniformMatrix4x3fv(program, location, 1, transpose, dataBuffer);
					break;
			}
			return true;
		}
		return false;
	}
}
