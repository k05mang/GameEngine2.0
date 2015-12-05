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
import renderers.RenderMode;
import renderers.Renderable;

public final class Disc extends Renderable {

	private float radius;
	
	/**
	 * Constructs a disc with the given radius and number of segments
	 * 
	 * @param radius Radius of the disc
	 * @param segments Fineness of the disc
	 * @param modes RenderModes this Disc should be compatible with, the first mode is the initial mode
	 * for the Disc to render with
	 */
	public Disc(float radius, int segments, RenderMode... modes){
		this.radius = radius;
		
		int maxSegment = Math.max(3, segments);
		IndexBuffer.IndexType dataType = null;
		
		//determine what data type the index buffer should be
		if(maxSegment-1 < Byte.MAX_VALUE){
			dataType = IndexBuffer.IndexType.BYTE;
		}else if(maxSegment-1 < Short.MAX_VALUE){
			dataType = IndexBuffer.IndexType.SHORT;
		}else if(maxSegment-1 < Integer.MAX_VALUE){
			dataType = IndexBuffer.IndexType.INT;
		}else{
			//TODO handle when the number of vertices and indices would exceed the max value
		}
		BufferObject vbo = new BufferObject(BufferType.ARRAY);
		vbos.add(vbo);
		
		for(int segment = 0; segment < maxSegment; segment++){
			double theta = 2*PI*(segment/(double)maxSegment);
			
			
			float u = (float)(cos(theta));
			float v = (float)(sin(theta));
			float x = this.radius*u;
			float z = this.radius*v;

			Vertex curVert = new Vertex(x, 0, z,  0,1,0, u/2+.5f, -v/2+.5f);

			mesh.add(curVert);
			
			curVert.addTo(vbo);
			
			int nextSeg = (segment+1)%maxSegment;//due to constantly computing this value cache it for reuse
			
			//only compute the indices if we are 3 or more segments from the end
			//since we are generating indices two segments ahead of the current one
			//this will prevent redundant face generation at the end
			if(segment < maxSegment-2){
				mesh.add(new Face(
						0,//base vert
						(segment+2)%maxSegment,//vert that is the second next segment
						nextSeg//vert that is the next segment
						));
			}
		}

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
	 * Constructs a copy of the given disc
	 * 
	 * Refer to {@link renderer.Renderable#Renderable(Renderable) Renderable's copy constructor} 
	 * for more information about cautions with the copy constructor
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
	
	@Override
	public void addMode(RenderMode mode) {
		// TODO Auto-generated method stub

	}

}
