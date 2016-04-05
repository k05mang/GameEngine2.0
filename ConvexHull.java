package physics.collision;

import glMath.MatrixUtil;
import glMath.matrices.Mat4;
import glMath.vectors.Vec3;

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
	}
	
	public ConvexHull(ConvexHull copy){
		mesh = copy.mesh;
		faces = copy.faces;
	}

	@Override
	public Vec3 support(Vec3 direction) {
		//change the direction vector based on the orientation of the hull
		//this way the vertices don't need to be transformed to test against
		//the direction vector
		Vec3 orientedDir = transforms.getOrientation().multVec(direction);
		
		return findSupport(orientedDir, faces.get(0).he1);
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
				//since the current vertex dot product is greater than the current one we know it is more in the
				//direction of the direction vector than the previously found vertex
				foundDotDir = curDotDir;
				foundEdge = curEdge;
			}
		}
		
		//if no vertex was then the current vertex is the vertex on the mesh most in the direction
		//of the given vector
		
		//transform the final vertex to reflect the world position of the vertex
		
	}
}
