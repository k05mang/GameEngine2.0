package shaders;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ShaderProgram {
	private HashMap<String, Uniform> uniforms;//mapping of uniform names to their uniform handlers
	private int programId;//id of the program generated by the GPU
	
	/**
	 * Creates a shader program object on the GPU
	 */
	public ShaderProgram(){
		uniforms = new HashMap<String, Uniform>();
		programId = glCreateProgram();
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
	}
	
	/**
	 * Links this shader program on the GPU and generates the binary executables for the GPU to run
	 * 
	 * @return True if the linking was successful, false otherwise
	 */
	public boolean link(){
		glLinkProgram(programId);
		return glGetProgrami(programId, GL_LINK_STATUS) == GL_TRUE ? true : false;
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
	
	/**
	 * Sets the uniform specified by the given name, and sets it to the value of data,
	 * if the uniform cannot be found then this function returns false
	 * 
	 * @param uniformName Name of the uniform to modify
	 * @param data Buffer containing the data to set the uniform to
	 * @return True if the uniform was found and set to the given data
	 */
	public boolean setUniform(String uniformName, Buffer data){
		Uniform found = uniforms.get(uniformName);
		if(found != null){
			found.set(data);
			return true;
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
	public boolean setUniform(String uniformName, float... variables){
		Uniform found = uniforms.get(uniformName);
		if(found != null){
			found.set(variables);
			return true;
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
		Uniform found = uniforms.get(uniformName);
		if(found != null){
			found.set(variables);
			return true;
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
		Uniform found = uniforms.get(uniformName);
		if(found != null){
			found.set(variables);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Sets a matrix uniform specified by the given name
	 * 
	 * @param uniformName Name of uniform to modify
	 * @param transpose Whether to transpose the data as it is being passed to the GPU
	 * @param data Data to set the uniform to
	 * @return True if the uniform was found and set to the given data
	 */
	public boolean setUniform(String uniformName, boolean transpose, FloatBuffer data){
		Uniform found = uniforms.get(uniformName);
		if(found != null){
			found.setMat(transpose, data);
			return true;
		}else{
			return false;
		}
	}
}
