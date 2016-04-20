package mesh.primitives.geometry;

import mesh.Mesh;
import mesh.primitives.Triangle;
import mesh.primitives.Vertex;
import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import renderers.RenderMode;

public final class Plane extends Mesh{
	float width, length;
	/**
	 * Constructs a plane with the given {@code sideLength} as the width and length of the plane along
	 * the x-z dimensions, the resulting plane will be square. The default mode the resulting
	 * mesh will render with will be SOLID_MODE.
	 * 
	 * @param sideLength Width and length of this plane along the x and z axis
	 */
	public Plane(float sideLength){
		this(sideLength, sideLength, SOLID_MODE);
	}

	/**
	 * Constructs a plane with the given {code width} being the dimension along the x axis, and {code length}
	 * being the dimension along the z axis. The default mode the resulting mesh will render with will be SOLID_MODE.
	 * 
	 * @param width Width of this plane along the x axis
	 * @param length Length of this plane along the z axis
	 */
	public Plane(float width, float length){
		this(width, length, SOLID_MODE);
	}

	/**
	 * Constructs a plane with the given {@code sideLength} as the width and length of the plane along
	 * the x-z dimensions, the resulting plane will be square. {@code defaultMode} will specify the mode the mesh will initially 
	 * render with. Selectable modes and what they entail are as follows:
	 * <ul>
	 * <li>SOLID_MODE: The mesh will render as a GL_TRIANGLES</li>
	 * <li>EDGE_MODE: The mesh will render as GL_LINES, where only the major edges of the mesh are rendered. This will
	 * only render edges of the mesh that define its shape.</li>
	 * </ul>
	 * 
	 * @param sideLength Width and length of this plane along the x and z axis
	 * @param defaultMode Defines the mode to render the mesh with
	 */
	public Plane(float sideLength, String defaultMode){
		this(sideLength, sideLength, defaultMode);
	}
	
	/**
	 * Constructs a plane with the given {code width} being the dimension along the x axis, and {code length}
	 * being the dimension along the z axis. {@code defaultMode} will specify the mode the mesh will initially 
	 * render with. Selectable modes and what they entail are as follows:
	 * <ul>
	 * <li>SOLID_MODE: The mesh will render as a GL_TRIANGLES</li>
	 * <li>EDGE_MODE: The mesh will render as GL_LINES, where only the major edges of the mesh are rendered. This will
	 * only render edges of the mesh that define its shape.</li>
	 * </ul>
	 * 
	 * @param width Width of this plane along the x axis
	 * @param length Length of this plane along the z axis
	 * @param defaultMode Defines the mode to render the mesh with
	 */
	public Plane(float width, float length, String defaultMode){
		super();
		this.width = Math.abs(width);
		this.length = Math.abs(length);
		IndexBuffer solidIbo = new IndexBuffer(IndexBuffer.IndexType.BYTE);
		IndexBuffer edgeIbo = new IndexBuffer(IndexBuffer.IndexType.BYTE);
		ibos.add(solidIbo);
		ibos.add(edgeIbo);
		vao.addIndexBuffer(SOLID_MODE, RenderMode.TRIANGLES, solidIbo);
		vao.addIndexBuffer(EDGE_MODE, RenderMode.LINES, edgeIbo);
		
		BufferObject vbo = new BufferObject(BufferType.ARRAY);
		vbos.add(vbo);
		
		Vertex backLeft = new Vertex(-this.width/2.0f,0,-this.length/2.0f, 0,1,0, 0,1, 1,0,0, 0,0,-1);
		Vertex frontLeft = new Vertex(-this.width/2.0f,0,this.length/2.0f, 0,1,0, 0,0, 1,0,0, 0,0,-1);
		Vertex backRight = new Vertex(this.width/2.0f,0,-this.length/2.0f, 0,1,0, 1,1, 1,0,0, 0,0,-1);
		Vertex frontRight = new Vertex(this.width/2.0f,0,this.length/2.0f, 0,1,0, 1,0, 1,0,0, 0,0,-1);
		
		backLeft.addTo(vbo);
		frontLeft.addTo(vbo);
		backRight.addTo(vbo);
		frontRight.addTo(vbo);
		
		geometry.add(backLeft);
		geometry.add(frontLeft);
		geometry.add(backRight);
		geometry.add(frontRight);
		
		geometry.add(new Triangle(0,1,2));
		geometry.add(new Triangle(2,1,3));

		//solid ibo
		solidIbo.add(0);
		solidIbo.add(1);
		solidIbo.add(2);
		
		solidIbo.add(2);
		solidIbo.add(1);
		solidIbo.add(3);

		//edge ibo
		edgeIbo.add(0);
		edgeIbo.add(1);
		
		edgeIbo.add(1);
		edgeIbo.add(3);
		
		edgeIbo.add(3);
		edgeIbo.add(2);
		
		edgeIbo.add(2);
		edgeIbo.add(0);
		
		vbo.flush(BufferUsage.STATIC_DRAW);
		vao.addVertexBuffer("default", vbo);
		
		//buffer index buffers to the gpu
		solidIbo.flush(BufferUsage.STATIC_DRAW);
		edgeIbo.flush(BufferUsage.STATIC_DRAW);
		
		if(defaultMode.equals(SOLID_MODE) || defaultMode.equals(EDGE_MODE)){
			vao.setIndexBuffer(defaultMode);
		}else{
			vao.setIndexBuffer(SOLID_MODE);
		}
		//specify the attributes for the vertex array
		vao.addAttrib(AttribType.VEC3, false, 0);//position
		vao.addAttrib(AttribType.VEC3, false, 0);//normal
		vao.addAttrib(AttribType.VEC2, false, 0);//uv
		vao.addAttrib(AttribType.VEC3, false, 0);//tangent
		vao.addAttrib(AttribType.VEC3, false, 0);//bitangent
		
		//register the vbo with the vao
		vao.registerVBO("default");

		//tell the vao what vbo to use for each attribute
		vao.setAttribVBO(0, "default");
		vao.setAttribVBO(1, "default");
		vao.setAttribVBO(2, "default");
		vao.setAttribVBO(3, "default");
		vao.setAttribVBO(4, "default");
		
		//enable the attributes for the vertex array
		vao.enableAttribute(0);
		vao.enableAttribute(1);
		vao.enableAttribute(2);
		vao.enableAttribute(3);
		vao.enableAttribute(4);
	}

	/**
	 * Constructs a copy of the given plane
	 * 
	 * @param copy Plane to copy
	 */
	public Plane(Plane copy){
		super(copy);
		width = copy.width;
		length = copy.length;
	}
	
	/**
	 * Gets the length of the plane along the z axis
	 * 
	 * @return Length of the plane along the z axis
	 */
	public float getLength(){
		return transforms.getScalars().z*length;
	}
	
	/**
	 * Gets the width of the plane along the x axis
	 * 
	 * @return Width of the plane along the x axis
	 */
	public float getWidth(){
		return transforms.getScalars().x*width;
	}
}
