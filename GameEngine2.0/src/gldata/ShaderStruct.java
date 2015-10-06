package gldata;

import java.util.ArrayList;
import java.util.HashMap;
import static gldata.ShaderParser.*;

public class ShaderStruct{
	public ArrayList<String> fields;
	public ArrayList<String> types;
	
	public ShaderStruct(String name, String memberList, HashMap<String, ShaderStruct> structMap){
		fields = new ArrayList<String>();
		types = new ArrayList<String>();
		String[] members = memberList.trim().split(space0plus+";");
		//iterate over the different groups of variables for the current structure
		for(int curGroup = 0; curGroup < members.length; curGroup++){
			//separate the type from the names
			String[] type_names = members[curGroup].trim().replaceFirst(space1plus, "@").split("\\@");
			String type = type_names[0];//store type
			String[] names = type_names[1].split(space0plus+","+space0plus);//separate the names
			//iterate over the names of the structure variables
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
}
