package physics.collision;

import glMath.VecUtil;
import glMath.vectors.Vec3;
import glMath.vectors.Vec4;

import java.util.ArrayList;

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
			if(distance >= 0){
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
				inFront = distance >= 0;
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
