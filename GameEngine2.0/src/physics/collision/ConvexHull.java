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

public class ConvexHull extends CollisionMesh {
	private Triangle baseTri;
	private Geometry mesh;
	
	public ConvexHull(Geometry mesh){
		this.mesh = mesh;
		genHull();
	}
	
	public ConvexHull(ConvexHull copy){
		mesh = copy.mesh;
		baseTri = copy.baseTri;
	}
	
	private void genHull(){
		//TODO this first step can be eliminated if the min and max points are cached during
		//mesh generation
		
		//iterate over the vertices to find the points that have the max/min for each axis
		int[] minMax = new int[6];//indices into the mesh for the vertices
		//order of the indices in this array are min x, max x, min y, max y, min z, max z
		
		//iterate over the vertices
		for(int curVertex = 0; curVertex < mesh.getNumVertices(); curVertex++){
			//get the position of the current vertex
			Vec3 pos = mesh.getVertex(curVertex).getPos();
			
			//POTENTIALLY NEED TO CHECK FOR THE SAME VERTEX BEING SELECTED FOR MULTIPLE MIN AND MAX, maybe
			
			//check for min and max
			//X min
			if(pos.x < mesh.getVertex(minMax[0]).getPos().x){
				minMax[0] = curVertex;
			}

			//X max
			if(pos.x > mesh.getVertex(minMax[1]).getPos().x){
				minMax[1] = curVertex;
			}
			
			//Y min
			if(pos.y < mesh.getVertex(minMax[2]).getPos().y){
				minMax[2] = curVertex;
			}

			//Y max
			if(pos.y > mesh.getVertex(minMax[3]).getPos().y){
				minMax[3] = curVertex;
			}
			
			//Z min
			if(pos.z < mesh.getVertex(minMax[4]).getPos().z){
				minMax[4] = curVertex;
			}

			//Z max
			if(pos.z > mesh.getVertex(minMax[5]).getPos().z){
				minMax[5] = curVertex;
			}
		}
		
		//compute the vertex pair that would create the longest edge
		int startVert = 0, endVert = 0;//vertex indices that make the longest edge
		float curLongest = 0;//value to keep track of the current edge length of start and end
		//iterate over each vertex and compute its length with the other vertices and decide if it's the longest
		for(int curVal = 0; curVal < 6; curVal++){
			for(int nextVal = curVal+1; nextVal < 6; nextVal++){
				//compute the length
				float curLength = VecUtil.subtract(
						mesh.getVertex(minMax[curVal]).getPos(),//current vertex
						mesh.getVertex(minMax[nextVal]).getPos()//next vertex
						).length();
				//check if the computed length is longer than the previous one
				if(curLength < curLongest){
					//if it is then set the tracking variables to the current values
					curLongest = curLength;
					startVert = minMax[curVal];
					endVert = minMax[nextVal];
				}
			}
		}
		
		//find the vertex that is farthest from this edge
		int vertIndex = -1;//variable to hold the farthest vertex index
		float farthestDist = 0;//variable to hold the current farthest vertex distance
		Vec3 edge = VecUtil.subtract(mesh.getVertex(endVert).getPos(), mesh.getVertex(startVert).getPos());//edge vector relative to the start vertex
		Vec3 normal = null;//triangle normal, this is computed to determine the normal of the edge relative to the vertex vector
		//iterate over the 6 extreme points and find the one farthest from the edge to create the triangle of the tetrahedra
		for(int curVertex = 0; curVertex < 6; curVertex++){
			//get the edge vector from the start vertex to the current vertex
			Vec3 relaPoint = VecUtil.subtract(mesh.getVertex(curVertex).getPos(), mesh.getVertex(startVert).getPos());
			
			//compute the normal vector of the edge being tested relative to the current vertex vector, then get the scalar of the projection of the
			//vertex vector with the normal to get the distance of the vertex from the edge
			float distance = relaPoint.comp(VecUtil.cross(relaPoint, edge, edge));
			
			//test if the distance computed is farther than the previous distance computed and update the variables
			if(distance > farthestDist){
				farthestDist = distance;
				vertIndex = curVertex;
				normal = VecUtil.cross(relaPoint, edge);
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
		ArrayList<Integer> posList = new ArrayList<Integer>(mesh.getNumVertices()), //vertex indices that are in front of the triangle
				negList = new ArrayList<Integer>(mesh.getNumVertices());//vertex indices that are in behind of the triangle

		boolean inFront = false;//boolean indicating what side of the triangle the farthest point will be which will decide which
		//list is assigned to the face and which gets partitioned
		
		//iterate over all the vertices and test which is the farthest
		for(int curPoint = 0; curPoint < mesh.getNumVertices(); curPoint++){
			//get the current point relative to the start point on the triangle
			Vec3 relaPoint = VecUtil.subtract(mesh.getVertex(curPoint).getPos(), mesh.getVertex(startVert).getPos());
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
		
		//create additional faces from the base triangle to the vertex to build the initial tetrahedra to expand
		Triangle face1 = new Triangle(vertIndex, baseTri.he1.sourceVert, baseTri.he2.sourceVert);
		Triangle face2 = new Triangle(vertIndex, baseTri.he2.sourceVert, baseTri.he3.sourceVert);
		Triangle face3 = new Triangle(vertIndex, baseTri.he3.sourceVert, baseTri.he1.sourceVert);
		//setup adjacency
		baseTri.he1.opposite = face1.he2;
		baseTri.he2.opposite = face2.he2;
		baseTri.he3.opposite = face3.he2;
		
		face1.he1.opposite = face3.he3;
		face1.he2.opposite = baseTri.he1;
		face1.he3.opposite = face2.he1;
		
		face2.he1.opposite = face1.he3;
		face2.he2.opposite = baseTri.he2;
		face2.he3.opposite = face3.he1;
		
		face3.he1.opposite = face2.he3;
		face3.he2.opposite = baseTri.he3;
		face3.he3.opposite = face1.he1;
		
		//expand the initial tetrahedra to comprise the convex hull of the mesh
		//create a hashmap to assign the triangles conflict lists in the expansion process
		HashMap<Triangle, ArrayList<Integer>> conflictLists = new HashMap<Triangle, ArrayList<Integer>>(4);
		ArrayList<Integer> partitionList = null;
		//first determine if the final vertex to make the tetrahedra was in front of or behind the triangle
		//this will decide which set of points to partition to the faces
		if(inFront){
			//since the vertex for the tetrahedra was in front of the triangle the positive list will
			//be partitioned for the other faces
			partitionList = posList;
			conflictLists.put(baseTri, negList);
		}else{
			//since the vertex for the tetrahedra was behind the triangle the negative list will
			//be partitioned for the other faces
			partitionList = negList;
			conflictLists.put(baseTri, posList);
		}
		
		//partition points between the faces and add them to the mapping
		conflictLists.put(baseTri, partitionPoints(partitionList, baseTri));
		conflictLists.put(face1, partitionPoints(partitionList, face1));
		conflictLists.put(face2, partitionPoints(partitionList, face2));
		conflictLists.put(face3, partitionPoints(partitionList, face3));
		
		expandTetrahedra(conflictLists);
	}
	
	private ArrayList<Integer> partitionPoints(ArrayList<Integer> points, Triangle face){
		//create an array list that will contain the found conflicting points for the Triangle
		ArrayList<Integer> conflictList = new ArrayList<Integer>(points.size());
		//calculate the face normal for use in determining whether the points is in front of the Triangle
		Vec3 normal = face.getNormal(mesh);
		//iterate over all the points that could be in front of the current face
		Iterator<Integer> pointsList = points.iterator();
		while(pointsList.hasNext()){
			Integer curIndex = pointsList.next();
			Vec3 curPoint = mesh.getVertex(curIndex).getPos();
			//determine if the point is in front of the face based on whether the dot product is positive
			//additionally 0 is considered behind the face
			if(normal.dot(curPoint) > 0){
				//if so then add the point to the conflict list for the current Triangle face
				conflictList.add(curIndex);
				pointsList.remove();
			}
		}

		return conflictList;
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
			if(curConflict != null){
				//find the point farthest from the face
				//calculate the face normal
				Vec3 normal = curFace.getNormal(mesh);
				float prevDist = 0;
				int farIndex = -1;
				for(Integer curIndex : curConflict){
					float distance = mesh.getVertex(curIndex).getPos().dot(normal);
					
					//check if the distance is greater than the previous distance
					if(distance > prevDist){
						//update the variables
						prevDist = distance;
						farIndex = curIndex;
					}
				}
				//update the hull with the new point
				extendHull(farIndex, curFace, faces, conflictLists);
			}//otherwise the face was either deleted in a previous iteration or has been finalized on the hull
		}
	}
	
	private void extendHull(int newPoint, Triangle base, LinkedList<Triangle> faces, HashMap<Triangle, ArrayList<Integer>> conflictLists){
		//create a list that will store the conflict list points of all the removed triangles
		ArrayList<Integer> partition = new ArrayList<Integer>(conflictLists.get(base));
		conflictLists.remove(base);//remove the first triangle from the conflict list since we know it will be deleted
		//create another list that will store the horizon edges
		ArrayList<HalfEdge> horizon = new ArrayList<HalfEdge>();
		//compute the edge horizon on the hull for the given point
		findHorizon(newPoint, base.he1.opposite, partition, horizon, conflictLists);
		findHorizon(newPoint, base.he2.opposite, partition, horizon, conflictLists);
		findHorizon(newPoint, base.he3.opposite, partition, horizon, conflictLists);
		
		//update the hull with the new point and extrude the edges of the horizon to the new point
		ArrayList<Triangle> newFaces = new ArrayList<Triangle>(horizon.size());
		//compute the first triangle
		newFaces.add(new Triangle(newPoint, horizon.get(0).next.sourceVert, horizon.get(0).sourceVert));
		//partition the removed faces conflict lists to the new face
		conflictLists.put(newFaces.get(0), partitionPoints(partition, newFaces.get(0)));
		//add the new face to the faces list for later iteration
		faces.add(newFaces.get(0));
		
		//iterate over the horizon edges and construct new triangles 
		for(int curEdge = 1; curEdge < horizon.size(); curEdge++){
			HalfEdge current = horizon.get(curEdge);
			//compute the new triangle
			newFaces.add(new Triangle(newPoint, current.next.sourceVert, current.sourceVert));
			//setup adjacency information for the new triangle
			//connect to the previous face
			newFaces.get(curEdge).he3.opposite = newFaces.get(curEdge-1).he1;
			newFaces.get(curEdge-1).he1.opposite = newFaces.get(curEdge).he3;
			//connect with the "hidden" face that is part of the hull
			newFaces.get(curEdge).he2.opposite = current;
			
			//if this is the last triangle being created then we need to setup the adjacency with the 
			//first triangle to complete the loop
			if(curEdge == horizon.size()-1){
				newFaces.get(curEdge).he1.opposite = newFaces.get(0).he3;
				newFaces.get(0).he3.opposite = newFaces.get(curEdge).he1;
			}
			
			//partition the removed faces conflict lists to the new face
			conflictLists.put(newFaces.get(curEdge), partitionPoints(partition, newFaces.get(curEdge)));
			
			//add the new face to the faces list for later iteration
			faces.add(newFaces.get(curEdge));
		}
	}
	
	/**
	 * Finds the Horizon edges for adding the new point of the hull
	 * 
	 * @param pointIndex Index of the vertex being added to the hull
	 * @param current Current half edge being processed for the DFS of the triangle faces of the hull
	 * @param partitionPoints List of all the collected points to partition as a result of removing faces from the hull
	 * @param horizon Array list to store the edges of the horizon
	 * @param conflictLists List of the points that are in front of a triangle that are used to generate a new point
	 * this list is also doubling as a "visited" marker for the recursive search. Once a face is visited it
	 * will be removed from this list and have it's points collected for later partitioning among the new faces, this
	 * process is necessary for removing the old faces from the hull.
	 */
	private void findHorizon(int pointIndex, 
			HalfEdge current, 
			ArrayList<Integer> partitionPoints, 
			ArrayList<HalfEdge> horizon, 
			HashMap<Triangle, ArrayList<Integer>> conflictLists){
		//check if the current half edges parent face has already not been visited
		if(conflictLists.get(current.parent) != null){
			//check if the current half edges parent face can be "seen" from the new point of the hull
			//get the normal of the current face
			Vec3 normal = current.parent.getNormal(mesh);
			//get the vector from a point on the triangle to the new point
			Vec3 newEdge = VecUtil.subtract(mesh.getVertex(pointIndex).getPos(), mesh.getVertex(current.sourceVert).getPos());
			//if it can, continue searching through the edges of the current face
			if(normal.dot(newEdge) > 0){
				//mark the current half edges parent face as being visited
				partitionPoints.addAll(conflictLists.get(current.parent));//add it's conflict list to the list of points
				conflictLists.remove(current.parent);//and remove it from the map
				//continue the search through the other edges that we haven't come from
				findHorizon(pointIndex, current.next.opposite, partitionPoints, horizon, conflictLists);
				findHorizon(pointIndex, current.prev.opposite, partitionPoints, horizon, conflictLists);
			}else{//otherwise add the half edge to the horizon
				horizon.add(current);
			}
		}
	}

	@Override
	public Vec3 support(Vec3 direction) {
		//change the direction vector based on the orientation of the hull
		//this way the vertices don't need to be transformed to test against
		//the direction vector
		Vec3 orientedDir = transforms.getOrientation().multVec(direction).normalize();

		//transform the final vertex to reflect the world position of the vertex
		return (Vec3)transforms.getTransform().multVec(new Vec4(findSupport(orientedDir, baseTri.he1),1)).swizzle("xyz");
	}
	
	private Vec3 findSupport(Vec3 direction, HalfEdge startEdge){
		
		//compute the dot product with the computed direction
		float foundDotDir = direction.dot(mesh.getVertex(startEdge.sourceVert).getPos());

		//iterate over all the adjacent vertices to the given vertex and find the vertex
		//most in the direction of the given vector
		HalfEdge curEdge = startEdge.opposite, foundEdge = startEdge;
		
		while(!curEdge.equals(startEdge)){//if the edge we are comparing with is the same as the start end the loop
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
