package primitives;

import glMath.Mat3;
import glMath.Vec3;
import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import gldata.VertexArray;
import static java.lang.Math.PI;
import static java.lang.Math.sin;
import static java.lang.Math.cos;
import renderers.Renderable;
import renderers.RenderMode;

public class Torus extends Renderable {
	
	private float tubeRadius, radius;

	/**
	 * Constructs a torus with the given radius, and tube radius, with the specified number of rings with the given number of
	 * segments per ring. The torus will be constructed such that it is compatible with the give RenderModes
	 * 
	 * Rings and ringSegs must be greater than 2, if the value is less than 3 a default value of 3 will be used instead.
	 * 
	 * @param radius Radius of the torus measured from the center of the torus to the middle of the torus's tube
	 * @param tubeRadius Radius of the tube of the torus
	 * @param rings Number of vertical segments defining the smoothness of the ring of the torus
	 * @param ringSegs Number of horizontal segments defining the smoothness of the torus tube
	 * @param modes RenderModes this Torus should be compatible with, the first mode is the initial mode
	 * for the Torus to render with
	 */
	public Torus(float radius, float tubeRadius, int rings, int ringSegs, RenderMode... modes){
		super();
		
		this.radius = radius;
		this.tubeRadius = tubeRadius;
		int maxRing = rings < 3 ? 3 : rings;
		int maxRingSeg = ringSegs < 3 ? 3 : ringSegs;
		
		int lastIndex = maxRingSeg*maxRing-1;
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
		BufferObject vbo = new BufferObject(BufferType.ARRAY);
		vbos.add(vbo);
		
		//loop controlling what ring is being calculated
		for(int curRing = 0; curRing < maxRing; curRing++){
			//loop controlling what segment of the current ring is being calculated
			for(int ringSeg = 0; ringSeg < maxRingSeg; ringSeg++){
				double phi = 2*PI*(ringSeg/(double)maxRingSeg);
				double theta = 2*PI*(curRing/(double)maxRing);
				
				float x = (float)( (radius + tubeRadius*cos(phi))*cos(theta) );
				float y = (float)(tubeRadius*sin(phi));
				float z = (float)( (radius + tubeRadius*cos(phi))*sin(theta) );
				
				float normX = x-radius*(float)cos(theta);
				float normZ = z-radius*(float)sin(theta);
				
				Vertex vert = new Vertex(x, y, z,  normX, y, normZ,  0, 0);
				vert.addTo(vbo);
				mesh.add(vert);
				
				int ringSegCycle = (ringSeg+1)%maxRingSeg;//controls the cycle connecting the last segment of the
				//current ring to the first segment
				
				int ringCyle = maxRing*( (curRing+1)%maxRing );//controls the cycle connecting the last ring to the
				//first ring
				
				mesh.add(new Face(
						ringSeg+maxRing*curRing,//current vertex (current segment of current rings)
						ringSegCycle+ringCyle,//next segment of next ring
						ringSeg+ringCyle//next segment of current ring
						));
				
				mesh.add(new Face(
						ringSeg+maxRing*curRing,//current vertex (current segment of current rings)
						ringSegCycle+maxRing*curRing,//current segment of next ring
						ringSegCycle+ringCyle//next segment of next ring
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
		vao.addAttrib(0, AttribType.VEC3, false, 0);//position
		vao.addAttrib(1, AttribType.VEC3, false, 0);//normal
		vao.addAttrib(2, AttribType.VEC2, false, 0);//uv
		
		//finalize the buffers in the vao
		vao.finalize();
		//enable the attributes for the vertex array
		vao.enableAttribute(0);
		vao.enableAttribute(1);
		vao.enableAttribute(2);
	}
	
	/**
	 * Constructs a copy of the given torus
	 * 
	 * Refer to {@link renderer.Renderable#Renderable(Renderable) Renderable's copy constructor} 
	 * for more information about cautions with the copy constructor
	 * 
	 * @param copy Torus to copy
	 */
	public Torus(Torus copy){
		super(copy);
		radius = copy.radius;
		tubeRadius = copy.tubeRadius; 
	}

	/**
	 * Gets the radius of the ring of the torus measured from the middle of the torus to the middle of the tube of the torus
	 * 
	 * @return Radius of the torus from the middle to the middle of the tube
	 */
	public float getRadius() {
		return radius;
	}

	/**
	 * Gets the radius of the tube of the torus
	 * 
	 * @return Radius of the tube of the torus
	 */
	public float getTubeRadius() {
		return tubeRadius;
	}

	@Override
	public void addMode(RenderMode mode) {
		// TODO Auto-generated method stub
		
	}
}
