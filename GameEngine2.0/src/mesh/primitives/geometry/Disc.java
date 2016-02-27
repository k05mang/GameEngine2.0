package mesh.primitives.geometry;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import mesh.Mesh;
import mesh.primitives.Face;
import mesh.primitives.Vertex;
import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import renderers.RenderMode;

public final class Disc extends Mesh {

	private float radius;
	
	public Disc(float radius, int segments){
		this(radius, segments, SOLID_MODE);
	}
	
	/**
	 * Constructs a disc with the given radius and number of segments
	 * 
	 * @param radius Radius of the disc
	 * @param segments Fineness of the disc
	 */
	public Disc(float radius, int segments, String defaultMode){
		this.radius = Math.abs(radius);
		
		int maxSegment = Math.max(3, segments);
		IndexBuffer.IndexType dataType = getIndexType(maxSegment-1);

		//create index buffers
		IndexBuffer solidIbo = new IndexBuffer(dataType);
		IndexBuffer edgeIbo = new IndexBuffer(dataType);
		//add index buffers to mesh list
		ibos.add(solidIbo);
		ibos.add(edgeIbo);
		//add index buffer to vertex array
		vao.addIndexBuffer(SOLID_MODE, RenderMode.TRIANGLES, solidIbo);
		vao.addIndexBuffer(EDGE_MODE, RenderMode.LINE_LOOP, edgeIbo);
		
		BufferObject vbo = new BufferObject(BufferType.ARRAY);
		vbos.add(vbo);
		
		for(int segment = 0; segment < maxSegment; segment++){
			double theta = 2*PI*(segment/(double)maxSegment);
			
			float u = (float)(cos(theta));
			float v = (float)(sin(theta));
			
			float x = this.radius*u;
			float z = this.radius*v;

			Vertex curVert = new Vertex(x, 0, z,  0,1,0, u/2+.5f, -v/2+.5f, 1,0,0, 0,0,1);

			geometry.add(curVert);
			
			curVert.addTo(vbo);
			edgeIbo.add(segment);
			int nextSeg = (segment+1)%maxSegment;//due to constantly computing this value cache it for reuse
			
			//only compute the indices if we are 3 or more segments from the end
			//since we are generating indices two segments ahead of the current one
			//this will prevent redundant face generation at the end
			if(segment < maxSegment-2){
				Face face = new Face(
						0,//base vert
						(segment+2)%maxSegment,//vert that is the second next segment
						nextSeg//vert that is the next segment
						);
				geometry.add(face);
				face.insertPrim(solidIbo);
			}
		}

		vbo.flush(BufferUsage.STATIC_DRAW);
		vao.addVertexBuffer("default", vbo);
		
		solidIbo.flush(BufferUsage.STATIC_DRAW);
		edgeIbo.flush(BufferUsage.STATIC_DRAW);
		
		if(defaultMode.equals(SOLID_MODE) || defaultMode.equals(EDGE_MODE)){
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
	 * Constructs a copy of the given disc
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
		return Math.max(transforms.getScalars().x, transforms.getScalars().z)*radius;
	}
}
