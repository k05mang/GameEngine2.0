package gldata;
import java.util.ArrayList;

public class ShaderParser {
	public static final String space1plus = "(\\s+|\\t+)+";
	public static final String space0plus = "(\\s*|\\t*)*";
	public static final String initializers = "(\\((\\d+|\\d+\\.\\d+f?|\\w|\\-|\\*|/|\\+|,|\\(|\\)|"+space0plus+")*\\)";
	
	public static ArrayList<String> parseArray(String variable){
		ArrayList<String> arrayIndices = new ArrayList<String>();
		int indexOfBrace1 = variable.indexOf("[");
		int indexOfBrace2 = variable.indexOf("]");
		int arraySize = Integer.parseInt(variable.substring(indexOfBrace1+1, indexOfBrace2));
		String varName = variable.substring(0, indexOfBrace1);
		//create the different names for each element of the array
		for(int array = 0; array < arraySize; array++){
			arrayIndices.add(varName+"["+array+"]");
		}
		 return arrayIndices;
	}
}
