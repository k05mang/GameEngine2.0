package primitives;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import renderers.RenderMode;
import renderers.Renderable;
import glMath.*;
import gldata.AttribType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import gldata.VertexArray;

public class Cylinder extends Renderable{
	private float length, radius;
	private int segments;
	
	/**
	 * Constructs a cylinder with the given radius, length, and subdivisions while making it compatible with the
	 * given RenderModes
	 * 
	 * Radius must be a value greater than 0, if radius is less than or equal to 0 then a default value 
	 * of .01 will be used instead.
	 * 
	 * Segments must be greater than 2, if the value is less than 3 a default value of 3 will be used instead.
	 * 
	 * @param radius Radius of the cylinder
	 * @param length Length of the cylinder from one end to the other
	 * @param segments Number of segments to define this cylinder
	 * @param modes RenderModes this Cone should be compatible with, the first mode is the initial mode
	 * for the Cone to render with
	 */
	public Cylinder(float radius, float length, int segments, RenderMode... modes){
		super();
		
		this.segments = segments < 3 ? 3 : segments;
		this.length = length;
		this.radius = radius <= 0 ? .01f : radius;
		
		int lastIndex = this.segments*4-1;
		IndexBuffer.IndexType dataType = null;
		
		//determine what data type the index buffer should be
		if(lastIndex < Byte.MAX_VALUE){
			dataType = IndexBuffer.IndexType.BYTE;
		}else if(lastIndex < Short.MAX_VALUE){
			dataType = IndexBuffer.IndexType.SHORT;
		}else if(lastIndex < Integer.MAX_VALUE){
			dataType = IndexBuffer.IndexType.INT;
		}else{
			//TODO handle when the number of vertices and indices would exceed the max value
		}
		//instantiate the vertex array
		vao = new VertexArray(modes[0], dataType);
		
		for(int segment = 0; segment < this.segments; segment++){
			double theta = 2*PI*(segment/(double)this.segments);
			
			float x = this.radius*(float)(cos(theta));
			float y = this.length/2.0f;
			float z = this.radius*(float)(sin(theta));

			Vertex capTop = new Vertex(x, y, z,  0,1,0, 0,0);
			Vertex sideTop = new Vertex(x, y, z,  x, 0, z, 0,0);
			
			Vertex sideBottom = new Vertex(x, -y, z,  x, 0, z, 0,0);
			Vertex capBottom = new Vertex(x, -y, z,  0,-1,0, 0,0);

			mesh.add(capTop);
			mesh.add(sideTop);
			mesh.add(sideBottom);
			mesh.add(capBottom);
			
			capTop.addTo(vao);
			sideTop.addTo(vao);
			sideBottom.addTo(vao);
			capBottom.addTo(vao);
			
			int nextSeg = (segment+1)%this.segments;//due to constantly computing this value cache it for reuse
			//left side face
			mesh.add(new Face(
					segment*4+1,//current segment top left
					nextSeg*4+2,//next segment bottom right
					segment*4+2//current segment bottom left
					));
			
			//right side face
			mesh.add(new Face(
					segment*4+1,//current segment top left
					nextSeg*4+1,//next segment top right
					nextSeg*4+2//next segment bottom right
					));
			
			//only compute the cap indices if we are 3 or more segments form the end
			//since the caps are generated with indices two segments ahead of the current one
			//this will prevent redundant face generation at the end
			if(segment < this.segments-2){
				//top cap face
				mesh.add(new Face(
						0,//base top cap vert which is 0
						((segment+2)%this.segments)*4,//vert that is the second next segment
						(nextSeg)*4//vert that is the next segment
						));
				
				//bottom cap face
				mesh.add(new Face(
						3,//base bottom cap vert which is 3
						(nextSeg)*4+3,//vert that is the next segment
						((segment+2)%this.segments)*4+3//vert that is the second next segment
						));
			}
		}
		
		mesh.insertIndices(vao, modes[0]);//insert indices for the initial RenderMode
		
		//check if there are additional modes that need to be accounted for
		if(modes.length > 0){
			for(RenderMode curMode : modes){
				//check if the primary RenderMode was already processed, this way it isn't redundantly processed
				if(curMode != modes[0]){
					IndexBuffer modeBuffer = new IndexBuffer(dataType);
					mesh.insertIndices(modeBuffer, curMode);//add indices to match the mode
					modeBuffer.flush(BufferUsage.STATIC_DRAW);
					vao.addIndexBuffer(curMode, modeBuffer);
				}
			}
		}
		//specify the attributes for the vertex array
		vao.addAttrib(0, AttribType.VEC3, false, 0);//position
		vao.addAttrib(1, AttribType.VEC3, false, 0);//normal
		vao.addAttrib(2, AttribType.VEC2, false, 0);//uv
		
		//finalize the buffers in the vao
		vao.finalize(BufferUsage.STATIC_DRAW, BufferUsage.STATIC_DRAW);
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
		segments = copy.segments;
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

	@Override
	public void addMode(RenderMode mode) {
		// TODO Auto-generated method stub
		
	}
}
