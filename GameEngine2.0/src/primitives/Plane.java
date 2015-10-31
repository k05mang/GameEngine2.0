package primitives;

import gldata.AttribType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import gldata.VertexArray;
import renderers.Renderable;
import renderers.RenderMode;

public class Plane extends Renderable{
	float width, length;
	
	public Plane(float width, float length, RenderMode... modes){
		super();
		this.width = width;
		this.length = length;
		
		vao = new VertexArray(modes[0], IndexBuffer.IndexType.BYTE);
		
		Vertex topLeft = new Vertex(-width,0,-length, 0,1,0, 0,1);
		Vertex bottomLeft = new Vertex(-width,0,length, 0,1,0, 0,0);
		Vertex topRight = new Vertex(width,0,-length, 0,1,0, 1,1);
		Vertex bottomRight = new Vertex(width,0,length, 0,1,0, 1,0);
		
		topLeft.addTo(vao);
		bottomLeft.addTo(vao);
		topRight.addTo(vao);
		bottomRight.addTo(vao);
		
		mesh.add(topLeft);
		mesh.add(bottomLeft);
		mesh.add(topRight);
		mesh.add(bottomRight);
		
		mesh.add(new Face(0,1,2));
		mesh.add(new Face(2,1,3));
		
		mesh.insertIndices(vao, modes[0]);//insert indices for the initial RenderMode
		
		//check if there are additional modes that need to be accounted for
		if(modes.length > 0){
			for(RenderMode curMode : modes){
				//check if the primary RenderMode was already processed, this way it isn't redundantly processed
				if(curMode != modes[0]){
					IndexBuffer modeBuffer = new IndexBuffer(IndexBuffer.IndexType.BYTE);
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
	
	public Plane(float sideLength, RenderMode... modes){
		this(sideLength, sideLength, modes);
	}
	
	public Plane(Plane copy){
		super(copy);
		width = copy.width;
		length = copy.length;
	}
	
	public float getLength(){
		return length;
	}
	
	public float getWidth(){
		return width;
	}

	@Override
	public void addMode(RenderMode mode) {
		// TODO Auto-generated method stub
		
	}
}
