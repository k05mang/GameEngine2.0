package primitives;

import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import renderers.Renderable;
import renderers.RenderMode;

public final class Plane extends Renderable{
	float width, length;
	
	/**
	 * Constructs a plane with the given width being the dimension along the x axis, and length
	 * being the dimension along the z axis. The plane is initially centered at the origin.
	 * 
	 * @param width Width of this plane along the x axis
	 * @param length Length of this plane along the z axis
	 * @param modes RenderModes this Plane should be compatible with, the first mode is the initial mode
	 * for the Plane to render with
	 */
	public Plane(float width, float length, RenderMode... modes){
		super();
		this.width = width/2.0f;
		this.length = length/2.0f;
		
		BufferObject vbo = new BufferObject(BufferType.ARRAY);
		vbos.add(vbo);
		
		Vertex topLeft = new Vertex(-this.width,0,-this.length, 0,1,0, 0,1);
		Vertex bottomLeft = new Vertex(-this.width,0,this.length, 0,1,0, 0,0);
		Vertex topRight = new Vertex(this.width,0,-this.length, 0,1,0, 1,1);
		Vertex bottomRight = new Vertex(this.width,0,this.length, 0,1,0, 1,0);
		
		topLeft.addTo(vbo);
		bottomLeft.addTo(vbo);
		topRight.addTo(vbo);
		bottomRight.addTo(vbo);
		
		mesh.add(topLeft);
		mesh.add(bottomLeft);
		mesh.add(topRight);
		mesh.add(bottomRight);
		
		mesh.add(new Face(0,1,2));
		mesh.add(new Face(2,1,3));
		
		vbo.flush(BufferUsage.STATIC_DRAW);
		vao.addVertexBuffer("default", vbo);
		
		IndexBuffer indices = new IndexBuffer(IndexBuffer.IndexType.BYTE);
		ibos.add(indices);
		indices.flush(BufferUsage.STATIC_DRAW);
		
		//check if there are additional modes that need to be accounted for
		if(modes.length > 0){
			for(RenderMode curMode : modes){
				IndexBuffer modeBuffer = new IndexBuffer(IndexBuffer.IndexType.BYTE);
				mesh.insertIndices(modeBuffer, curMode);//add indices to match the mode
				modeBuffer.flush(BufferUsage.STATIC_DRAW);
				vao.addIndexBuffer(curMode, modeBuffer);
				ibos.add(modeBuffer);
			}
			vao.setIndexBuffer(modes[0]);
		}
		//specify the attributes for the vertex array
		vao.addAttrib(0, AttribType.VEC3, false, 0, 0);//position
		vao.addAttrib(1, AttribType.VEC3, false, 0, 0);//normal
		vao.addAttrib(2, AttribType.VEC2, false, 0, 0);//uv
		
		//tell the vao the vertex buffer to use
		vao.setVertexBuffer("default", 0);
		
		//enable the attributes for the vertex array
		vao.enableAttribute(0);
		vao.enableAttribute(1);
		vao.enableAttribute(2);
	}
	
	/**
	 * Constructs a plane with the given length defining the dimensions of the plane, this
	 * effectively makes the plane square.
	 * 
	 * @param sideLength Length to set for the width and height of the plane
	 * @param modes RenderModes this Plane should be compatible with, the first mode is the initial mode
	 * for the Cone to render with
	 */
	public Plane(float sideLength, RenderMode... modes){
		this(sideLength, sideLength, modes);
	}

	/**
	 * Constructs a copy of the given plane
	 * 
	 * Refer to {@link renderer.Renderable#Renderable(Renderable) Renderable's copy constructor} 
	 * for more information about cautions with the copy constructor
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
		return length;
	}
	
	/**
	 * Gets the width of the plane along the x axis
	 * 
	 * @return Width of the plane along the x axis
	 */
	public float getWidth(){
		return width;
	}

	@Override
	public void addMode(RenderMode mode) {
		// TODO Auto-generated method stub
		
	}
}
