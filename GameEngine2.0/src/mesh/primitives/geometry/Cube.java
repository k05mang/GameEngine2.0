package mesh.primitives.geometry;

import mesh.Mesh;
import mesh.primitives.Triangle;
import mesh.primitives.Vertex;
import glMath.vectors.Vec3;
import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import renderers.RenderMode;

public final class Cube extends Mesh {
	private Vec3 halfDimensions;
	
	/**
	 * Constructs a cube with the given {@code width}, {@code height}, and {@code depth}.
	 * The default mode is SOLID_MODE.
	 * 
	 * @param width X dimension of the cube
	 * @param height Y dimension of the cube
	 * @param depth Z dimension of the cube
	 * @param defaultMode Defines the mode to render the mesh with
	 */
	public Cube(float width, float height, float depth){
		this(width, height, depth, SOLID_MODE);
	}

	/**
	 * Constructs a cube with the dimensions to use given as a vector. The components of the vector are assigned as such
	 * <ul>
	 * <li>X -> width</li>
	 * <li>Y -> height</li>
	 * <li>Z -> depth</li>
	 * </ul>
	 * <br>
	 * The default mode is SOLID_MODE.
	 * 
	 * @param dimensions Vector containing the dimensions of the cube
	 */
	public Cube(Vec3 dimensions){
		this(dimensions, SOLID_MODE);
	}
	
	/**
	 * Constructs a cube with the dimensions to use given as a vector. The components of the vector are assigned as such
	 * <ul>
	 * <li>X -> width</li>
	 * <li>Y -> height</li>
	 * <li>Z -> depth</li>
	 * </ul>
	 * <br>
	 * {@code defaultMode} will specify the mode the mesh will initially render with. Selectable modes and what they entail 
	 * are as follows:
	 * <ul>
	 * <li>SOLID_MODE: The mesh will render as a GL_TRIANGLES</li>
	 * <li>EDGE_MODE: The mesh will render as GL_LINES, where only the major edges of the mesh are rendered. This will
	 * only render edges of the mesh that define its shape.</li>
	 * </ul>
	 * 
	 * @param dimensions Vector containing the dimensions of the cube
	 * @param defaultMode Defines the mode to render the mesh with
	 */
	public Cube(Vec3 dimensions, String defaultMode){
		this(dimensions.x, dimensions.y, dimensions.z, defaultMode);
	}
	
	/**
	 * Constructs a cube with the given {@code width}, {@code height}, and {@code depth}. {@code defaultMode} will specify 
	 * the mode the mesh will initially render with. Selectable modes and what they entail are as follows:
	 * <ul>
	 * <li>SOLID_MODE: The mesh will render as a GL_TRIANGLES</li>
	 * <li>EDGE_MODE: The mesh will render as GL_LINES, where only the major edges of the mesh are rendered. This will
	 * only render edges of the mesh that define its shape.</li>
	 * </ul>
	 * 
	 * @param width X dimension of the cube
	 * @param height Y dimension of the cube
	 * @param depth Z dimension of the cube
	 * @param defaultMode Defines the mode to render the mesh with
	 */
	public Cube(float width, float height, float depth, String defaultMode){
		super();
		
		halfDimensions = new Vec3(Math.abs(width)/2.0f, Math.abs(height)/2.0f, Math.abs(depth)/2.0f);

		//specify the attributes for the vertex array
		vao.addAttrib(AttribType.VEC3, false, 0);//position
		vao.addAttrib(AttribType.VEC3, false, 0);//normal
		vao.addAttrib(AttribType.VEC2, false, 0);//uv
		vao.addAttrib(AttribType.VEC3, false, 0);//tangent
		vao.addAttrib(AttribType.VEC3, false, 0);//bitangent
		
		//create the vertex buffer
		vao.genVBO(DEFAULT_VBO);
		BufferObject vbo = vao.getVBO(DEFAULT_VBO);
		
		//-----xpos face------
		geometry.add(new Vertex(
				halfDimensions.x, halfDimensions.y, halfDimensions.z, 	//position
				1, 0, 0, 	//normal
				0, 1,		//uvs
				0,0,-1,		//tangent
				0,1,0		//bitangent
				));
		geometry.add(new Vertex(
				halfDimensions.x, -halfDimensions.y, halfDimensions.z, 
				1, 0, 0, 
				0, 0,
				0,0,-1,	
				0,1,0	
				));
		geometry.add(new Vertex(
				halfDimensions.x, halfDimensions.y, -halfDimensions.z,
				1, 0, 0, 
				1, 1,
				0,0,-1,	
				0,1,0	
				));
		geometry.add(new Vertex(
				halfDimensions.x, -halfDimensions.y, -halfDimensions.z,
				1, 0, 0, 
				1, 0,
				0,0,-1,	
				0,1,0	
				));
		//-----xneg face------
		geometry.add(new Vertex(
				-halfDimensions.x, halfDimensions.y, -halfDimensions.z,
				-1, 0, 0, 
				0, 1,
				0,0,1,	
				0,1,0	
				));
		geometry.add(new Vertex(
				-halfDimensions.x, -halfDimensions.y, -halfDimensions.z,
				-1, 0, 0, 
				0, 0,
				0,0,1,	
				0,1,0	
				));
		geometry.add(new Vertex(
				-halfDimensions.x, halfDimensions.y, halfDimensions.z,
				-1, 0, 0, 
				1, 1,
				0,0,1,	
				0,1,0	
				));
		geometry.add(new Vertex(
				-halfDimensions.x, -halfDimensions.y, halfDimensions.z,
				-1, 0, 0, 
				1, 0,
				0,0,1,	
				0,1,0	
				));
		
		//-----ypos face------
		geometry.add(new Vertex(
				-halfDimensions.x, halfDimensions.y, -halfDimensions.z,
				0, 1, 0, 
				0, 1,
				1,0,0,	
				0,0,-1	
				));
		geometry.add(new Vertex(
				-halfDimensions.x, halfDimensions.y, halfDimensions.z,
				0, 1, 0, 
				0, 0,
				1,0,0,	
				0,0,-1	
				));
		geometry.add(new Vertex(
				halfDimensions.x, halfDimensions.y, -halfDimensions.z,
				0, 1, 0, 
				1, 1,
				1,0,0,	
				0,0,-1	
				));
		geometry.add(new Vertex(
				halfDimensions.x, halfDimensions.y, halfDimensions.z,
				0, 1, 0, 
				1, 0,
				1,0,0,	
				0,0,-1	
				));
		//-----yneg face------
		geometry.add(new Vertex(
				-halfDimensions.x, -halfDimensions.y, halfDimensions.z,
				0, -1, 0, 
				0, 1,
				1,0,0,	
				0,0,1	
				));
		geometry.add(new Vertex(
				-halfDimensions.x, -halfDimensions.y, -halfDimensions.z,
				0, -1, 0, 
				0, 0,
				1,0,0,	
				0,0,1	
				));
		geometry.add(new Vertex(
				halfDimensions.x, -halfDimensions.y, halfDimensions.z,
				0, -1, 0, 
				1, 1,
				1,0,0,	
				0,0,1	
				));
		geometry.add(new Vertex(
				halfDimensions.x, -halfDimensions.y, -halfDimensions.z,
				0, -1, 0, 
				1, 0,
				1,0,0,	
				0,0,1	
				));
		
		//-----zpos face------
		geometry.add(new Vertex(
				-halfDimensions.x, halfDimensions.y, halfDimensions.z,
				0, 0, 1,
				0, 1,
				1,0,0,	
				0,1,0	
				));
		geometry.add(new Vertex(
				-halfDimensions.x, -halfDimensions.y, halfDimensions.z,
				0, 0, 1, 
				0, 0,
				1,0,0,	
				0,1,0	
				));
		geometry.add(new Vertex(
				halfDimensions.x, halfDimensions.y, halfDimensions.z,
				0, 0, 1, 
				1, 1,
				1,0,0,	
				0,1,0	
				));
		geometry.add(new Vertex(
				halfDimensions.x, -halfDimensions.y, halfDimensions.z,
				0, 0, 1, 
				1, 0,
				1,0,0,	
				0,1,0	
				));
		//-----zneg face------
		geometry.add(new Vertex(
				halfDimensions.x, halfDimensions.y, -halfDimensions.z,
				0, 0, -1, 
				0, 1,
				-1,0,0,	
				0,1,0	
				));
		geometry.add(new Vertex(
				halfDimensions.x, -halfDimensions.y, -halfDimensions.z, 
				0, 0, -1,
				0, 0,
				-1,0,0,	
				0,1,0	
				));
		geometry.add(new Vertex(
				-halfDimensions.x, halfDimensions.y, -halfDimensions.z, 
				0, 0, -1, 
				1, 1,
				-1,0,0,	
				0,1,0	
				));
		geometry.add(new Vertex(
				-halfDimensions.x, -halfDimensions.y, -halfDimensions.z,
				0, 0, -1, 
				1, 0,
				-1,0,0,	
				0,1,0	
				));

		//create index buffers
		vao.genIBO(SOLID_MODE, RenderMode.TRIANGLES, IndexBuffer.IndexType.BYTE);
		vao.genIBO(EDGE_MODE, RenderMode.LINES, IndexBuffer.IndexType.BYTE);
		
		//set some pointers to the index buffers
		IndexBuffer solidIbo = vao.getIBO(SOLID_MODE);
		IndexBuffer edgeIbo = vao.getIBO(EDGE_MODE);
		
		//xpos face of the edges
			//zpos edge
			edgeIbo.add(0);
			edgeIbo.add(1);
			//yneg edge
			edgeIbo.add(1);
			edgeIbo.add(3);
			//zneg
			edgeIbo.add(2);
			edgeIbo.add(3);
			//ypos
			edgeIbo.add(2);
			edgeIbo.add(0);
		//bridge edges
			//zpos
				//ypos
				edgeIbo.add(0);
				edgeIbo.add(6);
				//yneg
				edgeIbo.add(1);
				edgeIbo.add(7);
			//zneg
				//ypos
				edgeIbo.add(2);
				edgeIbo.add(4);
				//yneg
				edgeIbo.add(5);
				edgeIbo.add(3);
		//xneg face
			//zpos edge
			edgeIbo.add(6);
			edgeIbo.add(7);
			//yneg edge
			edgeIbo.add(7);
			edgeIbo.add(5);
			//zneg
			edgeIbo.add(5);
			edgeIbo.add(4);
			//ypos
			edgeIbo.add(4);
			edgeIbo.add(6);
		//generate the face indices
		for (int face = 0; face < 6; face++) {
			geometry.add(new Triangle(
					0 + 4 * face,
					1 + 4 * face,
					3 + 4 * face
					));
			geometry.add(new Triangle(
					0 + 4 * face,
					3 + 4 * face,
					2 + 4 * face
					));
		}

		geometry.insertVertices(vbo);
		geometry.insertIndices(solidIbo, RenderMode.TRIANGLES);
		//buffer index buffers to gpu
		edgeIbo.flush(BufferUsage.STATIC_DRAW);
		solidIbo.flush(BufferUsage.STATIC_DRAW);
		
		vbo.flush(BufferUsage.STATIC_DRAW);
		
		if(defaultMode.equals(SOLID_MODE) || defaultMode.equals(EDGE_MODE)){
			vao.setIndexBuffer(defaultMode);
		}else{
			vao.setIndexBuffer(SOLID_MODE);
		}

		//tell the vao what vbo to use for each attribute
		vao.setAttribVBO(0, DEFAULT_VBO);
		vao.setAttribVBO(1, DEFAULT_VBO);
		vao.setAttribVBO(2, DEFAULT_VBO);
		vao.setAttribVBO(3, DEFAULT_VBO);
		vao.setAttribVBO(4, DEFAULT_VBO);
		
		//enable the attributes for the vertex array
		vao.enableAttribute(0);
		vao.enableAttribute(1);
		vao.enableAttribute(2);
		vao.enableAttribute(3);
		vao.enableAttribute(4);
	}
	
	/**
	 * Constructs a cuboid with the given scale defining the different dimensions for the cuboid. The resulting cuboid will be a cube
	 * with width = height = depth = scale.
	 * 
	 * @param scale Dimension for the width, height, and depth of the cuboid
	 * @param modes RenderModes this Cuboid should be compatible with, the first mode is the initial mode
	 * for the Cone to render with
	 */
	public Cube(float scale, String defaultMode){
		this(scale, scale, scale, defaultMode);
	}
	
	/**
	 * Constructs a cuboid with the given scale defining the different dimensions for the cuboid. The resulting cuboid will be a cube
	 * with width = height = depth = scale.
	 * 
	 * @param scale Dimension for the width, height, and depth of the cuboid
	 */
	public Cube(float scale){
		this(scale, SOLID_MODE);
	}
	
	/**
	 * Constructs a copy of the given cuboid
	 * 
	 * @param copy Cuboid to copy
	 */
	public Cube(Cube copy){
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
