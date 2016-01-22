package mesh.primitives.geometry;

import static java.lang.Math.PI;
import static java.lang.Math.sin;
import static java.lang.Math.cos;


import mesh.Mesh;
import mesh.primitives.Face;
import mesh.primitives.Vertex;
import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import renderers.RenderMode;

public final class Sphere extends Mesh{
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
		this.radius = Math.abs(radius);
		
		BufferObject vbo = new BufferObject(BufferType.ARRAY);
		vbos.add(vbo);
		
		for(int curStack = 0; curStack < maxStack+1; curStack++){
			for(int curSlice = 0; curSlice < maxSlice+1; curSlice++){
				
				float u = curSlice/(float)maxSlice;
				float v = curStack/(float)maxStack;
				//pre-calculate the angles for the trig functions
				double phi = PI*v;
				double theta = 2*PI*u;
				
				float x = (float)( cos(theta)*sin(phi) );
				float y = (float)( cos(phi) );//since y is the up axis have it use the conventional z calculation
				float z = (float)( sin(theta)*sin(phi) );
				
				int curIndex = 0;//current index
				int nextStackIndex = 0;//same slice next stack
				
				if(curStack == 0 && curSlice < maxSlice){
					float uvOffset = .5f/maxSlice;
					nextStackIndex = maxSlice+curSlice;//same slice next stack
					Vertex vert = new Vertex(x,y,z, x,y,z, 1-u-uvOffset, 1-v);
					geometry.add(vert);
					
					geometry.add(new Face(
						curSlice,//current vertex index
						nextStackIndex+1,//next slice of the next stack 
						nextStackIndex//current slice of the next stack
						));
				}else if(curStack == maxStack && curSlice < maxSlice){
					float uvOffset = .5f/maxSlice;
					curIndex = maxSlice+(curStack-2)*(maxSlice+1)+curSlice;//current index
					nextStackIndex = maxSlice+(curStack-1)*(maxSlice+1)+curSlice;//same slice next stack
					Vertex vert = new Vertex(x,y,z, x,y,z, 1-u-uvOffset, 1-v);
					geometry.add(vert);
					
					geometry.add(new Face(
						curIndex,//current vertex index
						curIndex+1,//next index of current stack
						nextStackIndex//next stack next slice
						));
				}else if(curStack > 0 && curStack < maxStack){
					curIndex = maxSlice+(curStack-1)*(maxSlice+1)+curSlice;//current index
					nextStackIndex = maxSlice+(curStack)*(maxSlice+1)+curSlice;//same slice next stack
					Vertex vert = new Vertex(x,y,z, x,y,z, 1-u, 1-v);
					geometry.add(vert);
					if(curSlice < maxSlice && curStack < maxStack-1){
						geometry.add(new Face(
							curIndex,//current vertex index
							nextStackIndex+1,//next slice of the next stack 
							nextStackIndex//current slice of the next stack
							));
						geometry.add(new Face(
							curIndex,//current vertex index
							curIndex+1,//next index of current stack
							nextStackIndex+1//next stack next slice
							));
					}
				}
			}
		}
		geometry.genTangentBitangent();
		geometry.insertVertices(vbo);
		transforms.scale(this.radius, this.radius, this.radius);
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
	 * Refer to {@link renderer.Mesh#Renderable(Mesh) Renderable's copy constructor} 
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
