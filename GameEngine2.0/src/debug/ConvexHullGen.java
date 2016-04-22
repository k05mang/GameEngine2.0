package debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import renderers.RenderMode;
import glMath.VecUtil;
import glMath.vectors.Vec3;
import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import gldata.IndexBuffer.IndexType;
import mesh.Geometry;
import mesh.Mesh;
import mesh.primitives.HalfEdge;
import mesh.primitives.Triangle;

public class ConvexHullGen extends Mesh {

	private Triangle baseTri;
	HashMap<Triangle, ArrayList<Integer>> conflictLists;//create a "queue" that will track what Triangles need to be tested for points and extruded
	LinkedList<Triangle> faces;
	
	public ConvexHullGen(Geometry mesh) {
		conflictLists = new HashMap<Triangle, ArrayList<Integer>>(4);
		faces = new LinkedList<Triangle>();
		geometry = mesh;
		//create the buffer for containing the vertices
		vbos.add(new BufferObject(BufferType.ARRAY));
		//create separate ibo for rendering lines
		ibos.add(new IndexBuffer(IndexType.INT));
		//create separate ibo for rendering the points
		ibos.add(new IndexBuffer(IndexType.INT));
		
		//add just the vertex position information to the vbo
		for(int curVert = 0; curVert < geometry.getNumVertices(); curVert++){
			vbos.get(0).add(geometry.getVertex(curVert).getPos());
			//initialize the point ibo with all the points
			ibos.get(1).add(curVert);
		}
		//generate the initial hull
		genHull();
		
		//add the faces from the initial tetrahedra to the queue
		faces.add(baseTri);
		faces.add(baseTri.he1.opposite.parent);
		faces.add(baseTri.he2.opposite.parent);
		faces.add(baseTri.he3.opposite.parent);
//		expandTetrahedra();
		//insert the base hull faces
		insertFaces();
		//flush vertices to the GPU
		vbos.get(0).flush(BufferUsage.STATIC_DRAW);
		
		//flush indices to the GPU
		ibos.get(1).flush(BufferUsage.STATIC_DRAW);
		
		//setup the vao
		//add attribute
		vao.addAttrib(AttribType.VEC3, false, 0);
		vao.enableAttribute(0);
		//add vbo
		vao.addVertexBuffer("mesh", vbos.get(0));
		vao.registerVBO("mesh");
		vao.setAttribVBO(0, "mesh");
		
		//add index buffers
		vao.addIndexBuffer("lines", RenderMode.LINES, ibos.get(0));
		vao.addIndexBuffer("points", RenderMode.POINTS, ibos.get(1));
		
		vao.setIndexBuffer("lines");
		
		//expand the initial tetrahedra to comprise the convex hull of the mesh
//		expandTetrahedra();
	}

	public ConvexHullGen(Mesh copy) {
		super(copy);
	}
	
	private void insertFaces(){
		HashMap<Triangle, Boolean> visited = new HashMap<Triangle, Boolean>();

		ibos.get(0).reset(IndexType.INT);
		
		insertFace(baseTri, visited);

		ibos.get(0).flush(BufferUsage.STATIC_DRAW);
	}
	
	private void insertFace(Triangle face, HashMap<Triangle, Boolean> visited){
		//check if the face has been visited by checking if it was marked in the map
		if(visited.get(face) == null){
			//if it hasn't continue
			//mark it as visited
			visited.put(face, true);
			
//			face.insertPrim(ibos.get(0));
			//insert the lines
			ibos.get(0).add(face.he1.sourceVert);
			ibos.get(0).add(face.he2.sourceVert);

			ibos.get(0).add(face.he2.sourceVert);
			ibos.get(0).add(face.he3.sourceVert);

			ibos.get(0).add(face.he3.sourceVert);
			ibos.get(0).add(face.he1.sourceVert);
			
			//continue recursing along the face edges
			insertFace(face.he1.opposite.parent, visited);
			insertFace(face.he2.opposite.parent, visited);
			insertFace(face.he3.opposite.parent, visited);
		}
	}
	
	private void setCurrentPoints(ArrayList<Integer> points){
		ibos.get(1).reset(IndexType.INT);
		for(Integer point : points){
			ibos.get(1).add(point);
		}
		ibos.get(1).flush(BufferUsage.STATIC_DRAW);
	}
	
	private void genHull(){
		int startVert = 0, endVert = 0;//vertex indices that make the longest edge
		float farthestDist = 0;//value to keep track of the current edge length of start and end
		//iterate over each vertex and compute its length with the other vertices and decide if it's the longest
		for(int curVal = 0; curVal < 6; curVal++){
			for(int nextVal = curVal+1; nextVal < 6; nextVal++){
				//compute the length
				float curLength = VecUtil.subtract(
						geometry.getVertex(geometry.getMinMaxIndex(curVal)).getPos(),//current vertex
						geometry.getVertex(geometry.getMinMaxIndex(nextVal)).getPos()//next vertex
						).length();
				//check if the computed length is longer than the previous one
				if(curLength > farthestDist){
					//if it is then set the tracking variables to the current values
					farthestDist = curLength;
					startVert = geometry.getMinMaxIndex(curVal);
					endVert = geometry.getMinMaxIndex(nextVal);
				}
			}
		}
		
		//find the vertex that is farthest from this edge
		int vertIndex = -1;//variable to hold the farthest vertex index
		farthestDist = 0;//variable to hold the current farthest vertex distance
		Vec3 edge = VecUtil.subtract(geometry.getVertex(endVert).getPos(), geometry.getVertex(startVert).getPos());//edge vector relative to the start vertex
		Vec3 normal = null;//triangle normal, this is computed to determine the normal of the edge relative to the vertex vector
		//iterate over the 6 extreme points and find the one farthest from the edge to create the triangle of the tetrahedra
		for(int curVertex = 0; curVertex < 6; curVertex++){
			//get the edge vector from the start vertex to the current vertex
			Vec3 relaPoint = VecUtil.subtract(geometry.getVertex(geometry.getMinMaxIndex(curVertex)).getPos(), geometry.getVertex(startVert).getPos());
			
			//compute the normal vector of the edge being tested relative to the current vertex vector, then get the scalar of the projection of the
			//vertex vector with the normal to get the distance of the vertex from the edge
			float distance = relaPoint.comp(VecUtil.cross(edge, relaPoint, edge));
			
			//test if the distance computed is farther than the previous distance computed and update the variables
			if(distance > farthestDist){
				farthestDist = distance;
				vertIndex = geometry.getMinMaxIndex(curVertex);
				normal = VecUtil.cross(edge, relaPoint);
			}
		}
		
		//create the initial triangle for the hull
		baseTri = new Triangle(startVert, endVert, vertIndex);
		//find the vertex that is farthest from this triangle in the direction of the triangle normal
		//reset variables for reuse with the next vertex search
		normal.normalize();//normalize the face normal so that distance can be calculated with a simple dot product
		farthestDist = 0;
		vertIndex = -1;
		
		//create conflict lists for the different halves of the triangle for use in the expansion of the polyhedra
		ArrayList<Integer> posList = new ArrayList<Integer>(geometry.getNumVertices()), //vertex indices that are in front of the triangle
				negList = new ArrayList<Integer>(geometry.getNumVertices());//vertex indices that are in behind of the triangle

		boolean inFront = false;//boolean indicating what side of the triangle the farthest point will be which will decide which
		//list is assigned to the face and which gets partitioned
		
		//iterate over all the vertices and test which is the farthest
		for(int curPoint = 0; curPoint < geometry.getNumVertices(); curPoint++){
			//get the current point relative to the start point on the triangle
			Vec3 relaPoint = VecUtil.subtract(geometry.getVertex(curPoint).getPos(), geometry.getVertex(startVert).getPos());
			float distance = relaPoint.dot(normal);//since normal is normalized the magnitude of the projection of
			//the current point onto the normal is simply the dot product between them
			
			//check if the point is in front of the triangle or behind it based on whether the distance is positive or negative
			if(distance > 0){
				posList.add(curPoint); 
			}else{
				negList.add(curPoint);
			}
			
			//check if the distance is greater than the previous farthest distance
			if(Math.abs(distance) > farthestDist){
				farthestDist = Math.abs(distance);
				vertIndex = curPoint;
				//determine if the current farthest point is in front of the triangle or behind it, this is so we know what list to assign to
				//triangle after the loop completes
				inFront = distance > 0;
			}
		}

		
		ArrayList<Integer> partitionList = null;
		//first determine if the final vertex to make the tetrahedra was in front of or behind the triangle
		//this will decide which set of points to partition to the faces
		if(inFront){
			//since the vertex for the tetrahedra was in front of the triangle the positive list will
			//be partitioned for the other faces
			partitionList = posList;
			
			//additionally the winding of the base triangle needs to changed so that the partitioned list
			//points will now be in front of the triangle
			baseTri = new Triangle(baseTri.he3.sourceVert, baseTri.he2.sourceVert, baseTri.he1.sourceVert);
			
			//only add the conflict list for the face if it has points to work with
			if(!negList.isEmpty()){
				conflictLists.put(baseTri, negList);
			}
			
		}else{
			//since the vertex for the tetrahedra was behind the triangle the negative list will
			//be partitioned for the other faces
			partitionList = negList;
			//only add the conflict list for the face if it has points to work with
			if(!posList.isEmpty()){
				conflictLists.put(baseTri, posList);
			}
		}
		
		//create additional faces from the base triangle to the vertex to build the initial tetrahedra to expand
		Triangle face1 = new Triangle(vertIndex, baseTri.he2.sourceVert, baseTri.he1.sourceVert);
		Triangle face2 = new Triangle(vertIndex, baseTri.he3.sourceVert, baseTri.he2.sourceVert);
		Triangle face3 = new Triangle(vertIndex, baseTri.he1.sourceVert, baseTri.he3.sourceVert);
		
		//setup adjacency
		baseTri.he1.opposite = face1.he2;
		baseTri.he2.opposite = face2.he2;
		baseTri.he3.opposite = face3.he2;
		
		face1.he1.opposite = face2.he3;
		face1.he2.opposite = baseTri.he1;
		face1.he3.opposite = face3.he1;
		
		face2.he1.opposite = face3.he3;
		face2.he2.opposite = baseTri.he2;
		face2.he3.opposite = face1.he1;
		
		face3.he1.opposite = face1.he3;
		face3.he2.opposite = baseTri.he3;
		face3.he3.opposite = face2.he1;
		
		//partition points between the faces and add them to the mapping
		partitionPoints(partitionList, face1);
		partitionPoints(partitionList, face2);
		partitionPoints(partitionList, face3);
	}
	
	private void partitionPoints(ArrayList<Integer> points, Triangle face){
		//create an array list that will contain the found conflicting points for the Triangle
		ArrayList<Integer> conflictList = new ArrayList<Integer>(points.size());
		//calculate the face normal for use in determining whether the points is in front of the Triangle
		Vec3 normal = face.getNormal(geometry);
		//iterate over all the points that could be in front of the current face
		Iterator<Integer> pointsList = points.iterator();
		while(pointsList.hasNext()){
			Integer curIndex = pointsList.next();
			Vec3 curPoint = VecUtil.subtract(geometry.getVertex(curIndex).getPos(), geometry.getVertex(face.he1.sourceVert).getPos());
			//determine if the point is in front of the face based on whether the dot product is positive
			//additionally 0 is considered behind the face
			if(normal.dot(curPoint) > 0){
				//if so then add the point to the conflict list for the current Triangle face
				conflictList.add(curIndex);
				pointsList.remove();
			}
		}
		conflictLists.put(face, conflictList);
	}
	
	public void expandTetrahedra(){
		
		//iterate over the faces of the queue until the queue is empty
		if(!faces.isEmpty()){
			//get the next face in the queue
			Triangle curFace = faces.poll();
			//if it has a conflict list then determine what point in the list is farthest from the face
			ArrayList<Integer> curConflict = conflictLists.get(curFace);
			if(curConflict != null){
				if(!curConflict.isEmpty()){
//					setCurrentPoints(curConflict);
					//find the point farthest from the face
					//calculate the face normal
					Vec3 normal = curFace.getNormal(geometry);
					Vec3 vert1 = geometry.getVertex(curFace.he1.sourceVert).getPos();
					float prevDist = 0;
					int farIndex = -1;
					for(Integer curIndex : curConflict){
						float distance = normal.dot(VecUtil.subtract(geometry.getVertex(curIndex).getPos(), vert1));
						//check if the distance is greater than the previous distance
						if(distance > prevDist){
							//update the variables
							prevDist = distance;
							farIndex = curIndex;
						}
					}
					//update the hull with the new point
					extendHull(farIndex, curFace, faces);
				}
			}//otherwise the face was either deleted in a previous iteration or has been finalized on the hull
		}
	}
	
	private void extendHull(int newPoint, Triangle base, LinkedList<Triangle> faces){
		//create a list that will store the conflict list points of all the removed triangles
		ArrayList<Integer> partition = new ArrayList<Integer>(conflictLists.remove(base));//remove the first triangle from the conflict list 
		//since we know it will be deleted
		//create another list that will store the horizon edges
		ArrayList<HalfEdge> horizon = new ArrayList<HalfEdge>();
		//compute the edge horizon on the hull for the given point
		findHorizon(geometry.getVertex(newPoint).getPos(), base.he1.opposite, partition, horizon);
		findHorizon(geometry.getVertex(newPoint).getPos(), base.he2.opposite, partition, horizon);
		findHorizon(geometry.getVertex(newPoint).getPos(), base.he3.opposite, partition, horizon);
		
		Iterator<HalfEdge> edges = horizon.iterator();
		Triangle initial = null;
		Triangle prevTri = null;
		
		do{
			HalfEdge current = edges.next();
			//compute the new triangle
			Triangle curTri = new Triangle(newPoint, current.next.sourceVert, current.sourceVert);
			//setup adjacency information for the new triangle
			//connect to the previous face only if the previous faces exists which with the first face it won't
			if(prevTri != null){
				curTri.he1.opposite = prevTri.he3;
				prevTri.he3.opposite = curTri.he1;
			}else{
				initial = curTri;//since the first triangle needs to be remembered store it now since we are operating on it
			}
			//connect with the "hidden" face that is part of the hull
			curTri.he2.opposite = current;
			current.opposite = curTri.he2;
			
			//if this is the last triangle being created then we need to setup the adjacency with the 
			//first triangle to complete the loop
			if(!edges.hasNext()){
				curTri.he3.opposite = initial.he1;
				initial.he1.opposite = curTri.he3;
			}
			
			//partition the removed faces conflict lists to the new face
			partitionPoints(partition, curTri);
			
			//add the new face to the faces list for later iteration
			faces.add(curTri);
			
			//set the current triangle to be the previous triangle for the next iteration
			prevTri = curTri;
		}while(edges.hasNext());

		//check if the face we removed also was the triangle we use as an entry point into the half edge data structure
		if(conflictLists.get(baseTri) == null){
			//if it is then set the first new triangle to be the new entry triangle
			baseTri = initial;
		}
		
		insertFaces();
	}
	
	/**
	 * Finds the Horizon edges for adding the new point of the hull
	 * 
	 * @param newPoint Point being added to the hull
	 * @param current Current half edge being processed for the DFS of the triangle faces of the hull
	 * @param partitionPoints List of all the collected points to partition as a result of removing faces from the hull
	 * @param horizon Array list to store the edges of the horizon
	 * @param conflictLists List of the points that are in front of a triangle that are used to generate a new point
	 * this list is also doubling as a "visited" marker for the recursive search. Once a face is visited it
	 * will be removed from this list and have it's points collected for later partitioning among the new faces, this
	 * process is necessary for removing the old faces from the hull.
	 */
	private void findHorizon(Vec3 newPoint, 
			HalfEdge current, 
			ArrayList<Integer> partitionPoints, 
			ArrayList<HalfEdge> horizon){
		//check if the current half edges parent face has already not been visited or deleted
		if(conflictLists.get(current.parent) != null){
			//check if the current half edges parent face can be "seen" from the new point of the hull
			//get the normal of the current face
			Vec3 normal = current.parent.getNormal(geometry);
			//get the vector from a point on the triangle to the new point
			Vec3 newEdge = VecUtil.subtract(newPoint, geometry.getVertex(current.sourceVert).getPos());
			//if it can, continue searching through the edges of the current face
			if(normal.dot(newEdge) > 0){
				//mark the current half edges parent face as being visited
				partitionPoints.addAll(conflictLists.remove(current.parent));//add it's conflict list to the list of points and remove it from the map
				//continue the search through the other edges that we haven't come from
				findHorizon(newPoint, current.next.opposite, partitionPoints, horizon);
				findHorizon(newPoint, current.prev.opposite, partitionPoints, horizon);
			}else{//otherwise add the half edge to the horizon
				horizon.add(current);
			}
		}
	}
}
