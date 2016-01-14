package mesh.primitives.geometry;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import mesh.Renderable;
import mesh.primitives.Face;
import mesh.primitives.Vertex;
import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import renderers.RenderMode;

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
		this.radius = Math.abs(radius);
		
		int maxSegment = Math.max(3, segments);
		IndexBuffer.IndexType dataType = getIndexType(maxSegment-1);
		
		BufferObject vbo = new BufferObject(BufferType.ARRAY);
		vbos.add(vbo);
		
		for(int segment = 0; segment < maxSegment; segment++){
			double theta = 2*PI*(segment/(double)maxSegment);
			
			float u = (float)(cos(theta));
			float v = (float)(sin(theta));
			
			float x = u;
			float z = v;

			Vertex curVert = new Vertex(x, 0, z,  0,1,0, u/2+.5f, -v/2+.5f);

			geometry.add(curVert);
			
			curVert.addTo(vbo);
			
			int nextSeg = (segment+1)%maxSegment;//due to constantly computing this value cache it for reuse
			
			//only compute the indices if we are 3 or more segments from the end
			//since we are generating indices two segments ahead of the current one
			//this will prevent redundant face generation at the end
			if(segment < maxSegment-2){
				geometry.add(new Face(
						0,//base vert
						(segment+2)%maxSegment,//vert that is the second next segment
						nextSeg//vert that is the next segment
						));
			}
		}

		transforms.scale(this.radius, 1, this.radius);
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
}
