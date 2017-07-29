package mesh.curve;

import java.util.ArrayList;
import java.util.Stack;

import core.managers.SceneManager;
import glMath.VecUtil;
import glMath.vectors.Vec2;
import glMath.vectors.Vec3;
import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import mesh.Mesh;
import mesh.primitives.Vertex;
import renderers.RenderMode;

public class BezierMesh extends Mesh{

	private BezierCurve curve;
	private int numSegments;
//	private final int MIN_SEGMENTS = 50;
	public static final String CONTROL_NODES = "control", CURVE = "curve";
	private static final float CURVE_APPROX = SceneManager.config.get("System").getAsJsonObject().get("bezier-resolution").getAsFloat();
//	private static final float CURVE_APPROX = SceneManager.config.get("System").getAsJsonObject().get("bezier-approx").getAsFloat();//1.5f;//determines the recursion level for constructing the curve
	
	/**
	 * Constructs a Bezier curve using the given {@code curve} as the source of data for rendering, with the given
	 * {@code segments} defining it's fineness
	 * 
	 * @param curve BezierPath object to use as the source of data to render with
	 * @param segments Number of segments to approximate the curve with
	 */
	public BezierMesh(BezierCurve curve){
		super();
		this.curve = curve;
		this.curve.mesh = this;
		numSegments = (curve.getOrder()-1)*20;
		
		//specify the attributes for the vertex array of the curve vbo
		vao.addAttrib(AttribType.VEC3, false, 0);//position
		vao.addAttrib(AttribType.VEC3, false, 0);//normal
		
		//index buffer for the curve mesh
		vao.genIBO(CURVE, RenderMode.LINE_STRIP, IndexBuffer.IndexType.INT);

		//index buffer for the control mesh
		vao.genIBO(CONTROL_NODES, RenderMode.LINE_STRIP, getIndexType(curve.numPoints()));
		
		
		//vbo for the actual curve
		vao.genVBO(CURVE);

		//vbo for the control points
		vao.genVBO(CONTROL_NODES);
		
		constructCurve();
		
		//tell the vao what vbo to read from for each attribute
		vao.setAttribVBO(0, CURVE);
		vao.setAttribVBO(1, CURVE);
		
		vao.setIndexBuffer(CURVE);
		
		//enable the attributes for the vertex array
		vao.enableAttribute(0);
		vao.enableAttribute(1);
	}
	
	@Override
	public void setRenderMode(String mode){
		if(mode.equals(SOLID_MODE) || mode.equals(EDGE_MODE)){
			vao.setIndexBuffer(CURVE);
			vao.setAttribVBO(0, CURVE);
			vao.setAttribVBO(1, CURVE);
		}else{
			vao.setIndexBuffer(mode);
			vao.setAttribVBO(0, mode);
			vao.setAttribVBO(1, mode);
		}
	}
	
	/**
	 * Constructs the curve object from scratch using the initializing curve as the source
	 * Iterative curve generation (reliable and stable)
	 */
	void constructCurve(){
		geometry.empty();
		//indices for the curve mesh
		IndexBuffer curveIbo = vao.getIBO(CURVE);
		curveIbo.reset(getIndexType(numSegments));

		//indices for the control mesh
		IndexBuffer controlIbo = vao.getIBO(CONTROL_NODES);
		controlIbo.reset(getIndexType(curve.numPoints()));
		
		//vbo for the actual curve
		BufferObject curveVbo = vao.getVBO(CURVE);
		curveVbo.reset();
		
		//vbo for the control points
		BufferObject controlVbo = vao.getVBO(CONTROL_NODES);
		controlVbo.reset();
		
		float t = 0;
		//generate points, +1 to segments so the loop captures the last point
		for(int curVert = 0; curVert < numSegments+1; curVert++){
			t = curVert/(float)numSegments;
			Vertex vert = new Vertex(curve.getBezierPoint(t), curve.getNormal(t), new Vec2());
			//add the vertex to the geometry object
			geometry.add(vert);
			//add the position and normals to the vbo
			curveVbo.add(vert.getPos());
			curveVbo.add(vert.getNormal());
			//add the index to the ibo
			curveIbo.add(curVert);
		}
		
		ArrayList<Vec3> points = curve.getPoints();
		//add control points to vbo
		for(int curPoint = 0; curPoint < points.size(); curPoint++){
			controlVbo.add(points.get(curPoint));
			//normal values that aren't used by the control buffer
			controlVbo.add(0);
			controlVbo.add(0);
			controlVbo.add(0);
			controlIbo.add(curPoint);
		}
		
		//flush and add the curve vbo
		curveVbo.flush(BufferUsage.STATIC_DRAW);

		//flush and add the control vbo
		controlVbo.flush(BufferUsage.STATIC_DRAW);

		//flush curve ibo and add to vao
		curveIbo.flush(BufferUsage.STATIC_DRAW);

		//flush control ibo and add to vao
		controlIbo.flush(BufferUsage.STATIC_DRAW);
	}
	
	//recursive curve generation
//	void constructCurve(){
//		geometry.empty();
//		//indices for the curve mesh
//		IndexBuffer curveIbo = vao.getIBO(CURVE);
//		curveIbo.reset(IndexBuffer.IndexType.INT);
//
//		//indices for the control mesh
//		IndexBuffer controlIbo = vao.getIBO(CONTROL_NODES);
//		controlIbo.reset(getIndexType(this.curve.numPoints()));
//		
//		//vbo for the actual curve
//		BufferObject curveVbo = vao.getVBO(CURVE);
//		curveVbo.reset();
//		
//		//vbo for the control points
//		BufferObject controlVbo = vao.getVBO(CONTROL_NODES);
//		controlVbo.reset();
//
//		build(this.curve);
//		
//		ArrayList<Vec3> points = this.curve.getPoints();
//		//add control points to vbo
//		for(int curPoint = 0; curPoint < points.size(); curPoint++){
//			controlVbo.add(points.get(curPoint));
//			//normal values that aren't used by the control buffer
//			controlVbo.add(0);
//			controlVbo.add(0);
//			controlVbo.add(0);
//			controlIbo.add(curPoint);
//		}
//		
//		//flush and add the curve vbo
//		curveVbo.flush(BufferUsage.STATIC_DRAW);
//
//		//flush and add the control vbo
//		controlVbo.flush(BufferUsage.STATIC_DRAW);
//
//		//flush curve ibo and add to vao
//		curveIbo.flush(BufferUsage.STATIC_DRAW);
//
//		//flush control ibo and add to vao
//		controlIbo.flush(BufferUsage.STATIC_DRAW);
//	}
//	builds in a pseudo recursive method that utilizes the heap instead of stack by using stack data structures
//	private void build(BezierCurve curve){
//		//check if the curve is a point
//		if(curve.getOrder() == 0){
//			return;
//		}
//		//create the stack to simulate the recursion
//		Stack<BezierCurve> splitCurves = new Stack<BezierCurve>();
//		//add the parameter as the starting curve
//		splitCurves.push(curve);
//		boolean split = false;
//		BezierCurve curCurve = null;
//		float angle = 0;
//		//continue iterate over the sub curves if they exist
//		while(!splitCurves.isEmpty()){
//			//assess the current curves control nodes to determine the overall "curvy-ness" of the bezier path
//			curCurve  = splitCurves.pop();
//			//check the angle between each set of 3 controls to determine within how much of the approximation tolerance the whole curve is
//			for(int curCtrl = 0; curCtrl < curCurve.getOrder()-1; curCtrl++){//minus 1 to stop iterating with the last 3 points
//				//if the curve is outside of the tolerance for any set of 3 controls then the curve will be split near that first intolerance
//				//get the points
////				Vec3 start = curCurve.getBezierPoint(curCtrl/(float)(curCurve.getOrder()+1));
////				Vec3 mid = curCurve.getBezierPoint((curCtrl+1)/(float)(curCurve.getOrder()+1));
////				Vec3 end = curCurve.getBezierPoint((curCtrl+2)/(float)(curCurve.getOrder()+1));
//				Vec3 start = curCurve.getPoints().get(curCtrl);
//				Vec3 mid = curCurve.getPoints().get(curCtrl+1);
//				Vec3 end = curCurve.getPoints().get(curCtrl+2);
//				
//				//get the edges between the ends and the mid point for the calculation
//				Vec3 leftEdge = VecUtil.subtract(start, mid).normalize();
//				Vec3 rightEdge = VecUtil.subtract(end, mid).normalize();
//				//get the angle between the points
//				angle = (float)Math.toDegrees(Math.acos(leftEdge.dot(rightEdge)));
//				
//				//if the angle is not within the approximation to 180 degrees (straight line) then continue the recursion
//				if(180-angle > CURVE_APPROX){
//					split = true;
//					//split the curve at roughly the mid point
//					BezierCurve[] curves = curCurve.split((curCtrl+1)/(float)(curCurve.getOrder()+1));//roughly get the mid points t and split on that
//					//add the right half to the stack, this insures the right half is added after the left half
//					splitCurves.push(curves[1]);
//					//add the left half to the stack
//					splitCurves.push(curves[0]);
//					
//					//exit the inner for
//					break;
//				}else{
//					split = false;
//				}
//			}
//			//if we have determined that all the points in the curve are linear within the given tolerance then we can add the end points of the curve
//			//as a line segment
//			
//			//if we didn't split the curve then add the end points to the mesh
//			if(!split){
//				//add to the geometry
//				Vertex vert1 = new Vertex(curCurve.getBezierPoint(0), curCurve.getNormal(0), new Vec2());
//				Vertex vert2 = new Vertex(curCurve.getBezierPoint(1), curCurve.getNormal(1), new Vec2());
//				geometry.add(vert1);
//				geometry.add(vert2);
//				
//				//add the points to the vbo
//				vao.getVBO(CURVE).add(vert1.getPos());
//				vao.getVBO(CURVE).add(vert1.getNormal());
//				vao.getVBO(CURVE).add(vert2.getPos());
//				vao.getVBO(CURVE).add(vert2.getNormal());
//				
//				//add to the ibo
//				vao.getIBO(CURVE).add(geometry.numVertices()-2);
//				vao.getIBO(CURVE).add(geometry.numVertices()-1);
//			}
//		}
//	}
	
	//standard recursive method without too much consideration for stack retention
//	private void build(BezierCurve curve){
//		//check if the curve is a point
//		if(curve.getOrder() < 1){
//			//do nothing and simply exit. A curve shouldn't be drawing a point
//			return;
//		}else{//if there are only two controls add them and exit, this means the curve is a line
//			//assess the current curves control nodes to determine the overall "curvy-ness" of the bezier path
//			
//			//check the angle between each set of 3 controls to determine within how much of the approximation tolerance the whole curve is
//			for(int curCtrl = 0; curCtrl < curve.getOrder()-1; curCtrl++){//minus 1 to stop iterating with the last 3 points
//				//if the curve is outside of the tolerance for any set of 3 controls then the curve will be split near that first intolerance
//				//get the points
//				Vec3 start = curve.getBezierPoint(curCtrl/(float)(curve.getOrder()+1));
//				Vec3 mid = curve.getBezierPoint((curCtrl+1)/(float)(curve.getOrder()+1));
//				Vec3 end = curve.getBezierPoint((curCtrl+2)/(float)(curve.getOrder()+1));
//				
//				//get the edges between the ends and the mid point for the calculation
//				Vec3 leftEdge = VecUtil.subtract(start, mid).normalize();
//				Vec3 rightEdge = VecUtil.subtract(end, mid).normalize();
//				//get the angle between the points
//				float angle = (float)Math.toDegrees(Math.acos(leftEdge.dot(rightEdge)));
//				
//				//if the angle is not within the approximation to 180 degrees (straight line) then continue the recursion
//				if(180-angle > CURVE_APPROX){
//					//split the curve at roughly the mid point
//					BezierCurve[] curves = curve.split((curCtrl+1)/(float)(curve.getOrder()+1));//roughly get the mid points t and split on that
//					//recurse the left half
//					build(curves[0]);
//					//recurse the right half
//					build(curves[1]);
//					
//					//exit the loop
//					return;
//				}
//			}
//			//if we have determined that all the points in the curve are linear within the given tolerance then we can add the end points of the curve
//			//as a line segment
//			
//		}
//		
//		//add to the geometry
//		Vertex vert1 = new Vertex(curve.getBezierPoint(0), curve.getNormal(0), new Vec2());
//		Vertex vert2 = new Vertex(curve.getBezierPoint(1), curve.getNormal(1), new Vec2());
//		geometry.add(vert1);
//		geometry.add(vert2);
//		
//		//add the points to the vbo
//		vao.getVBO(CURVE).add(vert1.getPos());
//		vao.getVBO(CURVE).add(vert1.getNormal());
//		vao.getVBO(CURVE).add(vert2.getPos());
//		vao.getVBO(CURVE).add(vert2.getNormal());
//		
//		//add to the ibo
//		vao.getIBO(CURVE).add(geometry.numVertices()-2);
//		vao.getIBO(CURVE).add(geometry.numVertices()-1);
//	}
	
	//recursive method with attempted stack memory reduction
//	private void build(BezierCurve curve){
//		boolean split = false;
//		//check if the curve is a point
//		if(curve.getOrder() < 1){
//			//do nothing and simply exit. A curve shouldn't be drawing a point
//			return;
//		}else{//if there are only two controls add them and exit, this means the curve is a line
//			//assess the current curves control nodes to determine the overall "curvy-ness" of the bezier path
//			
//			//check the angle between each set of 3 controls to determine within how much of the approximation tolerance the whole curve is
//			for(int curCtrl = 0; curCtrl < curve.getOrder()-1; curCtrl++){//minus 1 to stop iterating with the last 3 points
//				//if the curve is outside of the tolerance for any set of 3 controls then the curve will be split near that first intolerance
//				//get the points
//				Vec3 start = curve.getBezierPoint(curCtrl/(float)(curve.getOrder()+1));
//				Vec3 mid = curve.getBezierPoint((curCtrl+1)/(float)(curve.getOrder()+1));
//				Vec3 end = curve.getBezierPoint((curCtrl+2)/(float)(curve.getOrder()+1));
//				
//				//get the edges between the ends and the mid point for the calculation
//				Vec3 leftEdge = VecUtil.subtract(start, mid).normalize();
//				Vec3 rightEdge = VecUtil.subtract(end, mid).normalize();
//				//get the angle between the points
//				float angle = (float)Math.toDegrees(Math.acos(leftEdge.dot(rightEdge)));
//				
//				//if the angle is not within the approximation to 180 degrees (straight line) then continue the recursion
//				if(180-angle > CURVE_APPROX){
//					split = true;
//					//split the curve at roughly the mid point
//					BezierCurve[] curves = curve.split((curCtrl+1)/(float)(curve.getOrder()+1));//roughly get the mid points t and split on that
//					splitCurves.push(curves[1]);
//					splitCurves.push(curves[0]);
////					splitCurves.addAll(Arrays.asList(curve.split((curCtrl+1)/(float)(curve.getOrder()+1))));//roughly get the mid points t and split on that
////					int leftIndex = splitCurves.size()-2;
////					int rightIndex = splitCurves.size()-1;
//					//recurse the left half
////					build(splitCurves.get(leftIndex));
////					build(curves[0]);
//					//recurse the right half
////					build(splitCurves.get(rightIndex));
////					build(curves[1]);
//					
//					//exit the loop
//					break;
////					return;
//				}
//			}
//			
//			if(split){
//				build(splitCurves.pop());
//				build(splitCurves.pop());
//				return;
//			}
//			//if we have determined that all the points in the curve are linear within the given tolerance then we can add the end points of the curve
//			//as a line segment
//			
//		}
//		
//		//add to the geometry
//		Vertex vert1 = new Vertex(curve.getBezierPoint(0), curve.getNormal(0), new Vec2());
//		Vertex vert2 = new Vertex(curve.getBezierPoint(1), curve.getNormal(1), new Vec2());
//		geometry.add(vert1);
//		geometry.add(vert2);
//		
//		//add the points to the vbo
//		vao.getVBO(CURVE).add(vert1.getPos());
//		vao.getVBO(CURVE).add(vert1.getNormal());
//		vao.getVBO(CURVE).add(vert2.getPos());
//		vao.getVBO(CURVE).add(vert2.getNormal());
//		
//		//add to the ibo
//		vao.getIBO(CURVE).add(geometry.numVertices()-2);
//		vao.getIBO(CURVE).add(geometry.numVertices()-1);
//	}
	
	void updateCurve(){
		//vbo for the actual curve
		BufferObject curveVbo = vao.getVBO(CURVE);
		
		//vbo for the control points
		BufferObject controlVbo = vao.getVBO(CONTROL_NODES);
		
		float incr = 1.0f/100;
		float t = 0;
		Vec3[] curveBuffer = new Vec3[100*2];//times 2 to account for the normals
		
		Vertex vert = new Vertex(0,0,0, 0,0,0, 0,0);
		//generate points
		for(int curVert = 0; curVert < 100; curVert++){
			vert.setPos(this.curve.getBezierPoint(t));
			vert.setNormal(this.curve.getNormal(t));
			geometry.setVertex(curVert, vert);
			//only update the position and normals since UV is just padding
			curveBuffer[curVert*2] = new Vec3(vert.getPos());
			curveBuffer[curVert*2+1] = new Vec3(vert.getNormal());
			t += incr;
		}
		curveVbo.set(0, curveBuffer);//update the vbo
		
		Vec3[] controlBuffer = new Vec3[this.curve.numPoints()*2];
		ArrayList<Vec3> points = this.curve.getPoints();
		//add control points to vbo
		for(int curPoint = 0; curPoint < points.size(); curPoint++){
			
			//only update the position and normals since UV is just padding
			controlBuffer[curPoint*2] = new Vec3(points.get(curPoint));
			controlBuffer[curPoint*2+1] = new Vec3();
		}
		controlVbo.set(0, controlBuffer);//update the vbo
	}
	
	@Override
	public void delete(){
		super.delete();
		this.curve.mesh = null;
	}
}
