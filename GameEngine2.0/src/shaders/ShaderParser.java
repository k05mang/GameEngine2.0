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
	
	//basic components for more complex regular expressions
	private static final String glslTypes = "bool|uint|int|float|double|((b|u|i|d)?vec\\d?)|(d?mat(\\d?|\\d?x\\d?))|((u|i)?(image|sampler)\\w+)";
	private static final String number = "((\\+|\\-)?+"+//sign component
										"(0(x|X)?+)?+"+//octal or hex value or neither
										"(((\\d|[A-Fa-f])++(\\.\\d++)?+)|"+//numeric or hex value
										"(\\.\\d++))++"+//decimal point
										"(u|U|f|F|lf|LF|((e|E)(\\+|\\-)?+\\d++))?+)";//ending signifier for unsigned ints, floats, doubles or power variables such as 1e23
	
	private static final String arrayBraces = "(\\[\\s*+\\d*+\\s*+\\])";
	private static final String type_name = "(\\w++"+arrayBraces+"*+)";
	private static final String parameters =  "(\\s*+"+number+"|true|false|("+type_name+"(\\.\\w++)*+)\\s*+)";//handles parameters to the constructor
	private static final String operator = "(\\s*+\\+|\\-|\\*|/|%\\s*+)";//potentially add more operators in the future
	private static final String operations = "(\\s*+(\\("+parameters+"("+operator+parameters+")+\\))|("+parameters+"("+operator+parameters+")++)\\s*+)";//deals with operations between values
	private static final String cast = "(\\s*+\\("+glslTypes+"\\)\\s*+)?+";//handles casting types
	private static final String start = "(\\s*+("+type_name+"\\()|\\{\\s*+)";//handles the beginning of the constructor invocation
	private static final String end = "(\\)|\\})";//handles the closing braces of the constructor
	//---------------------------------------------------------------------
	private static final String initializer = start+"("+cast+parameters+"*+\\s*+,?+\\s*+)*+"+end;//initializing a variable at the simplest level this could be next in other initializers
	
	//for defining a variable declaration which is a type followed by a variable name and then potentially comma separated names
	private static final String declarators = "("+type_name+"\\s*+(\\s*+,?+\\s*+"+type_name+"\\s*+)*+)";
	private static final String type_declarators = "(\\s*+"+type_name+//variable type
													"\\s++"+declarators+"\\s*+)";//comma separated values that might follow
	
	//qualifiers used in uniform declarations first is a more general type with the second being more for uniforms
	private static final String layoutQaulifiers = "(\\s*+\\(((\\w++(\\s*+=\\s*+\\w++)?+)\\s*+,?+\\s*+)++\\)\\s*+)";
	private static final String uniformQualifiers = "(((("+layoutQaulifiers+")|uniform)\\s++)++)";
	
	//generic member list for both structs and for uniform interfaces
	private static final String member_list = "\\{"
												+"(\\s*+"+uniformQualifiers+"?+"+type_declarators+";)++"+//handling elements between the definition braces
												"\\}";
	private static final String structDec = "\\s*+struct\\s++\\w++\\s*+";
	
	//main definitions for items being extracted from the shader file
	private static final String structDefinition = structDec+"\\{"
													+"(\\s*+"+type_declarators+";)++"+//handling elements between the definition braces
													"\\}\\s*+"+declarators+"*+;";
	
	private static final String uniformDefinition = uniformQualifiers+type_declarators+";";
	
	private static final String uniformBlock = uniformQualifiers+"\\s*+"+type_name+"\\s*+"+member_list+"\\s*+"+type_name+"?+;";
	
	private static final long TIME_OUT_THRESHOLD = 5000;//a 1/20th of a minute in milliseconds
	
	/**
	 * Constructs this object using the given file as the source file to parse. The source file is parsed and information is extracted
	 * from the file and stored in this object instance
	 * 
	 * @param file File to be parsed
	 */
	public ShaderParser(File file){
		structures = new HashMap<String, ShaderStruct>();
		source = new StringBuilder();
		uniforms = new ArrayList<Uniform>();
		try{
			Scanner shaderParser = new Scanner(file);
			String line;
			while(shaderParser.hasNextLine()){
				line = shaderParser.nextLine();
				//add source code line to stringBuilder
				source.append(line+"\n");
				//process potential uniforms or structs
				if(line.contains("uniform") || line.contains("struct")){
					//gather everything about the uniform until we are certain we have everything about this uniform
					//search for the ending semicolon
					StringBuilder uniformStructData = new StringBuilder(line);
					Matcher isStruct = Pattern.compile(structDefinition).matcher(uniformStructData);
					Matcher isUniInterface = Pattern.compile(uniformBlock).matcher("");
					long timeStart = System.currentTimeMillis();//mark the start time for this parsing
					do{
						boolean isUniformBlock = false;
						//determine if we are working with a uniform or struct
						if(uniformStructData.indexOf("uniform") > -1){
							//make sure there is enough information about the uniform to determine what type it is
							while(shaderParser.hasNextLine() && uniformStructData.indexOf(";") == -1){
								line = shaderParser.nextLine();
								source.append(line+"\n");
								uniformStructData.append(line.trim());
							}
							//determine if the uniform is a block interface and make sure all interface information is captured
							if(uniformStructData.indexOf("=") == -1 && uniformStructData.indexOf("{") > -1){//if there is not an equals and a brace we know it is an interface type
								isUniformBlock = true;
								//continue parsing until enough information has been gathered
								while(shaderParser.hasNextLine() && !isUniInterface.find()){
									line = shaderParser.nextLine();
									source.append(line+"\n");
									uniformStructData.append(line.trim());
									isUniInterface.reset(uniformStructData);
								}
							}
						}else{
							//while we haven't reached the end of the file and the end of a declaration, this way we can be certain that all lines of important data will be in uniformStructData 
							while(shaderParser.hasNextLine() && !isStruct.find()){
								line = shaderParser.nextLine();
								source.append(line+"\n");
								uniformStructData.append(line.trim());
								isStruct.reset(uniformStructData);
							}
						}
						
						//clean up the input of initializers
						String cleanInput = removeInitializer(uniformStructData.toString());
						uniformStructData.replace(0, uniformStructData.length(), cleanInput);
						
						//first process the structs for use in generating the uniforms
						processStruct(uniformStructData);
						//process the structs
						processUniforms(uniformStructData, isUniformBlock);
						isStruct.reset(uniformStructData);
						//by this point all properly formatted values should have been cleared out and only non struct or uniforms should be left or empty
						//this check prevents an infinite loop in the event of improperly formatted shader code
						//basically if data buffer has a "uniform" in it
						if(System.currentTimeMillis() - timeStart > TIME_OUT_THRESHOLD){
							System.err.println("Shader parser for file: \""+file.getName()+"\" failed");
							System.err.println("Parsing error while reading file has occurred, last processed data was: \""+uniformStructData+"\"");
							System.err.println("This line may contain incorrectly formatted data incomopatible with glsl, struct or uniform likely malformed");
							shaderParser.close();
							System.exit(0);
						}
					}while(uniformStructData.indexOf("uniform") > -1 || uniformStructData.indexOf("struct") > -1);//while there is still a uniform or struct to process in the stringbuilder
					
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
	 * Gets the struct map for this shader parser, this map contains the parsed structs from this shader file
	 * 
	 * @return HashMap containing the ShaderStruct extracted from this shader file
	 */
	protected HashMap<String, ShaderStruct> getStructs(){
		return structures;
	}
	
	/**
	 * Processes the given information as uniform variables and adds them to the uniform list, additionally modifies uniformData by 
	 * removing all instances of uniform declarations
	 * 
	 * @param uniformData StringBuilder containing uniform data extracted from the shader source file
	 */
	private void processUniforms(StringBuilder uniformData, boolean isUniformBlock){
		if(isUniformBlock){
			Matcher findUniform = Pattern.compile(uniformBlock).matcher(uniformData);
			while(findUniform.find()){
				
				String uniformsVars = findUniform.group().trim();
				//replace the qualifiers and make that the new parsing target
				Matcher findBlockName = Pattern.compile("\\s++\\w++\\{").matcher(uniformsVars);//for getting the block name
				findBlockName.find();
				String blockName = findBlockName.group();
				blockName = blockName.substring(0, blockName.length()-1).trim();//trim extra bracket {
				
				//find if there is an instance name for this block, and whether it is an array type
				Matcher isArray = Pattern.compile("\\}\\s*+\\w++"+arrayBraces+"++").matcher(uniformData);
				if(isArray.find()){
					String instanceName = isArray.group();
					String arrayBraces = instanceName.substring(instanceName.indexOf("["), instanceName.indexOf("]")+1);
					for(String name : parseArray("", blockName+arrayBraces)){
						//generate the variables and add them to the list of found uniforms
						uniforms.add(new Uniform(name, null));
					}
				}else{
					//generate the variables and add them to the list of found uniforms
					uniforms.add(new Uniform(blockName, null));
				}
				uniformData.delete(findUniform.start(), findUniform.end());
				findUniform.reset(uniformData);
			}
		}else{
			Matcher findUniform = Pattern.compile(uniformDefinition).matcher(uniformData);
			while(findUniform.find()){
				
				String uniformsVars = findUniform.group().trim();
				//replace the qualifiers and make that the new parsing target
				Matcher findQualifiers = Pattern.compile(uniformQualifiers).matcher(uniformsVars);//for removing "uniform" and layout qualifiers
				findQualifiers.find();
				
				uniformsVars = uniformsVars.substring(findQualifiers.end(),  uniformsVars.length()-1);//remove the semicolon at the end of the string
				ArrayList<String> variableNames = new ArrayList<String>();
				StringBuilder varType = new StringBuilder();
				parseVariable(uniformsVars, varType, variableNames);//parse data from variable declaration
				
				//add new data to uniform array list
				String type = varType.toString();//since the uniform constructor takes a string for it's type create a copy of the type for reuse in the loop
				ShaderStruct typeStruct = null;
				typeStruct = structures.get(type);
				//generate the variables and add them to the list of found uniforms
				for(String varName : variableNames){
					//check if the type is a struct type
					if(typeStruct != null){
						//have the struct generate the uniforms for this variable name
						for(Uniform uni : typeStruct.genUniforms(varName, structures)){
							uniforms.add(uni);
						}
					}else{
						uniforms.add(new Uniform(varName, type));
					}
				}
				uniformData.delete(findUniform.start(), findUniform.end());
				findUniform.reset(uniformData);
			}
		}
	}

	/**
	 * Processes the given information as struct declarations and constructs and adds ShaderStructs to the struct mapping, additionally all
	 * struct declarations are deleted from the structData variable. This means structData will have values modified.
	 * 
	 * @param structData StringBuilder containing information about one or more struct declarations parsed from the shader source file
	 */
	private void processStruct(StringBuilder structData){
		Matcher findStruct = Pattern.compile(structDefinition).matcher(structData);
		Matcher findName = Pattern.compile(structDec).matcher("");
		Matcher findMembers = Pattern.compile(member_list).matcher("");
		while(findStruct.find()){
			String struct = findStruct.group().trim();
			//find the struct type name
			findName.reset(struct);
			findName.find();
			StringBuilder name = new StringBuilder(findName.group());
			name.delete(0, 6);//remove the "struct" part to isolate the struct type name, struct is 6 characters long
			//find the members list
			findMembers.reset(struct);
			findMembers.find();
			String memberList = findMembers.group();
			memberList = memberList.substring(1, memberList.length()-1);//trim off the brackets {}
			structures.put(name.toString().trim(), new ShaderStruct(memberList.trim()));
			
			structData.delete(findStruct.start(), findStruct.end());
			findStruct.reset(structData);
		}
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
		Pattern arrayIndex = Pattern.compile(arrayBraces);
		
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
		int numVars = 0;
		//check if we need to empty the type parameter
		if(type.length() > 1){
			type.delete(0, type.length());//delete its contents if it has things in it
		}
		
		//pattern that matches for array types
		Matcher arrayType = Pattern.compile("^\\w++"+arrayBraces+"++").matcher(target);
		
		//separate the variable names from the type
		String baseType = null;
		String variables = null;
		boolean isArrayType = false;
		//check what type of variable it is
		if(arrayType.find()){
			baseType = arrayType.group().trim();//this type is the array type, which includes the base type
			type.append(target.substring(0, target.indexOf('[')));//this is the basic type that the array is of
			variables = target.substring(arrayType.end());
			isArrayType = true;
		}else{//this means the variable type is a plain object type like vec3
			//separate the type from the names
			String[] type_names = target.trim().replaceFirst("\\s++", "@").split("\\@");
			type.append(type_names[0].trim());
			baseType = type_names[0].trim();
			//base type and type will be the same since this isn't an array type
			variables = type_names[1].trim();
		}
		
		String[] variable_list = null;
		//determine if there are multiple declarations for this variable
		if(variables.contains(",")){
			variable_list = variables.split("\\s*+,\\s*+");
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
	
	/**
	 * Removes all initializers from the variables given
	 * 
	 * @param variable Variable to clean
	 * @return Variable cleaned of all initializers
	 */
	public static String removeInitializer(String variable){
		String result = variable;
		
		//remove all arithmetic operators and anything involving them such as numbers, operators, and parenthesis
		Matcher findOperations = Pattern.compile(operations).matcher(result);
		while(findOperations.find()){
			result = findOperations.replaceAll("0");//this will replace everything with a 0 this is for something like (23-42)*45/(78-21)->0*45/0->0
			findOperations.reset(result);
		}
		//remove all initializers
		Matcher findInit = Pattern.compile(initializer).matcher(result);
		//this loop handles nesting issues by eliminating nested values first and reducing the complexity of the wrapper constructors
		while(findInit.find()){
			result = findInit.replaceAll("");
//			System.out.println(result);
			findInit.reset(result);
		}
		//remove the "=" signs as well as any potential basic types like numbers and booleans
		Matcher equalsRemove = Pattern.compile("\\s*+=\\s*+"+parameters+"?+").matcher(result);
		return equalsRemove.replaceAll("");
	}
}
