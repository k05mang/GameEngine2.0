package mesh.primitives.geometry;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import mesh.Mesh;
import mesh.primitives.Triangle;
import mesh.primitives.Vertex;
import renderers.RenderMode;
import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;

public final class Cylinder extends Mesh{
	private float length, radius;
	
	/**
	 * Constructs a cylinder with the given {@code radius}, {@code length}, and {@code segments}. The default mode 
	 * the resulting mesh will render with will be SOLID_MODE.
	 * 
	 * Segments must be greater than 2, if the value is less than 3 a default value of 3 will be used instead.
	 * 
	 * @param radius Radius of the cylinder
	 * @param length Length of the cylinder from one end to the other
	 * @param segments Number of segments to define this cylinder
	 */
	public Cylinder(float radius, float length, int segments){
		this(radius, length, segments, SOLID_MODE);
	}
	
	/**
	 * Constructs a cylinder with the given {@code radius}, {@code length}, and {@code segments}. {@code defaultMode} will
	 * specify the mode the mesh will initially render with. Selectable modes and what they entail are as follows:
	 * <ul>
	 * <li>SOLID_MODE: The mesh will render as a GL_TRIANGLES</li>
	 * <li>EDGE_MODE: The mesh will render as GL_LINES, where only the major edges of the mesh are rendered. This will
	 * only render edges of the mesh that define its shape.</li>
	 * </ul>
	 * 
	 * Segments must be greater than 2, if the value is less than 3 a default value of 3 will be used instead.
	 * 
	 * @param radius Radius of the cylinder
	 * @param length Length of the cylinder from one end to the other
	 * @param segments Number of segments to define this cylinder
	 * @param defaultMode Defines the mode to render the mesh with
	 */
	public Cylinder(float radius, float length, int segments, String defaultMode){
		super();
		
		int maxSegment = Math.max(3, segments);
		this.length = Math.abs(length);
		this.radius = Math.abs(radius);

		IndexBuffer.IndexType dataType = getIndexType(4*maxSegment+2);
		//create index buffers
		IndexBuffer solidIbo = new IndexBuffer(dataType);
		IndexBuffer edgeIbo = new IndexBuffer(dataType);
		//add index buffers to mesh list
		ibos.add(solidIbo);
		ibos.add(edgeIbo);
		//add index buffers to vertex array
		vao.addIndexBuffer(SOLID_MODE, RenderMode.TRIANGLES, solidIbo);
		vao.addIndexBuffer(EDGE_MODE, RenderMode.LINES, edgeIbo);
		
		BufferObject vbo = new BufferObject(BufferType.ARRAY);
		vbos.add(vbo);
		float halfLength = this.length/2.0f;
		
		for(int segment = 0; segment < maxSegment+1; segment++){
			float u = (segment/(float)maxSegment);
			double theta = 2*PI*u;
			
			float x = this.radius*(float)cos(theta);
			float z = this.radius*(float)sin(theta);

			Vertex sideTop = new Vertex(x, halfLength, z,  x, 0, z, 1-u, 1);
			Vertex sideBottom = new Vertex(x, -halfLength, z,  x, 0, z, 1-u,0);

			geometry.add(sideTop);
			geometry.add(sideBottom);
			
			if(segment < maxSegment){
				Triangle left = new Triangle(
						segment*4,//current segment top left
						(segment+1)*4+1,//next segment bottom right
						segment*4+1//current segment bottom left
						);
				//left side face
				geometry.add(left);
				
				Triangle right = new Triangle(
						segment*4,//current segment top left
						(segment+1)*4,//next segment top right
						(segment+1)*4+1//next segment bottom right
						);
				//right side face
				geometry.add(right);

				left.insertPrim(solidIbo);
				right.insertPrim(solidIbo);

				//left edge
				edgeIbo.add(segment*4);
				edgeIbo.add(segment*4+1);
				//bottom edge
				edgeIbo.add(segment*4+1);
				edgeIbo.add((segment+1)*4+1);
				//right edge
				edgeIbo.add((segment+1)*4+1);
				edgeIbo.add((segment+1)*4);
				//top edge
				edgeIbo.add(segment*4);
				edgeIbo.add((segment+1)*4);
			}
			
			//only compute the cap indices if we are 3 or more segments from the end
			//since the caps are generated with indices two segments ahead of the current one
			//this will prevent redundant face generation at the end
			if(segment < maxSegment){
				Vertex capTop = new Vertex(x, halfLength, z,  0,1,0, x/2+.5f, -z/2+.5f);
				Vertex capBottom = new Vertex(x, -halfLength, z,  0,-1,0, x/2+.5f, -z/2+.5f);
				geometry.add(capTop);
				geometry.add(capBottom);
				if(segment < maxSegment-2){
					Triangle top = new Triangle(
							2,//base top cap vert which is 2
							(segment+2)*4+2,//vert that is the second next segment
							(segment+1)*4+2//vert that is the next segment
							);
					//top cap face
					geometry.add(top);

					Triangle bottom = new Triangle(
							3,//base bottom cap vert which is 3
							(segment+1)*4+3,//vert that is the next segment
							(segment+2)*4+3//vert that is the second next segment
							);
					//bottom cap face
					geometry.add(bottom);

					top.insertPrim(solidIbo);
					bottom.insertPrim(solidIbo);
				}
			}
		}
		//buffer index buffers to the gpu
		solidIbo.flush(BufferUsage.STATIC_DRAW);
		edgeIbo.flush(BufferUsage.STATIC_DRAW);

		geometry.genTangentBitangent();
		geometry.insertVertices(vbo);
		vbo.flush(BufferUsage.STATIC_DRAW);
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
	 * Constructs a copy of the given cylinder
	 * 
	 * @param copy Cylinder to copy
	 */
	public Cylinder(Cylinder copy){
		super(copy);
		radius = copy.radius;
		length = copy.length;
	}
	
	/**
	 * Gets the radius of this cylinder
	 * 
	 * @return Radius of this cylinder
	 */
	public float getRadius(){
		return Math.max(transforms.getScalars().x, transforms.getScalars().z)*radius;
	}
	
	/**
	 * Gets the length of this cylinder
	 * 
	 * @return Length of this cylinder
	 */
	public float getLength(){
		return transforms.getScalars().y*length;
	}
}
