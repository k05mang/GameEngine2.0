package primitives.geometry;

import java.util.HashMap;

import primitives.Face;
import primitives.Vertex;
import glMath.VecUtil;
import glMath.vectors.Vec2;
import glMath.vectors.Vec3;
import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import renderers.RenderMode;
import renderers.Renderable;

public final class Icosphere extends Renderable {
	private float radius;
	
	/**
	 * Constructs an icosphere, which is defined to be a an icosahedron subdivided where all points are equadistant from the center.
	 * The sphere will have the given radius, and will have a the given order of subdivisions making it more refined. The sphere will 
	 * also be compatible with the given RenderModes.
	 * 
	 * Order cannot be less than 0, any value less than 0 will be defaulted to 0. Additionally care should be taken when specifying 
	 * the order of the sphere subdivisions as the number of vertices grows exponentially with the number of vertices being equal to
	 * 12+(2^(order*2)-1)*10. Orders 5 and less are generally sufficient to create a smooth sphere.
	 * 
	 * @param radius Radius of the sphere
	 * @param order Order of magnitude of recursive subdivisions
	 * @param modes RenderModes this Icosphere should be compatible with, the first mode is the initial mode
	 * for the Icosphere to render with
	 */
	public Icosphere(float radius, int order, RenderMode... modes){
		super();
		this.radius = radius;
		int clampedOrder = Math.max(0,  order);
		int lastIndex = 11+((1 << (clampedOrder << 1))-1)*10;//11+(2^(clampedOrder*2)-1)*10
		
		IndexBuffer.IndexType dataType = getIndexType(lastIndex);
		
		BufferObject vbo = new BufferObject(BufferType.ARRAY);
		vbos.add(vbo);
		
		//approximate golden ratio 
		float goldenRatio = (float)(1.0+Math.sqrt(5.0))/2.0f;
		//get the length of the vectors formed by this method of generating the 12 starting vertices
		float length = (float)Math.sqrt(1+goldenRatio*goldenRatio);
		
		//use the cyclic permutation of (0, +/-1, +/-phi)
		//since values need to be normalized then scaled by the radius store the two non-zero values separately after being changed
		
		//represents the 1 of the permutation for an icosphere with edge length 2 
		float one = radius/length;
		//represents the phi in the equation above
		float phi = radius*goldenRatio/length;

		//phi > one
		HashMap<Vertex, Integer> vertMap = new HashMap<Vertex, Integer>();//map to quickly find the index of a vertex when we know the vertex
		//yz plane rectangle vertices
		Vertex point0 = new Vertex(0,one,phi, 0,one,phi, 0,0);//+y,+z
		mesh.add(point0);
		point0.addTo(vbo);
		vertMap.put(point0, 0);
		
		Vertex point1 = new Vertex(0,-one,phi, 0,-one,phi, 0,0);//-y,+z
		mesh.add(point1);
		point1.addTo(vbo);
		vertMap.put(point1, 1);
		
		Vertex point2 = new Vertex(0,one,-phi, 0,one,-phi, 0,0);//+y,-z
		mesh.add(point2);
		point2.addTo(vbo);
		vertMap.put(point1, 1);
		
		Vertex point3 = new Vertex(0,-one,-phi, 0,-one,-phi, 0,0);//-y,-z
		mesh.add(point3);
		point3.addTo(vbo);
		vertMap.put(point3, 3);
		
		//xy plane rectangle vertices
		Vertex point4 = new Vertex(one,phi,0, one,phi,0, 0,0);//+x,+y
		mesh.add(point4);
		point4.addTo(vbo);
		vertMap.put(point4, 4);
		
		Vertex point5 = new Vertex(-one,phi,0, -one,phi,0, 0,0);//-x,+y
		mesh.add(point5);
		point5.addTo(vbo);
		vertMap.put(point5, 5);
		
		Vertex point6 = new Vertex(one,-phi,0, one,-phi,0, 0,0);//+x,-y
		mesh.add(point6);
		point6.addTo(vbo);
		vertMap.put(point6, 6);
		
		Vertex point7 = new Vertex(-one,-phi,0, -one,-phi,0, 0,0);//-x,-y
		mesh.add(point7);
		point7.addTo(vbo);
		vertMap.put(point7, 7);
		
		//xz plane rectangle vertices
		Vertex point8 = new Vertex(phi,0,one, phi,0,one, 0,0);//+z,+x
		mesh.add(point8);
		point8.addTo(vbo);
		vertMap.put(point8, 8);
		
		Vertex point9 = new Vertex(phi,0,-one, phi,0,-one, 0,0);//-z,+x
		mesh.add(point9);
		point9.addTo(vbo);
		vertMap.put(point9, 9);
		
		Vertex point10 = new Vertex(-phi,0,one, -phi,0,one, 0,0);//+z,-x
		mesh.add(point10);
		point10.addTo(vbo);
		vertMap.put(point10, 10);
		
		Vertex point11 = new Vertex(-phi,0,-one, -phi,0,-one, 0,0);//-z,-x
		mesh.add(point11);
		point11.addTo(vbo);
		vertMap.put(point11, 11);
		
		//specify faces, when considering what portion is being computed consider the icosahedron from the positive z looking down the negative z, with positive y being up
		//pair of faces connected with top most positive y vertices
		subdivide(new Face(4,5,0), clampedOrder, vertMap);//front half
		subdivide(new Face(5,4,2), clampedOrder, vertMap);//back half
		
		//pair of faces connected with bottom most negative y vertices
		subdivide(new Face(7,6,1), clampedOrder, vertMap);//front half
		subdivide(new Face(6,7,3), clampedOrder, vertMap);//back half
		
		//pair of faces connected with right most positive x vertices
		subdivide(new Face(8,9,4), clampedOrder, vertMap);//top half
		subdivide(new Face(9,8,6), clampedOrder, vertMap);//bottom half
		
		//pair of faces connected with left most negative x vertices
		subdivide(new Face(11,10,5), clampedOrder, vertMap);//top half
		subdivide(new Face(10,11,7), clampedOrder, vertMap);//bottom half
		
		//pair of faces connected with front most positive z vertices
		subdivide(new Face(0,1,8), clampedOrder, vertMap);//right half
		subdivide(new Face(1,0,10), clampedOrder, vertMap);//left half
		
		//pair of faces connected with back most negative z vertices
		subdivide(new Face(3,2,9), clampedOrder, vertMap);//right half
		subdivide(new Face(2,3,11), clampedOrder, vertMap);//left half
		
		//positive z corners
		subdivide(new Face(0,5,10), clampedOrder, vertMap);//top left
		subdivide(new Face(0,8,4), clampedOrder, vertMap);//top right
		subdivide(new Face(1,6,8), clampedOrder, vertMap);//bottom right
		subdivide(new Face(1,10,7), clampedOrder, vertMap);//bottom left
		
		//negative z corners
		subdivide(new Face(2,11,5), clampedOrder, vertMap);//top left
		subdivide(new Face(2,4,9), clampedOrder, vertMap);//top right
		subdivide(new Face(3,9,6), clampedOrder, vertMap);//bottom right
		subdivide(new Face(3,7,11), clampedOrder, vertMap);//bottom left

		vbo.flush(BufferUsage.STATIC_DRAW);
		vao.addVertexBuffer("default", vbo);
		
		IndexBuffer indices = new IndexBuffer(dataType);
		ibos.add(indices);
		indices.flush(BufferUsage.STATIC_DRAW);
		
		//check if there are additional modes that need to be accounted for
		if(modes.length > 0){
			for(RenderMode curMode : modes){
				IndexBuffer modeBuffer = new IndexBuffer(dataType);
				mesh.insertIndices(modeBuffer, curMode);//add indices to match the mode
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
	 * Recursive subdivision function for subdividing the faces of the icosphere to the given order
	 * 
	 * @param base Face to subdivide on this call
	 * @param order The current recursive depth of this call
	 * @param vertMap Map relating Vertices to their index values, this allows for more efficient use of 
	 * memory on the GPU by reducing vertex redundancy
	 */
	private void subdivide(Face base, int order, HashMap<Vertex, Integer> vertMap){
		//if we are at the lowest order then add the face to the mesh
		if(order == 0){
			mesh.add(base);
		}else{//otherwise subdivide it and recurse
			
			//keep a general UV value for reuse
			Vec2 uv = new Vec2();
			
			Vec3 halfPoint = genVert(base.he1.sourceVert, base.he2.sourceVert);
			Vertex edge1 = new Vertex(halfPoint, halfPoint, uv);
			
			halfPoint = genVert(base.he2.sourceVert, base.he3.sourceVert);
			Vertex edge2 = new Vertex(halfPoint, halfPoint, uv);
			
			halfPoint = genVert(base.he3.sourceVert, base.he1.sourceVert);
			Vertex edge3 = new Vertex(halfPoint, halfPoint, uv);
			
			//add the new vertices to the mesh and the vert map if they ahven't been added already
			if(vertMap.get(edge1) == null){
				edge1.addTo(vbos.get(0));
				vertMap.put(edge1, mesh.getNumVertices());
				mesh.add(edge1);
			}
			
			if(vertMap.get(edge2) == null){
				edge2.addTo(vbos.get(0));
				vertMap.put(edge2, mesh.getNumVertices());
				mesh.add(edge2);
			}
			
			if(vertMap.get(edge3) == null){
				edge3.addTo(vbos.get(0));
				vertMap.put(edge3, mesh.getNumVertices());
				mesh.add(edge3);
			}
			
			//generate the faces
			Face top = new Face(base.he1.sourceVert, vertMap.get(edge1), vertMap.get(edge3));
			Face left = new Face(base.he2.sourceVert, vertMap.get(edge2), vertMap.get(edge1));
			Face right = new Face(base.he3.sourceVert, vertMap.get(edge3), vertMap.get(edge2));
			Face middle = new Face(vertMap.get(edge1), vertMap.get(edge2), vertMap.get(edge3));

			//recurse with a lower order
			subdivide(top, order-1, vertMap);
			subdivide(left, order-1, vertMap);
			subdivide(right, order-1, vertMap);
			subdivide(middle, order-1, vertMap);
		}
	}
	
	/**
	 * Generates the halfway vertex on an edge of the mesh defined by the start and end indices,
	 * the returned vertex will be of length radius from the center.
	 * 
	 * @param start Starting index to sample from the mesh for a vertex on the current edge
	 * @param end Ending index to sample from the mesh for a vertex on the current edge
	 * @return Vertex whose position is halfway between the given edge and is of length
	 * radius from the center of the sphere
	 */
	private Vec3 genVert(int start, int end){
		//compute the new vertex based on the radius and the edge vertices
		Vec3 startVec = mesh.getVertex(start).getPos();
		Vec3 endVec = mesh.getVertex(end).getPos();
		Vec3 halfPoint = VecUtil.add(startVec, endVec);
		//scale the position to match the radius
		return halfPoint.scale(radius/halfPoint.length());
	}

	/**
	 * Constructs a copy of the given icosphere
	 * 
	 * Refer to {@link renderer.Renderable#Renderable(Renderable) Renderable's copy constructor} 
	 * for more information about cautions with the copy constructor
	 * 
	 * @param copy Icosphere to copy
	 */
	public Icosphere(Icosphere copy){
		super(copy);
		radius = copy.radius;
	}

	/**
	 * Gets the radius of this icosphere
	 * 
	 * @return Radius of this icosphere
	 */
	public float getRadius(){
		return radius;
	}
	
	@Override
	public void addMode(RenderMode mode) {
		// TODO Auto-generated method stub

	}

}
