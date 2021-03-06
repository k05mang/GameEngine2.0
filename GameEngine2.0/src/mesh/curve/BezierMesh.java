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
		numSegments = 1+(curve.getOrder()-1)*20;//+1 to account for cases when the curve is just a line
		
		//specify the attributes for the vertex array of the curve vbo
		vao.addAttrib(AttribType.VEC3, false, 0);//position
		vao.addAttrib(AttribType.VEC3, false, 0);//normal
		
		//index buffer for the curve mesh
		vao.genIBO(CURVE, RenderMode.LINE_STRIP, getIndexType(numSegments));

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
	
	void updateCurve(){
		//vbo for the actual curve
		BufferObject curveVbo = vao.getVBO(CURVE);
		
		//vbo for the control points
		BufferObject controlVbo = vao.getVBO(CONTROL_NODES);
		
		float t = 0;
		Vec3[] curveBuffer = new Vec3[(numSegments+1)*2];//times 2 to account for the normals
		
		Vertex vert = new Vertex(0,0,0, 0,0,0, 0,0);
		//generate points
		for(int curVert = 0; curVert < numSegments+1; curVert++){
			t += curVert/(float)numSegments;
			vert.setPos(this.curve.getBezierPoint(t));
			vert.setNormal(this.curve.getNormal(t));
			geometry.setVertex(curVert, vert);
			//only update the position and normals since UV is just padding
			curveBuffer[curVert*2] = new Vec3(vert.getPos());
			curveBuffer[curVert*2+1] = new Vec3(vert.getNormal());
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
