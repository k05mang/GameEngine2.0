package mesh.primitives.geometry;

import static java.lang.Math.PI;
import static java.lang.Math.sin;
import static java.lang.Math.cos;


import mesh.Renderable;
import mesh.primitives.Face;
import mesh.primitives.Vertex;
import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import renderers.RenderMode;

public final class Sphere extends Renderable{
	private float radius;

	/**
	 * Constructs a sphere primitive with the given radius, and subdivisions while making it compatible
	 * with the given RenderModes.
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
		
		int maxSlice = Math.max(3, slices);
		int maxStack = Math.max(1, stacks);
		this.radius = radius;
		
		BufferObject vbo = new BufferObject(BufferType.ARRAY);
		vbos.add(vbo);
		
		for(int curStack = 0; curStack < maxStack+1; curStack++){
			for(int curSlice = 0; curSlice < maxSlice+1; curSlice++){
				
				float u = curSlice/(float)maxSlice;
				float v = curStack/(float)maxStack;
				//pre-calculate the angles for the trig functions
				double phi = PI*v;
				double theta = 2*PI*u;
				
				float x = (float)( this.radius*cos(theta)*sin(phi) );
				float y = (float)( this.radius*cos(phi) );//since y is the up axis have it use the conventional z calculation
				float z = (float)( this.radius*sin(theta)*sin(phi) );
				
				Vertex vert = new Vertex(x,y,z, x,y,z, 1-u, 1-v);
				geometry.add(vert);
				vert.addTo(vbo);//add vertex to vertex array
				
				//calculate indices
				//restrict face calculation to within the sphere boundaries, since the bottom will result in erratic behavior
				//when at the end of the a loop
				if(curStack < maxStack && curSlice < maxSlice){
					int curIndex = curStack*(maxSlice+1)+curSlice;//current index
					int nextStackIndex = (curStack+1)*(maxSlice+1)+curSlice;//same slice next stack
					
					//don't have the left face indexing generate faces for the bottom cap, only the top cap
					//the left triangle of the quad, or top cap if at the start of stack loop
					if(curStack != maxStack-1){
						geometry.add(new Face(
							curIndex,//current vertex index
							nextStackIndex+1,//next slice of the next stack 
							nextStackIndex//current slice of the next stack
							));
					}
					//don't have the right face indexing generate faces for the top cap, only the bottom cap
					//and the right triangle of the quad, or bottom cap if at the end of stack loop
					if(curStack != 0){
						geometry.add(new Face(
							curIndex,//current vertex index
							curIndex+1,//next index of current stack
							nextStackIndex+1//next stack next slice
							));
					}
				}
			}
		}
		
		vbo.flush(BufferUsage.STATIC_DRAW);
		vao.addVertexBuffer("default", vbo);
		
		IndexBuffer.IndexType dataType = getIndexType(geometry.getNumVertices());
		
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
}
