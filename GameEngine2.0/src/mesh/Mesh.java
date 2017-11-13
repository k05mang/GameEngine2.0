package mesh;

import static org.lwjgl.opengl.GL11.glDrawElements;
import glMath.matrices.Mat4;
import gldata.BufferObject;
import gldata.IndexBuffer;
import gldata.VertexArray;

import java.util.ArrayList;

import core.Resource;
import core.SpatialAsset;
import core.managers.SceneManager;

public abstract class Mesh implements Resource{
	protected Geometry geometry;
	protected VertexArray vao;
	public static final String SOLID_MODE = "solid", EDGE_MODE = "edges";
	protected static final String DEFAULT_VBO = "default";
	
	/**
	 * Constructs a Mesh with a VertexArray, Mesh, Transform, and array of BufferObjects and IndexBuffers
	 */
	public Mesh(){
		vao = new VertexArray();
		geometry = new Geometry();
	}
	
	/**
	 * Creates a copy of the given Mesh.
	 * <p>
	 * This Mesh will share the underlying VertexArray object as well as the Mesh object. Caution should be taken as this
	 * can lead to errors when the delete function is called since the shared VertexArray will be deleted.
	 * 
	 * The Transform object for this Mesh will be a copy of the Transform for the given Mesh and will not be shared.
	 * </p>
	 * 
	 * @param copy Mesh to copy from
	 */
	public Mesh(Mesh copy){
		vao = copy.vao;
		geometry = copy.geometry;//new Geometry(copy.geometry);
//		material = new String(copy.material);
	}
	
	/**
	 * Gets the geometry associated with this Mesh
	 * 
	 * @return The geometry used by this Mesh
	 */
	public Geometry getGeometry(){
		return geometry;
	}
	
	/**
	 * Gets the number of vertices associated with this Mesh
	 * 
	 * @return Number of vertices in this renderable's mesh
	 */
	public int getNumVertices(){
		return geometry.numVertices();
	}

	/**
	 * Gets the number of faces associated with this Mesh
	 * 
	 * @return Number of faces in this renderable's mesh
	 */
	public int getNumFaces(){
		return geometry.numFaces();
	}
	
	@Override
	public void delete(){
		vao.delete();
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
	 * Sets the mode with which this Mesh will render. Only modes that the mesh supports may be passed to this function.
	 * Modes that are guaranteed to be supported by this function are {@code SOLID_MODE} and {@code EDGE_MODE} as defined
	 * in this class.
	 * 
	 * @param mode Mode to render the mesh with
	 */
	public void setRenderMode(String mode){
		vao.setIndexBuffer(mode);
	}
}
