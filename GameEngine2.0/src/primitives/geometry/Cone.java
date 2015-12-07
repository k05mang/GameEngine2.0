package primitives.geometry;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import primitives.Face;
import primitives.Vertex;
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
//		for(int segment = 0; segment < subdiv+1; segment++){
//			
//			float bottomu = segment/(float)subdiv;
//			double theta = 2*PI*bottomu;
//			
//			float capu = (float)cos(theta);
//			float capv =(float)sin(theta);
//			
//			float x = this.radius*capu;
//			float z = this.radius*capv;
//			
//			if(segment < subdiv){
//				float topu = (segment+.5f)/(float)subdiv;
//				float topx = this.radius*(float)cos(2*PI*topu);
//				float topz = this.radius*(float)sin(2*PI*topu);
//				Vertex top = new Vertex(0, vertOffset, 0,  x, this.radius/this.length, z, topu,1);
//				mesh.add(top);
//				top.addTo(vbo);
//			}
//			
//			Vertex bottomVert = new Vertex(x, -vertOffset, z,  x, this.radius/this.length, z, bottomu,0);
//			mesh.add(bottomVert);
//			bottomVert.addTo(vbo);
//			
//			//vertex for the cap of the cone 
//			Vertex cap = new Vertex(x, -vertOffset, z,  0,-1,0, capu/2+.5f, -capv/2+.5f);
//			mesh.add(cap);
//			cap.addTo(vbo);
//			
//			int nextSeg = (segment+1)%(subdiv+1);//due to constantly computing this value cache it for reuse
//			if(segment < subdiv){
//				//side face
//				mesh.add(new Face(
//						segment*3,//current segment top
//						(segment+1)*3+1,//next segment bottom right
//						segment*3+1//current segment bottom left
//						));
//			}
//			
//			//only compute the cap indices if we are 3 or more segments from the end
//			//since the cap is generated with indices two segments ahead of the current one
//			//this will prevent redundant face generation at the end
//			if(segment < subdiv-2){
//				//top cap face
//				mesh.add(new Face(
//						2,//base bottom cap vert which is 2 (3rd index)
//						nextSeg*3+2,//vert that is the next segment
//						((segment+2)%(subdiv+1))*3+2//vert that is the second next segment
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
