package primitives;

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

	public BezierCurve(BezierPath curve, int segments){
		super();
		IndexBuffer indices = null;
		//determine what data type the index buffer should be
		if(segments < Byte.MAX_VALUE){
			indices = new IndexBuffer(IndexBuffer.IndexType.BYTE);
		}else if(segments < Short.MAX_VALUE){
			indices = new IndexBuffer(IndexBuffer.IndexType.SHORT);
		}else if(segments < Integer.MAX_VALUE){
			indices = new IndexBuffer(IndexBuffer.IndexType.INT);
		}else{
			//TODO handle when the number of vertices and indices would exceed the max value
		}
		ibos.add(indices);
		
		BufferObject vbo = new BufferObject(BufferType.ARRAY);
		vbos.add(vbo);
		
		float incr = 1.0f/segments;
		float t = 0;
		//generate points
		for(int curVert = 0; curVert < segments; curVert++){
			Vertex vert = new Vertex(curve.getBezierPoint(t), new Vec3(), new Vec2());
			mesh.add(vert);
			vert.addTo(vbo);
			t += incr;
			indices.add(curVert);
		}
		
		vbo.flush(BufferUsage.STATIC_DRAW);
		vao.addVertexBuffer("default", vbo);
		
		indices.flush(BufferUsage.STATIC_DRAW);
		vao.addIndexBuffer(RenderMode.LINE_STRIP, indices);
		//specify the attributes for the vertex array
		vao.addAttrib(0, AttribType.VEC3, false, 0, 0);//position
		vao.addAttrib(1, AttribType.VEC3, false, 0, 0);//normal
		vao.addAttrib(2, AttribType.VEC2, false, 0, 0);//UV
		
		//tell the vao the vertex buffer to use
		vao.setVertexBuffer("default", 0);
		vao.setIndexBuffer(RenderMode.LINE_STRIP);
		
		//enable the attributes for the vertex array
		vao.enableAttribute(0);
		vao.enableAttribute(1);
	}

	@Override
	public void addMode(RenderMode mode) {
		// TODO Auto-generated method stub
		
	}
}
