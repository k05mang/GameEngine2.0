package mesh.primitives;

import java.util.Arrays;

/**
 * Acts as a node in the half edge data structure used for mesh data traversal.
 * 
 * @author Kevin Mango
 *
 */
public class HalfEdge {
	public Triangle parent;
	public HalfEdge opposite, next, prev;
	public Integer sourceVert;
	
	/**
	 * Constructs a HalfEdge object using a single vertex index as the emanating vertex for this HalfEdge
	 * 
	 * @param emenatingVert Index of the vertex this HalfEdge emanates from
	 */
	public HalfEdge(Integer emenatingVert){
		sourceVert = emenatingVert;
		parent = null;
		opposite = null;
		next = null;
		prev = null;
	}

	/**
	 * Tests whether the given HalfEdge is a valid opposite edge to this HalfEdge. A valid opposite HalfEdge
	 * is one where the source vertex is equal to the next vertex of the opposite edge and vice versa.
	 * Below is an example of how an opposite HalfEdge is formed:
	 * <br>
	 * 13->10 original
	 * 	  |
	 * 13<-10 opposite
	 * <br>
	 * the edge formed from these would be:
	 * (13, 10)
	 * 
	 * @param test HalfEdge to compare this HalfEdge to as being opposite
	 * 
	 * @return True if the given HalfEdge is a valid opposite edge to this HalfEdge, false otherwise
	 */
	public boolean isOpposite(HalfEdge test){
		return test == null ? false : sourceVert.equals(test.next.sourceVert) && next.sourceVert.equals(test.sourceVert);
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof HalfEdge){
			HalfEdge cast = (HalfEdge)o;
			return sourceVert == cast.sourceVert && (parent == null ? cast.parent == null : parent.equals(cast.parent));
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return Arrays.hashCode(new int[]{next.sourceVert, sourceVert, prev.sourceVert});
	}
	
	@Override
	public String toString(){
		return sourceVert+"->"+next.sourceVert;
	}
}
