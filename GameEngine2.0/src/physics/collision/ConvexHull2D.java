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

public class ConvexHull2D extends ConvexHull {

	protected HalfEdge baseEdge;
	protected Vec3 planeNormal;
	
	protected ConvexHull2D(Triangle baseTri, Geometry mesh) {
		super(mesh);
		//create a hashmap to assign the edges conflict lists in the expansion process
		HashMap<HalfEdge, ArrayList<Integer>> conflictLists = new HashMap<HalfEdge, ArrayList<Integer>>(3);
		//create the initial hull using the base triangle
		baseEdge = new HalfEdge(baseTri.he1.sourceVert);
		HalfEdge forwardEdge = new HalfEdge(baseTri.he2.sourceVert);
		HalfEdge backwardEdge = new HalfEdge(baseTri.he3.sourceVert);
		//connect the edges
		baseEdge.next = forwardEdge;
		baseEdge.prev = backwardEdge;
		
		forwardEdge.next = backwardEdge;
		forwardEdge.prev = baseEdge;
		
		backwardEdge.next = baseEdge;
		backwardEdge.prev = forwardEdge;
		
		//partition the mesh vertices between the 3 edges of the triangle and store them in the conflict lists Hashmap
		//start by getting the triangle normal to use in calculating the normals for the edges
		planeNormal = baseTri.getNormal(mesh);
		//calculate the edge normals that will be initially used in determining the partition
		Vec3 edge1 = getEdgeNormal(baseTri.he1.sourceVert, baseTri.he2.sourceVert);

		Vec3 edge2 = getEdgeNormal(baseTri.he2.sourceVert, baseTri.he3.sourceVert);

		Vec3 edge3 = getEdgeNormal(baseTri.he3.sourceVert, baseTri.he1.sourceVert);
		//create the index list to partition
		ArrayList<Integer> partitionList = new ArrayList<Integer>(mesh.numVertices());
		//initialize the index list
		for(int curPoint = 0; curPoint < mesh.numVertices(); curPoint++){
			partitionList.add(curPoint);
		}
		//partition the points
		conflictLists.put(baseEdge, 
				partitionPoints(mesh, partitionList, edge1, mesh.getVertex(baseTri.he1.sourceVert).getPos()));

		conflictLists.put(baseEdge.next, 
				partitionPoints(mesh, partitionList, edge2, mesh.getVertex(baseTri.he2.sourceVert).getPos()));

		conflictLists.put(baseEdge.prev, 
				partitionPoints(mesh, partitionList, edge3, mesh.getVertex(baseTri.he3.sourceVert).getPos()));
		expand(conflictLists);
		
	}

	public ConvexHull2D(ConvexHull2D copy) {
		super(copy);
		baseEdge = copy.baseEdge;
		planeNormal = copy.planeNormal;
	}
	
	@Override
	public CollisionMesh copy(){
		return new ConvexHull2D(this);
	}
	
	private void expand(HashMap<HalfEdge, ArrayList<Integer>> conflictLists){
		//create a "queue" that will track what Edges need to be tested for points and extended
		LinkedList<HalfEdge> edges = new LinkedList<HalfEdge>();
		//add the edges from the initial triangle to the queue
		edges.add(baseEdge);
		edges.add(baseEdge.next);
		edges.add(baseEdge.prev);
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
					Vec3 edgeNormal = getEdgeNormal(curEdge.sourceVert, curEdge.next.sourceVert);
					float prevDist = 0;
					int farIndex = -1;
					for(Integer curIndex : curConflict){
						float distance = edgeNormal.dot(VecUtil.subtract(mesh.getVertex(curIndex).getPos(), mesh.getVertex(curEdge.sourceVert).getPos()));
						//check if the distance is greater than the previous distance
						if(distance > prevDist){
							//update the variables
							prevDist = distance;
							farIndex = curIndex;
						}
					}
					//update the hull with the new point
					extendHull(farIndex, curEdge, edges, conflictLists);
				}
			}//otherwise the edge was either deleted in a previous iteration or has been finalized on the hull
		}
	}
	
	private void extendHull(int newPoint, HalfEdge base, LinkedList<HalfEdge> edges, HashMap<HalfEdge, ArrayList<Integer>> conflictLists){
		//create a list that will store the conflict list points of all the removed edges
		ArrayList<Integer> partition = new ArrayList<Integer>();//remove the first triangle from the conflict list 
		//since we know it will be deleted
		
		//the base half edge acts as a pointer into the hull structure that is being formed using the half edge pointers
		//so we can simply loop through all the edges "in front" of the base half edge and then again through the edges "behind"
		//the base edge, stopping when we reached edge boundaries that represent the horizon of the hull
		HalfEdge forward = base.next, backward = base, curEdge = base.next;
		float dot = 0;
		boolean removedBase = false;
		//forward check loop
		do{
			//get the current edge normal
			Vec3 edgeNormal = getEdgeNormal(curEdge.sourceVert, curEdge.next.sourceVert);
			Vec3 newPointLine = VecUtil.subtract(mesh.getVertex(newPoint).getPos(), mesh.getVertex(curEdge.sourceVert).getPos());//line from the new point to the 
			//current vertex that may qualify as a horizon point
			dot = newPointLine.dot(edgeNormal);//get the dot product between the new point line and the edge normal
			//if the edge is visible to the new point remove it from the conflict lists
			if(dot >= 0){
				partition.addAll(conflictLists.remove(curEdge));
				//check if the edge we are removing is the baseEdge, in which case we need to re-assign baseEdge at the end
				if(curEdge.equals(baseEdge)){
					removedBase = true;
				}
			}else{
				forward = curEdge;
			}
			curEdge = curEdge.next;
		}while(dot >= 0);//while the forward edge can still be seen by the new point
		
		//back check loop
		curEdge = base;
		dot = 0;
		do{
			//get the current edge normal
			Vec3 edgeNormal = getEdgeNormal(curEdge.sourceVert, curEdge.next.sourceVert);
			Vec3 newPointLine = VecUtil.subtract(mesh.getVertex(newPoint).getPos(), mesh.getVertex(curEdge.sourceVert).getPos());//line from the new point to the 
			//current vertex that may qualify as a horizon point
			dot = newPointLine.dot(edgeNormal);//get the dot product between the new point line and the edge normal
			//if the edge is visible to the new point remove it from the conflict lists
			if(dot >= 0){
				partition.addAll(conflictLists.remove(curEdge));
				//check if the edge we are removing is the baseEdge, in which case we need to re-assign baseEdge at the end
				if(curEdge.equals(baseEdge)){
					removedBase = true;
				}
			}else{
				backward = curEdge.next;
			}
			curEdge = curEdge.prev;
		}while(dot >= 0);//while the backward edge can still be seen by the new point
		//we will need to remove the backward edge since it's next pointer will be re-assigned to establish the new edge leading toward the new point
		
		//after finding the horizon points, we need to partition the list of conflicting points for all the edges removed between the new edges being formed
		//additionally we need to change the half edge pointers to represent the new hull
		HalfEdge newEdge = new HalfEdge(newPoint);//create the new edge
		//if the baseEdge was removed while searching for the horizon then use the newEdge as the baseEdge for the hull
		if(removedBase){
			baseEdge = newEdge;
		}
		//establish forward and backward pointers to the horizon edges
		newEdge.next = forward;
		newEdge.prev = backward;
		
		//since the hashcode for reading conflict lists is based on the next and previous values of the half edge we need to re-assign the current forward
		//half edge in the conflict lists hashmap
		//first we need the current conflict lists
		ArrayList<Integer> forwardList = conflictLists.remove(forward);
		//establish the horizon edges with the new edge to disconnect the removed half edges from the hull
		forward.prev = newEdge;
		backward.next = newEdge;
		
		//after adjusting the forward half edge pointer we can re-assign the old conflict lists only if the conflict list existed in the first place
		if(forwardList != null){
			conflictLists.put(forward, forwardList);
		}
		
		//partition the conflicting points from removed edges to the newly created edges
		conflictLists.put(newEdge, 
				partitionPoints(mesh, partition, getEdgeNormal(newPoint, forward.sourceVert), mesh.getVertex(newPoint).getPos()));
		conflictLists.put(backward, 
				partitionPoints(mesh, partition, getEdgeNormal(backward.sourceVert, newPoint), mesh.getVertex(backward.sourceVert).getPos()));
		
		//then we add the new edge to the linked list for further parsing
		edges.add(newEdge);
		edges.add(forward);//needs to be re-added due to value re-assignment that changed hashcode values
		
	}
	
	/**
	 * Gets the edge normal for a given edge based on a base vertex index and the next index in the edge
	 * 
	 * @param baseIndex Base index to start the edge from
	 * @param nextIndex Next index to extend the previous index to, to create the edge from base index to next index
	 * 
	 * @return Outward facing normal for the given edge
	 */
	private Vec3 getEdgeNormal(int baseIndex, int nextIndex){
		return VecUtil.cross(
				VecUtil.subtract(mesh.getVertex(nextIndex).getPos(), mesh.getVertex(baseIndex).getPos()),
				planeNormal).normalize();
	}

	@Override
	public Vec3 support(Vec3 direction) {
		//change the direction vector based on the orientation of the hull to bring it into model space
		//this way the vertices don't need to be transformed to world space to test against
		//the direction vector
		Vec3 orientedDir = transforms.getOrientation().conjugate().multVec(direction).normalize();
		
		//first check if the direction vector we are searching in is perpendicular to the plane the convex hull lies on
		if(orientedDir.dot(planeNormal) == 1 || orientedDir.dot(planeNormal) == -1){
			//in this case we will simply return the base edge vertex
			return transforms.transform(mesh.getVertex(baseEdge.sourceVert).getPos());
		}else{
			//otherwise we need to find the vertex in the direction of the model space direction vector
			//first we need to see how the base vertex relates to the neighboring vertices
			//this will also determine which direction we want to iterate from to possibly find the most aligned vertex the fastest
			
			float curDotProd = orientedDir.dot(mesh.getVertex(baseEdge.sourceVert).getPos());
			float forwardDotProd = orientedDir.dot(mesh.getVertex(baseEdge.next.sourceVert).getPos());
			float backwardDotProd = orientedDir.dot(mesh.getVertex(baseEdge.prev.sourceVert).getPos());
			float max = Math.max(curDotProd, Math.max(forwardDotProd, backwardDotProd));
			HalfEdge foundEdge = baseEdge;
			//check which dot product was the largest
			if(max == forwardDotProd){//else we move forward and continue to move forward until we find the dot product that is greatest
				foundEdge = baseEdge.next;//move the found edge forward from the base edge
				curDotProd = forwardDotProd;//set the current dot product value to test to the forward edge
				while(!foundEdge.equals(baseEdge)){
					forwardDotProd = orientedDir.dot(mesh.getVertex(foundEdge.next.sourceVert).getPos());
					//if the new dot product is greater than the previous one we update the test value for the current dot product
					if(forwardDotProd > curDotProd){
						curDotProd = forwardDotProd;
					}else{
						//if it is not then we found the vertex we are looking for and can return it
						return transforms.transform(mesh.getVertex(foundEdge.sourceVert).getPos());
					}
					foundEdge = foundEdge.next;
				}
			}else if(max == backwardDotProd){//else we move backward and continue to move backward until we find the dot product that is greatest
				foundEdge = baseEdge.prev;//move the found edge backward from the base edge
				curDotProd = backwardDotProd;//set the current dot product value to test to the backward edge
				while(!foundEdge.equals(baseEdge)){
					backwardDotProd = orientedDir.dot(mesh.getVertex(foundEdge.prev.sourceVert).getPos());
					//if the new dot product is greater than the previous one we update the test value for the current dot product
					if(backwardDotProd > curDotProd){
						curDotProd = backwardDotProd;
					}else{
						//if it is not then we found the vertex we are looking for and can return it
						return transforms.transform(mesh.getVertex(foundEdge.sourceVert).getPos());
					}
					foundEdge = foundEdge.prev;
				}
			}
			return transforms.transform(mesh.getVertex(baseEdge.sourceVert).getPos());
		}
	}
	
	public Vec3 getPlaneNormal(){
		return transforms.getMatrix().getNormalMatrix().multVec(planeNormal).normalize();
	}
}
