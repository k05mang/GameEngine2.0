package primitives;

import java.util.Arrays;

/**
 * Represents an edge of a mesh and is generally contained in the Face class
 * @author Kevin Mango
 *
 */
public class  Edge {
	public int start, end;
	private int hashCode;
	
	public Edge(int v1, int v2){
		start = v1;
		end = v2;
	    hashCode = Arrays.hashCode(new int[]{start, end});//generate a hashcode for this edge
	}
	
	@Override
	public int hashCode(){
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof Edge){
			Edge edge = (Edge)obj;
			return edge.start == start && edge.end == end;
		}
		else{
			return false;
		}
	}
}
