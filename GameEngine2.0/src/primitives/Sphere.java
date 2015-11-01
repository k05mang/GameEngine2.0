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

	/**
	 * Constructs a sphere primitive with the given radius, and subdivisions while making it compatible
	 * with the given RenderModes.
	 * 
	 * Radius must be a value greater than 0, if radius is less than or equal to 0 then a default value 
	 * of .01 will be used instead.
	 * 
	 * Slices must be greater than 2, if the value is less than 3 a default value of 3 will be used instead.
	 * 
	 * Stacks must be greater than 0, if teh value given is less than 1 then a default value of 1 will be used instead.
	 * 
	 * @param radius Radius of the sphere
	 * @param slices Number of vertical segments
	 * @param stacks Number of horizontal segments between the top tip and bottom tip of the sphere
	 * @param modes RenderModes this Sphere should be compatible with, the first mode is the initial mode
	 * for the sphere to render with
	 */
	public Sphere(float radius, int slices, int stacks, RenderMode... modes){
		super();
		
		int maxSlice = slices < 3 ? 3 : slices;
		int maxStack = stacks < 1 ? 1 : stacks;
		this.radius = radius <= 0 ? .01f : radius;
		
		int lastIndex = maxStack*maxSlice+1;//value of the last index

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
		int upperStack = maxStack+1;
		//add the top vertex
		//TODO calculate UV
		Vertex top = new Vertex(0,this.radius,0, 0,1,0, 0,0);
		mesh.add(top);
		top.addTo(vao);//add vertex to the vertex array
		for(int curStack = 1; curStack < upperStack; curStack++){
			for(int curSlice = 0; curSlice < maxSlice; curSlice++){
				//pre-calculate the angles for the trig functions
				double phi = PI*(curStack/(double)upperStack);
				double theta = 2*PI*(curSlice/(double)maxSlice);
				
				float x = (float)( this.radius*cos(theta)*sin(phi) );
				float y = (float)( this.radius*cos(phi) );//since y is the up axis have it use the conventional z calculation
				float z = (float)( this.radius*sin(theta)*sin(phi) );
				//TODO calculate UV
				Vertex vert = new Vertex(x,y,z, x,y,z, 0,0);
				mesh.add(vert);
				vert.addTo(vao);//add vertex to vertex array
				
				int cycleControl = (curSlice+1)%maxSlice;//controls the offset from the start of a stack, when the first slice is reached
				//this will loop back to 0 to specify using the start index
				
				//calculate indices
				//check if we are generating for either caps of the sphere or if we are generating middle values
				if(curStack == 1){
					mesh.add(new Face(
							0,
							cycleControl+1,
							curSlice+1
							));
				}else{
					//two triangles need to be made per face
					int curIndexStart = (curStack-1)*maxSlice+1;
					int prevIndexStart = (curStack-2)*maxSlice+1;
					//the left triangle of the quad
					mesh.add(new Face(
							prevIndexStart+curSlice,//previous index at the same slice
							prevIndexStart+cycleControl,//next index of the previous stack 
							curIndexStart+curSlice//current index
							));
					//and the right triangle of the quad
					mesh.add(new Face(
							prevIndexStart+cycleControl,//next index of the previous stack 
							curIndexStart+cycleControl,//next index of current stack
							curIndexStart+curSlice//current index
							));
				}
				
				if(curStack == upperStack-1){
					//index choice will modes[0]tain winding
					int lastRingStart = lastIndex-maxSlice;
					mesh.add(new Face(
							lastIndex,
							lastRingStart+curSlice,//current index
							lastRingStart+cycleControl//next index
							));
				}
			}
		}
		//add the end vertex
		//TODO calculate UV
		Vertex bottom = new Vertex(0,-this.radius,0, 0,-1,0, 0,0);
		mesh.add(bottom);
		bottom.addTo(vao);//add vertex to vertex array
		
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
	 * Constructs a sphere with the given radius, and subdivisions. And making it
	 * compatible with the given RenderModes
	 * 
	 * @param radius Radius of the sphere
	 * @param divisions Number of vertical and horizontal subdivisions for constructing the sphere
	 * @param modes RenderModes this Sphere should be compatible with, the first mode is the initial mode
	 * for the sphere to render with
	 */
	public Sphere(float radius, int divisions, RenderMode... modes){
		this(radius, divisions, divisions, modes);
	}
	
	/**
	 * Constructs a sphere that is the copy of the given sphere.
	 * 
	 * Refer to {@link renderer.Renderable#Renderable(Renderable) Renderable's copy constructor} 
	 * for more information about cautions with the copy constructor
	 * 
	 * @param copy Sphere to copy
	 */
	public Sphere(Sphere copy){
		 super(copy);
		 radius = copy.radius;
	}
	
	/**
	 * Gets the radius of this sphere
	 * 
	 * @return Radius of this sphere
	 */
	public float getRadius(){
		return radius;
	}

	@Override
	public void addMode(RenderMode mode) {
		// TODO Auto-generated method stub
	}
}
