package shaders;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class Uniform {
	private String name, typeName;
	private int location, type, size;
	public static final int 
		UNKNOWN = 0,
		BLOCK = 1,
		FLOAT = 2,
		INT = 3,
		BOOL = 4,
		MAT2 = 5,
		MAT2X3 = 6,
		MAT2X4 = 7,
		MAT3 = 8,
		MAT3X2 = 9,
		MAT3X4 = 10,
		MAT4 = 11,
		MAT4X2 = 12,
		MAT4X3 = 13
		;
	
	/**
	 * Constructs a uniform object with a name and a type given as strings, if the type given is null
	 * then the uniform will be considered a block uniform
	 * 
	 * @param name Name of the uniform in the shader program
	 * @param type String identifying what type of uniform variable this will represent
	 */
	public Uniform(String name, String type){
		this.name = name;
		if(type != null){
			if(type.contains("sampler") || type.contains("image")){
				this.type = INT;
				size = 1;
			}else{
				switch(type){
					case "float":
						this.type = FLOAT;
						size = 1;
						break;
					case "int":
						this.type = INT;
						size = 1;
						break;
					case "uint":
						this.type = INT;
						size = 1;
						break;
					case "bool":
						this.type = BOOL;
						size = 1;
						break;
						
					case "vec2":
						this.type = FLOAT;
						size = 2;
						break;
					case "ivec2":
						this.type = INT;
						size = 2;
						break;
					case "uvec2":
						this.type = INT;
						size = 2;
						break;
					case "bvec2":
						this.type = BOOL;
						size = 2;
						break;
						
					case "vec3":
						this.type = FLOAT;
						size = 3;
						break;
					case "ivec3":
						this.type = INT;
						size = 3;
						break;
					case "uvec3":
						this.type = INT;
						size = 3;
						break;
					case "bvec3":
						this.type = BOOL;
						size = 3;
						break;
						
					case "vec4":
						this.type = FLOAT;
						size = 4;
						break;
					case "ivec4":
						this.type = INT;
						size = 4;
						break;
					case "uvec4":
						this.type = INT;
						size = 4;
						break;
					case "bvec4":
						this.type = BOOL;
						size = 4;
						break;
						
					case "mat2":
						this.type = MAT2;
						size = 4;
						break;
					case "mat2x2":
						this.type = MAT2;
						size = 4;
						break;
					case "mat2x3":
						this.type = MAT2X3;
						size = 6;
						break;
					case "mat2x4":
						this.type = MAT2X4;
						size = 8;
						break;
						
					case "mat3":
						this.type = MAT3;
						size = 9;
						break;
					case "mat3x3":
						this.type = MAT3;
						size = 9;
						break;
					case "mat3x2":
						this.type = MAT3X2;
						size = 6;
						break;
					case "mat3x4":
						this.type = MAT3X4;
						size = 12;
						break;
						
					case "mat4":
						this.type = MAT4;
						size = 16;
						break;
					case "mat4x4":
						this.type = MAT4;
						size = 16;
						break;
					case "mat4x2":
						this.type = MAT4X2;
						size = 8;
						break;
					case "mat4x3":
						this.type = MAT4X3;
						size = 12;
						break;
					default:
						this.type = UNKNOWN;
						size = 0;
						break;
				}
			}
		}else{
			this.type = BLOCK;
		}
		location = -1;
		this.typeName = type;
	}
	
	/**
	 * Copies the given uniform, the location variable is the only variable not copied and must be given a new value
	 * with the getLocation function
	 * 
	 * @param copy Uniform variable to copy
	 */
	public Uniform(Uniform copy){
		name = copy.name; 
		typeName = copy.typeName;
		location = 0; 
		type = copy.type; 
		size = copy.size;
	}
	
	/**
	 * Gets the uniform location from the GPU, this is used to index the variable for updating
	 * 
	 * @param program Program to search for the variable binding
	 * @return The location of this uniform object binding in the given program object
	 */
	public int getLocation(int program){
		location = glGetUniformLocation(program, name);
		return location;
	}
	
	/**
	 * Gets the name of this uniform variable
	 * 
	 * @return String containing the name of this uniform variable
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Gets the type that this uniform is associated with
	 * 
	 * @return The integral constant representing this uniform variables type
	 */
	public int getType(){
		return type;
	}
	
	/**
	 * Gets the string representing the type of variable this uniform is associated
	 * 
	 * @return Null if this uniform represents a block interface, otherwise a string containing the type of this uniform 
	 */
	public String getTypeName(){
		return typeName;
	}
	
	/**
	 * Sets the value of this uniform object on the GPU
	 * 
	 * @param data Buffer of data to use in changing the value of this uniform
	 */
	public void set(Buffer data){
		if(data instanceof FloatBuffer){
			FloatBuffer dataCast = (FloatBuffer)data;
			if(type == FLOAT){
				switch(size){
					case 1:
						glUniform1fv(location, dataCast);
						break;
					case 2:
						glUniform2fv(location, dataCast);
						break;
					case 3:
						glUniform3fv(location, dataCast);
						break;
					case 4:
						glUniform4fv(location, dataCast);
						break;
				}
			}else if(type >= MAT2){
				this.setMat(false, dataCast);
			}else{
				System.err.println("Types do not match, FloatBuffers can only be used with uniforms that are of type float or mat in GLSL");
			}
		}else if(data instanceof IntBuffer){
			IntBuffer dataCast = (IntBuffer)data;
			if(type == INT || type == BOOL){
				switch(size){
					case 1:
						glUniform1iv(location, dataCast);
						break;
					case 2:
						glUniform2iv(location, dataCast);
						break;
					case 3:
						glUniform3iv(location, dataCast);
						break;
					case 4:
						glUniform4iv(location, dataCast);
						break;
				}
			}
		}else{
			System.err.println("Failed to buffer data to uniform, buffer data is not of type FloatBuffer or IntBuffer");
		}
	}
	
	public void set(float... variables){
		ByteBuffer data = BufferUtils.createByteBuffer(size*4);
		//Determine is the data is smaller than this uniforms size, in which case we pad the missing data with 0's
		//or if it is larger than or equal in size, just take that given data up to the size of the uniform and add it to the buffer
		if (variables.length >= size) {
			for (int insert = 0; insert < size; insert++) {
				//determine if the type of the uniform is mismatched and convert to the needed type
				if (type == FLOAT || type >= MAT2){
					data.putFloat(variables[insert]);
				}else{
					data.putInt((int)variables[insert]);
				}
			}
			data.flip();//move the writer position back to the start of the buffer for reads
		} else if (variables.length < size) {
			//get the data that we can from the passed values
			for (int insert = 0; insert < variables.length; insert++) {
				//determine if the type of the uniform is mismatched and convert to the needed type
				if (type == FLOAT || type >= MAT2){
					data.putFloat(variables[insert]);
				}else{
					data.putInt((int)variables[insert]);
				}
			}
			
			//pad the remaining space needed for the buffer with 0's
			for (int remaining = size - variables.length; remaining < size; remaining++) {
				//determine if the type of the uniform is mismatched and put the appropriate padding
				if (type == FLOAT || type >= MAT2){
					data.putFloat(0.0f);
				}else{
					data.putInt(0);
				}
			}
			data.flip();
		}
		//call the set function variant that handles what function to call to update the uniform
		if (type == FLOAT || type >= MAT2){
			this.set(data.asFloatBuffer());
		}else{
			this.set(data.asIntBuffer());
		}
	}
	
	public void set(int... variables){
		ByteBuffer data = BufferUtils.createByteBuffer(size*4);
		//Determine is the data is smaller than this uniforms size, in which case we pad the missing data with 0's
		//or if it is larger than or equal in size, just take that given data up to the size of the uniform and add it to the buffer
		if (variables.length >= size) {
			for (int insert = 0; insert < size; insert++) {
				//determine if the type of the uniform is mismatched and convert to the needed type
				if (type == FLOAT || type >= MAT2){
					data.putFloat((float)variables[insert]);
				}else{
					data.putInt(variables[insert]);
				}
			}
			data.flip();//move the writer position back to the start of the buffer for reads
		} else if (variables.length < size) {
			//get the data that we can from the passed values
			for (int insert = 0; insert < variables.length; insert++) {
				//determine if the type of the uniform is mismatched and convert to the needed type
				if (type == FLOAT || type >= MAT2){
					data.putFloat((float)variables[insert]);
				}else{
					data.putInt(variables[insert]);
				}
			}
			
			//pad the remaining space needed for the buffer with 0's
			for (int remaining = size - variables.length; remaining < size; remaining++) {
				//determine if the type of the uniform is mismatched and put the appropriate padding
				if (type == FLOAT || type >= MAT2){
					data.putFloat(0.0f);
				}else{
					data.putInt(0);
				}
			}
			data.flip();
		}
		//call the set function variant that handles what function to call to update the uniform
		if (type == FLOAT || type >= MAT2){
			this.set(data.asFloatBuffer());
		}else{
			this.set(data.asIntBuffer());
		}
	}
	
	public void set(boolean... variables){
		if (type == BOOL) {
			IntBuffer data = BufferUtils.createIntBuffer(size);
			//Determine is the data is smaller than this uniforms size, in which case we pad the missing data with 0's
			//or if it is larger than or equal in size, just take that given data up to the size of the uniform and add it to the buffer
			if (variables.length >= size) {
				for (int insert = 0; insert < size; insert++) {
					data.put(variables[insert] ? 1 : 0);
				}
				data.flip();
			} else if (variables.length < size) {
				//get the data that we can from the passed values
				for (int insert = 0; insert < variables.length; insert++) {
					data.put(variables[insert] ? 1 : 0);
				}
				
				//pad the remaining space needed for the buffer with 0's
				for (int remaining = size - variables.length; remaining < size; remaining++) {
					data.put(0);
				}
				data.flip();
			}
			this.set(data);
		}else{
			System.err.println("Types do not match, this uniform is not of type bool");
		}
	}
	
	public void setMat(boolean transpose, FloatBuffer data){
		if(type >= MAT2){
			switch (type) {
				case MAT2:
					glUniformMatrix2fv(location, transpose, data);
					break;
				case MAT2X3:
					glUniformMatrix2x3fv(location, transpose, data);
					break;
				case MAT2X4:
					glUniformMatrix2x4fv(location, transpose, data);
					break;
				case MAT3:
					glUniformMatrix3fv(location, transpose, data);
					break;
				case MAT3X2:
					glUniformMatrix3x2fv(location, transpose, data);
					break;
				case MAT3X4:
					glUniformMatrix3x4fv(location, transpose, data);
					break;
				case MAT4:
					glUniformMatrix4fv(location, transpose, data);
					break;
				case MAT4X2:
					glUniformMatrix4x2fv(location, transpose, data);
					break;
				case MAT4X3:
					glUniformMatrix4x3fv(location, transpose, data);
					break;
			}
		}else{
			System.err.println("Types do not match, this uniform is not a matrix type");
		}
	}
}
