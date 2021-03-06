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
import renderers.RenderMode;

public final class Disc extends Mesh {

	private float radius;
	
	/**
	 * Constructs a disc with the given {@code radius} and number of {@code segments}. The default mode the resulting
	 * mesh will render with will be SOLID_MODE.
	 * 
	 * Segments must be a value greater than 2. If the given value is less than 3 then 3 will be used as default.
	 * 
	 * @param radius Radius of the disc
	 * @param segments Fineness of the disc
	 */
	public Disc(float radius, int segments){
		this(radius, segments, SOLID_MODE);
	}
	
	/**
	 * Constructs a disc with the given {@code radius} and number of {@code segments}. {@code defaultMode} will
	 * specify the mode the mesh will initially render with. Selectable modes and what they entail are as follows:
	 * <ul>
	 * <li>SOLID_MODE: The mesh will render as a GL_TRIANGLES</li>
	 * <li>EDGE_MODE: The mesh will render as GL_LINES, where only the major edges of the mesh are rendered. This will
	 * only render edges of the mesh that define its shape.</li>
	 * </ul>
	 * 
	 * Segments must be a value greater than 2. If the given value is less than 3 then 3 will be used as default.
	 * 
	 * @param radius Radius of the disc
	 * @param segments Fineness of the disc
	 * @param defaultMode Defines the mode to render the mesh with
	 */
	public Disc(float radius, int segments, String defaultMode){
		this.radius = Math.abs(radius);
		
		int maxSegment = Math.max(3, segments);
		
		//specify the attributes for the vertex array
		vao.addAttrib(AttribType.VEC3, false, 0);//position
		vao.addAttrib(AttribType.VEC3, false, 0);//normal
		vao.addAttrib(AttribType.VEC2, false, 0);//uv
		vao.addAttrib(AttribType.VEC3, false, 0);//tangent
		vao.addAttrib(AttribType.VEC3, false, 0);//bitangent
		
		//get the datatype for the index buffers
		IndexBuffer.IndexType dataType = getIndexType(maxSegment-1);

		//create index buffers
		vao.genIBO(SOLID_MODE, RenderMode.TRIANGLES, dataType);
		vao.genIBO(EDGE_MODE, RenderMode.LINE_LOOP, dataType);
		
		//set some pointers to the index buffers
		IndexBuffer solidIbo = vao.getIBO(SOLID_MODE);
		IndexBuffer edgeIbo = vao.getIBO(EDGE_MODE);
		
		//create the vertex buffer
		vao.genVBO(DEFAULT_VBO);
		BufferObject vbo = vao.getVBO(DEFAULT_VBO);
		
		for(int segment = 0; segment < maxSegment; segment++){
			double theta = 2*PI*(segment/(double)maxSegment);
			
			float u = (float)(cos(theta));
			float v = (float)(sin(theta));
			
			float x = this.radius*u;
			float z = this.radius*v;

			Vertex curVert = new Vertex(x, 0, z,  0,1,0, u/2+.5f, -v/2+.5f, 1,0,0, 0,0,1);

			geometry.add(curVert);
			
			curVert.addTo(vbo);
			edgeIbo.add(segment);
			int nextSeg = (segment+1)%maxSegment;//due to constantly computing this value cache it for reuse
			
			//only compute the indices if we are 3 or more segments from the end
			//since we are generating indices two segments ahead of the current one
			//this will prevent redundant face generation at the end
			if(segment < maxSegment-2){
				Triangle triangle = new Triangle(
						0,//base vert
						(segment+2)%maxSegment,//vert that is the second next segment
						nextSeg//vert that is the next segment
						);
				geometry.add(triangle);
				triangle.insertPrim(solidIbo);
			}
		}

		vbo.flush(BufferUsage.STATIC_DRAW);
		
		solidIbo.flush(BufferUsage.STATIC_DRAW);
		edgeIbo.flush(BufferUsage.STATIC_DRAW);
		
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
	 * Constructs a copy of the given disc
	 * 
	 * @param copy Disc to copy
	 */
	public Disc(Disc copy){
		super(copy);
	}
	
	/**
	 * Gets the radius of the disc
	 * 
	 * @return Radius of the disc
	 */
	public float getRadius(){
		return radius;
	}
}
