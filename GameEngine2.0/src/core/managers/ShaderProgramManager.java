package core.managers;

import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import shaders.Shader;
import shaders.ShaderProgram;
import shaders.ShaderStage;

public class ShaderProgramManager extends ResourceManager<ShaderProgram> {

	public ShaderProgramManager(){
		super();
		//load shader programs specified by the config file
		//first get the shader program section from the configuration file
		JsonElement shaderConfig = SceneManager.config.system("ShaderPrograms");
		
		//iterate over each element of the object and parse them
		JsonObject shaderPrograms = shaderConfig.getAsJsonObject();
		
		for(Entry<String, JsonElement> program : shaderPrograms.entrySet()){
			//create the shader program
			resources.put(program.getKey(), new ShaderProgram());
			//iterate over the current entry to get the shaders associated with it
			JsonObject shaders = program.getValue().getAsJsonObject();
			for(Entry<String, JsonElement> shader : shaders.entrySet()){
				ShaderStage stage = null;
				//determine the shader stage this value is associated with based on the string property name
				switch(shader.getKey().toLowerCase()){
					//vertex shader types
					case "vert":
						stage = ShaderStage.VERTEX;
						break;
					case "vertex":
						stage = ShaderStage.VERTEX;
						break;
						
					//fragment shader types
					case "frag":
						stage = ShaderStage.FRAG;
						break;
					case "fragment":
						stage = ShaderStage.FRAG;
						break;
						
					//Geometry shader types
					case "geo":
						stage = ShaderStage.GEO;
						break;
					case "geometry":
						stage = ShaderStage.GEO;
						break;
						
					//Compute shader types
					case "compute":
						stage = ShaderStage.COMPUTE;
						break;
						
					//Tesselation control shader types
					case "tess-control":
						stage = ShaderStage.TESS_CONTROL;
						break;
						
					//Tesselation Evaluation shader types
					case "tess-eval":
						stage = ShaderStage.TESS_EVAL;
						break;
					default:
						//throw new 	
				}
				//load shader code
				Shader curShader = new Shader(shader.getValue().getAsString(), stage);
				//compile the shader code
				if(!curShader.compile()){
					System.out.println(curShader.getInfoLog());
				}
				//attach the shader to the shader program
				resources.get(program.getKey()).attach(curShader);
			}
			
			//link the program
			if(!resources.get(program.getKey()).link()){
				//if it failed to link print the error log
				System.out.println(resources.get(program.getKey()).getInfoLog());
			}	
		}
	}
}
