package mesh.primitives.geometry;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import mesh.Mesh;
import mesh.primitives.Face;
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
	 * Constructs a cylinder with the given radius, length, and subdivisions while making it compatible with the
	 * given RenderModes
	 * 
	 * Segments must be greater than 2, if the value is less than 3 a default value of 3 will be used instead.
	 * 
	 * @param radius Radius of the cylinder
	 * @param length Length of the cylinder from one end to the other
	 * @param segments Number of segments to define this cylinder
	 * @param modes RenderModes this Cylinder should be compatible with, the first mode is the initial mode
	 * for the Cylinder to render with
	 */
	public Cylinder(float radius, float length, int segments, RenderMode... modes){
		super();
		
		int maxSegment = Math.max(3, segments);
		this.length = Math.abs(length);
		this.radius = Math.abs(radius);
		
		BufferObject vbo = new BufferObject(BufferType.ARRAY);
		vbos.add(vbo);
		
		for(int segment = 0; segment < maxSegment+1; segment++){
			float u = (segment/(float)maxSegment);
			double theta = 2*PI*u;
			
			float x = (float)cos(theta);
			float z = (float)sin(theta);

			Vertex sideTop = new Vertex(x, 1, z,  x, 0, z, 1-u, 1);
			Vertex sideBottom = new Vertex(x, -1, z,  x, 0, z, 1-u,0);

			geometry.add(sideTop);
			geometry.add(sideBottom);
			
			if(segment < maxSegment){
				//left side face
				geometry.add(new Face(
						segment*4,//current segment top left
						(segment+1)*4+1,//next segment bottom right
						segment*4+1//current segment bottom left
						));
				
				//right side face
				geometry.add(new Face(
						segment*4,//current segment top left
						(segment+1)*4,//next segment top right
						(segment+1)*4+1//next segment bottom right
						));
			}
			
			//only compute the cap indices if we are 3 or more segments from the end
			//since the caps are generated with indices two segments ahead of the current one
			//this will prevent redundant face generation at the end
			if(segment < maxSegment){
				Vertex capTop = new Vertex(x, 1, z,  0,1,0, x/2+.5f, -z/2+.5f);
				Vertex capBottom = new Vertex(x, -1, z,  0,-1,0, x/2+.5f, -z/2+.5f);
				geometry.add(capTop);
				geometry.add(capBottom);
				if(segment < maxSegment-2){
					//top cap face
					geometry.add(new Face(
							2,//base top cap vert which is 2
							(segment+2)*4+2,//vert that is the second next segment
							(segment+1)*4+2//vert that is the next segment
							));
					
					//bottom cap face
					geometry.add(new Face(
							3,//base bottom cap vert which is 3
							(segment+1)*4+3,//vert that is the next segment
							(segment+2)*4+3//vert that is the second next segment
							));
				}
			}
		}

		geometry.genTangentBitangent();
		geometry.insertVertices(vbo);
		transforms.scale(this.radius, this.length/2, this.radius);
		vbo.flush(BufferUsage.STATIC_DRAW);
		vao.addVertexBuffer("default", vbo);

		IndexBuffer.IndexType dataType = getIndexType(geometry.getNumVertices());
		//check if there are additional modes that need to be accounted for
		if(modes.length > 0){
			for(RenderMode curMode : modes){
				IndexBuffer modeBuffer = new IndexBuffer(dataType);
				geometry.insertIndices(modeBuffer, curMode);//add indices to match the mode
				modeBuffer.flush(BufferUsage.STATIC_DRAW);
				vao.addIndexBuffer(curMode.toString(), curMode, modeBuffer);
				ibos.add(modeBuffer);
			}
			vao.setIndexBuffer(modes[0].toString());
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
	 * Refer to {@link renderer.Mesh#Renderable(Mesh) Renderable's copy constructor} 
	 * for more information about cautions with the copy constructor
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
		return radius;
	}
	
	/**
	 * Gets the length of this cylinder
	 * 
	 * @return Length of this cylinder
	 */
	public float getLength(){
		return length;
	}
}
