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
		Vec3 edge1 = getEdgeNormal(normal, baseTri.he1.sourceVert, baseTri.he2.sourceVert);

		Vec3 edge2 = getEdgeNormal(normal, baseTri.he2.sourceVert, baseTri.he3.sourceVert);

		Vec3 edge3 = getEdgeNormal(normal, baseTri.he3.sourceVert, baseTri.he1.sourceVert);
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
		expand(normal, conflictLists);
		
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
					Vec3 edgeNormal = getEdgeNormal(planeNormal, curEdge.sourceVert, curEdge.next.sourceVert);
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
					extendHull(farIndex, curEdge, planeNormal, edges, conflictLists);
				}
			}//otherwise the edge was either deleted in a previous iteration or has been finalized on the hull
		}
	}
	
	private void extendHull(int newPoint, HalfEdge base, Vec3 planeNormal, LinkedList<HalfEdge> edges, HashMap<HalfEdge, ArrayList<Integer>> conflictLists){
		//create a list that will store the conflict list points of all the removed edges
		ArrayList<Integer> partition = new ArrayList<Integer>(conflictLists.remove(base));//remove the first triangle from the conflict list 
		//since we know it will be deleted
		
		//the base half edge acts as a pointer into the hull structure that is being formed using the half edge pointers
		//so we can simply loop through all the edges "in front" of the base half edge and then again through the edges "behind"
		//the base edge, stopping when we reached edge boundaries that represent the horizon of the hull
		HalfEdge forward = base.next, backward = base, curEdge = base.next;
		float dot = 0;
		//forward check loop
		do{
			//get the current edge normal
			Vec3 edgeNormal = getEdgeNormal(planeNormal, curEdge.next.sourceVert, curEdge.sourceVert);
			Vec3 newPointLine = VecUtil.subtract(mesh.getVertex(newPoint).getPos(), mesh.getVertex(curEdge.sourceVert).getPos());//line from the new point to the 
			//current vertex that may qualify as a horizon point
			dot = newPointLine.dot(edgeNormal);//get the dot product between the new point line and the edge normal
			//if the edge is visible to the new point remove it from the conflict lists
			if(dot >= 0){
				partition.addAll(conflictLists.remove(curEdge));
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
			Vec3 edgeNormal = getEdgeNormal(planeNormal, curEdge.prev.sourceVert, curEdge.sourceVert);
			Vec3 newPointLine = VecUtil.subtract(mesh.getVertex(newPoint).getPos(), mesh.getVertex(curEdge.sourceVert).getPos());//line from the new point to the 
			//current vertex that may qualify as a horizon point
			dot = newPointLine.dot(edgeNormal);//get the dot product between the new point line and the edge normal
			//if the edge is visible to the new point remove it from the conflict lists
			partition.addAll(conflictLists.remove(curEdge));
			if(dot < 0){
				backward = curEdge;
			}
			curEdge = curEdge.prev;
		}while(dot >= 0);//while the backward edge can still be seen by the new point
		//we will need to remove the backward edge since it's next pointer will be re-assigned to establish the new edge leading toward the new point
		
		//after finding the horizon points, we need to partition the list of conflicting points for all the edges removed between the new edges being formed
		//additionally we need to change the half edge pointers to represent the new hull
		HalfEdge newEdge = new HalfEdge(newPoint);//create the new edge
		//establish its forward and backward pointers to the horizon edges
		newEdge.next = forward;
		newEdge.prev = backward;
		
		//since the hashcode for reading conflict lists is based on the next and previous values of the half edge we need to re-assign the current forward
		//half edge in the conflict lists hashmap
		//first we need the current conflict lists
		ArrayList<Integer> forwardList = conflictLists.remove(forward);
		//establish the horizon edges with the new edge to disconnect the removed half edges from the hull
		forward.prev = newEdge;
		backward.next = newEdge;
		
		//after adjusting the forward half edge pointer we can re-assign the old conflict lists
		conflictLists.put(forward, forwardList);
		
		//partition the conflicting points from removed edges to the newly created edges
		conflictLists.put(newEdge, 
				partitionPoints(mesh, partition, getEdgeNormal(planeNormal, newEdge.next.sourceVert, newPoint), mesh.getVertex(newPoint).getPos()));
		conflictLists.put(backward, 
				partitionPoints(mesh, partition, getEdgeNormal(planeNormal, newPoint, backward.sourceVert), mesh.getVertex(backward.sourceVert).getPos()));
		
		//then we add the new edges to the linked list for further parsing
		edges.add(forward);//needs to be re-added due to value re-assignment that changed hashcode values
		edges.add(backward);
		edges.add(newEdge);
		
	}
	
	private Vec3 getEdgeNormal(Vec3 planeNormal, int baseIndex, int nextIndex){
		return VecUtil.cross(
				VecUtil.subtract(mesh.getVertex(nextIndex).getPos(), mesh.getVertex(baseIndex).getPos()),
				planeNormal).normalize();
	}

	@Override
	public Vec3 support(Vec3 direction) {
		// TODO Auto-generated method stub
		return null;
	}

}
