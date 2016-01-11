package mesh;

import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;

import renderers.RenderMode;

public class OBJ extends Renderable {
	
	public OBJ(Geometry mesh){
		super();
		
		geometry = new Geometry(mesh);
		//create vertex buffer
		BufferObject vbo = new BufferObject(BufferType.ARRAY);
		vbos.add(vbo);
		vao.addVertexBuffer("default", vbo);
		
		//construct index buffer
		IndexBuffer ibo = new IndexBuffer(getIndexType(geometry.getNumVertices()-1));
		ibos.add(ibo);
		vao.addIndexBuffer("default", RenderMode.TRIANGLES, ibo);
		
		//add indices and vertices to the buffers
		geometry.insertVertices(vbo);
		geometry.insertIndices(ibo, RenderMode.TRIANGLES);

		vbo.flush(BufferUsage.STATIC_DRAW);
		ibo.flush(BufferUsage.STATIC_DRAW);
		
		vao.setIndexBuffer("default");

		vao.addAttrib(0, AttribType.VEC3, false, 0);
		vao.addAttrib(1, AttribType.VEC3, false, 0);
		vao.addAttrib(2, AttribType.VEC2, false, 0);
		
		vao.registerVBO("default");

		vao.setAttribVBO(0, "default");
		vao.setAttribVBO(1, "default");
		vao.setAttribVBO(2, "default");

		vao.enableAttribute(0);
		vao.enableAttribute(1);
		vao.enableAttribute(2);
	}
}
