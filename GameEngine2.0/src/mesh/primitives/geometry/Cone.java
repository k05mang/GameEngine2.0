package mesh.primitives.geometry;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import mesh.Mesh;
import mesh.primitives.Triangle;
import mesh.primitives.Vertex;
import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import gldata.VertexArray;
import renderers.RenderMode;

public final class Cone extends Mesh {

	private float length, radius;
	
	/**
	 * Constructs a cone primitive with the given {@code radius}, {@code length}, and {@code slices}. {@code centered} 
	 * determines whether the cones origin will be at the center of the cone or the tip. The default mode the resulting
	 * mesh will render with will be SOLID_MODE.
	 * 
	 * Slices must be greater than 2, if the value is less than 3 a default value of 3 will be used instead.
	 * 
	 * @param radius Radius of the base of the cone
	 * @param length Length of the cone from the base to the tip
	 * @param slices Number of subdivisions to make for the cone
	 * @param centered Whether the cone should start with its center at the middle of the cone or at the tip of the cone
	 */
	public Cone(float radius, float length, int slices, boolean centered){
		this(radius, length, slices, centered, SOLID_MODE);
	}
	
	/**
	 * Constructs a cone primitive with the given {@code radius}, {@code length}, and {@code slices}. {@code centered} 
	 * determines whether the cones origin will be at the center of the cone or the tip. {@code defaultMode} will
	 * specify the mode the cone will initially render with. Selectable modes and what they entail are as follows:
	 * <ul>
	 * <li>SOLID_MODE: The mesh will render as a GL_TRIANGLES</li>
	 * <li>EDGE_MODE: The mesh will render as GL_LINES, where only the major edges of the mesh are rendered. This will
	 * only render edges of the mesh that define its shape.</li>
	 * </ul>
	 * 
	 * Slices must be greater than 2, if the value is less than 3 a default value of 3 will be used instead.
	 * 
	 * @param radius Radius of the base of the cone
	 * @param length Length of the cone from the base to the tip
	 * @param slices Number of subdivisions to make for the cone
	 * @param centered Whether the cone should start with its center at the middle of the cone or at the tip of the cone
	 * @param defaultMode Defines the mode to render the mesh with
	 */
	public Cone(float radius, float length, int slices, boolean centered, String defaultMode){
		super();
		
		//check and restrict the values passed to the constructor if they do not meet minimum requirements
		int subdiv = Math.max(3, slices);
		this.radius = Math.abs(radius);
		this.length = Math.abs(length);

		//specify the attributes for the vertex array
		vao.addAttrib(AttribType.VEC3, false, 0);//position
		vao.addAttrib(AttribType.VEC3, false, 0);//normal
		vao.addAttrib(AttribType.VEC2, false, 0);//uv
		
		//get the datatype for the index buffers
		IndexBuffer.IndexType dataType = getIndexType(subdiv);

		//create the index buffers
		vao.genIBO(SOLID_MODE, RenderMode.TRIANGLES, dataType);
		vao.genIBO(EDGE_MODE, RenderMode.LINE_STRIP, dataType);
		
		//set some pointers to the index buffers
		IndexBuffer solidIbo = vao.getIBO(SOLID_MODE);
		IndexBuffer edgeIbo = vao.getIBO(EDGE_MODE);
		
		//create the vertex buffer
		vao.genVBO(DEFAULT_VBO);
		BufferObject vbo = vao.getVBO(DEFAULT_VBO);
		
		Vertex tip = new Vertex(0,0,0, 0,centered ? this.length/2.0f : 0,0, 0,0);
		geometry.add(tip);
		tip.addTo(vbo);
		for(int segment = 1; segment < subdiv+1; segment++){
			double theta = 2*PI*(segment/(double)subdiv);
			
			float x = this.radius*(float)(cos(theta));
			float z = this.radius*(float)(sin(theta));
			float vertOffset = -(centered ? this.length/2.0f : this.length);
			
			Vertex bottomVert = new Vertex(x, vertOffset, z,  x, vertOffset, z, 0,0);
			geometry.add(bottomVert);
			bottomVert.addTo(vbo);
			
			Triangle side = new Triangle(
					0,	
					(segment+1)%(subdiv+1) == 0 ? 1 : (segment+1)%(subdiv+1),
					segment
					);
			geometry.add(side);
			side.insertPrim(edgeIbo);
			side.insertPrim(solidIbo);
			//generate faces for the bottom cap, only when on the third to last vertex
			if(segment < subdiv-1){
				Triangle bottom = new Triangle(
						1,			//the first bottom vertex
						segment+1, 	//the vertex that is segment+1 of the bottom ring
						segment+2   //the vertex that is segment+2 of the bottom ring
						);
				geometry.add(bottom);
				bottom.insertPrim(solidIbo);
			}
		}
		
		//flush the index buffers
		solidIbo.flush(BufferUsage.STATIC_DRAW);
		edgeIbo.flush(BufferUsage.STATIC_DRAW);
		//flush the vertex buffer
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
		
		//enable the attributes for the vertex array
		vao.enableAttribute(0);
		vao.enableAttribute(1);
		vao.enableAttribute(2);
	}
	
	/**
	 * Constructs a copy of the given cone
	 * 
	 * @param copy Cone to copy
	 */
	public Cone(Cone copy){
		super(copy);
		radius = copy.radius;
		length = copy.length;
	}

	/**
	 * Gets the radius of the base of this cone
	 *  
	 * @return Radius of the base of this cone
	 */
	public float getRadius(){
		return radius;
	}
	
	/**
	 * Gets the length of this cone
	 * 
	 * @return Length of this cone
	 */
	public float getLength(){
		return length;
	}
}
