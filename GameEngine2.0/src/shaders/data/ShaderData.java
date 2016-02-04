package shaders.data;

import static org.lwjgl.opengl.GL43.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;

import shaders.Uniform;
import shaders.UniformType;

public class ShaderData {

	private int program;
	private HashMap<String, Uniform> uniforms;
	
	public ShaderData(int program){
		this.program = program;
		uniforms = new HashMap<String, Uniform>();
	}
	
	public void introspect(){
		loadUniforms();
	}
	
	private void loadUniforms(){
		//get the number of uniforms to acquire
		int numUniforms = glGetProgramInterfacei(program, Interface.UNIFORM.value, GL_ACTIVE_RESOURCES);
		//create a buffer to store the name of the uniforms
		ByteBuffer nameBuffer = BufferUtils.createByteBuffer(glGetProgramInterfacei(program, Interface.UNIFORM.value, GL_MAX_NAME_LENGTH));
		StringBuilder name = new StringBuilder(nameBuffer.capacity());
		//create the buffer that specifies the properties to query
		IntBuffer props = BufferUtils.createIntBuffer(4);
		props.put(Property.BLOCK_INDEX.value);//for now used to identify whether the uniform is a block interface
		props.put(Property.LOCATION.value);
		props.put(Property.NAME_LENGTH.value);
		props.put(Property.TYPE.value);
		props.flip();
		//create the buffer that will contain the properties queried by the system
		IntBuffer params = BufferUtils.createIntBuffer(4);
		//iterate over the uniforms and retrieve the data for each of them
		for(int curUniform = 0; curUniform < numUniforms; curUniform++){
			//get information about the uniform
			glGetProgramResourceiv(program, Interface.UNIFORM.value, curUniform, props, null, params);
			//check if the uniform is a block type
			if(params.get(0) != -1){
				continue;
			}

			nameBuffer.position(0);
			//get the name of the uniform
			glGetProgramResourceName(program, Interface.UNIFORM.value, curUniform, nameBuffer.capacity(), null, nameBuffer);
			for(int curChar = 0; curChar < params.get(2)-1; curChar++){
				name.append((char)nameBuffer.get(curChar));
			}
			
			uniforms.put(
					name.toString(), 
					new Uniform(UniformType.getType(params.get(3)), params.get(1))
					);
			name.delete(0, params.get(2)-1);
			params.position(0);
		}
	}
	
	public Uniform getUniform(String name){
		return uniforms.get(name);
	}
}
