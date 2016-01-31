package shaders;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL41.*;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.Configuration;

import gldata.BufferObject;

public class Uniform {
	private int location, size;
	public UniformType type;
	private ByteBuffer dataBuffer;
	
	public Uniform(UniformType type, int location){
		this(type, location, type.getSize());
	}
	
	public Uniform(UniformType type, int location, int size){
		this.type = type;
		this.location = location;
		this.size = size;
		dataBuffer = BufferUtils.createByteBuffer(4*size);
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
	 * Sets the value of this uniform object on the GPU with the given data buffer
	 * 
	 * @param data Buffer of data to use in changing the value of this uniform
	 * @return True if the variable could be set false otherwise
	 */
	private boolean set(int program, boolean transpose){
		if(type.isFloat()){
			switch(size){
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
			return true;
		}else if(type.isMatrix()){
			return this.setMat(program, transpose);
		}else if(type.isInt()){
			switch(size){
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
			return true;
		}
		return false;
	}
	
	public boolean set(int program, boolean transpose, float... variables){
		dataBuffer.position(0);
		//Determine is the data is smaller than this uniforms size, in which case we pad the missing data with 0's
		//or if it is larger than or equal in size, just take that given data up to the size of the uniform and add it to the buffer
		if (variables.length >= size) {
			for (int insert = 0; insert < size; insert++) {
				//determine if the type of the uniform is mismatched and convert to the needed type
				if (type.isFloat() || type.isMatrix()){
					dataBuffer.putFloat(variables[insert]);
				}else{
					dataBuffer.putInt((int)variables[insert]);
				}
			}
		} else if (variables.length < size) {
			//get the dataBuffer that we can from the passed values
			for (int insert = 0; insert < variables.length; insert++) {
				//determine if the type of the uniform is mismatched and convert to the needed type
				if (type.isFloat() || type.isMatrix()){
					dataBuffer.putFloat(variables[insert]);
				}else{
					dataBuffer.putInt((int)variables[insert]);
				}
			}
			
			//pad the remaining space needed for the buffer with 0's
			for (int remaining = size - variables.length; remaining < size; remaining++) {
				//determine if the type of the uniform is mismatched and put the appropriate padding
				if (type.isFloat() || type.isMatrix()){
					dataBuffer.putFloat(0.0f);
				}else{
					dataBuffer.putInt(0);
				}
			}
		}
		dataBuffer.flip();//move the writer position back to the start of the buffer for reads
		return this.set(program, transpose);
	}

	public boolean set(int program, boolean transpose, int... variables){
		dataBuffer.position(0);
		//Determine is the data is smaller than this uniforms size, in which case we pad the missing data with 0's
		//or if it is larger than or equal in size, just take that given data up to the size of the uniform and add it to the buffer
		if (variables.length >= size) {
			for (int insert = 0; insert < size; insert++) {
				//determine if the type of the uniform is mismatched and convert to the needed type
				if (type.isFloat() || type.isMatrix()){
					dataBuffer.putFloat((float)variables[insert]);
				}else{
					dataBuffer.putInt(variables[insert]);
				}
			}
		} else if (variables.length < size) {
			//get the dataBuffer that we can from the passed values
			for (int insert = 0; insert < variables.length; insert++) {
				//determine if the type of the uniform is mismatched and convert to the needed type
				if (type.isFloat() || type.isMatrix()){
					dataBuffer.putFloat((float)variables[insert]);
				}else{
					dataBuffer.putInt(variables[insert]);
				}
			}
			
			//pad the remaining space needed for the buffer with 0's
			for (int remaining = size - variables.length; remaining < size; remaining++) {
				//determine if the type of the uniform is mismatched and put the appropriate padding
				if (type.isFloat() || type.isMatrix()){
					dataBuffer.putFloat(0.0f);
				}else{
					dataBuffer.putInt(0);
				}
			}
		}
		dataBuffer.flip();//move the writer position back to the start of the buffer for reads
		return this.set(program, transpose);
	}

	public boolean set(int program, boolean... variables){
		if (type.isInt()) {
			dataBuffer.position(0);
			//Determine is the data is smaller than this uniforms size, in which case we pad the missing data with 0's
			//or if it is larger than or equal in size, just take that given data up to the size of the uniform and add it to the buffer
			if (variables.length >= size) {
				for (int insert = 0; insert < size; insert++) {
					dataBuffer.putInt(variables[insert] ? 1 : 0);
				}
			} else if (variables.length < size) {
				//get the dataBuffer that we can from the passed values
				for (int insert = 0; insert < variables.length; insert++) {
					dataBuffer.putInt(variables[insert] ? 1 : 0);
				}
				
				//pad the remaining space needed for the buffer with 0's
				for (int remaining = size - variables.length; remaining < size; remaining++) {
					dataBuffer.putInt(0);
				}
			}
			dataBuffer.flip();//move the writer position back to the start of the buffer for reads
			return this.set(program, false);
		}
		return false;
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
	
	/**
	 * Binds a buffer object to this uniform that is used to set the uniform interfaces values
	 * 
	 * @param buffer Buffer to bind to this uniform interface block
	 * @return True if the variable could be set false otherwise
	 */
//	public boolean bindBuffer(BufferObject buffer){
//		if(type == BLOCK){
//			glBindBufferBase(GL_UNIFORM_BUFFER, location, buffer.getId());
//			return true;
//		}else{
//			return false;
//		}
//	}
//	
//	/**
//	 * Binds a buffer object to this uniform that is used to set the uniform interfaces values, 
//	 * the buffer is read starting from the given index up through the uniform blocks total size
//	 *  
//	 * @param buffer Buffer to bind to the uniform interface block
//	 * @param offset Offset into the buffer to start reading data from
//	 * @return True if the variable could be set false otherwise
//	 */
//	public boolean bindBuffer(BufferObject buffer, int offset){
//		if(type == BLOCK){
//			glBindBufferRange(GL_UNIFORM_BUFFER, location, buffer.getId(), offset, size);
//			return true;
//		}else{
//			return false;
//		}
//	}
//	
//	/**
//	 * Binds a buffer object to this uniform that is used to set the uniform interfaces values, 
//	 * the buffer is read starting from the given index up through the dataSize given to this function
//	 * 
//	 * @param buffer Buffer to bind to the uniform interface block
//	 * @param offset Offset into the buffer to start reading data from
//	 * @param dataSize Size of the buffer to read for this uniform object
//	 * @return True if the variable could be set false otherwise
//	 */
//	public boolean bindBuffer(BufferObject buffer, int offset, int dataSize){
//		if(type == BLOCK){
//			glBindBufferRange(GL_UNIFORM_BUFFER, location, buffer.getId(), offset, dataSize);
//			return true;
//		}else{
//			return false;
//		}
//	}
}
