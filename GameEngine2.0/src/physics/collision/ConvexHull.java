package physics.collision;

import glMath.VecUtil;
import glMath.vectors.Vec3;
import glMath.vectors.Vec4;

import java.util.ArrayList;

import mesh.Geometry;
import mesh.primitives.HalfEdge;
import mesh.primitives.Triangle;

public class ConvexHull extends CollisionMesh {
	private ArrayList<Triangle> faces;
	private Geometry mesh;
	
	public ConvexHull(Geometry mesh){
		this.mesh = mesh;
		//allocate enough space to store as many faces as there are in the mesh
		//since this is the most number of faces that the convex hull could end 
		//generating, the actual number of faces is generally less than this
		//which will be adjusted at the end of computation
		faces = new ArrayList<Triangle>(mesh.getNumFaces());
		genHull();
	}
	
	public ConvexHull(ConvexHull copy){
		mesh = copy.mesh;
		faces = copy.faces;
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
		int startVert, endVert;//vertex indices that make the longest edge
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
		
		//find the vertex that is farthest from this triangle in the direction of the triangle normal
		
		//expand the initial tetrahedra to comprise the convex hull of the mesh
	}

	@Override
	public Vec3 support(Vec3 direction) {
		//change the direction vector based on the orientation of the hull
		//this way the vertices don't need to be transformed to test against
		//the direction vector
		Vec3 orientedDir = transforms.getOrientation().multVec(direction);

		//transform the final vertex to reflect the world position of the vertex
		return (Vec3)transforms.getTransform().multVec(new Vec4(findSupport(orientedDir, faces.get(0).he1),1)).swizzle("xyz");
	}
	
	private Vec3 findSupport(Vec3 direction, HalfEdge startEdge){
		
		//compute the dot product with the computed direction
		float foundDotDir = direction.dot(mesh.getVertex(startEdge.sourceVert).getPos());

		//iterate over all the adjacent vertices to the given vertex and find the vertex
		//most in the direction of the given vector
		HalfEdge curEdge = startEdge.opposite, foundEdge = startEdge;
		
		while(!curEdge.equals(startEdge)){//if the edge we are comparing with is the same as the start end the loop
			//get the adjacent vertex and get it's dot product with the direction
			float curDotDir = direction.dot(mesh.getVertex(curEdge.sourceVert).getPos());
			
			//compare the dot product of the current adjacent vertex with the previous found vertex dot product
			if(curDotDir > foundDotDir){
				//since the current vertex dot product is greater than the previous one we know it is more in the
				//direction of the direction vector than the previously found vertex
				foundDotDir = curDotDir;
				foundEdge = curEdge;
			}
			//update the current edge to be the next edge that has the next adjacent vertex to the starting vertex
			curEdge = curEdge.next.opposite;
		}
		
		//if no vertex was found then the current vertex is the vertex on the mesh most in the direction
		//of the given vector
		if(foundEdge.equals(startEdge)){
			return mesh.getVertex(foundEdge.sourceVert).getPos();
		}else{
			//recurse using the found vertex as a starting point
			return findSupport(direction, foundEdge);
		}
	}
}
