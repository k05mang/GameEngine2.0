package mesh;

import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;

import renderers.RenderMode;

public class OBJ extends Mesh {
	
	public OBJ(Geometry mesh, String material){
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

		vao.addAttrib(AttribType.VEC3, false, 0);//position
		vao.addAttrib(AttribType.VEC3, false, 0);//normal
		vao.addAttrib(AttribType.VEC2, false, 0);//uv
		vao.addAttrib(AttribType.VEC3, false, 0);//tangent
		vao.addAttrib(AttribType.VEC3, false, 0);//bitangent
		
		vao.registerVBO("default");

		vao.setAttribVBO(0, "default");
		vao.setAttribVBO(1, "default");
		vao.setAttribVBO(2, "default");
		vao.setAttribVBO(3, "default");
		vao.setAttribVBO(4, "default");

		vao.enableAttribute(0);
		vao.enableAttribute(1);
		vao.enableAttribute(2);
		vao.enableAttribute(3);
		vao.enableAttribute(4);
		
		this.material = material;
	}
}
