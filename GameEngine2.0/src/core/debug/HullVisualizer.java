package core.debug;

import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import gldata.IndexBuffer.IndexType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import mesh.Geometry;
import mesh.Mesh;
import mesh.primitives.HalfEdge;
import mesh.primitives.Triangle;
import renderers.RenderMode;
import shaders.ShaderProgram;

public class HullVisualizer extends Mesh {
	
	public static final String FACES = "faces", LINES = "lines";
	
	public HullVisualizer(ConvexHull3D hull) {
		
		//create the buffer for containing the vertices
		vbos.add(new BufferObject(BufferType.ARRAY));
		//create separate ibo for rendering faces
		ibos.add(new IndexBuffer(IndexType.INT));
		//create separate ibo for rendering lines
		ibos.add(new IndexBuffer(IndexType.INT));
		
		//add just the vertex position information to the vbo
		for(int curVert = 0; curVert < geometry.numVertices(); curVert++){
			vbos.get(0).add(geometry.getVertex(curVert).getPos());
			//initialize the point ibo with all the points
			ibos.get(2).add(curVert);
		}
		//flush vertices to the GPU
		vbos.get(0).flush(BufferUsage.STATIC_DRAW);
		
		//flush indices to the GPU
		ibos.get(2).flush(BufferUsage.STATIC_DRAW);
		
		//setup the vao
		//add attribute
		vao.addAttrib(AttribType.VEC3, false, 0);
		vao.enableAttribute(0);
		//add vbo
		vao.addVertexBuffer("mesh", vbos.get(0));
		vao.registerVBO("mesh");
		vao.setAttribVBO(0, "mesh");
		
		//add index buffers
		vao.genIBO(FACES, RenderMode.TRIANGLES, ibos.get(0));
		vao.genIBO(LINES, RenderMode.LINES, ibos.get(1));
		
		vao.setIndexBuffer(FACES);
	}
}
