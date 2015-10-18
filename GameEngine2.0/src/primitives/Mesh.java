package primitives;
import java.util.ArrayList;
import java.util.HashMap;

import gldata.VertexArray;
import gldata.BufferObject;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.*;

public class Mesh {
	private ArrayList<Vertex> vertices;
	private ArrayList<Face> faces;
	private HashMap<Edge, HalfEdge> edgeMap;
	
	public Mesh(){
		vertices = new ArrayList<Vertex>();
		faces = new ArrayList<Face>();
		edgeMap = new HashMap<Edge, HalfEdge>();
	}
	
	/**
	 * Adds a vertex to this mesh
	 * 
	 * @param vert Vertex to add to this mesh
	 */
	public void add(Vertex vert){
		vertices.add(vert);
	}
	
	/**
	 * Adds a face to this mesh, the face should contain indices that correspond to the inserted vertices of this mesh
	 * 
	 * @param face Face to add to this mesh
	 */
	public void add(Face face){
		faces.add(face);
		
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
	 * Inserts this meshes vertices into the given BufferObject
	 * 
	 * @param vao BUfferObject to add the vertices to
	 */
	public void insertVertices(BufferObject buffer){
		for(Vertex vert : vertices){
			vert.addTo(buffer);
		}
	}
	
	/**
	 * Inserts this meshes vertices into the given vertex array
	 * 
	 * @param vao VertexArray to add the vertices to
	 */
	public void insertVertices(VertexArray vao){
		for(Vertex vert : vertices){
			vert.addTo(vao);
		}
	}

	/**
	 * Adds this meshes indices to the given buffer object with the specified GLenum format.
	 * <p>
	 * The following are the currently supported types for indexing
	 * <ul>
	 * <li>GL_POINTS</li>
	 * <li>GL_LINES</li>
	 * <li>GL_TRIANGLES</li>
	 * <li>GL_TRIANGLES_ADJACENCY</li>
	 * </ul>
	 * 
	 * @param buffer BufferObject to add indices to
	 * @param type GLenum specifying the format with which to insert the indices
	 */
	public void insertIndices(BufferObject buffer, int type){
		switch(type){
			case GL_POINTS:
				for(int index = 0; index < vertices.size(); index++){
					buffer.add(index);
				}
				break;
			case GL_LINES:
				insertLines(buffer);
				break;
			case GL_TRIANGLES:
				for(Face curFace : faces){
					curFace.insertPrim(buffer);
				}
				break;
			case GL_TRIANGLES_ADJACENCY:
				for(Face curFace : faces){
					curFace.insertPrimAdj(buffer);
				}
				break;
		}
	}
	
	/**
	 * Adds this meshes indices to the given vertex array with the specified GLenum format.
	 * <p>
	 * The following are the currently supported types for indexing
	 * <ul>
	 * <li>GL_POINTS</li>
	 * <li>GL_LINES</li>
	 * <li>GL_TRIANGLES</li>
	 * <li>GL_TRIANGLES_ADJACENCY</li>
	 * </ul>
	 * 
	 * @param vao VertexArray to add indices to
	 * @param type GLenum specifying the format with which to insert the indices
	 */
	public void insertIndices(VertexArray vao, int type){
		switch(type){
			case GL_POINTS:
				for(int index = 0; index < vertices.size(); index++){
					vao.add(index);
				}
				break;
			case GL_LINES:
				insertLines(vao);
				break;
			case GL_TRIANGLES:
				for(Face curFace : faces){
					curFace.insertPrim(vao);
				}
				break;
			case GL_TRIANGLES_ADJACENCY:
				for(Face curFace : faces){
					curFace.insertPrimAdj(vao);
				}
				break;
		}
	}

	/**
	 * Inserts this meshes indices into the given buffer object as separate lines without repeating pairs
	 * 
	 * @param buffer BufferObject to insert indices into
	 */
	private void insertLines(BufferObject buffer){
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

	/**
	 * Inserts this meshes indices into the given vertex array as separate lines without repeating pairs
	 * 
	 * @param vao VertexArray to insert indices into
	 */
	private void insertLines(VertexArray vao){
		HashMap<Edge, Boolean> visited = new HashMap<Edge, Boolean>();
		for(Face curFace : faces){
			//create edge that is ordered opposite since the edge in the map will have an opposite ordering when it was added
			Edge edge1 = new Edge(curFace.e1.end, curFace.e1.start);
			//check if the line being processed from this face was already added as part of another face iteration
			if(visited.get(edge1) == null){//if we didn't find it, it means it wasn't added yet
				vao.add(curFace.e1.start);
				vao.add(curFace.e1.end);
				visited.put(curFace.e1, true);
			}

			Edge edge2 = new Edge(curFace.e2.end, curFace.e2.start);
			//check if the line being processed from this face was already added as part of another face iteration
			if(visited.get(edge2) == null){//if we didn't find it, it means it wasn't added yet
				vao.add(curFace.e2.start);
				vao.add(curFace.e2.end);
				visited.put(curFace.e2, true);
			}
			
			Edge edge3 = new Edge(curFace.e3.end, curFace.e3.start);
			//check if the line being processed from this face was already added as part of another face iteration
			if(visited.get(edge3) == null){//if we didn't find it, it means it wasn't added yet
				vao.add(curFace.e3.start);
				vao.add(curFace.e3.end);
				visited.put(curFace.e3, true);
			}
		}
	}
}
