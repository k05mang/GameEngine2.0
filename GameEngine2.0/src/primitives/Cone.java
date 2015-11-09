package primitives;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import gldata.VertexArray;
import renderers.Renderable;
import renderers.RenderMode;

public final class Cone extends Renderable {

	private float length, radius;
	
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
	 * @param modes RenderModes this Cone should be compatible with, the first mode is the initial mode
	 * for the Cone to render with
	 */
	public Cone(float radius, float length, int slices, boolean centered, RenderMode... modes){
		super();
		
		//check and restrict the values passed to the constructor if they do not meet minimum requirements
		int subdiv = Math.max(3, slices);
		this.radius = radius;
		this.length = length;
		float vertOffset = centered ? this.length/2.0f : this.length;
		
		IndexBuffer.IndexType dataType = null;
		
		//determine what data type the index buffer should be
		if(subdiv < Byte.MAX_VALUE){
			dataType = IndexBuffer.IndexType.BYTE;
		}else if(subdiv < Short.MAX_VALUE){
			dataType = IndexBuffer.IndexType.SHORT;
		}else if(subdiv < Integer.MAX_VALUE){
			dataType = IndexBuffer.IndexType.INT;
		}else{
			//TODO handle when the number of vertices and indices would exceed the max value
		}
		BufferObject vbo = new BufferObject(BufferType.ARRAY);
		vbos.add(vbo);
		
		Vertex tip = new Vertex(0, centered ? this.length/2.0f : 0 ,0, 0,1,0, 0,0);
		mesh.add(tip);
		tip.addTo(vbo);
		for(int segment = 1; segment < subdiv+1; segment++){
			double theta = 2*PI*(segment/(double)subdiv);
			
			float x = this.radius*(float)(cos(theta));
			float z = this.radius*(float)(sin(theta));
			
			Vertex bottomVert = new Vertex(x, -vertOffset, z,  x, -vertOffset, z, 0,0);
			mesh.add(bottomVert);
			bottomVert.addTo(vbo);
			
			Face side = new Face(
					0,	
					(segment+1)%(subdiv+1) == 0 ? 1 : (segment+1)%(subdiv+1),
					segment
					);
			mesh.add(side);
			
			if(segment < subdiv-1){
				Face bottom = new Face(
						1,			//the first bottom vertex
						segment+1, 	//the vertex that is segment+1 of the bottom ring
						segment+2   //the vertex that is segment+2 of the bottom ring
						);
				mesh.add(bottom);
			}
		}
//		float tipY = centered ? this.length/2.0f : 0;
//		for(int segment = 0; segment < subdiv; segment++){
//			double theta = 2*PI*(segment/(double)subdiv);
//			double thetaTop = 2*PI*((segment+.5)/(double)subdiv);
//			
//			float x = this.radius*(float)(cos(theta));
//			float z = this.radius*(float)(sin(theta));
//			
//			float xTop = this.radius*(float)(cos(thetaTop));
//			float zTop = this.radius*(float)(sin(thetaTop));
//			
//			Vertex top = new Vertex(0, tipY, 0,  xTop, this.length, zTop, 0,0);
//			mesh.add(top);
//			top.addTo(vbo);
//			
//			Vertex side = new Vertex(x, -vertOffset, z,  x, this.length, z, 0,0);
//			mesh.add(side);
//			side.addTo(vbo);
//			
//			
//			Vertex bottom = new Vertex(x, -vertOffset, z,  0, -1, 0, 0,0);
//			mesh.add(bottom);
//			bottom.addTo(vbo);
//			
//			//make the side face
//			mesh.add(new Face(
//					segment*3,//tip vert
//					((segment+1)%subdiv)*3+1,//bottom right vert
//					segment*3+1//bottom left vert
//					));
//			
//			//make the bottom face
//			//condition to prevent extra faces from being added due to how the bottom indices are formed
//			if(segment < subdiv-2){
//				mesh.add(new Face(
//						2,//first bottom vert will act as the base vertex for the bottom faces triangles
//						(segment+1)*3+2,//second segment bottom vert
//						(segment+2)*3+2//third segment bottom vert
//						
//						));
//			}
//		}

		vbo.flush(BufferUsage.STATIC_DRAW);
		vao.addVertexBuffer("default", vbo);
		
		IndexBuffer indices = new IndexBuffer(dataType);
		ibos.add(indices);
		indices.flush(BufferUsage.STATIC_DRAW);
		
		//check if there are additional modes that need to be accounted for
		if(modes.length > 0){
			for(RenderMode curMode : modes){
				IndexBuffer modeBuffer = new IndexBuffer(dataType);
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
	 * Constructs a copy of the given cone
	 * 
	 * Refer to {@link renderer.Renderable#Renderable(Renderable) Renderable's copy constructor} 
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

	@Override
	public void addMode(RenderMode mode) {
		// TODO Auto-generated method stub
		
	}
}
