package physics.collision;

import glMath.VecUtil;
import glMath.vectors.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import mesh.Geometry;
import mesh.primitives.HalfEdge;
import mesh.primitives.Triangle;

public class ConvexHull2D extends ConvexHull {

	protected ConvexHull2D(Triangle baseTri, Geometry mesh) {
		//create a hashmap to assign the edges conflict lists in the expansion process
		HashMap<HalfEdge, ArrayList<Integer>> conflictLists = new HashMap<HalfEdge, ArrayList<Integer>>(3);
				
		//partition the mesh vertices between the 3 edges of the triangle and store them in the conflict lists Hashmap
		//start by getting the triangle normal to use in calculating the normals for the edges
		Vec3 normal = baseTri.getNormal(mesh);
		//calculate the edge normals that will be initially used in determining the partition
		Vec3 edge1 = VecUtil.cross(
				VecUtil.subtract(mesh.getVertex(baseTri.he2.sourceVert).getPos(), mesh.getVertex(baseTri.he1.sourceVert).getPos()), //edge1
				normal).normalize();

		Vec3 edge2 = VecUtil.cross(
				VecUtil.subtract(mesh.getVertex(baseTri.he3.sourceVert).getPos(), mesh.getVertex(baseTri.he2.sourceVert).getPos()), //edge2
				normal).normalize();

		Vec3 edge3 = VecUtil.cross(
				VecUtil.subtract(mesh.getVertex(baseTri.he1.sourceVert).getPos(), mesh.getVertex(baseTri.he3.sourceVert).getPos()), //edge3
				normal).normalize();
		//create the index list to partition
		ArrayList<Integer> partitionList = new ArrayList<Integer>(mesh.getNumVertices());
		//initialize the index list
		for(int curPoint = 0; curPoint < mesh.getNumVertices(); curPoint++){
			partitionList.add(curPoint);
		}
		//partition the points
		conflictLists.put(baseTri.he1, 
				partitionPoints(mesh, partitionList, edge1, mesh.getVertex(baseTri.he1.sourceVert).getPos()));

		conflictLists.put(baseTri.he2, 
				partitionPoints(mesh, partitionList, edge2, mesh.getVertex(baseTri.he2.sourceVert).getPos()));

		conflictLists.put(baseTri.he3, 
				partitionPoints(mesh, partitionList, edge3, mesh.getVertex(baseTri.he3.sourceVert).getPos()));
		
	}

	public ConvexHull2D(ConvexHull copy) {
		super(copy);
	}
	
	private void expand(Vec3 planeNormal, HashMap<HalfEdge, ArrayList<Integer>> conflictLists){
		//create a "queue" that will track what Edges need to be tested for points and extended
		LinkedList<HalfEdge> edges = new LinkedList<HalfEdge>();
		//add the edges from the initial triangle to the queue
		edges.add(baseTri.he1);
		edges.add(baseTri.he2);
		edges.add(baseTri.he3);
		//iterate over the edges of the queue until the queue is empty
		while(!edges.isEmpty()){
			//get the next edge in the queue
			HalfEdge curEdge = edges.poll();
			//if it has a conflict list then determine what point in the list is farthest from the edge
			ArrayList<Integer> curConflict = conflictLists.get(curEdge);
			//this indicates that the face was deleted
			if(curConflict != null){
				//check if there are any points to extend this edge to
				if(!curConflict.isEmpty()){
					//find the point farthest from the edge
					Vec3 vert1 = mesh.getVertex(curEdge.sourceVert).getPos();
					float prevDist = 0;
					int farIndex = -1;
					for(Integer curIndex : curConflict){
						float distance = planeNormal.dot(VecUtil.subtract(mesh.getVertex(curIndex).getPos(), vert1));
						//check if the distance is greater than the previous distance
						if(distance > prevDist){
							//update the variables
							prevDist = distance;
							farIndex = curIndex;
						}
					}
					//update the hull with the new point
					extendHull(farIndex, curFace, edges, conflictLists);
				}
			}//otherwise the edge was either deleted in a previous iteration or has been finalized on the hull
		}
	}
	
	private void extendHull(int newPoint, HalfEdge base, LinkedList<Triangle> faces, HashMap<Triangle, ArrayList<Integer>> conflictLists){
		//create a list that will store the conflict list points of all the removed edges
		ArrayList<Integer> partition = new ArrayList<Integer>(conflictLists.remove(base));//remove the first triangle from the conflict list 
		//since we know it will be deleted
		//create another list that will store the horizon edges
		ArrayList<HalfEdge> horizon = new ArrayList<HalfEdge>();
		//TODO figure out how to go about computing and storing the horizon since it would be better to compute triangles at the end
		//this way they can also be triangulated
		
		//compute the edge horizon on the hull for the given point
		findHorizon(mesh.getVertex(newPoint).getPos(), base.he1.opposite, partition, horizon, conflictLists);
		findHorizon(mesh.getVertex(newPoint).getPos(), base.he2.opposite, partition, horizon, conflictLists);
		findHorizon(mesh.getVertex(newPoint).getPos(), base.he3.opposite, partition, horizon, conflictLists);
		
		Iterator<HalfEdge> edges = horizon.iterator();
		Triangle initial = null;
		Triangle prevTri = null;
		
		//TODO check if other pointers from different edges of the triangle are keeping their pointers to the 
		//old triangles something like the .next of an edge
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
		// TODO Auto-generated method stub
		return null;
	}

}
