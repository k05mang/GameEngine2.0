package shaders;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.util.ArrayList;
import java.io.File;

public class Shader {
	private int shaderId, type;
	private ShaderParser parser;
	
	Shader(String fileName, int shaderType){
		type = shaderType;
		File shaderFile = new File(fileName);
		parser = new ShaderParser(shaderFile);
		shaderId = glCreateShader(type);
		glShaderSource(shaderId, parser.getSource());
	}
	
	/**
	 * Attaches this shader to the specified shader program
	 * 
	 * @param program Id for the shader program to attach this shader to
	 */
	public void attach(int program){
		glAttachShader(program, shaderId);
	}
	
	/**
	 * Detaches this shader from the specified shader program
	 * 
	 * @param program Id for the shader program to detach this shader to
	 */
	public void detach(int program){
		glDetachShader(program, shaderId);
	}
	
	/**
	 * Deletes this shader from the GPU
	 */
	public void delete(){
		glDeleteShader(shaderId);
	}
	
	/**
	 * Compiles this shader and returns whether the compilation was successful
	 * 
	 * @return True if the compilation was successful, false otherwise
	 */
	public boolean compile(){
		glCompileShader(shaderId);
		return glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_TRUE	? true : false;
	}
	
	/**
	 * Determines if this shader has been successfully compiled on the GPU
	 * @return True if this shader was successfully compiled on the GPU
	 */
	public boolean isCompiled(){
		return glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_TRUE	? true : false;
	}
	
	/**
	 * Gets the info log for this shader, in the event that the shader log has nothing in it
	 * this function will return an empty string
	 * 
	 * @return Shader log containing any error information from the GPU
	 */
	public String getInfoLog(){
		return glGetShaderInfoLog(shaderId);
	}
	
	/**
	 * Gets the list of uniforms associated with this shader
	 * 
	 * @return List of the uniforms found while parsing this shader
	 */
	public ArrayList<Uniform> getUniforms(){
		return parser.getUniforms();
	}
}
