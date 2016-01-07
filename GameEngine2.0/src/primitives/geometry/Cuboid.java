package primitives.geometry;

import primitives.Face;
import primitives.Vertex;
import glMath.vectors.Vec3;
import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import renderers.RenderMode;
import renderers.Renderable;

public final class Cuboid extends Renderable {
	private Vec3 halfDimensions;
	
	/**
	 * Constructs a cuboid with the given width, height, and depth while also being compatible with the given RenderModes
	 * 
	 * @param width X dimension of the cuboid
	 * @param height Y dimension of the cuboid
	 * @param depth Z dimension of the cuboid
	 * @param modes RenderModes this Cuboid should be compatible with, the first mode is the initial mode
	 * for the Cuboid to render with
	 */
	public Cuboid(float width, float height, float depth, RenderMode... modes){
		super();
		
		halfDimensions = new Vec3(width/2.0f,height/2.0f,depth/2.0f);
		
		BufferObject vbo = new BufferObject(BufferType.ARRAY);
		vbos.add(vbo);
		
		//-----zpos face------
		mesh.add(new Vertex(-halfDimensions.x, halfDimensions.y, halfDimensions.z, 
				0, 0, 1,
				0, 1));
		mesh.add(new Vertex(-halfDimensions.x, -halfDimensions.y, halfDimensions.z, 
				0, 0, 1, 
				0, 0));
		mesh.add(new Vertex(halfDimensions.x, halfDimensions.y, halfDimensions.z, 
				0, 0, 1, 
				1, 1));
		mesh.add(new Vertex(halfDimensions.x, -halfDimensions.y, halfDimensions.z, 
				0, 0, 1, 
				1, 0));
		//-----zneg face------
		mesh.add(new Vertex(halfDimensions.x, halfDimensions.y, -halfDimensions.z, 
				0, 0, -1, 
				0, 1));
		mesh.add(new Vertex(halfDimensions.x, -halfDimensions.y, -halfDimensions.z, 
				0, 0, -1,
				0, 0));
		mesh.add(new Vertex(-halfDimensions.x, halfDimensions.y, -halfDimensions.z, 
				0, 0, -1, 
				1, 1));
		mesh.add(new Vertex(-halfDimensions.x, -halfDimensions.y, -halfDimensions.z, 
				0, 0, -1, 
				1, 0));
		
		//-----xpos face------
		mesh.add(new Vertex(halfDimensions.x, halfDimensions.y, halfDimensions.z, 
				1, 0, 0, 
				0, 1));
		mesh.add(new Vertex(halfDimensions.x, -halfDimensions.y, halfDimensions.z, 
				1, 0, 0, 
				0, 0));
		mesh.add(new Vertex(halfDimensions.x, halfDimensions.y, -halfDimensions.z, 
				1, 0, 0, 
				1, 1));
		mesh.add(new Vertex(halfDimensions.x, -halfDimensions.y, -halfDimensions.z, 
				1, 0, 0, 
				1, 0));
		//-----xneg face------
		mesh.add(new Vertex(-halfDimensions.x, halfDimensions.y, -halfDimensions.z, 
				-1, 0, 0, 
				0, 1));
		mesh.add(new Vertex(-halfDimensions.x, -halfDimensions.y, -halfDimensions.z, 
				-1, 0, 0, 
				0, 0));
		mesh.add(new Vertex(-halfDimensions.x, halfDimensions.y, halfDimensions.z, 
				-1, 0, 0, 
				1, 1));
		mesh.add(new Vertex(-halfDimensions.x, -halfDimensions.y, halfDimensions.z, 
				-1, 0, 0, 
				1, 0));
		
		//-----ypos face------
		mesh.add(new Vertex(-halfDimensions.x, halfDimensions.y, -halfDimensions.z, 
				0, 1, 0, 
				0, 1));
		mesh.add(new Vertex(-halfDimensions.x, halfDimensions.y, halfDimensions.z, 
				0, 1, 0, 
				0, 0));
		mesh.add(new Vertex(halfDimensions.x, halfDimensions.y, -halfDimensions.z, 
				0, 1, 0, 
				1, 1));
		mesh.add(new Vertex(halfDimensions.x, halfDimensions.y, halfDimensions.z, 
				0, 1, 0, 
				1, 0));
		//-----yneg face------
		mesh.add(new Vertex(-halfDimensions.x, -halfDimensions.y, halfDimensions.z, 
				0, -1, 0, 
				0, 1));
		mesh.add(new Vertex(-halfDimensions.x, -halfDimensions.y, -halfDimensions.z, 
				0, -1, 0, 
				0, 0));
		mesh.add(new Vertex(halfDimensions.x, -halfDimensions.y, halfDimensions.z, 
				0, -1, 0, 
				1, 1));
		mesh.add(new Vertex(halfDimensions.x, -halfDimensions.y, -halfDimensions.z, 
				0, -1, 0, 
				1, 0));
		
		//generate the face indices
		for (int face = 0; face < 6; face++) {
			mesh.add(new Face(
					0 + 4 * face,
					1 + 4 * face,
					3 + 4 * face
					));
			mesh.add(new Face(
					0 + 4 * face,
					3 + 4 * face,
					2 + 4 * face
					));
		}

		mesh.insertVertices(vbo);
		
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
				vao.addIndexBuffer(curMode.toString(), curMode, modeBuffer);
				ibos.add(modeBuffer);
			}
			vao.setIndexBuffer(modes[0].toString());
		}

		//specify the attributes for the vertex array
		vao.addAttrib(0, AttribType.VEC3, false, 0);//position
		vao.addAttrib(1, AttribType.VEC3, false, 0);//normal
		vao.addAttrib(2, AttribType.VEC2, false, 0);//uv
		
		//register the vbo with the vao
		vao.registerVBO("default");

		//tell the vao what vbo to use for each attribute
		vao.setAttribVBO(0, "default");
		vao.setAttribVBO(1, "default");
		vao.setAttribVBO(2, "default");
		
		//enable the attributes for the vertex array
		vao.enableAttribute(0);
		vao.enableAttribute(1);
		vao.enableAttribute(2);
	}
	
	/**
	 * Constructs a cuboid with the dimensions to use given as a vector. The components of the vector are assigned as such
	 * <ul>
	 * <li>X -> width</li>
	 * <li>Y -> height</li>
	 * <li>Z -> depth</li>
	 * </ul>
	 * 
	 * @param dimensions Vector containing the dimensions of the cuboid
	 * @param modes RenderModes this Cuboid should be compatible with, the first mode is the initial mode
	 * for the Cone to render with
	 */
	public Cuboid(Vec3 dimensions, RenderMode... modes){
		this(dimensions.x, dimensions.y, dimensions.z, modes);
	}
	
	/**
	 * Constructs a cuboid with the given scale defining the different dimensions for the cuboid. The resulting cuboid will be a cube
	 * with width = height = depth = scale.
	 * 
	 * @param scale Dimension for the width, height, and depth of the cuboid
	 * @param modes RenderModes this Cuboid should be compatible with, the first mode is the initial mode
	 * for the Cone to render with
	 */
	public Cuboid(float scale, RenderMode... modes){
		this(scale, scale, scale, modes);
	}
	
	/**
	 * Constructs a copy of the given cuboid
	 * 
	 * Refer to {@link renderer.Renderable#Renderable(Renderable) Renderable's copy constructor} 
	 * for more information about cautions with the copy constructor
	 * 
	 * @param copy Cuboid to copy
	 */
	public Cuboid(Cuboid copy){
		super(copy);
		this.halfDimensions = copy.halfDimensions;
	}
	
	/**
	 * Gets the width of the cuboid
	 * 
	 * @return Width of the cuboid
	 */
	public float getWidth(){
		return halfDimensions.x*2;
	}

	/**
	 * Gets the height of the cuboid
	 * 
	 * @return Height of the cuboid
	 */
	public float getHeight(){
		return halfDimensions.y*2;
	}

	/**
	 * Gets the depth of the cuboid
	 * 
	 * @return Depth of the cuboid
	 */
	public float getDepth(){
		return halfDimensions.z*2;
	}
}
