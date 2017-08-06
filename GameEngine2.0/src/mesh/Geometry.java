package mesh;
import glMath.VecUtil;
import glMath.vectors.Vec2;
import glMath.vectors.Vec3;
import gldata.BufferObject;
import gldata.IndexBuffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import mesh.primitives.Edge;
import mesh.primitives.HalfEdge;
import mesh.primitives.Triangle;
import mesh.primitives.Vertex;
import physics.collision.AABB;
import renderers.RenderMode;

public class Geometry {
	private ArrayList<Vertex> vertices;
	private HashMap<Vertex, Integer> hashVerts;
	private ArrayList<Triangle> faces;
	private HashMap<Edge, HalfEdge> edgeMap;
	private int[] minMax;//cache for points that are farthest along each axis
	private Vec3 geometricCenter;//center point of all the vertices
	/*
	 * minMax[0] = Min x value
	 * minMax[1] = Max x value
	 * minMax[2] = Min y value
	 * minMax[3] = Max y value
	 * minMax[4] = Min z value
	 * minMax[5] = Max z value
	 */
	public static final int 
	MIN_X = 0,
	MAX_X = 1,
	MIN_Y = 2,
	MAX_Y = 3,
	MIN_Z = 4,
	MAX_Z = 5;
	
	/**
	 * Constructs an empty mesh for storing vertices and triangular faces
	 */
	public Geometry(){
		vertices = new ArrayList<Vertex>();
		hashVerts = new HashMap<Vertex, Integer>();
		faces = new ArrayList<Triangle>();
		edgeMap = new HashMap<Edge, HalfEdge>();
		minMax = new int[6];
		geometricCenter = new Vec3();
	}
	
	public Geometry(Geometry copy){
		vertices = new ArrayList<Vertex>(copy.vertices.size());
		hashVerts = new HashMap<Vertex, Integer>(copy.vertices.size());
		faces = new ArrayList<Triangle>(copy.faces.size());
		edgeMap = new HashMap<Edge, HalfEdge>();
		geometricCenter = new Vec3(copy.geometricCenter);
		
		//copy each vertex into this geometry object
		for(Vertex vert : copy.vertices){
			vertices.add(new Vertex(vert));
			hashVerts.put(vertices.get(vertices.size()-1), vertices.size()-1);
		}
		
		//copy the faces
		for(Triangle triangle : copy.faces){
			add(triangle);
		}
		//copy the minMax indices
		minMax = Arrays.copyOf(copy.minMax, 6);
	}
	
	/**
	 * Gets the number of indices of the given value there are in the minMax array. This is to help
	 * determine if a new index should be assigned a min max value based on the quantity of that index
	 * in the array. This will help increase array diversity.
	 * 
	 * @param value Value to count in the array
	 * 
	 * @return number of indices in minMax with that value
	 */
	private int getCount(int value){
		int count = 0;
		if(minMax[MIN_X] == value){
			count++;
		}
		if(minMax[MAX_X] == value){
			count++;
		}
		if(minMax[MIN_Y] == value){
			count++;
		}
		if(minMax[MAX_Y] == value){
			count++;
		}
		if(minMax[MIN_Z] == value){
			count++;
		}
		if(minMax[MAX_Z] == value){
			count++;
		}
		
		return count;
	}
	
	/**
	 * Adds a vertex to this mesh
	 * 
	 * @param vert Vertex to add to this mesh
	 */
	public void add(Vertex vert){
		vertices.add(new Vertex(vert));
		hashVerts.put(vertices.get(vertices.size()-1), vertices.size()-1);
		//compute the new geomtric center
		geometricCenter.scale((vertices.size()-1)/(float)vertices.size());//scale the current center by (n-1)/n
		//this brings the previous vertices into the appropriate average range
		//the center is defined as the average of the sums of the vertices, but since we don't know when 
		//the max number of vertices has been added we only have the n-1 average stored in the geometric center
		//to adjust those previous values to bring them into the n average with the scalar (n-1)/n
		geometricCenter.add(VecUtil.scale(vert.getPos(), 1.0f/vertices.size()));
		
		//check if this vertex is a minimum or maximum along any axis
		//X min
		if(vert.getPos().x < vertices.get(minMax[0]).getPos().x){
			minMax[0] = vertices.size()-1;
		}else if(vert.getPos().x == vertices.get(minMax[0]).getPos().x && getCount(minMax[0]) > 1){
			//if the value we checked would also qualify for the min value and the value already in the list
			//has multiple assignments, then we can replace this with the new vertex
			minMax[0] = vertices.size()-1;
		}

		//X max
		if(vert.getPos().x > vertices.get(minMax[1]).getPos().x){
			minMax[1] = vertices.size()-1;
		}else if(vert.getPos().x == vertices.get(minMax[1]).getPos().x && getCount(minMax[1]) > 1){
			//if the value we checked would also qualify for the min value and the value already in the list
			//has multiple assignments, then we can replace this with the new vertex
			minMax[1] = vertices.size()-1;
		}
		
		//Y min
		if(vert.getPos().y < vertices.get(minMax[2]).getPos().y){
			minMax[2] = vertices.size()-1;
		}else if(vert.getPos().y == vertices.get(minMax[2]).getPos().y && getCount(minMax[2]) > 1){
			//if the value we checked would also qualify for the min value and the value already in the list
			//has multiple assignments, then we can replace this with the new vertex
			minMax[2] = vertices.size()-1;
		}

		//Y max
		if(vert.getPos().y > vertices.get(minMax[3]).getPos().y){
			minMax[3] = vertices.size()-1;
		}else if(vert.getPos().y == vertices.get(minMax[3]).getPos().y && getCount(minMax[3]) > 1){
			//if the value we checked would also qualify for the min value and the value already in the list
			//has multiple assignments, then we can replace this with the new vertex
			minMax[3] = vertices.size()-1;
		}
		
		//Z min
		if(vert.getPos().z < vertices.get(minMax[4]).getPos().z){
			minMax[4] = vertices.size()-1;
		}else if(vert.getPos().z == vertices.get(minMax[4]).getPos().z && getCount(minMax[4]) > 1){
			//if the value we checked would also qualify for the min value and the value already in the list
			//has multiple assignments, then we can replace this with the new vertex
			minMax[4] = vertices.size()-1;
		}

		//Z max
		if(vert.getPos().z > vertices.get(minMax[5]).getPos().z){
			minMax[5] = vertices.size()-1;
		}else if(vert.getPos().z == vertices.get(minMax[5]).getPos().z && getCount(minMax[5]) > 1){
			//if the value we checked would also qualify for the min value and the value already in the list
			//has multiple assignments, then we can replace this with the new vertex
			minMax[5] = vertices.size()-1;
		}
	}
	
	/**
	 * Gets the geometric center of all the vertices
	 * 
	 * @return Center point relative to all this geometries vertices
	 */
	public Vec3 getGeometricCenter(){
		return geometricCenter;
	}
	
	/**
	 * Translates all the vertices in this geometric object to the geometric center of the vertices
	 * this also changes the geometric center to be the origin, as all the points were moved
	 */
	public void moveToGeoCenter(){
		//only perform the move operation if the center is anything but the origin
		if(!geometricCenter.isZero()){
			//iterate over the vertices and translate their position vector by the geometric center
			for(int curVert = 0; curVert < vertices.size(); curVert++){
				//remove the old hashmap value for the current vertex, since it will need to be updated after the translation
				hashVerts.remove(vertices.get(curVert));
				//adjust the vertex position
				vertices.get(curVert).setPos(VecUtil.subtract(vertices.get(curVert).getPos(), geometricCenter));
				//add the adjusted vertex back into the hash map
				hashVerts.put(vertices.get(curVert), curVert);
			}
			//adjust the geometric center variable
			geometricCenter.set(0,0,0);
		}
	}
	
	/**
	 * Gets the index of the vertex of this geometry based on the requested value passed to {@code index}.
	 * Appropriate index values are defined as constants of this class.
	 * 
	 * @param index Value to query about this geometry
	 * 
	 * @return Index of the vertex that matches the requested value, if the index does not exist then a -1 is returned instead
	 */
	public int getMinMaxIndex(int index){
		//check to make sure the index is within the bounds of the array
		if(index < minMax.length && index > -1){
			return minMax[index];
		}else{
			//otherwise return -1 indicating unknown index
			return -1;
		}
	}
	
	/**
	 * Adds a face to this mesh, the face should contain indices that correspond to the inserted vertices of this mesh
	 * 
	 * @param triangle Face to add to this mesh
	 */
	public void add(Triangle triangle){
		faces.add(new Triangle(triangle));
		
		//create edge that the half edge is associated with but in reverse order, this is due to the edge that marked the half edge
		//potentially in the map has an opposite ordering
		Edge mapEdge1 = new Edge(triangle.e1.end, triangle.e1.start);
		//check if the edge map has that edge which should return the corresponding HalfEdge
		HalfEdge mapHE1 = edgeMap.get(mapEdge1);
		//check if we found the half edge
		if(mapHE1 != null){
			//if we did set their opposites to be each other
			triangle.he1.opposite = mapHE1;
			mapHE1.opposite = triangle.he1;
		}else{//if not add it to the map
			edgeMap.put(triangle.e1, triangle.he1);
		}
		
		Edge mapEdge2 = new Edge(triangle.e2.end, triangle.e2.start);
		//check if the edge map has that edge which should return the corresponding HalfEdge
		HalfEdge mapHE2 = edgeMap.get(mapEdge2);
		//check if we found the half edge
		if(mapHE2 != null){
			//if we did set their opposites to be each other
			triangle.he2.opposite = mapHE2;
			mapHE2.opposite = triangle.he2;
		}else{//if not add it to the map
			edgeMap.put(triangle.e2, triangle.he2);
		}
		
		Edge mapEdge3 = new Edge(triangle.e3.end, triangle.e3.start);
		//check if the edge map has that edge which should return the corresponding HalfEdge
		HalfEdge mapHE3 = edgeMap.get(mapEdge3);
		//check if we found the half edge
		if(mapHE3 != null){
			//if we did set their opposites to be each other
			triangle.he3.opposite = mapHE3;
			mapHE3.opposite = triangle.he3;
		}else{//if not add it to the map
			edgeMap.put(triangle.e3, triangle.he3);
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
		minMax[0] = 0;
		minMax[1] = 0;
		minMax[2] = 0;
		minMax[3] = 0;
		minMax[4] = 0;
		minMax[5] = 0;
	}
	
	/**
	 * Sets the value of the vertex at {@code index} to the given value {@code vert}
	 * 
	 * @param index Index of the vertex to modify
	 * @param vert Vertex to set the vertex at {@code index} to
	 */
	public void setVertex(int index, Vertex vert){
		vertices.get(index).set(vert);
	}
	
	/**
	 * Gets the number of vertices stored in this mesh
	 * 
	 * @return Number of vertices in this mesh
	 */
	public int numVertices(){
		return vertices.size();
	}
	
	/**
	 * Gets the number of faces in this mesh, a face is defined as a triangular portion of the mesh
	 * comprised of 3 connected vertices
	 * 
	 * @return Number of faces in this mesh
	 */
	public int numFaces(){
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
			throw new IndexOutOfBoundsException("Index out of bounds for retrieval of Vertex from mesh, Index: "+index+" Size: "+vertices.size());
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
		for(Triangle curFace : faces){
			Vertex v0 = vertices.get(curFace.he1.sourceVert);
			Vertex v1 = vertices.get(curFace.he2.sourceVert);
			Vertex v2 = vertices.get(curFace.he3.sourceVert);
			
			//get the current face normal
			Vec3 faceNormal = curFace.getNormal(this);
			
			//sum the current vertex normal and the face normal to get a normal that is the average of the faces sharing that normal
			v0.setNormal(VecUtil.add(v0.getNormal(), faceNormal));
			v1.setNormal(VecUtil.add(v1.getNormal(), faceNormal));
			v2.setNormal(VecUtil.add(v2.getNormal(), faceNormal));
		}
	}
	
	public void genTangentBitangent(){
		for(Triangle curFace : faces){
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
	public Triangle getFace(int index) throws IndexOutOfBoundsException{
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
				for(Triangle curFace : faces){
					curFace.insertPrim(buffer);
				}
				break;
			case TRIANGLES_ADJ:
				for(Triangle curFace : faces){
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
		for(Triangle curFace : faces){
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
