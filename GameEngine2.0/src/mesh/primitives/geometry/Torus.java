package mesh.primitives.geometry;

import mesh.Mesh;
import mesh.primitives.Triangle;
import mesh.primitives.Vertex;
import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import static java.lang.Math.PI;
import static java.lang.Math.sin;
import static java.lang.Math.cos;
import renderers.RenderMode;

public final class Torus extends Mesh {
	
	private float tubeRadius, radius;
	public static final String 
		TUBE_RINGS = "rings",
		FULL_TUBE_RINGS = "full_rings";

	/**
	 * Constructs a torus with the given {@code radius}, and {@code tubeRadius}, with {@code segments} rings
	 * each with the given number of {@code segments} per ring. The default mode is SOLID_MODE.
	 * 
	 * @param radius Radius of the torus measured from the center of the torus to the middle of the torus's tube
	 * @param tubeRadius Radius of the tube of the torus
	 * @param segments Number of rings and segments per ring
	 */
	public Torus(float radius, float tubeRadius, int segments){
		this(radius, tubeRadius, segments, segments, SOLID_MODE);
	}

	/**
	 * Constructs a torus with the given {@code radius}, and {@code tubeRadius}, with the specified number of 
	 * {@code rings} each with the given number of {@code segments} per ring.  The default mode is SOLID_MODE.
	 * 
	 * Rings and ringSegs must be greater than 2, if the value is less than 3 a default value of 3 will be used instead.
	 * 
	 * @param radius Radius of the torus measured from the center of the torus to the middle of the torus's tube
	 * @param tubeRadius Radius of the tube of the torus
	 * @param rings Number of vertical segments defining the smoothness of the ring of the torus
	 * @param ringSegs Number of horizontal segments defining the smoothness of the torus tube
	 * @param defaultMode Defines the mode to render the mesh with
	 */
	public Torus(float radius, float tubeRadius, int rings, int ringSegs){
		this(radius, tubeRadius, rings, ringSegs, SOLID_MODE);
	}
	
	/**
	 * Constructs a torus with the given {@code radius}, and {@code tubeRadius}, with {@code segments} rings
	 * each with the given number of {@code segments} per ring. {@code defaultMode} will specify 
	 * the mode the mesh will initially render with. Selectable modes and what they entail are as follows:
	 * <ul>
	 * <li>SOLID_MODE: The mesh will render as a GL_TRIANGLES</li>
	 * <li>EDGE_MODE: The mesh will render as GL_LINES, where only the major edges of the mesh are rendered. This will
	 * only render edges of the mesh that define its shape.</li>
	 * <li>TUBE_RINGS: The mesh will render as a GL_LINES, where only the rings of the torus are rendered</li>
	 * <li>FULL_TUBE_RINGS: The mesh will render as a GL_LINES, where only the rings of the full torus tube are rendered</li>
	 * </ul>
	 * 
	 * @param radius Radius of the torus measured from the center of the torus to the middle of the torus's tube
	 * @param tubeRadius Radius of the tube of the torus
	 * @param segments Number of rings and segments per ring
	 * @param defaultMode Defines the mode to render the mesh with
	 */
	public Torus(float radius, float tubeRadius, int segments, String defaultMode){
		this(radius, tubeRadius, segments, segments, defaultMode);
	}
	
	/**
	 * Constructs a torus with the given {@code radius}, and {@code tubeRadius}, with the specified number of 
	 * {@code rings} each with the given number of {@code segments} per ring. {@code defaultMode} will specify 
	 * the mode the mesh will initially render with. Selectable modes and what they entail are as follows:
	 * <ul>
	 * <li>SOLID_MODE: The mesh will render as a GL_TRIANGLES</li>
	 * <li>EDGE_MODE: The mesh will render as GL_LINES, where only the major edges of the mesh are rendered. This will
	 * only render edges of the mesh that define its shape.</li>
	 * <li>TUBE_RINGS: The mesh will render as a GL_LINES, where only the rings of the torus are rendered</li>
	 * <li>FULL_TUBE_RINGS: The mesh will render as a GL_LINES, where only the rings of the full torus tube are rendered</li>
	 * </ul>
	 * 
	 * Rings and ringSegs must be greater than 2, if the value is less than 3 a default value of 3 will be used instead.
	 * 
	 * @param radius Radius of the torus measured from the center of the torus to the middle of the torus's tube
	 * @param tubeRadius Radius of the tube of the torus
	 * @param rings Number of vertical segments defining the smoothness of the ring of the torus
	 * @param ringSegs Number of horizontal segments defining the smoothness of the torus tube
	 * @param defaultMode Defines the mode to render the mesh with
	 */
	public Torus(float radius, float tubeRadius, int rings, int ringSegs, String defaultMode){
		super();
		
		this.radius = Math.abs(radius);
		this.tubeRadius = Math.abs(tubeRadius);
		int maxRing = Math.max(3, rings);
		int maxRingSeg = Math.max(3, ringSegs);

		IndexBuffer.IndexType dataType = getIndexType((maxRing+1)*(maxRingSeg+1));
		//create index buffers
		IndexBuffer solidIbo = new IndexBuffer(dataType);
		IndexBuffer edgeIbo = new IndexBuffer(dataType);
		IndexBuffer tubeRingIbo = new IndexBuffer(dataType);
		IndexBuffer fullTubeIbo = new IndexBuffer(dataType);
		//add index buffers to mesh list
		ibos.add(solidIbo);
		ibos.add(edgeIbo);
		ibos.add(tubeRingIbo);
		ibos.add(fullTubeIbo);
		//add index buffers to vertex array
		vao.addIndexBuffer(SOLID_MODE, RenderMode.TRIANGLES, solidIbo);
		vao.addIndexBuffer(EDGE_MODE, RenderMode.LINES, edgeIbo);
		vao.addIndexBuffer(TUBE_RINGS, RenderMode.LINES, tubeRingIbo);
		vao.addIndexBuffer(FULL_TUBE_RINGS, RenderMode.LINES, fullTubeIbo);
		
		BufferObject vbo = new BufferObject(BufferType.ARRAY);
		vbos.add(vbo);
		
		//loop controlling what ring is being calculated
		for(int curRing = 0; curRing < maxRing+1; curRing++){
			//loop controlling what segment of the current ring is being calculated
			for(int ringSeg = 0; ringSeg < maxRingSeg+1; ringSeg++){
				
				float u = curRing/(float)maxRing;
				float v = ringSeg/(float)maxRingSeg;

				double theta = 2*PI*u;//major circle
				double phi = 2*PI*v;//minor circle
				
				
				float x = (float)( (-radius + tubeRadius*cos(phi))*cos(theta) );
				float y = (float)(tubeRadius*sin(phi));
				float z = (float)( (-radius + tubeRadius*cos(phi))*sin(theta) );
				
				float normX = x+radius*(float)cos(theta);
				float normZ = z+radius*(float)sin(theta);
				
				Vertex vert = new Vertex(x, y, z,  normX, y, normZ, u, v);
				geometry.add(vert);

				//prevent generating faces on the last loop since the faces need for the end of the
				//torus has already been generated
				if(curRing < maxRing && ringSeg < maxRingSeg){
					Triangle left = new Triangle(
							ringSeg+(maxRingSeg+1)*curRing,//current vertex (current segment of current rings)
							(ringSeg+1)+(maxRingSeg+1)*(curRing+1),//next segment of next ring
							(ringSeg+1)+(maxRingSeg+1)*curRing//next segment of current ring
							);
					geometry.add(left);
					
					Triangle right = new Triangle(
							ringSeg+(maxRingSeg+1)*curRing,//current vertex (current segment of current rings)
							ringSeg+(maxRingSeg+1)*(curRing+1),//current segment of next ring
							(ringSeg+1)+(maxRingSeg+1)*(curRing+1)//next segment of next ring
							);
					geometry.add(right);
					
					//add values to the index buffer
					left.insertPrim(solidIbo);
					right.insertPrim(solidIbo);
					
					//edge ibo
					edgeIbo.add(ringSeg+(maxRingSeg+1)*curRing);
					edgeIbo.add((ringSeg+1)+(maxRingSeg+1)*curRing);
					
					edgeIbo.add((ringSeg+1)+(maxRingSeg+1)*curRing);
					edgeIbo.add((ringSeg+1)+(maxRingSeg+1)*(curRing+1));
					
					edgeIbo.add((ringSeg+1)+(maxRingSeg+1)*(curRing+1));
					edgeIbo.add(ringSeg+(maxRingSeg+1)*(curRing+1));
					
					edgeIbo.add(ringSeg+(maxRingSeg+1)*(curRing+1));
					edgeIbo.add(ringSeg+(maxRingSeg+1)*curRing);
					
					//tube ring ibo
					tubeRingIbo.add(ringSeg+(maxRingSeg+1)*curRing);
					tubeRingIbo.add((ringSeg+1)+(maxRingSeg+1)*curRing);
					
					//full tube ring ibo
					fullTubeIbo.add(ringSeg+(maxRingSeg+1)*(curRing+1));
					fullTubeIbo.add(ringSeg+(maxRingSeg+1)*curRing);
				}
			}
		}
		//buffer index buffers to gpu
		solidIbo.flush(BufferUsage.STATIC_DRAW);
		edgeIbo.flush(BufferUsage.STATIC_DRAW);
		tubeRingIbo.flush(BufferUsage.STATIC_DRAW);
		fullTubeIbo.flush(BufferUsage.STATIC_DRAW);
		
		geometry.genTangentBitangent();
		geometry.insertVertices(vbo);
		vbo.flush(BufferUsage.STATIC_DRAW);
		vao.addVertexBuffer("default", vbo);

		if(
			defaultMode.equals(SOLID_MODE) ||
			defaultMode.equals(EDGE_MODE) ||
			defaultMode.equals(TUBE_RINGS) ||
			defaultMode.equals(FULL_TUBE_RINGS)
		){
			vao.setIndexBuffer(defaultMode);
		}else{
			vao.setIndexBuffer(SOLID_MODE);
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
	 * Constructs a copy of the given torus
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
}
