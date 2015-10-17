package gldata;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

import glMath.Vec2;
import glMath.Vec3;
import glMath.Vec4;

import glMath.Mat2;
import glMath.Mat3;
import glMath.Mat4;

public class VertexArray {

	private int vaoId;
	private BufferObject vertexBuffer, indices;
	private ArrayList<VertexAttrib> attributes;
	private boolean finished;
	
	public VertexArray(){
		vaoId = glGenVertexArrays();
		vertexBuffer = new BufferObject(GL_ARRAY_BUFFER);
		indices = new BufferObject(GL_ELEMENT_ARRAY_BUFFER);
		attributes = new ArrayList<VertexAttrib>();
		finished = false;
	}
	
	/**
	 * Finalizes this vertex array by uploading buffers to the GPU and setting attribute data
	 * 
	 * @param bufferUsage GLenum determining how the vertex buffer is to be used
	 * @param indicesUsage GLenum determining how the index buffer is to be used
	 */
	public void finalize(int bufferUsage, int indicesUsage){
		
	}
	
	/**
	 * Deletes this vertex array and its associated buffers
	 */
	public void delete(){
		glDeleteVertexArrays(vaoId);
		vertexBuffer.delete();
		indices.delete();
	}
	
	/**
	 * Adds an attribute definition for this vertex array, the vertex attribute index being set is the previous index set offset by 1,
	 * the index will also be further offset if the previous index set was of a matrix type
	 * 
	 * @param type
	 * @param normalize
	 * @param divisor
	 */
	public void addAttrib(AttribType type, boolean normalize, int divisor){
		
	}
	
	public void bind(){
		
	}
	
	public void unbind(){
		
	}

	public void add(float value){
		
	}
	
	public void add(double value){
		
	}
	
	public void add(byte value){
		
	}
	
	public void add(short value){
		
	}
	
	public void add(int value){
		
	}

	public void add(Vec2 value){
		
	}
	
	public void add(Vec3 value){
		
	}
	
	public void add(Vec4 value){
		
	}

	public void add(Mat2 value){
		
	}
	
	public void add(Mat3 value){
		
	}
	
	public void add(Mat4 value){
		
	}

	public void addIndex(int value){
		
	}
	

	protected class VertexAttrib {
	
		public AttribType type;
		public boolean normalize;
		public int divisor;
		
		public VertexAttrib(AttribType type, boolean normalize, int divisor){
			this.type = type;
			this.normalize = normalize;
			this.divisor = divisor;
		}
	}
}
