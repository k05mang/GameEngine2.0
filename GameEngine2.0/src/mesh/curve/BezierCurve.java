package mesh.curve;

import java.util.ArrayList;

import mesh.Mesh;
import mesh.primitives.Vertex;
import mesh.primitives.geometry.Cone;
import glMath.VecUtil;
import glMath.vectors.Vec2;
import glMath.vectors.Vec3;
import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import renderers.RenderMode;

public class BezierCurve extends Mesh{

	private BezierPath path;
	private int numSegments;
	private final int MIN_SEGMENTS = 50;
	public static final String CONTROL_NODES = "control", CURVE = "curve";
	
	/**
	 * Constructs a Bezier curve using the given {@code curve} as the source of data for rendering, with the given
	 * {@code segments} defining it's fineness
	 * 
	 * @param curve BezierPath object to use as the source of data to render with
	 * @param segments Number of segments to approximate the curve with
	 */
	public BezierCurve(BezierPath curve){
		super();
		path = curve;
		path.curve = this;
		numSegments = MIN_SEGMENTS+(path.getOrder())*20;
		
		//specify the attributes for the vertex array of the curve vbo
		vao.addAttrib(AttribType.VEC3, false, 0);//position
		vao.addAttrib(AttribType.VEC3, false, 0);//normal
		
		//index buffer for the curve mesh
		vao.genIBO(CURVE, RenderMode.LINE_STRIP, getIndexType(numSegments));
		IndexBuffer curveIbo = vao.getIBO(CURVE);

		//index buffer for the control mesh
		vao.genIBO(CONTROL_NODES, RenderMode.LINE_STRIP, getIndexType(curve.numPoints()));
		IndexBuffer controlIbo = vao.getIBO(CONTROL_NODES);
		
		
		//vbo for the actual curve
		vao.genVBO(CURVE);
		BufferObject curveVbo = vao.getVBO(CURVE);

		//vbo for the control points
		vao.genVBO(CONTROL_NODES);
		BufferObject controlVbo = vao.getVBO(CONTROL_NODES);
		
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
	 */
	void constructCurve(){
		numSegments = MIN_SEGMENTS+(path.getOrder())*20;
		geometry.empty();
		//indices for the curve mesh
		IndexBuffer curveIbo = vao.getIBO(CURVE);
		curveIbo.reset(getIndexType(numSegments));

		//indices for the control mesh
		IndexBuffer controlIbo = vao.getIBO(CONTROL_NODES);
		controlIbo.reset(getIndexType(path.numPoints()));
		
		//vbo for the actual curve
		BufferObject curveVbo = vao.getVBO(CURVE);
		curveVbo.reset();
		
		//vbo for the control points
		BufferObject controlVbo = vao.getVBO(CONTROL_NODES);
		controlVbo.reset();
		
		float incr = 1.0f/numSegments;
		float t = 0;
		//generate points
		for(int curVert = 0; curVert < numSegments; curVert++){
			Vertex vert = new Vertex(path.getBezierPoint(t), path.getNormal(t), new Vec2());
			geometry.add(vert);
			curveVbo.add(vert.getPos());
			curveVbo.add(vert.getNormal());
			t += incr;
			curveIbo.add(curVert);
		}
		
		ArrayList<Vec3> points = path.getPoints();
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
	
	/**
	 * Updates the curve points and the control points for rendering
	 */
	void updateCurve(){
		//vbo for the actual curve
		BufferObject curveVbo = vao.getVBO(CURVE);
		
		//vbo for the control points
		BufferObject controlVbo = vao.getVBO(CONTROL_NODES);
		
		float incr = 1.0f/numSegments;
		float t = 0;
		Vec3[] curveBuffer = new Vec3[numSegments*2];//times 2 to account for the normals
		
		Vertex vert = new Vertex(0,0,0, 0,0,0, 0,0);
		//generate points
		for(int curVert = 0; curVert < numSegments; curVert++){
			//TODO add normals
			vert.setPos(path.getBezierPoint(t));
			vert.setNormal(path.getNormal(t));
			geometry.setVertex(curVert, vert);
			//only update the position and normals since UV is just padding
			curveBuffer[curVert*2] = new Vec3(vert.getPos());
			curveBuffer[curVert*2+1] = new Vec3(vert.getNormal());
			t += incr;
		}
		curveVbo.set(0, curveBuffer);//update the vbo
		
		Vec3[] controlBuffer = new Vec3[path.numPoints()*2];
		ArrayList<Vec3> points = path.getPoints();
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
		path.curve = null;
	}
}
