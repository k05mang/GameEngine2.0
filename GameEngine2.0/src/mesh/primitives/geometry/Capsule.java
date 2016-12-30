package mesh.primitives.geometry;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import renderers.RenderMode;
import mesh.Mesh;
import mesh.primitives.Triangle;
import mesh.primitives.Vertex;

public class Capsule extends Mesh {
	
	private float length, radius;

	public Capsule(float radius, float length, int segments, String defaultMode) {
		int maxSegment = Math.max(3, segments);
		int capStack = maxSegment >> 1;//stacks used in the hemisphere caps calculation
		int maxStack = (capStack << 1)-1;
		this.radius = Math.abs(radius);
		this.length = Math.max(0, Math.abs(length)-this.radius-this.radius);//subtract the diameter of the sphere so that the whole capsule is the full length
		
		IndexBuffer.IndexType dataType = getIndexType((maxSegment+1)*(maxStack+2));
		
		//create index buffers
		IndexBuffer solidIbo = new IndexBuffer(dataType);
		IndexBuffer edgeIbo = new IndexBuffer(dataType);
		//add index buffers to mesh list
		ibos.add(solidIbo);
		ibos.add(edgeIbo);
		//add index buffers to vertex array
		vao.addIndexBuffer(SOLID_MODE, RenderMode.TRIANGLES, solidIbo);
		vao.addIndexBuffer(EDGE_MODE, RenderMode.LINES, edgeIbo);
		
		BufferObject vbo = new BufferObject(BufferType.ARRAY);
		vbos.add(vbo);
		float halfLength = this.length/2.0f;
		int trueCurStack = 0;//tracks the real current stack value, due to manipulation of the loop counter
		for(int curStack = 0; curStack < maxStack+1; curStack++,trueCurStack++){
			for(int curSegment = 0; curSegment < maxSegment+1; curSegment++){
				float u = curSegment/(float)maxSegment;
				float v = trueCurStack/(float)(maxStack+1);
				//pre-calculate the angles for the trig functions
				double phi = PI*v;
				double theta = 2*PI*u;
				
				float x = (float)( this.radius*cos(theta)*sin(phi) );
				//since y is the up axis have it use the conventional z calculation
				//add/subtract the half length value depending on what hemisphere we are on
				float y = (float)( this.radius*cos(phi)+(trueCurStack < capStack ? halfLength : -halfLength) );
				float z = (float)( this.radius*sin(theta)*sin(phi) );
				float normY = y+(trueCurStack < capStack ? -halfLength : halfLength);
				
				int curIndex = 0;//current index
				int nextStackIndex = 0;//same slice next stack
				
				if(curStack == 0 && curSegment < maxSegment){//sphere top cap
					float uvOffset = .5f/maxSegment;
					nextStackIndex = maxSegment+curSegment;//same slice next stack
					Vertex vert = new Vertex(x,y,z, x,normY,z, 1-u-uvOffset, 1-v);
					geometry.add(vert);
					
					Triangle top = new Triangle(
							curSegment,//current vertex index
							nextStackIndex+1,//next slice of the next stack 
							nextStackIndex//current slice of the next stack
							);
					geometry.add(top);
					
					//add indices to index buffers
					top.insertPrim(solidIbo);
					
					//edge ibo
					edgeIbo.add(curSegment);
					edgeIbo.add(nextStackIndex);
					
				}else if(curStack == maxStack && curSegment < maxSegment){//sphere bottom cap
					float uvOffset = .5f/maxSegment;
					curIndex = maxSegment+(trueCurStack-2)*(maxSegment+1)+curSegment;//current index
					nextStackIndex = maxSegment+(trueCurStack-1)*(maxSegment+1)+curSegment;//same slice next stack
					Vertex vert = new Vertex(x,y,z, x,normY,z, 1-u-uvOffset, 1-v);
					geometry.add(vert);
					
					Triangle bottom = new Triangle(
							curIndex,//current vertex index
							curIndex+1,//next index of current stack
							nextStackIndex//next stack same slice
							);
					geometry.add(bottom);
					
					//add indices to index buffers
					bottom.insertPrim(solidIbo);
					
					//edge ibo
					edgeIbo.add(curIndex);
					edgeIbo.add(nextStackIndex);
					
				}else if(curStack > 0 && curStack < maxStack){//sphere mid section
					curIndex = maxSegment+(trueCurStack-1)*(maxSegment+1)+curSegment;//current index
					nextStackIndex = maxSegment+trueCurStack*(maxSegment+1)+curSegment;//same slice next stack
					Vertex vert = new Vertex(x,y,z, x,normY,z, 1-u, 1-v);
					geometry.add(vert);
					if(curSegment < maxSegment && curStack < maxStack-1){
						Triangle left = new Triangle(
								curIndex,//current vertex index
								nextStackIndex+1,//next slice of the next stack 
								nextStackIndex//current slice of the next stack
								);
						geometry.add(left);
						
						Triangle right = new Triangle(
								curIndex,//current vertex index
								curIndex+1,//next index of current stack
								nextStackIndex+1//next stack next slice
								);
						geometry.add(right);
						
						//add indices to index buffers
						left.insertPrim(solidIbo);
						right.insertPrim(solidIbo);
						
						//edge ibo
						edgeIbo.add(curIndex);
						edgeIbo.add(nextStackIndex);
						
						edgeIbo.add(nextStackIndex);
						edgeIbo.add(nextStackIndex+1);
						
						edgeIbo.add(nextStackIndex+1);
						edgeIbo.add(curIndex+1);
						
						edgeIbo.add(curIndex+1);
						edgeIbo.add(curIndex);
					}
				}
			}

			//check if we are at the cap split point and need to repeat the current stack
			if(trueCurStack == capStack){
				//if we are reduce curStack by 1 to redo the mid section loop
				curStack--;
			}
		}

		//buffer index buffers to the gpu
		solidIbo.flush(BufferUsage.STATIC_DRAW);
		edgeIbo.flush(BufferUsage.STATIC_DRAW);

		geometry.genTangentBitangent();
		geometry.insertVertices(vbo);
		vbo.flush(BufferUsage.STATIC_DRAW);
		vao.addVertexBuffer("default", vbo);

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

	public Capsule(Mesh copy) {
		super(copy);
		// TODO Auto-generated constructor stub
	}

}
