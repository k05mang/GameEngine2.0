package mesh;
import java.util.ArrayList;
import java.util.HashMap;

import mesh.primitives.Edge;
import mesh.primitives.Face;
import mesh.primitives.HalfEdge;
import mesh.primitives.Vertex;
import renderers.RenderMode;
import glMath.VecUtil;
import glMath.vectors.Vec2;
import glMath.vectors.Vec3;
import gldata.IndexBuffer;
import gldata.VertexArray;
import gldata.BufferObject;

public class Geometry {
	private ArrayList<Vertex> vertices;
	private HashMap<Vertex, Integer> hashVerts;
	private ArrayList<Face> faces;
	private HashMap<Edge, HalfEdge> edgeMap;
	
	/**
	 * Constructs an empty mesh for storing vertices and triangular faces
	 */
	public Geometry(){
		vertices = new ArrayList<Vertex>();
		hashVerts = new HashMap<Vertex, Integer>();
		faces = new ArrayList<Face>();
		edgeMap = new HashMap<Edge, HalfEdge>();
	}
	
	public Geometry(Geometry geo){
		vertices = new ArrayList<Vertex>(geo.vertices.size());
		hashVerts = new HashMap<Vertex, Integer>(geo.vertices.size());
		faces = new ArrayList<Face>(geo.faces.size());
		edgeMap = new HashMap<Edge, HalfEdge>();
		
		//copy each vertex into this geometry object
		for(Vertex vert : geo.vertices){
			vertices.add(new Vertex(vert));
			hashVerts.put(vert, vertices.size()-1);
		}
		
		//copy the faces
		for(Face face : geo.faces){
			add(face);
		}
	}
	
	/**
	 * Adds a vertex to this mesh
	 * 
	 * @param vert Vertex to add to this mesh
	 */
	public void add(Vertex vert){
		vertices.add(new Vertex(vert));
		hashVerts.put(vert, vertices.size()-1);
	}
	
	/**
	 * Adds a face to this mesh, the face should contain indices that correspond to the inserted vertices of this mesh
	 * 
	 * @param face Face to add to this mesh
	 */
	public void add(Face face){
		faces.add(new Face(face));
		
		//create edge that the half edge is associated with but in reverse order, this is due to the edge that marked the half edge
		//potentially in the map has an opposite ordering
		Edge mapEdge1 = new Edge(face.e1.end, face.e1.start);
		//check if the edgemap has that edge which should return the corresponding halfedge
		HalfEdge mapHE1 = edgeMap.get(mapEdge1);
		//check if we found the half edge
		if(mapHE1 != null){
			//if we did set their opposites to be each other
			face.he1.opposite = mapHE1;
			mapHE1.opposite = face.he1;
		}else{//if not add it to the map
			edgeMap.put(face.e1, face.he1);
		}
		
		Edge mapEdge2 = new Edge(face.e2.end, face.e2.start);
		//check if the edgemap has that edge which should return the corresponding halfedge
		HalfEdge mapHE2 = edgeMap.get(mapEdge2);
		//check if we found the half edge
		if(mapHE2 != null){
			//if we did set their opposites to be each other
			face.he2.opposite = mapHE2;
			mapHE2.opposite = face.he2;
		}else{//if not add it to the map
			edgeMap.put(face.e2, face.he2);
		}
		
		Edge mapEdge3 = new Edge(face.e3.end, face.e3.start);
		//check if the edgemap has that edge which should return the corresponding halfedge
		HalfEdge mapHE3 = edgeMap.get(mapEdge3);
		//check if we found the half edge
		if(mapHE3 != null){
			//if we did set their opposites to be each other
			face.he3.opposite = mapHE3;
			mapHE3.opposite = face.he3;
		}else{//if not add it to the map
			edgeMap.put(face.e3, face.he3);
		}
	}
	
	/**
	 * Empties all the data of this mesh and resets it to when it was initialized
	 */
	public void empty(){
		vertices.clear();
		hashVerts.clear();
		faces.clear();
		edgeMap.clear();
	}
	
	/**
	 * Sets the value of the vertex at {@code index} to the given value {@code vert}
	 * 
	 * @param index Index of the vertex to modify
	 * @param vert Vertex to set the vertex at {@code index} to
	 * @return True if the value was set, false if the index was out of bounds
	 */
	public boolean setVertex(int index, Vertex vert){
		if(index < 0 || index > vertices.size()-1){
			return false;
		}else{
			vertices.get(index).set(vert);
			return true;
		}
	}
	
	/**
	 * Gets the number of vertices stored in this mesh
	 * 
	 * @return Number of vertices in this mesh
	 */
	public int getNumVertices(){
		return vertices.size();
	}
	
	/**
	 * Gets the number of faces in this mesh, a face is defined as a triangular portion of the mesh
	 * comprised of 3 connected vertices
	 * 
	 * @return Number of faces in this mesh
	 */
	public int getNumFaces(){
		return faces.size();
	}
	
	/**
	 * Gets the Vertex at the specified index of this mesh
	 * 
	 * @param index Index of the Vertex to retrieve
	 * @return Vertex at the specified index in this mesh
	 * @throws IndexOutOfBoundsException
	 */
	public Vertex getVertex(int index) throws IndexOutOfBoundsException{
		if(index > vertices.size()-1 || index < 0){
			throw new IndexOutOfBoundsException("Index out of bounds for retrieval of Vertex from mesh");
		}else{
			return vertices.get(index);
		}
	}
	
	public int getIndex(Vertex vert){
		if(hashVerts.get(vert) != null){
			return hashVerts.get(vert);
		}else{
			return -1;
		}
	}
	
	public void genNormals(){
		for(Face curFace : faces){
			Vertex v0 = vertices.get(curFace.he1.sourceVert);
			Vertex v1 = vertices.get(curFace.he2.sourceVert);
			Vertex v2 = vertices.get(curFace.he3.sourceVert);
			
			//construct vectors with the first vertex as their origin
			//v1-v0
			Vec3 e1 = VecUtil.subtract(v1.getPos(), v0.getPos());
			
			//v2-v0
			Vec3 e2 = VecUtil.subtract(v2.getPos(), v0.getPos());
			
			//then compute their cross product
			Vec3 faceNormal = e1.cross(e2);
			
			//sum the current vertex normal and the face normal to get a normal that is the average of the faces sharing that normal
			v0.setNormal(VecUtil.add(v0.getNormal(), faceNormal));
			v1.setNormal(VecUtil.add(v1.getNormal(), faceNormal));
			v2.setNormal(VecUtil.add(v2.getNormal(), faceNormal));
		}
	}
	
	public void genTangentBitangent(){
		for(Face curFace : faces){
			Vertex v0 = vertices.get(curFace.he1.sourceVert);
			Vertex v1 = vertices.get(curFace.he2.sourceVert);
			Vertex v2 = vertices.get(curFace.he3.sourceVert);
			
			//construct vectors with the first vertex as their origin
			//v1-v0
			Vec3 e1 = VecUtil.subtract(v1.getPos(), v0.getPos());
			
			//v2-v0
			Vec3 e2 = VecUtil.subtract(v2.getPos(), v0.getPos());
			
			//compute the changes in the UVs
			Vec2 uv1 = VecUtil.subtract(v1.getUV(), v0.getUV());
			Vec2 uv2 = VecUtil.subtract(v2.getUV(), v0.getUV());
			
			//compute determinant of the texture space basis matrix
			float textDet = 1.0f/(uv1.x * uv2.y - uv1.y * uv2.x);
			
			Vec3 tangent = (VecUtil.scale(e1, uv2.y).subtract(VecUtil.scale(e2, uv1.y))).scale(textDet);
			Vec3 bitangent = (VecUtil.scale(e2, uv1.x).subtract(VecUtil.scale(e1, uv2.x))).scale(textDet);
			//add the tangent and bitangent to the vertices
			v0.addToTangent(tangent);
			v0.addToBitangent(bitangent);
			
			v1.addToTangent(tangent);
			v1.addToBitangent(bitangent);
			
			v2.addToTangent(tangent);
			v2.addToBitangent(bitangent);
		}
	}

	/**
	 * Gets the Face at the specified index of this mesh
	 * 
	 * @param index Index of the Face to retrieve
	 * @return Face at the specified index in this mesh
	 * @throws IndexOutOfBoundsException
	 */
	public Face getFace(int index) throws IndexOutOfBoundsException{
		if(index > faces.size()-1 || index < 0){
			throw new IndexOutOfBoundsException("Index out of bounds for retrieval of Face from mesh");
		}else{
			return faces.get(index);
		}
	}
	
	/**
	 * Inserts this meshes vertices into the given BufferObject
	 * 
	 * @param vao BufferObject to add the vertices to
	 */
	public void insertVertices(BufferObject buffer){
		for(Vertex vert : vertices){
			vert.addTo(buffer);
		}
	}

	/**
	 * Adds this meshes indices to the given index buffer with the specified RenderMode.
	 * <p>
	 * The following are the currently supported types for index insertion:
	 * <ul>
	 * <li>GL_POINTS</li>
	 * <li>GL_LINES</li>
	 * <li>GL_TRIANGLES</li>
	 * <li>GL_TRIANGLES_ADJACENCY</li>
	 * </ul>
	 * 
	 * @param buffer IndexBuffer to add indices to
	 * @param type RenderMode specifying the format with which to insert the indices
	 */
	public void insertIndices(IndexBuffer buffer, RenderMode type){
		switch(type){
			case POINTS:
				for(int index = 0; index < vertices.size(); index++){
					buffer.add(index);
				}
				break;
			case LINES:
				insertLines(buffer);
				break;
			case LINES_ADJ:
				break;
			case LINE_LOOP:
				break;
			case LINE_STRIP:
				break;
			case LINE_STRIP_ADJ:
				break;
				
			case TRIANGLES:
				for(Face curFace : faces){
					curFace.insertPrim(buffer);
				}
				break;
			case TRIANGLES_ADJ:
				for(Face curFace : faces){
					curFace.insertPrimAdj(buffer);
				}
				break;
			case TRIANGLE_FAN:
				break;
			case TRIANGLE_STRIP:
				break;
			case TRIANGLE_STRIP_ADJ:
				break;
				
			
			case PATCHES:
				break;
		}
	}

	/**
	 * Inserts this meshes indices into the given buffer object as separate lines without repeating pairs
	 * 
	 * @param buffer BufferObject to insert indices into
	 */
	private void insertLines(IndexBuffer buffer){
		HashMap<Edge, Boolean> visited = new HashMap<Edge, Boolean>();
		for(Face curFace : faces){
			//create edge that is ordered opposite since the edge in the map will have an opposite ordering when it was added
			Edge edge1 = new Edge(curFace.e1.end, curFace.e1.start);
			//check if the line being processed from this face was already added as part of another face iteration
			if(visited.get(edge1) == null){//if we didn't find it, it means it wasn't added yet
				buffer.add(curFace.e1.start);
				buffer.add(curFace.e1.end);
				visited.put(curFace.e1, true);
			}

			Edge edge2 = new Edge(curFace.e2.end, curFace.e2.start);
			//check if the line being processed from this face was already added as part of another face iteration
			if(visited.get(edge2) == null){//if we didn't find it, it means it wasn't added yet
				buffer.add(curFace.e2.start);
				buffer.add(curFace.e2.end);
				visited.put(curFace.e2, true);
			}
			
			Edge edge3 = new Edge(curFace.e3.end, curFace.e3.start);
			//check if the line being processed from this face was already added as part of another face iteration
			if(visited.get(edge3) == null){//if we didn't find it, it means it wasn't added yet
				buffer.add(curFace.e3.start);
				buffer.add(curFace.e3.end);
				visited.put(curFace.e3, true);
			}
		}
	}
}
