package collision;

import java.util.ArrayList;

import mesh.primitives.Face;
import mesh.primitives.Vertex;
import glMath.MatrixUtil;
import glMath.Quaternion;
import glMath.matrices.Mat4;
import glMath.vectors.Vec3;

public class ConvexHull implements CollisionMesh {
	private ArrayList<Vec3> verts;
	private Face adjInfo;
	private Mat4 modelMat, origModel;
	private Quaternion orientation, origOrient;
	
	public ConvexHull(){
		modelMat = new Mat4(1);
		orientation = new Quaternion();
		origModel = new Mat4(1);
		origOrient = new Quaternion();
		verts = new ArrayList<Vec3>();
	}
	
	public ConvexHull(ArrayList<Vertex> vertices, Face adjInfo){
		modelMat = new Mat4(1);
		orientation = new Quaternion();
		origModel = new Mat4(1);
		origOrient = new Quaternion();
		
		verts = new ArrayList<Vec3>(vertices.size());
		
		for(Vertex vert : vertices){
			verts.add(vert.getPos());
		}
		
		this.adjInfo = adjInfo;
	}
	
	public ConvexHull(ConvexHull copy){
	    verts = copy.verts;
		adjInfo = copy.adjInfo;
		modelMat = new Mat4(copy.modelMat);
		orientation = new Quaternion(copy.orientation);
		origModel = new Mat4(copy.modelMat);
		origOrient = new Quaternion(copy.orientation);
	}
	
	public void setVerts(ArrayList<Vec3> newVerts, Face adjInfo){
		this.verts = newVerts;
		this.adjInfo = adjInfo;
	}
	
	public void translate(float x, float y, float z){
		modelMat.leftMult(MatrixUtil.makeTranslate(x, y, z));
	}
	
	public void translate(Vec3 translation){
		modelMat.leftMult(MatrixUtil.makeTranslate(translation));
	}
	
	public void scale(float factor){
		modelMat.leftMult(MatrixUtil.makeScale(factor, factor, factor));
	}
	
	public void scale(float x, float y, float z){
		modelMat.leftMult(MatrixUtil.makeScale(x, y, z));
	}
	
	public void scale(Vec3 scalars){
		modelMat.leftMult(MatrixUtil.getScaleMat(scalars));
	}
	
	public void rotate(float x, float y, float z, float theta){
		modelMat.leftMult(Quaternion.fromAxisAngle(x, y, z, theta).asMatrix());
	}
	
	public void rotate(Vec3 axis, float theta){
		modelMat.leftMult(Quaternion.fromAxisAngle(axis, theta).asMatrix());
	}
	
	public void orient(float x, float y, float z, float theta){
		orientation.set(Quaternion.multiply(Quaternion.fromAxisAngle(x, y, z, theta), orientation));
	}
	
	public void orient(Vec3 axis, float theta){
		orientation.set(Quaternion.multiply(Quaternion.fromAxisAngle(axis, theta), orientation));
	}
	
	public void orient(Vec3 angles){
		orientation.set(Quaternion.multiply(new Quaternion(angles), orientation));
	}
	
	public void orient(float roll, float pitch, float yaw){
		orientation.set(Quaternion.multiply(new Quaternion(roll, pitch, yaw), orientation));
	}
	
	public Quaternion getOrientation(){
		return orientation;
	}
	
	public void setOrientation(Quaternion orient){
		orientation.set(orient);
	}
	
	public Mat4 getModelMatrix(){
		return modelMat;
	}
	
	public ArrayList<Vec3> getVerts(){
		return verts;
	}
	
	public Face getAdjInfo(){
		return adjInfo;
	}

	@Override
	public Vec3 getCenter() {
		return (Vec3)this.getModelMatrix().multVec(center).swizzle("xyz");
	}

	@Override
	public void setData(Mat4 modelMat, Quaternion orient) {
		this.modelMat.setMatrix(modelMat);
		this.orientation.set(orient);
	}
	
	@Override
	public void setData(Mat4 modelMat){
		this.modelMat.setMatrix(modelMat);
		orientation.set(0,0,0);
	}

	@Override
	public Vec3 support(Vec3 direction) {
		//precompute the transformation matrix
		Mat4 transformMat = (Mat4)MatrixUtil.multiply(modelMat, orientation.asMatrix());
		
		Face.HalfEdge original = new Face.HalfEdge(adjInfo.halfEdges.get(0));
		
		//actual vertex being processed, post transformation
		Vec3 curVert = (Vec3)transformMat.multVec(verts.get(original.sourceVert)).swizzle("xyz");
		
		//variable for storing the currently processed vertex dot product with the direction vector
		float dotDir = curVert.dot(direction);
		
		//half edge pointing to the originating edge for the current iteration
		//this value is used to end the iteration over a vertices neighbors
		
		Face.HalfEdge farthestEdge = new Face.HalfEdge(original);
		
		while(true){
			
			Face.HalfEdge current = original.opposite.next;
			Face.HalfEdge adjVertEdge = current.next;
			//loop through the adjacent vertices checking whether they are closer in the
			//given direction vector than the current vertex terminating if we reach the edge
			//we started with
			while(!current.equals(original)){
				curVert.set( (Vec3)transformMat.multVec(verts.get(adjVertEdge.sourceVert)).swizzle("xyz") );
				
				if(curVert.dot(direction) > dotDir){
					dotDir = curVert.dot(direction);
					farthestEdge.set(adjVertEdge);
				}
				//set the current edge to the next edge
				current = current.opposite.next;
				//advance to the next adjacent vertex
				adjVertEdge = current.next;
			}
			//if the current index hasn't changed then we know that the original vertex was 
			//the farthest in the given direction
			if(farthestEdge.sourceVert.equals(original.sourceVert)){
//				System.out.println(curIndex);
				return (Vec3)transformMat.multVec(verts.get(original.sourceVert)).swizzle("xyz");
			}

			original.set(farthestEdge);
		}
	}

	@Override
	public CollisionMesh copy() {
		return new ConvexHull(this);
	}

	@Override
	public void resetModel() {
		modelMat.setMatrix(origModel);
	}

	@Override
	public void resetOrientation() {
		orientation.set(origOrient);
	}

	@Override
	public void reset() {
		modelMat.setMatrix(origModel);
		orientation.set(origOrient);
	}
}
