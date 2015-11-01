package primitives;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import gldata.AttribType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import gldata.VertexArray;
import renderers.RenderMode;
import renderers.Renderable;

public class Disc extends Renderable {

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
		
		int maxSegment = segments < 3 ? 3 : segments;
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
		//instantiate the vertex array
		vao = new VertexArray(modes[0], dataType);
		
		for(int segment = 0; segment < maxSegment; segment++){
			double theta = 2*PI*(segment/(double)maxSegment);
			
			float x = this.radius*(float)(cos(theta));
			float z = this.radius*(float)(sin(theta));

			Vertex curVert = new Vertex(x, 0, z,  0,1,0, 0,0);

			mesh.add(curVert);
			
			curVert.addTo(vao);
			
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
