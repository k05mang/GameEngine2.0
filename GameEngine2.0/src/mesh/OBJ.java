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
		//establish attributes
		vao.addAttrib(AttribType.VEC3, false, 0);//position
		vao.addAttrib(AttribType.VEC3, false, 0);//normal
		vao.addAttrib(AttribType.VEC2, false, 0);//uv
		vao.addAttrib(AttribType.VEC3, false, 0);//tangent
		vao.addAttrib(AttribType.VEC3, false, 0);//bitangent
		
		//create vertex buffer
		vao.genVBO(SOLID_MODE);
		//create index buffer
		vao.genIBO(SOLID_MODE, RenderMode.TRIANGLES, getIndexType(geometry.numVertices()-1));
		
		//add indices and vertices to the buffers
		geometry.insertVertices(vao.getVBO(SOLID_MODE));
		geometry.insertIndices(vao.getIBO(SOLID_MODE), RenderMode.TRIANGLES);

		vao.getVBO(SOLID_MODE).flush(BufferUsage.STATIC_DRAW);
		vao.getIBO(SOLID_MODE).flush(BufferUsage.STATIC_DRAW);
		
		vao.setIndexBuffer(SOLID_MODE);

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
