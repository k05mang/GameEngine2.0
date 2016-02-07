package mesh.primitives;

import gldata.IndexBuffer;
import gldata.VertexArray;
import gldata.BufferObject;

public class Face {
	public Edge e1, e2, e3;
	public HalfEdge he1, he2, he3;
	//public boolean isStored;
	public static final int INDEX_ADJ = 6, INDEX_NOADJ = 3;
	
	/**
	 * Constructs a triangle primitive from a set of given vertex indices.
	 * <p>
	 * The triangle constructed with these indices will be wound in the order that
	 * the constructor receives the indices
	 * 
	 * @param v0 First vertex of the triangle 
	 * @param v1 Second vertex of the triangle 
	 * @param v2 Third vertex of the triangle 
	 */
	public Face(int v0, int v1, int v2){
		//isStored = false;
		
		e1 = new Edge(v0, v1);
		e2 = new Edge(v1, v2);
		e3 = new Edge(v2, v0);

		he1 = new HalfEdge(v0);
		he2 = new HalfEdge(v1);
		he3 = new HalfEdge(v2);
		
		//setting the sequential ordering of the half edges
		he1.next = he2;
		he2.next = he3;
		he3.next = he1;
		
		//setting the reverse ordering of the half edges
		he1.prev = he3;
		he2.prev = he1;
		he3.prev = he2;
		
		//setting the half edges parent face
		he1.parent = this;
		he2.parent = this;
		he3.parent = this;
	}
	
	/**
	 * Constructs a face that is a copy of the given face. The given face will only have its basic
	 * primitive indices copied, other information stored in the face, such as adjacency information,
	 * will not be copied.
	 * 
	 * @param face Face to copy
	 */
	public Face(Face face){
		this(face.he1.sourceVert, face.he2.sourceVert, face.he3.sourceVert);
	}
	
	/**
	 * Stores this face's vertex indices, representing the primitive of the face, in the given index buffer
	 * 
	 * @param vao IndexBuffer to place the indices into
	 */
	public void insertPrim(IndexBuffer vao){
		vao.add(he1.sourceVert);
		vao.add(he2.sourceVert);
		vao.add(he3.sourceVert);
	}

	
	/**
	 * Stores this face's vertex indices, representing the primitive and adjacent information compatible with
	 * GL_TRIANGLES_ADJACENCY, in the given index buffer
	 * 
	 * @param buffer IndexBuffer to place the indices into
	 */
	public void insertPrimAdj(IndexBuffer buffer){
		buffer.add(he1.sourceVert);
		buffer.add(he1.opposite != null ? he1.opposite.prev.sourceVert : -1);
		
		buffer.add(he2.sourceVert);
		buffer.add(he2.opposite != null ? he2.opposite.prev.sourceVert : -1);
		
		buffer.add(he3.sourceVert);
		buffer.add(he3.opposite != null ? he3.opposite.prev.sourceVert : -1);
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Face){
			Face face = (Face)o;
			return face.he1.sourceVert == he1.sourceVert &&
					face.he2.sourceVert == he2.sourceVert &&
					face.he3.sourceVert == he3.sourceVert;
		}else{
			return false;
		}
	}
}
