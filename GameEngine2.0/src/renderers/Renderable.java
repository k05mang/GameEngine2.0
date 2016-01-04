package renderers;

import java.util.ArrayList;

import primitives.Mesh;
import glMath.Transform;
import glMath.matrices.Mat4;
import gldata.VertexArray;
import gldata.BufferObject;
import gldata.IndexBuffer;

public abstract class Renderable {
	protected Transform transforms;
	protected Mesh mesh;
	protected VertexArray vao;
	protected ArrayList<BufferObject> vbos;
	protected ArrayList<IndexBuffer> ibos;
	
	/**
	 * Constructs a Renderable with a VertexArray, Mesh, Transform, and array of BufferObjects and IndexBuffers
	 */
	public Renderable(){
		vao = new VertexArray();
		mesh = new Mesh();
		transforms = new Transform();
		vbos = new ArrayList<BufferObject>();
		ibos = new ArrayList<IndexBuffer>();
	}
	
	/**
	 * Creates a copy of the given Renderable.
	 * <p>
	 * This Renderable will share the underlying VertexArray object as well as the Mesh object. Caution should be taken as this
	 * can lead to errors when the delete function is called since the shared VertexArray will be deleted.
	 * 
	 * The Transform object for this Renderable will be a copy of the Transform for the given Renderable and will not be shared.
	 * </p>
	 * @param copy
	 */
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
	
	/**
	 * Deletes all data from the GPU related to this Renderable object
	 */
	public void delete(){
		vao.delete();
		for(BufferObject buffer : vbos){
			buffer.delete();
		}
		for(IndexBuffer buffer : ibos){
			buffer.delete();
		}
	}
	
	/**
	 * Gets the IndexBuffer IndexType based on the given {@code size}, the value returned is to be passed to 
	 * an IndexBuffer to decide the IndexType of the buffer.
	 * 
	 * @param size Max index being passed to an index buffer
	 * @return IndexType for the given size
	 */
	protected IndexBuffer.IndexType getIndexType(int size){
		//determine what data type the index buffer should be
		if(size < Byte.MAX_VALUE){
			return IndexBuffer.IndexType.BYTE;
		}else if(size < Short.MAX_VALUE){
			return IndexBuffer.IndexType.SHORT;
		}else if(size < Integer.MAX_VALUE){
			return IndexBuffer.IndexType.INT;
		}else{
			return null;
		}
	}
}
