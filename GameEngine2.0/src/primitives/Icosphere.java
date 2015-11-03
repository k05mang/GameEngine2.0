package primitives;

import glMath.Vec3;
import gldata.AttribType;
import gldata.BufferUsage;
import gldata.IndexBuffer;
import gldata.VertexArray;
import renderers.RenderMode;
import renderers.Renderable;

public class Icosphere extends Renderable {
	private float radius;
	
	public Icosphere(float radius, int order, RenderMode... modes){
		super();
		this.radius = radius;
		
		int maxIndex = 12;
		IndexBuffer.IndexType dataType = null;
		
		//determine what data type the index buffer should be
		if(maxIndex-1 < Byte.MAX_VALUE){
			dataType = IndexBuffer.IndexType.BYTE;
		}else if(maxIndex-1 < Short.MAX_VALUE){
			dataType = IndexBuffer.IndexType.SHORT;
		}else if(maxIndex-1 < Integer.MAX_VALUE){
			dataType = IndexBuffer.IndexType.INT;
		}else{
			//TODO handle when the number of vertices and indices would exceed the max value
		}
		//instantiate the vertex array
		vao = new VertexArray(modes[0], dataType);
		
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
		
		//yz plane rectangle vertices
		Vertex point0 = new Vertex(0,one,phi, 0,one,phi, 0,0);//+y,+z
		mesh.add(point0);
		point0.addTo(vao);
		Vertex point1 = new Vertex(0,-one,phi, 0,-one,phi, 0,0);//-y,+z
		mesh.add(point1);
		point1.addTo(vao);
		Vertex point2 = new Vertex(0,one,-phi, 0,one,-phi, 0,0);//+y,-z
		mesh.add(point2);
		point2.addTo(vao);
		Vertex point3 = new Vertex(0,-one,-phi, 0,-one,-phi, 0,0);//-y,-z
		mesh.add(point3);
		point3.addTo(vao);
		//xy plane rectangle vertices
		Vertex point4 = new Vertex(one,phi,0, one,phi,0, 0,0);//+x,+y
		mesh.add(point4);
		point4.addTo(vao);
		Vertex point5 = new Vertex(-one,phi,0, -one,phi,0, 0,0);//-x,+y
		mesh.add(point5);
		point5.addTo(vao);
		Vertex point6 = new Vertex(one,-phi,0, one,-phi,0, 0,0);//+x,-y
		mesh.add(point6);
		point6.addTo(vao);
		Vertex point7 = new Vertex(-one,-phi,0, -one,-phi,0, 0,0);//-x,-y
		mesh.add(point7);
		point7.addTo(vao);
		//xz plane rectangle vertices
		Vertex point8 = new Vertex(phi,0,one, phi,0,one, 0,0);//+z,+x
		mesh.add(point8);
		point8.addTo(vao);
		Vertex point9 = new Vertex(phi,0,-one, phi,0,-one, 0,0);//-z,+x
		mesh.add(point9);
		point9.addTo(vao);
		Vertex point10 = new Vertex(-phi,0,one, -phi,0,one, 0,0);//+z,-x
		mesh.add(point10);
		point10.addTo(vao);
		Vertex point11 = new Vertex(-phi,0,-one, -phi,0,-one, 0,0);//-z,-x
		mesh.add(point11);
		point11.addTo(vao);
		
		//specify faces, when considering what portion is being computed consider the icosahedron from the positive z looking down the negative z
		//pair of faces connected with top most positive y vertices
		mesh.add(new Face(4,5,0));//front half
		mesh.add(new Face(5,4,2));//back half
		
		//pair of faces connected with bottom most negative y vertices
		mesh.add(new Face(7,6,1));//front half
		mesh.add(new Face(6,7,3));//back half
		
		//pair of faces connected with right most positive x vertices
		mesh.add(new Face(8,9,4));//top half
		mesh.add(new Face(9,8,6));//bottom half
		
		//pair of faces connected with left most negative x vertices
		mesh.add(new Face(11,10,5));//top half
		mesh.add(new Face(10,11,7));//bottom half
		
		//pair of faces connected with front most positive z vertices
		mesh.add(new Face(0,1,8));//right half
		mesh.add(new Face(1,0,10));//left half
		
		//pair of faces connected with back most negative z vertices
		mesh.add(new Face(3,2,9));//right half
		mesh.add(new Face(2,3,11));//left half
		
		//positive z corners
		mesh.add(new Face(0,5,10));//top left
		mesh.add(new Face(0,8,4));//top right
		mesh.add(new Face(1,6,8));//bottom right
		mesh.add(new Face(1,10,7));//bottom left
		
		//negative z corners
		mesh.add(new Face(2,11,5));//top left
		mesh.add(new Face(2,4,9));//top right
		mesh.add(new Face(3,9,6));//bottom right
		mesh.add(new Face(3,7,11));//bottom left
		
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
	
	public Icosphere(Icosphere copy){
		super(copy);
		radius = copy.radius;
	}
	
	public float getRadius(){
		return radius;
	}
	
	@Override
	public void addMode(RenderMode mode) {
		// TODO Auto-generated method stub

	}

}
