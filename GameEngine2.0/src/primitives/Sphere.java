package primitives;

import static java.lang.Math.PI;
import static java.lang.Math.sin;
import static java.lang.Math.cos;

import java.util.ArrayList;

import gldata.AttribType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import gldata.VertexArray;
import renderers.RenderMode;
import renderers.Renderable;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_INVALID_FRAMEBUFFER_OPERATION;

public class Sphere extends Renderable{
	private float radius;
	private int slices, stacks;

	public Sphere(float radius, int slices, int stacks, RenderMode main, RenderMode... modes){
		super();
		
		this.slices = slices < 3 ? 3 : slices;
		this.stacks = stacks < 1 ? 1 : stacks;
		this.radius = radius <= 0 ? .01f : radius;
		
		int lastIndex = this.stacks*this.slices+1;//value of the last index
		
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
		vao = new VertexArray(main, dataType);
		
		int upperStack = this.stacks+1;
		//add the top vertex
		//TODO calculate UV
		Vertex top = new Vertex(0,this.radius,0, 0,this.radius,0, 0,0);
		mesh.add(top);
		top.addTo(vao);//add vertex to the vertex array
		for(int curStack = 1; curStack < upperStack; curStack++){
			for(int curSlice = 0; curSlice < this.slices; curSlice++){
				//pre-calculate the angles for the trig functions
				double phi = PI*(curStack/(double)upperStack);
				double theta = 2*PI*(curSlice/(double)this.slices);
				
				float x = (float)( this.radius*cos(theta)*sin(phi) );
				float y = (float)( this.radius*cos(phi) );//since y is the up axis have it use the conventional z calculation
				float z = (float)( this.radius*sin(theta)*sin(phi) );
				//TODO calculate UV
				Vertex vert = new Vertex(x,y,z, x,y,z, 0,0);
				mesh.add(vert);
				vert.addTo(vao);//add vertex to vertex array
				
				int cycleControl = (curSlice+1)%this.slices;//controls the offset from the start of a stack, when the first slice is reached
				//this will loop back to 0 to specify using the start index
				
				//calculate indices
				//check if we are generating for either caps of the sphere or if we are generating middle values
				if(curStack == 1){
					mesh.add(new Face(
							0,
							curSlice+1,
							cycleControl+1
							));
				}else if(curStack == upperStack){
					//index choice will maintain winding
					int lastRingStart = lastIndex-this.slices;
					mesh.add(new Face(
							lastIndex,
							lastRingStart+cycleControl,//next index
							lastRingStart+curSlice//current index
							));
				}else{
					//two triangles need to be made per face
					int curIndexStart = (curStack-1)*this.slices+1;
					int prevIndexStart = (curStack-2)*this.slices+1;
					//the left triangle of the quad
					mesh.add(new Face(
							prevIndexStart+curSlice,//previous index at the same slice
							curIndexStart+curSlice,//current index
							prevIndexStart+cycleControl//next index of the previous stack 
							));
					//and the right triangle of the quad
					mesh.add(new Face(
							prevIndexStart+cycleControl,//next index of the previous stack 
							curIndexStart+curSlice,//current index
							curIndexStart+cycleControl//next index of current stack
							));
				}
			}
		}
		//add the end vertex
		//TODO calculate UV
		Vertex bottom = new Vertex(0,-this.radius,0, 0,-this.radius,0, 0,0);
		mesh.add(bottom);
		bottom.addTo(vao);//add vertex to vertex array
		
		mesh.insertIndices(vao, main);//insert indices for the main rendering mode
		
		//check if there are additional modes that need to be accounted for
		if(modes.length > 0){
			for(RenderMode curMode : modes){
				//check if the primary RenderMode passed to main was already processed, this way it isn't redundantly processed
				if(curMode != main){
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
//		System.out.println(glGetError());
//		switch(glGetError()){
//			case GL_INVALID_ENUM:
//				System.out.println("GL_INVALID_ENUM");
//				break;
//			case GL_INVALID_VALUE:
//				System.out.println("GL_INVALID_VALUE");
//				break;
//			case GL_INVALID_OPERATION:
//				System.out.println("GL_INVALID_OPERATION");
//				break;
//			case GL_INVALID_FRAMEBUFFER_OPERATION:
//				System.out.println("GL_INVALID_FRAMEBUFFER_OPERATION");
//				break;
//			case GL_OUT_OF_MEMORY:
//				System.out.println("GL_OUT_OF_MEMORY");
//				break;
//			case GL_STACK_UNDERFLOW:
//				System.out.println("GL_STACK_UNDERFLOW");
//				break;
//			case GL_STACK_OVERFLOW:
//				System.out.println("GL_STACK_OVERFLOW");
//				break;
//		}
		//enable the attributes for the vertex array
		vao.enableAttribute(0);
		vao.enableAttribute(1);
		vao.enableAttribute(2);
	}
	
	public Sphere(float radius, int divisions, RenderMode main, RenderMode... modes){
		this(radius, divisions, divisions, main, modes);
	}
	
	public Sphere(Sphere copy){
		 super(copy);
		 radius = copy.radius;
		 slices = copy.slices;
		 stacks = copy.stacks;
	}

	@Override
	public void addMode(RenderMode mode) {
		// TODO Auto-generated method stub
	}
}
