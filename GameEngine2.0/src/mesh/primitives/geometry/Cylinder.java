package mesh.primitives.geometry;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import mesh.Renderable;
import mesh.primitives.Face;
import mesh.primitives.Vertex;
import renderers.RenderMode;
import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;

public final class Cylinder extends Renderable{
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
		this.length = length;
		this.radius = radius;
		
		int lastIndex = maxSegment*4-1;
		IndexBuffer.IndexType dataType = getIndexType(lastIndex);
		
		BufferObject vbo = new BufferObject(BufferType.ARRAY);
		vbos.add(vbo);
		
		for(int segment = 0; segment < maxSegment+1; segment++){
			float u = (segment/(float)maxSegment);
			double theta = 2*PI*u;
			
			float capu = (float)cos(theta);
			float capv = (float)sin(theta);
			
			float x = this.radius*capu;
			float y = this.length/2.0f;
			float z = this.radius*capv;

			Vertex capTop = new Vertex(x, y, z,  0,1,0, capu/2+.5f, -capv/2+.5f);
			Vertex sideTop = new Vertex(x, y, z,  x, 0, z, 1-u, 1);
			
			Vertex sideBottom = new Vertex(x, -y, z,  x, 0, z, 1-u,0);
			Vertex capBottom = new Vertex(x, -y, z,  0,-1,0, capu/2+.5f, -capv/2+.5f);

			geometry.add(capTop);
			geometry.add(sideTop);
			geometry.add(sideBottom);
			geometry.add(capBottom);
			
			capTop.addTo(vbo);
			sideTop.addTo(vbo);
			sideBottom.addTo(vbo);
			capBottom.addTo(vbo);
			
			int nextSeg = (segment+1)%maxSegment;//due to constantly computing this value cache it for reuse
			if(segment < maxSegment){
				//left side face
				geometry.add(new Face(
						segment*4+1,//current segment top left
						(segment+1)*4+2,//next segment bottom right
						segment*4+2//current segment bottom left
						));
				
				//right side face
				geometry.add(new Face(
						segment*4+1,//current segment top left
						(segment+1)*4+1,//next segment top right
						(segment+1)*4+2//next segment bottom right
						));
			}
			
			//only compute the cap indices if we are 3 or more segments from the end
			//since the caps are generated with indices two segments ahead of the current one
			//this will prevent redundant face generation at the end
			if(segment < maxSegment-2){
				//top cap face
				geometry.add(new Face(
						0,//base top cap vert which is 0
						((segment+2)%maxSegment)*4,//vert that is the second next segment
						nextSeg*4//vert that is the next segment
						));
				
				//bottom cap face
				geometry.add(new Face(
						3,//base bottom cap vert which is 3
						nextSeg*4+3,//vert that is the next segment
						((segment+2)%maxSegment)*4+3//vert that is the second next segment
						));
			}
		}
		vbo.flush(BufferUsage.STATIC_DRAW);
		vao.addVertexBuffer("default", vbo);
		
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
	 * Constructs a copy of the given cylinder
	 * 
	 * Refer to {@link renderer.Renderable#Renderable(Renderable) Renderable's copy constructor} 
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
