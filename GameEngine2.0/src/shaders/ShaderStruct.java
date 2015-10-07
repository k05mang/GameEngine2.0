package shaders;

import java.util.ArrayList;
import java.util.HashMap;
import static shaders.ShaderParser.*;

public class ShaderStruct{
	public ArrayList<String> fields;//contains the names of the members of the struct
	public ArrayList<String> types;//contains the types for each of the structs members
	
	public ShaderStruct(String memberList){
		fields = new ArrayList<String>();
		types = new ArrayList<String>();
		String[] members = memberList.trim().split(space0plus+";");
		
		//iterate over the different groups of variables for the current structure
		for(int curGroup = 0; curGroup < members.length; curGroup++){
			//separate the type from the names
			String[] type_names = members[curGroup].trim().replaceFirst(space1plus, "@").split("\\@");
			//replaces the first space with an @ then splits it around that to decompose the member name from it's type
			String type = type_names[0];//store type
			String[] names = type_names[1].split(space0plus+","+space0plus);//separate the names that are listed with commas
			//this is for in case the members were listed, i.e. int one, two, three, four;
			
			//iterate over the names of the structure variables (this loop will only be 1 for structure members with only one element)
			for(int curName = 0; curName < names.length; curName++){
				String curVar = names[curName].trim();
				//determine if the name is an array type or not and store the names
				if(!curVar.contains("[")){
					fields.add(curVar);
					types.add(type);
				}
				else{
					ArrayList<String> arrayIndices = ShaderParser.parseArray(curVar);
					for(String curIndex : arrayIndices){
						types.add(type);
						fields.add(curIndex);
					}
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
