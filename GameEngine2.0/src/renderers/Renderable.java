package renderers;

import primitives.Mesh;
import glMath.Mat4;
import glMath.Transform;
import gldata.VertexArray;

public abstract class Renderable {
	private Transform transforms;
	private Mesh mesh;
	private VertexArray vao;
	
	public Renderable(RenderMode... modes){
		vao = null;
		mesh = new Mesh();
		transforms = new Transform();
	}
	
	public void transform(Transform transform){
		transforms.transform(transform);
	}
	
	public Mat4 getModelView(){
		return transforms.getTransform();
	}
	
	public VertexArray getVAO(){
		return vao;
	}
	
	public Mesh getMesh(){
		return mesh;
	}
}
