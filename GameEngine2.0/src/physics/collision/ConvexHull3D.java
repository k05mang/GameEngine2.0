package physics.collision;

import glMath.VecUtil;
import glMath.vectors.Vec3;
import glMath.vectors.Vec4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import mesh.Geometry;
import mesh.primitives.HalfEdge;
import mesh.primitives.Triangle;

public class ConvexHull3D extends ConvexHull {
	private Triangle baseTri;
	
	protected ConvexHull3D(Triangle baseTri, int vertIndex, boolean inFront, Geometry mesh, ArrayList<Integer> posList, ArrayList<Integer> negList){
		super(mesh);
		this.baseTri = baseTri;
		//create a hashmap to assign the triangles conflict lists in the expansion process
		HashMap<Triangle, ArrayList<Integer>> conflictLists = new HashMap<Triangle, ArrayList<Integer>>(4);
		ArrayList<Integer> partitionList = null;
		//first determine if the final vertex to make the tetrahedra was in front of or behind the triangle
		//this will decide which set of points to partition to the faces
		if(inFront){
			//since the vertex for the tetrahedra was in front of the triangle the positive list will
			//be partitioned for the other faces
			partitionList = posList;
			
			//additionally the winding of the base triangle needs to changed so that the partitioned list
			//points will now be in front of the triangle
			this.baseTri = new Triangle(this.baseTri.he3.sourceVert, this.baseTri.he2.sourceVert, this.baseTri.he1.sourceVert);
			
			//only add the conflict list for the face if it has points to work with
			if(!negList.isEmpty()){
				conflictLists.put(this.baseTri, negList);
			}
			
		}else{
			//since the vertex for the tetrahedra was behind the triangle the negative list will
			//be partitioned for the other faces
			partitionList = negList;
			//only add the conflict list for the face if it has points to work with
			if(!posList.isEmpty()){
				conflictLists.put(this.baseTri, posList);
			}
		}
		
		//create additional faces from the base triangle to the vertex to build the initial tetrahedra to expand
		Triangle face1 = new Triangle(vertIndex, this.baseTri.he2.sourceVert, this.baseTri.he1.sourceVert);
		Triangle face2 = new Triangle(vertIndex, this.baseTri.he3.sourceVert, this.baseTri.he2.sourceVert);
		Triangle face3 = new Triangle(vertIndex, this.baseTri.he1.sourceVert, this.baseTri.he3.sourceVert);
		
		//setup adjacency
		this.baseTri.he1.opposite = face1.he2;
		this.baseTri.he2.opposite = face2.he2;
		this.baseTri.he3.opposite = face3.he2;
		
		face1.he1.opposite = face2.he3;
		face1.he2.opposite = this.baseTri.he1;
		face1.he3.opposite = face3.he1;
		
		face2.he1.opposite = face3.he3;
		face2.he2.opposite = this.baseTri.he2;
		face2.he3.opposite = face1.he1;
		
		face3.he1.opposite = face1.he3;
		face3.he2.opposite = this.baseTri.he3;
		face3.he3.opposite = face2.he1;
		
		//partition points between the faces and add them to the mapping
		conflictLists.put(face1, 
				partitionPoints(mesh, partitionList, face1.getNormal(mesh), mesh.getVertex(face1.he1.sourceVert).getPos()));
		conflictLists.put(face2, 
				partitionPoints(mesh, partitionList, face2.getNormal(mesh), mesh.getVertex(face2.he1.sourceVert).getPos()));
		conflictLists.put(face3, 
				partitionPoints(mesh, partitionList, face3.getNormal(mesh), mesh.getVertex(face3.he1.sourceVert).getPos()));

		//expand the initial tetrahedra to comprise the convex hull of the mesh
		expandTetrahedra(conflictLists);
	}
	
	public ConvexHull3D(ConvexHull3D copy){
		super(copy);
		baseTri = copy.baseTri;
	}
	
	@Override
	public CollisionMesh copy(){
		return new ConvexHull3D(this);
	}
	
	private void expandTetrahedra(HashMap<Triangle, ArrayList<Integer>> conflictLists){
		//create a "queue" that will track what Triangles need to be tested for points and extruded
		LinkedList<Triangle> faces = new LinkedList<Triangle>();
		//add the faces from the initial tetrahedra to the queue
		faces.add(baseTri);
		faces.add(baseTri.he1.opposite.parent);
		faces.add(baseTri.he2.opposite.parent);
		faces.add(baseTri.he3.opposite.parent);
		//iterate over the faces of the queue until the queue is empty
		while(!faces.isEmpty()){
			//get the next face in the queue
			Triangle curFace = faces.poll();
			//if it has a conflict list then determine what point in the list is farthest from the face
			ArrayList<Integer> curConflict = conflictLists.get(curFace);
			//this indicates that the face was deleted
			if(curConflict != null){
				//check if there are any points to extend this faces to
				if(!curConflict.isEmpty()){
					//find the point farthest from the face
					//calculate the face normal
					Vec3 normal = curFace.getNormal(mesh);
					Vec3 vert1 = mesh.getVertex(curFace.he1.sourceVert).getPos();
					float prevDist = 0;
					int farIndex = -1;
					for(Integer curIndex : curConflict){
						float distance = normal.dot(VecUtil.subtract(mesh.getVertex(curIndex).getPos(), vert1));
						//check if the distance is greater than the previous distance
						if(distance > prevDist){
							//update the variables
							prevDist = distance;
							farIndex = curIndex;
						}
					}
					//update the hull with the new point
					extendHull(farIndex, curFace, faces, conflictLists);
				}
			}//otherwise the face was either deleted in a previous iteration or has been finalized on the hull
		}
	}
	
	private void extendHull(int newPoint, Triangle base, LinkedList<Triangle> faces, HashMap<Triangle, ArrayList<Integer>> conflictLists){
		//create a list that will store the conflict list points of all the removed triangles
		ArrayList<Integer> partition = new ArrayList<Integer>(conflictLists.remove(base));//remove the first triangle from the conflict list 
		//since we know it will be deleted
		//create another list that will store the horizon edges
		ArrayList<HalfEdge> horizon = new ArrayList<HalfEdge>();
		//compute the edge horizon on the hull for the given point
		findHorizon(mesh.getVertex(newPoint).getPos(), base.he1.opposite, partition, horizon, conflictLists);
		findHorizon(mesh.getVertex(newPoint).getPos(), base.he2.opposite, partition, horizon, conflictLists);
		findHorizon(mesh.getVertex(newPoint).getPos(), base.he3.opposite, partition, horizon, conflictLists);
		
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
			conflictLists.put(curTri, 
					partitionPoints(mesh, partition, curTri.getNormal(mesh), mesh.getVertex(curTri.he1.sourceVert).getPos()));
			
			//add the new face to the faces list for later iteration
			faces.add(curTri);
			
			//set the current triangle to be the previous triangle for the next iteration
			prevTri = curTri;
		}while(edges.hasNext());

		//check if a face we removed also was the triangle we use as an entry point into the half edge data structure
		if(conflictLists.get(baseTri) == null){
			//if it is then set the first new triangle to be the new entry triangle
			baseTri = initial;
		}
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
			ArrayList<HalfEdge> horizon, 
			HashMap<Triangle, ArrayList<Integer>> conflictLists){
		//check if the current half edges parent face has not been visited
		if(conflictLists.get(current.parent) != null){
			//check if the current half edges parent face can be "seen" from the new point of the hull
			//get the normal of the current face
			Vec3 normal = current.parent.getNormal(mesh);
			//get the vector from a point on the triangle to the new point
			Vec3 newEdge = VecUtil.subtract(newPoint, mesh.getVertex(current.sourceVert).getPos());
			//if it can, continue searching through the edges of the current face
			if(normal.dot(newEdge) >= 0){
				//mark the current half edges parent face as being visited
				partitionPoints.addAll(conflictLists.remove(current.parent));//add it's conflict list to the list of points and remove it from the map
				//continue the search through the other edges that we haven't come from
				findHorizon(newPoint, current.next.opposite, partitionPoints, horizon, conflictLists);
				findHorizon(newPoint, current.prev.opposite, partitionPoints, horizon, conflictLists);
			}else{//otherwise add the half edge to the horizon
				horizon.add(current);
			}
		}
	}

	@Override
	public Vec3 support(Vec3 direction) {
		//change the direction vector based on the orientation of the hull to bring it into model space
		//this way the vertices don't need to be transformed to world space to test against
		//the direction vector
		Vec3 orientedDir = transforms.getOrientation().conjugate().multVec(direction).normalize();
		
		//transform the final vertex back into world space
		return (Vec3)transforms.getTransform().multVec(new Vec4(findSupport(orientedDir, baseTri.he1),1)).swizzle("xyz");
	}
	
	private Vec3 findSupport(Vec3 direction, HalfEdge startEdge){
		
		//compute the dot product with the computed direction
		float foundDotDir = direction.dot(mesh.getVertex(startEdge.sourceVert).getPos());

		//iterate over all the adjacent vertices to the given vertex and find the vertex
		//most in the direction of the given vector
		HalfEdge curEdge = startEdge.opposite, foundEdge = startEdge;
		do{
			//get the adjacent vertex and get it's dot product with the direction to compute its distance in the given direction
			float curDotDir = direction.dot(mesh.getVertex(curEdge.sourceVert).getPos());
			
			//compare the dot product of the current adjacent vertex with the previous found vertex dot product
			if(curDotDir > foundDotDir){
				//since the current vertex dot product is greater than the previous one we know it is farther in the
				//direction of the direction vector than the previously found vertex
				foundDotDir = curDotDir;
				foundEdge = curEdge;
			}
			//update the current edge to be the next edge that has the next adjacent vertex to the starting vertex
			curEdge = curEdge.next.opposite;
		}
		while(!curEdge.opposite.equals(startEdge));//if the next edge we are comparing with is the same as the start end the loop
		
		//if no vertex was found then the current vertex is the vertex on the mesh farthest in the direction
		//of the given vector
		if(foundEdge.equals(startEdge)){
			return mesh.getVertex(foundEdge.sourceVert).getPos();
		}else{
			//recurse using the found vertex as a starting point
			return findSupport(direction, foundEdge);
		}
	}
}
