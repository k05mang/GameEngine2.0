package mesh.primitives.geometry;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import mesh.Mesh;
import mesh.primitives.Face;
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
	
	public Cone(float radius, float length, int slices, boolean centered){
		this(radius, length, slices, centered, SOLID_MODE);
	}
	
	/**
	 * Constructs a cone primitive with the given radius, length, and subdivisions while making
	 * it compatible with the given RenderModes.
	 * 
	 * Slices must be greater than 2, if the value is less than 3 a default value of 3 will be used instead.
	 * 
	 * @param radius Radius of the base of the cone
	 * @param length Length of the cone from the base to the tip
	 * @param slices Number of subdivisions to make for the cone
	 * @param centered Whether the cone should start with its center at the middle of the cone or at the tip of the cone
	 */
	public Cone(float radius, float length, int slices, boolean centered, String defaultMode){
		super();
		
		//check and restrict the values passed to the constructor if they do not meet minimum requirements
		int subdiv = Math.max(3, slices);
		this.radius = Math.abs(radius);
		this.length = Math.abs(length);
		
		IndexBuffer.IndexType dataType = getIndexType(subdiv);

		//create the index buffers
		IndexBuffer solidIbo = new IndexBuffer(dataType);
		IndexBuffer edgeIbo = new IndexBuffer(dataType);
		//add them to the mesh list
		ibos.add(solidIbo);
		ibos.add(edgeIbo);
		
		//add the ibos to the vertex array
		vao.addIndexBuffer(SOLID_MODE, RenderMode.TRIANGLES, solidIbo);
		vao.addIndexBuffer(EDGE_MODE, RenderMode.LINE_STRIP, edgeIbo);
		
		//create the vertex buffer
		BufferObject vbo = new BufferObject(BufferType.ARRAY);
		vbos.add(vbo);
		
		Vertex tip = new Vertex(0,0,0, 0,1,0, 0,0);
		geometry.add(tip);
		tip.addTo(vbo);
		for(int segment = 1; segment < subdiv+1; segment++){
			double theta = 2*PI*(segment/(double)subdiv);
			
			float x = (float)(cos(theta));
			float z = (float)(sin(theta));
			
			Vertex bottomVert = new Vertex(x, -1, z,  x, -1, z, 0,0);
			geometry.add(bottomVert);
			bottomVert.addTo(vbo);
			
			Face side = new Face(
					0,	
					(segment+1)%(subdiv+1) == 0 ? 1 : (segment+1)%(subdiv+1),
					segment
					);
			geometry.add(side);
			side.insertPrim(edgeIbo);
			side.insertPrim(solidIbo);
			//generate faces for the bottom cap, only when on the third to last vertex
			if(segment < subdiv-1){
				Face bottom = new Face(
						1,			//the first bottom vertex
						segment+1, 	//the vertex that is segment+1 of the bottom ring
						segment+2   //the vertex that is segment+2 of the bottom ring
						);
				geometry.add(bottom);
				bottom.insertPrim(solidIbo);
			}
		}

		if(centered){
			transforms.translate(0, this.length/2.0f, 0);
		}
		
		transforms.scale(this.radius, this.length, this.radius);
		//flush the index buffers
		solidIbo.flush(BufferUsage.STATIC_DRAW);
		edgeIbo.flush(BufferUsage.STATIC_DRAW);
		//flush the vertex buffer
		vbo.flush(BufferUsage.STATIC_DRAW);
		//add the vertex buffer to the vertex array
		vao.addVertexBuffer("default", vbo);
		if(defaultMode.equals(SOLID_MODE) || defaultMode.equals(EDGE_MODE)){
			vao.setIndexBuffer(defaultMode);
		}else{
			vao.setIndexBuffer(SOLID_MODE);
		}
		
		//specify the attributes for the vertex array
		vao.addAttrib(AttribType.VEC3, false, 0);//position
		vao.addAttrib(AttribType.VEC3, false, 0);//normal
		vao.addAttrib(AttribType.VEC2, false, 0);//uv
		
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
	 * Constructs a copy of the given cone
	 * 
	 * Refer to {@link Mesh.Renderable#Renderable(Mesh) Renderable's copy constructor} 
	 * for more information about cautions with the copy constructor
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
