package shaders;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import gldata.BufferObject;

public class ShaderProgram {
	private HashMap<String, Uniform> uniforms;//mapping of uniform names to their uniform handlers
	private int programId;//id of the program generated by the GPU
	private ArrayList<Shader> shaders;
	
	/**
	 * Creates a shader program object on the GPU
	 */
	public ShaderProgram(){
		uniforms = new HashMap<String, Uniform>();
		shaders = new ArrayList<Shader>();
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
	 * Links this shader program on the GPU and generates the binary executables for the GPU to run.
	 * Additionally any shaders are detached from the program after linking is successful
	 * 
	 * @return True if the linking was successful, false otherwise
	 */
	public boolean link(){
		glLinkProgram(programId);
		//if we linked successfully add the uniforms to the map and fix any that were unknown while parsing a single shader file
		if(glGetProgrami(programId, GL_LINK_STATUS) == GL_TRUE){
			HashMap<String, ShaderStruct> structs = new HashMap<String, ShaderStruct>();
			//finalize all unknown uniforms and add them to the uniform hashmap
			//get all the structs parsed from the shader associated with this program
			for(Shader shader : shaders){
				structs.putAll(shader.getStructs());
				//detach the shaders
				shader.detach(programId);
			}
			//iterate and add uniforms
			for(Shader shader : shaders){
				for(Uniform uniform : shader.getUniforms()){
					//check if the uniform is an unknown type
					if(uniform.getType() == Uniform.UNKNOWN){
						//get the struct that defines this uniform, if anything goes wrong here that means the parser failed and needs further work
						ShaderStruct structure = structs.get(uniform.getTypeName());
						//generate new uniforms from the struct and add them
						for(Uniform structUni : structure.genUniforms(uniform.getName(), structs)){
							structUni.getBinding(programId);
							uniforms.put(structUni.getName(), structUni);
						}
					}else{
						//copy the uniform
						Uniform copy = new Uniform(uniform);
						//get its location in the newly linked shader program
						copy.getBinding(programId);
						//add it to the programs uniform variable mapping
						uniforms.put(copy.getName(), copy);
					}
				}
			}
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
			return found.set(data);
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
			return found.set(variables);
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
			return found.set(variables);
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
			return found.set(variables);
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
			return found.setMat(transpose, data);
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
	public boolean bindBuffer(String uniformName, BufferObject buffer){
		Uniform found = uniforms.get(uniformName);
		if(found != null){
			return found.bindBuffer(buffer);
		}else{
			return false;
		}
	}
	
	public boolean bindBuffer(String uniformName, BufferObject buffer, int startOffset){
		Uniform found = uniforms.get(uniformName);
		if(found != null){
			return found.bindBuffer(buffer, startOffset);
		}else{
			return false;
		}
	}
	
	public boolean bindBuffer(String uniformName, BufferObject buffer, int startOffset, int readSize){
		Uniform found = uniforms.get(uniformName);
		if(found != null){
			return found.bindBuffer(buffer, startOffset, readSize);
		}else{
			return false;
		}
	}
}
