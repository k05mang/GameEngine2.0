package mesh.primitives.geometry;

import static java.lang.Math.PI;
import static java.lang.Math.sin;
import static java.lang.Math.cos;


import mesh.Mesh;
import mesh.primitives.Triangle;
import mesh.primitives.Vertex;
import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import renderers.RenderMode;

public final class Sphere extends Mesh{
	private float radius;
	public static final String 
	LATERAL_MODE = "lateral",
	ORBITAL_MODE = "orbit";

	/**
	 * Constructs a sphere primitive with the given {@code radius}, horizontal and vertical {@code divisions}. 
	 * The default mode for the sphere is set to SOLID_MODE.
	 * 
	 * @param radius Radius of the sphere
	 * @param divisions Number of vertical and horizontal segments
	 */
	public Sphere(float radius, int divisions){
		this(radius, divisions, divisions, SOLID_MODE);
	}
	
	/**
	 * Constructs a sphere primitive with the given {@code radius}, horizontal {@code stacks}, and vertical {@code slices}. 
	 * The default mode for the sphere is set to SOLID_MODE.
	 * 
	 * Slices must be greater than 2, if the value is less than 3 a default value of 3 will be used instead.
	 * 
	 * Stacks must be greater than 0, if the value given is less than 1 then a default value of 1 will be used instead.
	 * 
	 * @param radius Radius of the sphere
	 * @param slices Number of vertical segments
	 * @param stacks Number of horizontal segments between the top tip and bottom tip of the sphere
	 */
	public Sphere(float radius, int slices, int stacks){
		this(radius, slices, stacks, SOLID_MODE);
	}
	
	/**
	 * Constructs a sphere primitive with the given {@code radius}, horizontal and vertical {@code divisions}. 
	 * {@code defaultMode} will specify the mode the mesh will initially render with. Selectable modes and what they entail 
	 * are as follows:
	 * <ul>
	 * <li>SOLID_MODE: The mesh will render as a GL_TRIANGLES</li>
	 * <li>EDGE_MODE: The mesh will render as GL_LINES, where only the major edges of the mesh are rendered. This will
	 * only render edges of the mesh that define its shape.</li>
	 * <li>LATERAL_MODE: The mesh will render as a GL_LINES, where only the edges that define the vertical slices are rendered</li>
	 * <li>ORBITAL_MODE: The mesh will render as a GL_LINES, where only the edges that define the horizontal stacks are rendered</li>
	 * </ul>
	 * 
	 * @param radius Radius of the sphere
	 * @param divisions Number of vertical and horizontal segments
	 * @param defaultMode Defines the mode to render the mesh with
	 */
	public Sphere(float radius, int divisions, String defaultMode){
		this(radius, divisions, divisions, defaultMode);
	}
	
	/**
	 * Constructs a sphere primitive with the given {@code radius}, horizontal {@code stacks}, and vertical {@code slices}. 
	 * {@code defaultMode} will specify the mode the mesh will initially render with. Selectable modes and what they entail 
	 * are as follows:
	 * <ul>
	 * <li>SOLID_MODE: The mesh will render as a GL_TRIANGLES</li>
	 * <li>EDGE_MODE: The mesh will render as GL_LINES, where only the major edges of the mesh are rendered. This will
	 * only render edges of the mesh that define its shape.</li>
	 * <li>LATERAL_MODE: The mesh will render as a GL_LINES, where only the edges that define the vertical slices are rendered</li>
	 * <li>ORBITAL_MODE: The mesh will render as a GL_LINES, where only the edges that define the horizontal stacks are rendered</li>
	 * </ul>
	 * 
	 * Slices must be greater than 2, if the value is less than 3 a default value of 3 will be used instead.
	 * 
	 * Stacks must be greater than 0, if the value given is less than 1 then a default value of 1 will be used instead.
	 * 
	 * @param radius Radius of the sphere
	 * @param slices Number of vertical segments
	 * @param stacks Number of horizontal segments between the top tip and bottom tip of the sphere
	 * @param defaultMode Defines the mode to render the mesh with
	 */
	public Sphere(float radius, int slices, int stacks, String defaultMode){
		super();
		
		int maxSlice = Math.max(3, slices);
		int maxStack = Math.max(1, stacks);
		this.radius = Math.abs(radius);
		
		IndexBuffer.IndexType dataType = getIndexType((maxSlice+1)*(maxStack+1));

		IndexBuffer solidIbo = new IndexBuffer(dataType);
		IndexBuffer edgeIbo = new IndexBuffer(dataType);
		IndexBuffer latIbo = new IndexBuffer(dataType);
		IndexBuffer orbIbo = new IndexBuffer(dataType);
		
		ibos.add(solidIbo);
		ibos.add(edgeIbo);
		ibos.add(latIbo);
		ibos.add(orbIbo);

		vao.addIndexBuffer(SOLID_MODE, RenderMode.TRIANGLES, solidIbo);
		vao.addIndexBuffer(EDGE_MODE, RenderMode.LINES, edgeIbo);
		vao.addIndexBuffer(LATERAL_MODE, RenderMode.LINES, latIbo);
		vao.addIndexBuffer(ORBITAL_MODE, RenderMode.LINES, orbIbo);
		
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
				
				int curIndex = 0;//current index
				int nextStackIndex = 0;//same slice next stack
				
				if(curStack == 0 && curSlice < maxSlice){
					float uvOffset = .5f/maxSlice;
					nextStackIndex = maxSlice+curSlice;//same slice next stack
					Vertex vert = new Vertex(x,y,z, x,y,z, 1-u-uvOffset, 1-v);
					geometry.add(vert);
					
					Triangle top = new Triangle(
							curSlice,//current vertex index
							nextStackIndex+1,//next slice of the next stack 
							nextStackIndex//current slice of the next stack
							);
					geometry.add(top);
					
					//add indices to index buffers
					top.insertPrim(solidIbo);
					
					//edge ibo
					edgeIbo.add(curSlice);
					edgeIbo.add(nextStackIndex);
					
					//lateral ibo
					latIbo.add(curSlice);
					latIbo.add(nextStackIndex);
					
					//orbital ibo indices not needed for caps
					
				}else if(curStack == maxStack && curSlice < maxSlice){
					float uvOffset = .5f/maxSlice;
					curIndex = maxSlice+(curStack-2)*(maxSlice+1)+curSlice;//current index
					nextStackIndex = maxSlice+(curStack-1)*(maxSlice+1)+curSlice;//same slice next stack
					Vertex vert = new Vertex(x,y,z, x,y,z, 1-u-uvOffset, 1-v);
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
					
					//lateral ibo
					latIbo.add(curIndex);
					latIbo.add(nextStackIndex);
					
					//orbital ibo indices not needed for caps
					
				}else if(curStack > 0 && curStack < maxStack){
					curIndex = maxSlice+(curStack-1)*(maxSlice+1)+curSlice;//current index
					nextStackIndex = maxSlice+(curStack)*(maxSlice+1)+curSlice;//same slice next stack
					Vertex vert = new Vertex(x,y,z, x,y,z, 1-u, 1-v);
					geometry.add(vert);
					if(curSlice < maxSlice && curStack < maxStack-1){
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
						
						//lateral ibo
						latIbo.add(curIndex);
						latIbo.add(nextStackIndex);
						
						//orbital ibo
						orbIbo.add(curIndex+1);
						orbIbo.add(curIndex);
					}
				}
			}
		}
		geometry.genTangentBitangent();
		geometry.insertVertices(vbo);
		
		vbo.flush(BufferUsage.STATIC_DRAW);
		vao.addVertexBuffer("default", vbo);
		
		//buffer the index buffers to the gpu
		solidIbo.flush(BufferUsage.STATIC_DRAW);
		edgeIbo.flush(BufferUsage.STATIC_DRAW);
		latIbo.flush(BufferUsage.STATIC_DRAW);
		orbIbo.flush(BufferUsage.STATIC_DRAW);
		
		if(
			defaultMode.equals(SOLID_MODE) ||
			defaultMode.equals(EDGE_MODE) ||
			defaultMode.equals(LATERAL_MODE) ||
			defaultMode.equals(ORBITAL_MODE)
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
	 * Constructs a sphere that is the copy of the given sphere.
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
