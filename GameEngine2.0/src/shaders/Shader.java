package shaders;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.IOException;

public class Shader {
	private int shaderId;
	private ShaderStage type;
	private String fileName;
	
	/**
	 * Constructs a shader of the given ShaderStage {@code shaderType}, and shader source code
	 * in a file with the pathname {@code fileName}
	 * 
	 * @param fileName String specifying the pathname for a file containing the source code for the shader
	 * @param shaderType The type of shader this shader will be
	 */
	public Shader(String fileName, ShaderStage shaderType){
		this(new File(fileName), shaderType);
	}
	
	/**
	 * Constructs a shader of the given ShaderStage {@code shaderType}, and shader source code
	 * in {@code file}
	 * 
	 * @param file File containing the shader source code
	 * @param shaderType The type of shader this shader will be
	 */
	public Shader(File file, ShaderStage shaderType){
		type = shaderType;
		fileName = file.getName();
		shaderId = glCreateShader(type.type);
		glShaderSource(shaderId, Shader.getSource(file));
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
		shaderId = 0;
	}
	
	/**
	 * Compiles this shader and returns whether the compilation was successful
	 * 
	 * @return True if the compilation was successful, false otherwise
	 */
	public boolean compile(){
		glCompileShader(shaderId);
		return glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_TRUE;
	}
	
	/**
	 * Determines if this shader has been successfully compiled on the GPU
	 * @return True if this shader was successfully compiled on the GPU
	 */
	public boolean isCompiled(){
		return glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_TRUE;
	}
	
	/**
	 * Gets the shader stage this shader is compiled for
	 * 
	 * @return {@code ShaderStage} indicating the pipeline stage this shader functions in
	 */
	public ShaderStage getShaderStage(){
		return type;
	}
	
	/**
	 * Gets the info log for this shader, in the event that the shader log has nothing in it
	 * this function will return an empty string
	 * 
	 * @return Shader log containing any error information from the GPU
	 */
	public String getInfoLog(){
		return fileName+"\n"+glGetShaderInfoLog(shaderId);
	}
	
	@Override
	public boolean equals(Object shader){
		if(shader instanceof Shader){
			return shaderId == ((Shader)shader).shaderId && shaderId != 0;
		}else{
			return false;
		}
	}
	
	/**
	 * Parses the given file and stores it in a string to be passed to the GPU for shader
	 * compilation
	 * 
	 * @param file File to parse
	 * @return String containing the shader source code from the file
	 */
	public static String getSource(File file){
		StringBuilder source = new StringBuilder();
		try{
			Scanner shaderParser = new Scanner(file);
			String line;
			while(shaderParser.hasNextLine()){
				line = shaderParser.nextLine();
				//add source code line to stringBuilder
				source.append(line+"\n");
			}
			shaderParser.close();
		}catch(IOException e){
			e.printStackTrace();
			System.exit(0);
		}
		return source.toString();
	}
}
