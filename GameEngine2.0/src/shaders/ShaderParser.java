package shaders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.IOException;

public class ShaderParser {
	private HashMap<String, ShaderStruct> structures;
	private ArrayList<Uniform> uniforms;
	private StringBuilder source;

	public static final String initializers = "(\\((\\d+|\\d+\\.\\d+f?|\\w|\\-|\\*|/|\\+|,|\\(|\\)|\\s*)*\\)";//add more for array initializers
	public static final String glslTypes = "bool|uint|int|float|double|(b|u|i|d)?vec\\d?|d?mat(\\d?|\\d?x\\d?)|(u|i)?(image|sampler)\\w+";
	
	ShaderParser(File fileName){
		structures = new HashMap<String, ShaderStruct>();
		try{
			Scanner shaderParser = new Scanner(fileName);
			String line;
			//set of regular expressions to use in determining information about the shader
			String uniform = "uniform";//due to constant usage, make a universal object for it
			/*String glslTypes = "float|int|uint|bool|mat2|mat2x2|mat2x3|mat2x4|mat3|mat3x3|mat3x2|mat3x4|mat4|mat4x4|mat4x2|mat4x3|"+
			"vec2|uvec2|ivec2|bvec2|vec3|uvec3|ivec3|bvec3|vec4|uvec4|ivec4|bvec4|\\w*sampler\\w*";*/
			
			while(shaderParser.hasNextLine()){
				line = shaderParser.nextLine();
				//add source code line to stringBuilder
				source.append(line+"\n");
				//process potential uniforms
				if(line.contains(uniform)){
					//gather everything about the uniform until we are certain we have everything about this uniform
					//search for the ending semicolon
					StringBuilder uniformData = new StringBuilder();
					while(shaderParser.hasNextLine() && !line.contains(";")){
						uniformData.append(line);
						source.append(line+"\n");
						line = shaderParser.nextLine();
					}
					uniformData.append(line);//add the last bit of data that contained the semicolon
					
					Matcher findType = Pattern.compile(glslTypes).matcher(uniformData);
					findType.find();
					uniformData.delete(0, findType.start());//removes all the specifier at the beginning of the string
					
					//check if we ended up picking up multiple lines of variable declarations
					//i.e. uniform vec3 pos;uniform vec2 uv;\n
					if(uniformData.indexOf("uniform") != -1){//this means a case as described above occurred
						
					}else if(uniformData.indexOf("struct") != -1){//this would mean a struct was started and potentially finished in the same line as the uniform
						//this case handles when a struct is defined and possibly compelted on the same line
						//i.e. uniform vec3 pos;struct light{\n
						
						
					}else{//this means only one uniform declaration was found and nothing else was found on that line that requires additional extraction
						//check if there was something else appended to the end of the declaration while parsing
						//i.e. uniform vec3 pos;vec2 uv;
						if(uniformData.indexOf(";") == uniformData.length()-1){//this means it was just that single uniform declaration
							uniformData.deleteCharAt(uniformData.length()-1);//remove the semicolon at the end of the string
							ArrayList<String> variableNames = new ArrayList<String>();
							StringBuilder varType = new StringBuilder();
							parseVariable(uniformData.toString(), varType, variableNames);//parse data from variable declaration
							
							//add new data to uniform array list
							String type = varType.toString();//since the uniform constructor takes a string for it's type create a copy of the type for reuse in the loop
							for(String varName : variableNames){
								uniforms.add(new Uniform(varName, type));
							}
						}else{
							String uniformDec = uniformData.substring(uniformData.indexOf(";"));//trim down to the important uniform information
							ArrayList<String> variableNames = new ArrayList<String>();
							StringBuilder varType = new StringBuilder();
							parseVariable(uniformDec, varType, variableNames);//parse data from variable declaration
							
							//add new data to uniform array list
							String type = varType.toString();//since the uniform constructor takes a string for it's type create a copy of the type for reuse in the loop
							for(String varName : variableNames){
								uniforms.add(new Uniform(varName, type));
							}
						}
					}
				}
				
				//check if there was a struct declaration
				//this is not an else if since the condition above will change the line variable in the event that a struct declaration was found while parsing a uniform line
				if(line.contains("struct")){
					StringBuilder structInfo = new StringBuilder(line);
					//continue reading the shader storing additional information about the structure in the string builder
					//while also updating the line variable and the source string builder
					while(shaderParser.hasNextLine() && !line.contains("}")){
						line = shaderParser.nextLine();
						structInfo.append(line);
						source.append(line+"\n");
					}
					//convert the string builder to a string for processing
					String structBody = structInfo.toString();
					int left = structBody.indexOf("struct");
					int right = structBody.indexOf('}');
					//get the substring starting after the keyword struct and before the } brace
					//then split on {  dividing the data into the name and the member list of the struct
					String[] structData = structBody.substring(left+6, right).split("\\s*\\{\\s*");
				}
			}
			shaderParser.close();
		}catch(IOException e){
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * Gets the source code for the shader that was extracted from the file and stored in a stringbuilder
	 * @return String containing the source code for the shader parsed by this parser
	 */
	public String getSource(){
		return source.toString();
	}
	
	/**
	 * Gets the list of uniforms extracted from the parsers shader file
	 * 
	 * @return List of uniform objects from this parsers shader file
	 */
	public ArrayList<Uniform> getUniforms(){
		return uniforms;
	}
	
	/**
	 * Parses a given variable name and type as array's and generates all the index names of the variable
	 * i.e. vec3[3] pos; becomes; pos[0],pos[1],pos[2]
	 * This function supports either type or variable being an array type, either type or variable could be 
	 * a 2D array while the other is non array or both could be 1D array types. Additionally
	 * one can be a 1D array while the other is a non array.
	 * 
	 * @param type		Type for this function to parse, this can be a non array, 1D or 2D array
	 * @param variable	Name of the variable that names will be generated for, this can be non array, 1D or 2D array
	 * @return List containing the variable with each index of the array appended to the end
	 */
	public static ArrayList<String> parseArray(String type, String variable){
		boolean isTypeArray = type.contains("[");
		boolean isVariableArray = variable.contains("[");
		ArrayList<String> arrayIndices = new ArrayList<String>();//array of index names
		Pattern arrayIndex = Pattern.compile("\\[\\s*\\d+\\s*\\]");
		
		//decide how to handle the passed data
		if(isTypeArray && isVariableArray){//both are declared as array
			Matcher index = arrayIndex.matcher(type);
			int outerLoop = 0;
			int innerloop = 0;
			index.find();
			String firstIndex = index.group();
			
			index.reset(variable);
			index.find();
			String secondIndex = index.group();
			 
			//trim the index braces and extract the count for the index
			outerLoop = Integer.parseInt(firstIndex.substring(1, firstIndex.length()-1).trim());
			 
			//trim the index braces and extract the count for the index
			innerloop = Integer.parseInt(secondIndex.substring(1, secondIndex.length()-1).trim());
			
			 //"trim" the indices off the end of the name to get the variable name
			 String varName = variable.substring(0, variable.indexOf('['));
			 
			//loop through the indices
			for(int outerIndex = 0; outerIndex < outerLoop; outerIndex++){
				for(int innerIndex = 0; innerIndex < innerloop; innerIndex++){
					arrayIndices.add(varName+"["+outerIndex+"]"+"["+innerIndex+"]");
				}
			}
		}else if(isTypeArray){//only type is declared as array
			//determine if it is a double array or single
			Matcher doubleArray = arrayIndex.matcher(type);
			ArrayList<String> indices = new ArrayList<String>();
			
			//get all the index points
			while(doubleArray.find()){
				indices.add(doubleArray.group());
			}
			
			//check if there is more than one index
			if(indices.size() > 1){
				 int outerLoop = 0;
				 int innerloop = 0;
				 String firstIndex = indices.get(0);
				 String secondIndex = indices.get(1);

				 //trim the index braces and extract the count for the index
				 outerLoop = Integer.parseInt(firstIndex.substring(1, firstIndex.length()-1).trim());
				 
				 //trim the index braces and extract the count for the index
				 innerloop = Integer.parseInt(secondIndex.substring(1, secondIndex.length()-1).trim());
				 
				 //loop through the indices
				 for(int outerIndex = 0; outerIndex < outerLoop; outerIndex++){
					 for(int innerIndex = 0; innerIndex < innerloop; innerIndex++){
						 arrayIndices.add(variable+"["+outerIndex+"]"+"["+innerIndex+"]");
					 }
				 }
			}else{
				int loop = 0;
				String braces = indices.get(0);
				
				//remove index braces and extract array size
				loop = Integer.parseInt(braces.substring(1, braces.length()-1).trim());
				
				//loop through the indices
				for(int index = 0; index < loop; index++){
					 arrayIndices.add(variable+"["+index+"]");
				}
			}
		}else{//only the variable is declared as an array
			//determine if it is a double array or single
			Matcher doubleArray = arrayIndex.matcher(variable);
			ArrayList<String> indices = new ArrayList<String>();
			 
			 //"trim" the indices off the end of the name to get the variable name
			 String varName = variable.substring(0, variable.indexOf('['));
			
			//get all the index points
			while(doubleArray.find()){
				indices.add(doubleArray.group());
			}
			
			//check if there is more than one index
			if(indices.size() > 1){
				 int outerLoop = 0;
				 int innerloop = 0;
				 String firstIndex = indices.get(0);
				 String secondIndex = indices.get(1);

				 //trim the index braces and extract the count for the index
				 outerLoop = Integer.parseInt(firstIndex.substring(1, firstIndex.length()-1).trim());
				 
				 //trim the index braces and extract the count for the index
				 innerloop = Integer.parseInt(secondIndex.substring(1, secondIndex.length()-1).trim());
				 
				 //loop through the indices
				 for(int outerIndex = 0; outerIndex < outerLoop; outerIndex++){
					 for(int innerIndex = 0; innerIndex < innerloop; innerIndex++){
						 arrayIndices.add(varName+"["+outerIndex+"]"+"["+innerIndex+"]");
					 }
				 }
			}else{
				int loop = 0;
				String braces = indices.get(0);
				
				//remove index braces and extract array size
				loop = Integer.parseInt(braces.substring(1, braces.length()-1).trim());
				
				//loop through the indices
				for(int index = 0; index < loop; index++){
					 arrayIndices.add(varName+"["+index+"]");
				}
			}
		}
		 return arrayIndices;
	}
	
	/**
	 * Parses a string that contains information about a single field declaration line
	 * the line can have as many variable names in it but must have existed between two semicolons
	 * i.e. 
	 * vec3 pos, lightPos, alpha;//ok
	 * vec3 lightPos, pos;float alpha;int id;//error
	 * The parameter target is what is being parsed.
	 * 
	 * Type is an empty stringbuilder that will have the parsed variable type added to it, if this is not
	 * empty it will be emptied.
	 * 
	 * Names is an array list containing the parsed names of the variables
	 * 
	 * @param target String to parse and extract from
	 * @param type	 StringBuilder to put the variables type in for returning to caller function, the base type in case of an array type
	 * @param names	 Array list that will have the names of the variables added to it
	 * 
	 * @return The number of variable names extracted from the line of text
	 */
	public static int parseVariable(String target, StringBuilder type, ArrayList<String> names){
		//TODO HANDLE WHEN THERE ARE INITIALIZERS FOR A VARIABLE additionally handle precision modifiers prefixing variables
		int numVars = 0;
		//check if we need to empty the type parameter
		if(type.length() > 1){
			type.delete(0, type.length());//delete its contents if it has things in it
		}
		
		//remove precision qualifiers
		Matcher findType = Pattern.compile(glslTypes).matcher(target);
		findType.find();
		String cleanTarget = target.substring(findType.start());
		//pattern that matches for array types
		Matcher arrayType = Pattern.compile("^\\w+(\\s*\\[\\s*\\d+\\s*\\])+").matcher(cleanTarget);
		
		//separate the variable names from the type
		String baseType = null;
		String variables = null;
		boolean isArrayType = false;
		//check what type of variable it is
		if(arrayType.find()){
			baseType = arrayType.group().trim();//this type is the array type, which includes the base type
			type.append(cleanTarget.substring(0, cleanTarget.indexOf('[')));//this is the basic type that the array is of
			variables = cleanTarget.substring(arrayType.end());
			isArrayType = true;
		}else{//this means the variable type is a plain object type like vec3
			//separate the type from the names
			String[] type_names = cleanTarget.trim().replaceFirst("\\s+", "@").split("\\@");
			type.append(type_names[0].trim());
			baseType = type_names[0].trim();
			//base type and type will be the same since this isn't an array type
			variables = type_names[1].trim();
		}
		
		String[] variable_list = null;
		//determine if there are multiple declarations for this variable
		if(variables.contains(",")){
			variable_list = variables.split("\\s*,\\s*");
		}
		
		//check if there is more than one variable declaration
		if(variable_list != null){
			//iterate over the variables
			for(int curName = 0; curName < variable_list.length; curName++){
				String curVar = variable_list[curName].trim();
				boolean isArrayVar = curVar.contains("[");//check if an array type was declared in the variable instead of the type
				if(isArrayType || isArrayVar){
					//decompose the names with their respective indices
					ArrayList<String> indices = ShaderParser.parseArray(baseType, curVar);
					//iterate over them and add them to the fields using the base type
					for(String indexName : indices){
						names.add(indexName);
						numVars++;
					}
				}else{//we are absolutely certain by this point it is a simple uniform
					names.add(curVar);
					numVars++;
				}
			}
		}else{//this means there aren't multiple variable declarations
			boolean isArrayVar = variables.contains("[");//check if an array type was declared in the variable instead of the type
			if(isArrayType || isArrayVar){
				ArrayList<String> indices = ShaderParser.parseArray(baseType, variables.trim());
				for(String indexName : indices){
					names.add(indexName);
					numVars++;
				}
			}else{//we are absolutely certain by this point it is a simple uniform
				names.add(variables.trim());
				numVars++;
			}
		}
		return numVars;
	}
	

	
//	private void genUniforms(String type, String names){
//		String[] varNames = names.trim().replaceAll("="+\\s*+"("+type+initializers+")|true|false|\\d+|\\d+\\.\\d+f?)"+\\s*+",?", ",").split(\\s*+","+\\s*);
//		
//		for(int vars = 0; vars < varNames.length; vars++){
//			if(!varNames[vars].contains("[")){
//				uniforms.put(varNames[vars], new Uniform(varNames[vars], type));
//			}
//			else{
//				int indexOfBrace1 = varNames[vars].indexOf("[");
//				int indexOfBrace2 = varNames[vars].indexOf("]");
//				int arraySize = Integer.parseInt(varNames[vars].substring(indexOfBrace1+1, indexOfBrace2));
//				String uniformName = varNames[vars].substring(0, indexOfBrace1);
//				
//				for(int array = 0; array < arraySize; array++){
//					uniforms.put(uniformName+"["+array+"]", new Uniform(uniformName+"["+array+"]", type));
//				}
//			}
//		}
//	}
//	
//	/**
//	 * Generates a shader object, reads the source code for a shader, and compiles the shader to be used
//	 * in the rendering pipeline
//	 * 
//	 * @param fileName String specifying the file path to the file containing the shader code
//	 * 
//	 * @param shaderType The type of shader to be generated, this must be one of the types specified
//	 * in the openGL specification, that is GL_VERTEX_SHADER or GL_FRAGMENT_SHADER
//	 * 
//	 * @param gl OpenGL graphics context with which to work with shaders
//	 * 
//	 * @return Returns an int representing the handle for the generated shader object
//	 */
//	protected int createShader(String fileName, int shaderType){
//		int shaderID = 0;
//		StringBuilder source = new StringBuilder();
//		
//		/*scan the file taking each line and adding it to the source array list and the lengths of each line to the 
//		Integer array list*/
//		
//		//System.out.println(source.toString());
//		//create a shader object and store it in a handler for the application to use
//		shaderID = glCreateShader(shaderType);
//		//send the source code read from the file to the shader object
//		glShaderSource(shaderID, source.toString());
//		//compile the source code stored on the specified shader object
//		glCompileShader(shaderID);
//		
//		return shaderID;
//	}
//	
//	/**
//	 * 
//	 * @param structName
//	 * @param uniformNames Names of the instances of the uniform structure to be formed
//	 * @param structMap
//	 */
//	private void genUniformStructs(String structName, String uniformNames, HashMap<String, ShaderStruct> structMap){
//		//split up the instance names
//		String[] instanceNames = uniformNames.replaceAll("="+\\s*+"("+structName+initializers+")|true|false|\\d+|\\d+\\.\\d+f?)"+\\s*+",?", ",").split(\\s*+","+\\s*);
//		//iterate over every instance name
//		for(int curInstance = 0; curInstance < instanceNames.length; curInstance++){
//			//get the name of the instance
//			String curVar = instanceNames[curInstance];
//			//check if it is an array type
//			if(!curVar.contains("[")){
//				genStructUniform(curVar, structName, structMap);
//			}
//			else{
//				int indexOfBrace1 = curVar.indexOf("[");
//				int indexOfBrace2 = curVar.indexOf("]");
//				int arraySize = Integer.parseInt(curVar.substring(indexOfBrace1+1, indexOfBrace2));
//				String varName = curVar.substring(0, indexOfBrace1);
//				//create the different names for each element of the array
//				for(int array = 0; array < arraySize; array++){
//					genStructUniform(varName+"["+array+"]", structName, structMap);
//				}
//			}
//		}
//	}
//	
//	private void genStructUniform(String name, String curType, HashMap<String, ShaderStruct> structMap){
//		String glslTypes = "float|int|uint|bool|mat2|mat2x2|mat2x3|mat2x4|mat3|mat3x3|mat3x2|mat3x4|mat4|mat4x4|mat4x2|mat4x3|"+
//				"vec2|uvec2|ivec2|bvec2|vec3|uvec3|ivec3|bvec3|vec4|uvec4|ivec4|bvec4";
//		if(curType.matches(glslTypes)){
//			uniforms.put(name, new Uniform(name, curType));
//		}else{
//			ShaderStruct current = structMap.get(curType);
//			for(int structVar = 0; structVar < current.fields.size(); structVar++){
//				String curName = name+"."+current.fields.get(structVar);
//				genStructUniform(curName, current.fieldTypes.get(structVar), structMap);
//			}
//		}
//	}
}
