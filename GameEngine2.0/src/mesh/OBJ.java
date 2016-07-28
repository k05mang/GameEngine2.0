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
		geometry.moveToGeoCenter();
		//create vertex buffer
		BufferObject vbo = new BufferObject(BufferType.ARRAY);
		vbos.add(vbo);
		vao.addVertexBuffer(SOLID_MODE, vbo);
		
		//construct index buffer
		IndexBuffer ibo = new IndexBuffer(getIndexType(geometry.getNumVertices()-1));
		ibos.add(ibo);
		vao.addIndexBuffer(SOLID_MODE, RenderMode.TRIANGLES, ibo);
		
		//add indices and vertices to the buffers
		geometry.insertVertices(vbo);
		geometry.insertIndices(ibo, RenderMode.TRIANGLES);

		vbo.flush(BufferUsage.STATIC_DRAW);
		ibo.flush(BufferUsage.STATIC_DRAW);
		
		vao.setIndexBuffer(SOLID_MODE);

		vao.addAttrib(AttribType.VEC3, false, 0);//position
		vao.addAttrib(AttribType.VEC3, false, 0);//normal
		vao.addAttrib(AttribType.VEC2, false, 0);//uv
		vao.addAttrib(AttribType.VEC3, false, 0);//tangent
		vao.addAttrib(AttribType.VEC3, false, 0);//bitangent
		
		vao.registerVBO(SOLID_MODE);

		vao.setAttribVBO(0, SOLID_MODE);
		vao.setAttribVBO(1, SOLID_MODE);
		vao.setAttribVBO(2, SOLID_MODE);
		vao.setAttribVBO(3, SOLID_MODE);
		vao.setAttribVBO(4, SOLID_MODE);

		vao.enableAttribute(0);
		vao.enableAttribute(1);
		vao.enableAttribute(2);
		vao.enableAttribute(3);
		vao.enableAttribute(4);
		
		this.material = material;
	}
}
