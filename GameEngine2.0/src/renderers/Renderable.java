package renderers;

import primitives.Mesh;
import glMath.Mat4;
import glMath.Transform;
import gldata.VertexArray;

public abstract class Renderable {
	protected Transform transforms;
	protected Mesh mesh;
	protected VertexArray vao;
	
	/**
	 * Constructs a Renderable object with a default Mesh, Transform, and RenderMode. Additionally the
	 * Renderable contains a VertexArray that is set to null in this constructor and should be instantiated
	 * in the derived class. This is allow better use of the VertexArray's default IndexBuffer, giving a derived
	 * class the flexibility of specifying a specific RenderMode and IndexType for the default buffer.
	 */
	public Renderable(){
		vao = null;
		mesh = new Mesh();
		transforms = new Transform();
	}
	
	public Renderable(Renderable copy){
		vao = copy.vao;
		mesh = copy.mesh;
		transforms = new Transform(copy.transforms);
	}
	
	/**
	 * Transforms this renderable's transform by the given Transform object
	 * 
	 * @param transform Transform to modify this renderable with
	 */
	public void transform(Transform transform){
		transforms.transform(transform);
	}
	
	/**
	 * Gets the matrix representation of this renderable's Transform class
	 * 
	 * @return Matrix of this renderable's Transformations
	 */
	public Mat4 getModelView(){
		return transforms.getTransform();
	}
	
	/**
	 * Gets this renderable's VertexArray used to render its mesh data to the opengl context
	 * 
	 * @return This renderable's VertexArray object
	 */
	public VertexArray getVAO(){
		return vao;
	}
	
	/**
	 * Gets the mesh associated with this Renderable
	 * 
	 * @return The mesh used by this Renderable
	 */
	public Mesh getMesh(){
		return mesh;
	}
	
	/**
	 * Gets the number of vertices associated with this Renderable
	 * 
	 * @return Number of vertices in this renderable's mesh
	 */
	public int getNumVertices(){
		return mesh.getNumVertices();
	}

	/**
	 * Gets the number of faces associated with this Renderable
	 * 
	 * @return Number of faces in this renderable's mesh
	 */
	public int getNumFaces(){
		return mesh.getNumFaces();
	}
	
	/**
	 * Sets the RenderMode for this renderable to use
	 * 
	 * @param mode Mode this renderable will render as 
	 * @return True if the renderable can render in the given mode, false if it cannot
	 */
	public boolean setRenderMode(RenderMode mode){
		return vao.setIndexBuffer(mode);
	}
	
	/**
	 * Adds a RenderMode to this renderable, after this function is called the Renderable should be able to render
	 * when passed the specified RenderMode
	 * 
	 * @param mode RenderMode to add to this Renderable
	 */
	public abstract void addMode(RenderMode mode);
}
