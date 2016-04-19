package mesh.primitives;

import java.util.Arrays;

/**
 * Represents an edge of a Triangle that is part of a geometric mesh
 * 
 * @author Kevin Mango
 *
 */
public class  Edge {
	public int start, end;
	private int hashCode;
	
	/**
	 * Constructs an Edge with the given vertex indices as a start and end point for the Edge.
	 * The ordering of these inputs does determine equality of two Edges.
	 * <br>
	 * For example given two edges Edge1 (0,1), and Edge2 (1,0), Edge1 != Edge2 and 
	 * Edge1.hashCode() != Edge2.hashCode()
	 * 
	 * @param v1 Start vertex index for the Edge
	 * @param v2 End vertex index for the Edge
	 */
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
