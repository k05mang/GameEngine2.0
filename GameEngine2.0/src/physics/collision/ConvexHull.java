package physics.collision;

import glMath.VecUtil;
import glMath.vectors.Vec3;

import java.util.ArrayList;
import java.util.Iterator;

import mesh.Geometry;
import mesh.Mesh;
import mesh.primitives.Triangle;

public abstract class ConvexHull extends CollisionMesh {
	protected Geometry mesh;
	
	protected ConvexHull(Geometry mesh) {
		this.mesh = mesh;
	}

	protected ConvexHull(ConvexHull copy) {
		super(copy);
		mesh = copy.mesh;
	}
	
	@Override
	public abstract CollisionMesh copy();

	@Override
	public abstract Vec3 support(Vec3 direction);
	
	/**
	 * Partitions the index list {@code points} that correspond to vertices in the {@code mesh}. The list is split into two parts
	 * based on the {@code normal} passed to the function. Points are calculated relative to the given {@code relaPoint}, from there
	 * points that are in the direction of the given normal are considered in front of either a face or line, depending on what the 
	 * normal given is representative of, and are added to the return list and removed from the given list {@code points}.
	 * 
	 * @param mesh Geometry containing the actual 3D points to calculate the partition
	 * @param points Indices that need to be partitioned
	 * @param normal The direction to use in calculating the partition
	 * @param relaPoint Point to use when translating the indexed point into the space relative to the normal for calculations
	 * 
	 * @return List of index points into the geometry that are considered "in front" of the the geometric primitive the points
	 * were partitioned for
	 */
	protected static ArrayList<Integer> partitionPoints(Geometry mesh, ArrayList<Integer> points, Vec3 normal, Vec3 relaPoint){
		//create an array list that will contain the found conflicting points for the Triangle
		ArrayList<Integer> conflictList = new ArrayList<Integer>(points.size());
		//iterate over all the points that could be in front of the current face
		Iterator<Integer> pointsList = points.iterator();
		while(pointsList.hasNext()){
			Integer curIndex = pointsList.next();
			Vec3 curPoint = VecUtil.subtract(mesh.getVertex(curIndex).getPos(), relaPoint);
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
	
	public static ConvexHull get(Mesh mesh){
		return get(mesh.getGeometry());
	}
	
	public static ConvexHull get(Geometry mesh){
		//compute the vertex pair that would create the longest edge
		int startVert = 0, endVert = 0;//vertex indices that make the longest edge
		float farthestDist = 0;//value to keep track of the current edge length of start and end
		//iterate over each vertex and compute its length with the other vertices and decide if it's the longest
		for(int curVal = 0; curVal < 6; curVal++){
			for(int nextVal = curVal+1; nextVal < 6; nextVal++){
				//compute the length
				float curLength = VecUtil.subtract(
						mesh.getVertex(mesh.getMinMaxIndex(curVal)).getPos(),//current vertex
						mesh.getVertex(mesh.getMinMaxIndex(nextVal)).getPos()//next vertex
						).length();
				//check if the computed length is longer than the previous one
				if(curLength > farthestDist){
					//if it is then set the tracking variables to the current values
					farthestDist = curLength;
					startVert = mesh.getMinMaxIndex(curVal);
					endVert = mesh.getMinMaxIndex(nextVal);
				}
			}
		}
		
		//find the vertex that is farthest from this edge
		int vertIndex = -1;//variable to hold the farthest vertex index
		farthestDist = 0;//variable to hold the current farthest vertex distance
		Vec3 edge = VecUtil.subtract(mesh.getVertex(endVert).getPos(), mesh.getVertex(startVert).getPos());//edge vector relative to the start vertex
		Vec3 normal = null;//triangle normal, this is computed to determine the normal of the edge relative to the vertex vector
		//iterate over the 6 extreme points and find the one farthest from the edge to create the triangle of the tetrahedra
		for(int curVertex = 0; curVertex < 6; curVertex++){
			//get the edge vector from the start vertex to the current vertex
			Vec3 relaPoint = VecUtil.subtract(mesh.getVertex(mesh.getMinMaxIndex(curVertex)).getPos(), mesh.getVertex(startVert).getPos());
			
			//compute the normal vector of the edge being tested relative to the current vertex vector, then get the scalar of the projection of the
			//vertex vector with the normal to get the distance of the vertex from the edge
			float distance = relaPoint.comp(VecUtil.cross(edge, relaPoint, edge));
			
			//test if the distance computed is farther than the previous distance computed and update the variables
			if(distance > farthestDist){
				farthestDist = distance;
				vertIndex = mesh.getMinMaxIndex(curVertex);
				normal = VecUtil.cross(edge, relaPoint);
			}
		}
		
		//at this point we can check if the points are co-linear meaning the mesh provided is a line of points
		if(vertIndex == -1){
			//if the mesh is co-linear then this makes it impossible or illogical to create a convex hull to represent
			//the collision mesh for this Geometry
			System.err.println("Mesh passed to convex hull generator is co-linear, unable to create convex hull");
			return null;
		}
		
		//create the initial triangle for the hull
		Triangle baseTri = new Triangle(startVert, endVert, vertIndex);
		//find the vertex that is farthest from this triangle in the direction of the triangle normal
		//reset variables for reuse with the next vertex search
		normal.normalize();//normalize the face normal so that distance can be calculated with a simple dot product
		farthestDist = 0;
		vertIndex = -1;
		
		//create conflict lists for the different halves of the triangle for use in the expansion of the polyhedra
		ArrayList<Integer> posList = new ArrayList<Integer>(mesh.numVertices()), //vertex indices that are in front of the triangle
				negList = new ArrayList<Integer>(mesh.numVertices());//vertex indices that are in behind of the triangle

		boolean inFront = false;//boolean indicating what side of the triangle the farthest point will be which will decide which
		//list is assigned to the face and which gets partitioned
		
		//iterate over all the vertices and test which is the farthest
		for(int curPoint = 0; curPoint < mesh.numVertices(); curPoint++){
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
		
		//at this point we can determine if the mesh is co-planar this means that the convex hull for this mesh should be 2D, as such the
		//generator will pass relevant information to the proper class constructors for the convex hull
		if(vertIndex == -1){
			return new ConvexHull2D(baseTri, mesh);
		}else{
			return new ConvexHull3D(baseTri, vertIndex, inFront, mesh, posList, negList);
		}
	}

}
