package shaders;

import java.util.ArrayList;
import java.util.HashMap;

class ShaderStruct{
	public ArrayList<String> fields;//contains the names of the members of the struct
	public ArrayList<String> types;//contains the types for each of the structs members
	
	/**
	 * Constructs this shader struct with a string containing the member fields of the structure as they appear between {} in the shader file
	 * 
	 * @param memberList Fields of the struct as they are listed in the source file, without the enclosing braces {}
	 */
	public ShaderStruct(String memberList){
		fields = new ArrayList<String>();
		types = new ArrayList<String>();
		String[] members = memberList.trim().split("\\s*+;\\s*+");
		StringBuilder type = new StringBuilder();
		//iterate over the different groups of variables for the current structure
		for(int curGroup = 0; curGroup < members.length; curGroup++){
			int numVariables = ShaderParser.parseVariable(members[curGroup], type, fields);
			//add the type to the types array so that it matches with the new fields added from the variable parsing
			for(int addType = 0; addType < numVariables; addType++){
				types.add(type.toString());
			}
		}
	}
	
	/**
	 * Generates uniform objects with the given uniformName as the prefix for each of this structs fields
	 * 
	 * @param uniformName Name of the instance variable in the shader, this acts as a prefix to the full name of the uniform
	 * @param structMap Map of the different structures in the shader currently being parsed, this will be used to further extend
	 * the names of fields that are of a structure type instead of a base type
	 * 
	 * @return List of uniform objects each representing the full name for the given uniform name
	 */
	public ArrayList<Uniform> genUniforms(String uniformName, HashMap<String, ShaderStruct> structMap){
		ArrayList<Uniform> uniforms = new ArrayList<Uniform>();
		for(int curField = 0; curField < fields.size(); curField++){
			//see if this field is a struct type that is in the struct map
			ShaderStruct fromMap = null;
			if((fromMap = structMap.get(types.get(curField))) != null){
				//call the other structs uniform generation function and append the resulting values to this list
				uniforms.addAll(
						fromMap.genUniforms(uniformName+"."+fields.get(curField), structMap)
						);
			}else{//otherwise it is a primitive type in glsl
				uniforms.add(
						new Uniform(uniformName+"."+fields.get(curField), types.get(curField))
						);
			}
		}
		return uniforms;
	}
	
	/**
	 * Prints the fields and types of this struct
	 */
//	public void print(){
//		for(int curField = 0; curField < fields.size(); curField++){
//			System.out.println(types.get(curField)+" "+fields.get(curField));
//		}
//	}
}
