package shaders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static shaders.ShaderParser.*;

public class ShaderStruct{
	public ArrayList<String> fields;//contains the names of the members of the struct
	public ArrayList<String> types;//contains the types for each of the structs members
	
	public ShaderStruct(String memberList){
		fields = new ArrayList<String>();
		types = new ArrayList<String>();
		String[] members = memberList.trim().split(space0plus+";");
		String arrayPattern = "\\w+"+space0plus+"\\["+space0plus+"\\d+"+space0plus+"\\]";//pattern for matching an array declaration
		
		//pattern that matches for array types
		Matcher arrayType = Pattern.compile("^("+arrayPattern+space0plus+"\\["+space0plus+"\\d+"+space0plus+"\\]|"+arrayPattern+")").matcher("");
		
		//iterate over the different groups of variables for the current structure
		for(int curGroup = 0; curGroup < members.length; curGroup++){
			//separate the variable names from the type
			//reset matchers with new input
			arrayType.reset(members[curGroup]);
			
			String type = null;
			String baseType = null;
			String variables = null;
			boolean isArrayType = false;
			//check what type of variable it is
			if(arrayType.find()){
				type = arrayType.group();//this type is the array type, which includes the base type
				baseType = members[curGroup].substring(0, members[curGroup].indexOf('['));//this is the basic type that the array is of
				variables = members[curGroup].substring(arrayType.end());
				isArrayType = true;
			}else{//this means the variable type is a plain object type like vec3
				//separate the type from the names
				String[] type_names = members[curGroup].trim().replaceFirst(space1plus, "@").split("\\@");
				type = type_names[0];
				baseType = type;
				//base type and type will be the same since this isn't an array type
				variables = type_names[1];
			}
			
			String[] variable_list = null;
			//determine if there are multiple declarations for this variable
			if(variables.contains(",")){
				variable_list = variables.split(space0plus+","+space0plus);
			}
			
			//check if there is more than one variable declaration
			if(variable_list != null){
				//iterate over the variables
				for(int curName = 0; curName < variable_list.length; curName++){
					String curVar = variable_list[curName].trim();
					isArrayType = curVar.contains("[");//check if an array type was declared in the variable instead of the type
					if(isArrayType){
						//decompose the names with their respective indices
						ArrayList<String> indices = ShaderParser.parseArray(type, curVar);
						//iterate over them and add them to the fields using the base type
						for(String indexName : indices){
							fields.add(indexName);
							types.add(baseType);
						}
					}else{//we are absolutely certain by this point it is a simple uniform
						fields.add(curVar);
						types.add(baseType);
					}
				}
			}else{//this means there aren't multiple variable declarations
				isArrayType = variables.contains("[");//check if an array type was declared in the variable instead of the type
				if(isArrayType){
					ArrayList<String> indices = ShaderParser.parseArray(type, variables.trim());
					for(String indexName : indices){
						fields.add(indexName);
						types.add(baseType);
					}
				}else{//we are absolutely certain by this point it is a simple uniform
					fields.add(variables.trim());
					types.add(baseType);
				}
			}
		}
	}
	
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
}
