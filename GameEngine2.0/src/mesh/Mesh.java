package mesh;

import static org.lwjgl.opengl.GL11.glDrawElements;
import glMath.Transform;
import glMath.matrices.Mat4;
import gldata.BufferObject;
import gldata.IndexBuffer;
import gldata.VertexArray;

import java.util.ArrayList;

import core.Resource;
import core.SceneManager;

public abstract class Mesh implements Resource{
	protected Transform transforms;
	protected Geometry geometry;
	protected VertexArray vao;
	protected ArrayList<BufferObject> vbos;
	protected ArrayList<IndexBuffer> ibos;
	protected String material;
	
	/**
	 * Constructs a Renderable with a VertexArray, Mesh, Transform, and array of BufferObjects and IndexBuffers
	 */
	public Mesh(){
		vao = new VertexArray();
		geometry = new Geometry();
		transforms = new Transform();
		vbos = new ArrayList<BufferObject>();
		ibos = new ArrayList<IndexBuffer>();
		material = "default";
	}
	
	/**
	 * Creates a copy of the given Renderable.
	 * <p>
	 * This Renderable will share the underlying VertexArray object as well as the Mesh object. Caution should be taken as this
	 * can lead to errors when the delete function is called since the shared VertexArray will be deleted.
	 * 
	 * The Transform object for this Renderable will be a copy of the Transform for the given Renderable and will not be shared.
	 * </p>
	 * 
	 * @param copy Mesh to copy from
	 */
	public Mesh(Mesh copy){
		vao = copy.vao;
		geometry = copy.geometry;//new Geometry(copy.geometry);
		transforms = new Transform(copy.transforms);
		material = copy.material;
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
	 * Gets the mesh associated with this Renderable
	 * 
	 * @return The mesh used by this Renderable
	 */
	public Geometry getMesh(){
		return geometry;
	}
	
	/**
	 * Gets the number of vertices associated with this Renderable
	 * 
	 * @return Number of vertices in this renderable's mesh
	 */
	public int getNumVertices(){
		return geometry.getNumVertices();
	}

	/**
	 * Gets the number of faces associated with this Renderable
	 * 
	 * @return Number of faces in this renderable's mesh
	 */
	public int getNumFaces(){
		return geometry.getNumFaces();
	}
	
	/**
	 * Sets the IndexBuffer for this Renderable to use, this will decide the RenderMode for the Renderable as well
	 * 
	 * @param buffer IndexBuffer to use
	 * @return True if the IndexBuffer exists in this Renderable
	 */
	public boolean setIndexBuffer(String buffer){
		return vao.setIndexBuffer(buffer);
	}
	
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
	 * Sets this mesh's transformation to the given transformation
	 * 
	 * @param trans Transformation to set this mesh to
	 */
	public void setTransform(Transform trans){
		transforms.set(trans);
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
	
	/**
	 * Renders the mesh using render state information stored in the mesh
	 */
	public void render(){
		vao.bind();
		glDrawElements(vao.getRenderMode().mode, vao.getNumIndices(), vao.getIndexType().enumType, 0);
		vao.unbind();
	}
	
	/**
	 * Sets the material this mesh should use when rendering
	 * 
	 * @param material Material to use when rendering this mesh
	 */
	public void setMaterial(String material){
		if(SceneManager.materials.get(material) != null){
			this.material = material;
		}
	}
	
	/**
	 * Gets the material this mesh is currently using to render with
	 * 
	 * @return Id of the material this mesh is using to render with
	 */
	public String getMaterial(){
		return material;
	}
}