package primitives;

/**
 * Acts as a node in the half edge data structure used for mesh data traversal.
 * 
 * @author Kevin Mango
 *
 */
public class HalfEdge {
	public Face parent;
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
	
//	public HalfEdge(HalfEdge copy){
//		parent = copy.parent;
//		opposite = copy.opposite;
//		next = copy.next;
//		sourceVert = copy.sourceVert;
//	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof HalfEdge){
			HalfEdge cast = (HalfEdge)o;
			return sourceVert == cast.sourceVert && parent.equals(cast.parent);
		}else{
			return false;
		}
	}
	
//	public void set(HalfEdge setter){
//		parent = setter.parent;
//		opposite = setter.opposite;
//		next = setter.next;
//		sourceVert = setter.sourceVert;
//	}
}
