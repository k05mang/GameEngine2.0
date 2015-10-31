package primitives;

import glMath.Mat3;
import glMath.Vec3;
import gldata.AttribType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import gldata.VertexArray;
import static java.lang.Math.PI;
import static java.lang.Math.sin;
import static java.lang.Math.cos;
import renderers.Renderable;
import renderers.RenderMode;

public class Torus extends Renderable {
	
	private int rings, ringSegs;
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
	 * @param modes RenderModes this Cone should be compatible with, the first mode is the initial mode
	 * for the Cone to render with
	 */
	public Torus(float radius, float tubeRadius, int rings, int ringSegs, RenderMode... modes){
		super();
		
		this.radius = radius;
		this.tubeRadius = tubeRadius;
		this.rings = rings < 3 ? 3 : rings;
		this.ringSegs = ringSegs < 3 ? 3 : ringSegs;
		
		int lastIndex = this.ringSegs*this.rings-1;
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
		
		//loop controlling what ring is being calculated
		for(int curRing = 0; curRing < this.rings; curRing++){
			//loop controlling what segment of the current ring is being calculated
			for(int ringSeg = 0; ringSeg < this.ringSegs; ringSeg++){
				double phi = 2*PI*(ringSeg/(double)this.ringSegs);
				double theta = 2*PI*(curRing/(double)this.rings);
				
				float x = (float)( (radius + tubeRadius*cos(phi))*cos(theta) );
				float y = (float)(tubeRadius*sin(phi));
				float z = (float)( (radius + tubeRadius*cos(phi))*sin(theta) );
				
				float normX = x-radius*(float)cos(theta);
				float normZ = z-radius*(float)sin(theta);
				
				Vertex vert = new Vertex(x, y, z,  normX, y, normZ,  0, 0);
				vert.addTo(vao);
				mesh.add(vert);
				
				int ringSegCycle = (ringSeg+1)%this.ringSegs;//controls the cycle connecting the last segment of the
				//current ring to the first segment
				
				int ringCyle = this.rings*( (curRing+1)%this.rings );//controls the cycle connecting the last ring to the
				//first ring
				
				mesh.add(new Face(
						ringSeg+this.rings*curRing,//current vertex (current segment of current rings)
						ringSegCycle+ringCyle,//next segment of next ring
						ringSeg+ringCyle//next segment of current ring
						));
				
				mesh.add(new Face(
						ringSeg+this.rings*curRing,//current vertex (current segment of current rings)
						ringSegCycle+this.rings*curRing,//current segment of next ring
						ringSegCycle+ringCyle//next segment of next ring
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
	 * Constructs a copy of the given torus
	 * 
	 * Refer to {@link renderer.Renderable#Renderable(Renderable) Renderable's copy constructor} 
	 * for more information about cautions with the copy constructor
	 * 
	 * @param copy Torus to copy
	 */
	public Torus(Torus copy){
		super(copy);
		rings = copy.rings;
		ringSegs = copy.ringSegs; 
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
