package core.debug;

import physics.collision.ConvexHull2D;
import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import gldata.IndexBuffer.IndexType;
import mesh.Mesh;
import mesh.primitives.HalfEdge;
import renderers.RenderMode;
import shaders.ShaderProgram;

public class ConvexHull2DGen extends Mesh {

	public ConvexHull2DGen() {
		// TODO Auto-generated constructor stub
	}

	public ConvexHull2DGen(Mesh copy) {
		super(copy);
		// TODO Auto-generated constructor stub
	}
	
	public ConvexHull2DGen(ConvexHull2D hull){
		super();
		//add the vertex buffer to the array of buffers
		vbos.add(new BufferObject(BufferType.ARRAY));
		ibos.add(new IndexBuffer(IndexType.INT));
		
		vao.addVertexBuffer("mesh", vbos.get(0));
		vao.addIndexBuffer("lines", RenderMode.LINE_LOOP, ibos.get(0));
		
		//add the vertices to the vbo and the indices to the ibo
		int cur = 0;
		HalfEdge curEdge = hull.baseEdge.next;
		
		while(!curEdge.equals(hull.baseEdge)){
			vbos.get(0).add(hull.mesh.getVertex(curEdge.sourceVert).getPos());
			ibos.get(0).add(cur);
			
			cur++;
			curEdge = curEdge.next;
		}

		vbos.get(0).add(hull.mesh.getVertex(curEdge.sourceVert).getPos());
		ibos.get(0).add(cur);

		vbos.get(0).flush(BufferUsage.STATIC_DRAW);
		ibos.get(0).flush(BufferUsage.STATIC_DRAW);
		
		vao.addAttrib(AttribType.VEC3, false, 0);
		vao.enableAttribute(0);
		//add vbo
		vao.registerVBO("mesh");
		vao.setAttribVBO(0, "mesh");
		
		vao.setIndexBuffer("lines");
	}
	
	public void render(ShaderProgram shader){
		shader.setUniform("model", getModelView());
		
		shader.setUniform("color", 1f, 1f, 1f, 1);
		render();
	}
}
