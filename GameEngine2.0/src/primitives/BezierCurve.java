package primitives;

import java.util.ArrayList;

import glMath.vectors.Vec2;
import glMath.vectors.Vec3;
import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import renderers.RenderMode;
import renderers.Renderable;

public class BezierCurve extends Renderable{

	private BezierPath path;
	private int numSegments;
	
	/**
	 * Constructs a Bezier curve using the given {@code curve} as the source of data for rendering, with the given
	 * {@code segments} defining it's fineness
	 * 
	 * @param curve BezierPath object to use as the source of data to render with
	 * @param segments Number of segments to approximate the curve with
	 */
	public BezierCurve(BezierPath curve, int segments){
		super();
		numSegments = segments;
		path = curve;
		path.curve = this;
		//indices for the curve mesh
		IndexBuffer curveIbo = new IndexBuffer(getIndexType(segments));
		ibos.add(curveIbo);
		
		//indices for the curve mesh
		IndexBuffer controlIbo = new IndexBuffer(getIndexType(curve.numPoints()));
		ibos.add(controlIbo);
		
		//vbo for the actual curve
		BufferObject curveVbo = new BufferObject(BufferType.ARRAY);
		vbos.add(curveVbo);
		
		//vbo for the control points
		BufferObject controlVbo = new BufferObject(BufferType.ARRAY);
		vbos.add(controlVbo);
		
		constructCurve(segments);
		
		//flush and add the curve vbo
		vao.addVertexBuffer("curve", curveVbo);
		
		//flush and add the control vbo
		vao.addVertexBuffer("control", controlVbo);

		//flush curve ibo and add to vao
		vao.addIndexBuffer(RenderMode.LINE_STRIP, curveIbo);
		
		//flush control ibo and add to vao
		vao.addIndexBuffer(RenderMode.LINES, controlIbo);
		
		//specify the attributes for the vertex array of the curve vbo
		vao.addAttrib(0, AttribType.VEC3, false, 0);//position
		vao.addAttrib(1, AttribType.VEC3, false, 0);//normal
				
		//bind the vbos to the vao
		vao.registerVBO("curve");
		vao.registerVBO("control");
		
		//tell the vao what vbo to read from for each attribute
		vao.setAttribVBO(0, "curve");
		vao.setAttribVBO(1, "curve");
		
		vao.setIndexBuffer(RenderMode.LINE_STRIP);
		
		//enable the attributes for the vertex array
		vao.enableAttribute(0);
		vao.enableAttribute(1);
	}
	
	/**
	 * Sets this curve up to be able to render the curve itself
	 */
	public void renderCurve(){
		vao.setIndexBuffer(RenderMode.LINE_STRIP);
		vao.setAttribVBO(0, "curve");
		vao.setAttribVBO(1, "curve");
	}
	
	/**
	 * Sets this curve up to be able to render the control points of the curve
	 */
	public void renderControl(){
		vao.setIndexBuffer(RenderMode.LINES);
		vao.setAttribVBO(0, "control");
		vao.setAttribVBO(1, "control");
	}
	
	/**
	 * Constructs the curve object from scratch using the initializing curve as the source
	 * 
	 * @param segments Number of segments to construct the curve approximation with
	 */
	void constructCurve(int segments){
		
		mesh.empty();
		IndexBuffer curveIbo = ibos.get(0);
		curveIbo.reset(getIndexType(numSegments));
		
		//indices for the curve mesh
		IndexBuffer controlIbo = ibos.get(1);
		controlIbo.reset(getIndexType(path.numPoints()));
		
		//vbo for the actual curve
		BufferObject curveVbo = vbos.get(0);
		curveVbo.reset();
		
		//vbo for the control points
		BufferObject controlVbo = vbos.get(1);
		controlVbo.reset();
		
		float incr = 1.0f/numSegments;
		float t = 0;
		//generate points
		for(int curVert = 0; curVert < numSegments; curVert++){
			//TODO add normals
			Vertex vert = new Vertex(path.getBezierPoint(t), new Vec3(), new Vec2());
			mesh.add(vert);
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
			if(curPoint < points.size()-1){
				controlIbo.add(curPoint);
				controlIbo.add(curPoint+1);
			}
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
		BufferObject curveVbo = vbos.get(0);
		
		//vbo for the control points
		BufferObject controlVbo = vbos.get(1);
		
		float incr = 1.0f/numSegments;
		float t = 0;
		Vec3[] curveBuffer = new Vec3[numSegments*2];//times 2 to account for the normals
		Vertex vert = new Vertex(0,0,0, 0,0,0, 0,0);
		//generate points
		for(int curVert = 0; curVert < numSegments; curVert++){
			//TODO add normals
			vert.setPos(path.getBezierPoint(t));
			mesh.setVertex(curVert, vert);
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

	@Override
	public void addMode(RenderMode mode) {
		// TODO Auto-generated method stub
		
	}
}
