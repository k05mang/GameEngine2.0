package mesh.primitives;

import glMath.VecUtil;
import glMath.vectors.Vec3;
import gldata.IndexBuffer;

import java.util.Arrays;

import mesh.Geometry;

public class Triangle {
	public Edge e1, e2, e3;
	public HalfEdge he1, he2, he3;
	private int hashCode;
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
	public Triangle(int v0, int v1, int v2){
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
		
		hashCode = Arrays.hashCode(new int[]{he1.sourceVert, he2.sourceVert, he3.sourceVert});
	}
	
	/**
	 * Constructs a face that is a copy of the given face. The given face will only have its basic
	 * primitive indices copied, other information stored in the face, such as adjacency information,
	 * will not be copied.
	 * 
	 * @param triangle Face to copy
	 */
	public Triangle(Triangle triangle){
		this(triangle.he1.sourceVert, triangle.he2.sourceVert, triangle.he3.sourceVert);
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
	
	/**
	 * Gets the normal of the face based on the vertices from the given Geometry class that this Triangles
	 * vertex indices are associated with
	 * 
	 * @param mesh Geometry that this faces vertex indices are associated with, and that will be used in
	 * calculating the normal of the Triangle
	 * 
	 * @return Normalized Vec3 that represents the normal of the Triangle
	 */
	public Vec3 getNormal(Geometry mesh){
		Vec3 edge1 = VecUtil.subtract(mesh.getVertex(he2.sourceVert).getPos(), 
				mesh.getVertex(he1.sourceVert).getPos());
		Vec3 edge2 = VecUtil.subtract(mesh.getVertex(he3.sourceVert).getPos(), 
				mesh.getVertex(he1.sourceVert).getPos());
		return edge1.cross(edge2).normalize();
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Triangle){
			Triangle triangle = (Triangle)o;
			return triangle.he1.sourceVert == he1.sourceVert &&
					triangle.he2.sourceVert == he2.sourceVert &&
					triangle.he3.sourceVert == he3.sourceVert;
		}else{
			return false;
		}
	}
	
	@Override 
	public int hashCode(){
		return hashCode;
	}
	
	@Override
	public String toString(){
		return "Vertices: "+he1.sourceVert+"->"+he2.sourceVert+"->"+he3.sourceVert+
				"\nHalf Edge 1: "+he1.toString()+"\nHalf Edge 2: "+he2.toString()+"\nHalf Edge 3: "+he3.toString();
	}
}
