package glMath.matrices;

import glMath.vectors.Vector;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * 
 * @author Kevin Mango
 *
 */
public interface Matrix {
	
	/**
	 * Inverts this matrix only if the matrix is invertible 
	 * 
	 * @return This matrix post operation
	 */
	public Matrix invert();
	
	/**
	 * Inverts this matrix only if the matrix is invertible, this function does not 
	 * alter the original matrix
	 * 
	 * @return A copy of this matrix post operation
	 */
	public Matrix inverse();
	
	/**
	 * Computes the determinant of this matrix
	 * 
	 * @return The determinant of this matrix
	 */
	public float determinant();
	
	/**
	 * Transposes this matrix
	 * 
	 * @return This matrix post operation
	 */
	public Matrix transpose();
	
	/**
	 * Adds the given matrix to this matrix
	 * 
	 * @param rhs Matrix to add from
	 * @return This matrix post operation
	 */
	public Matrix add(Matrix rhs);
	
	/**
	 * Subtracts the given matrix from this matrix
	 * 
	 * @param rhs Matrix to subtract with
	 * @return This matrix post operation
	 */
	public Matrix subtract(Matrix rhs);
	
	/**
	 * Sets this matrix to the identity matrix
	 * 
	 * @return This matrix post operation
	 */
	public Matrix loadIdentity();
	
	/**
	 * Multiplies this matrix by some scalar 
	 * 
	 * @param factor Scalar to multiply into the matrix
	 * @return This matrix post operation
	 */
	public Matrix multFactor(float factor);
	
	/**
	 * Multiplies a Vector by this matrix returning the resulting vector after multiplication
	 * 
	 * @param vec Vector to be multiplied by this matrix
	 * @return The resulting vector after multiplication of the vector by this matrix
	 */
	public Vector multVec(Vector vec);
	
	/**
	 * Multiplies this matrix by the given matrix, storing the result in this matrix
	 * 
	 * @param rhs Matrix to concatenate to this matrix
	 * @return This matrix post operation
	 */
	public Matrix multiply(Matrix rhs);
	
	/**
	 * Multiplies the given matrix on the left side of this matrix, storing the result in this matrix
	 * 
	 * @param lhs Matrix to multiply with this one
	 * @return This matrix post operation
	 */
	public Matrix leftMult(Matrix lhs);
	
	/**
	 * Orthogonalize this matrix
	 * 
	 * @return This matrix post operation
	 */
	//public Matrix orthogonalize();
	
	/**
	 * Gets the column of this matrix at the specified index
	 * 
	 * @param index Index of the column to retrieve from the matrix
	 * @return The vector representing the column at index in this matrix
	 */
	public Vector col(int index);

	/**
	 * Gets the value at the specified index in the Matrix. Matrix indices are defined such that 0 specifies the 
	 * element at the top of the first column, from there all values are enumerated moving down a column.
	 * <p>
	 * Mat3 example:
	 * <br>
	 * 0 3 6
	 * 1 4 7
	 * 2 5 8
	 * </p>
	 * 
	 * @param index Index of the element to retrieve
	 * @return Value of the element at the specified index
	 * @throws IndexOutOfBoundsException
	 */
	public float valueAt(int index) throws IndexOutOfBoundsException;
	
	/**
	 * Sets the column of this matrix at index to the given Vector
	 * 
	 * @param index Index of the column to be changed
 	 * @param column Vector to set this matrix column to
	 */
	public void setColumn(int index, Vector column);
	
	/**
	 * Sets this matrix to the given matrix
	 * 
	 * @param mat Matrix to store into this matrix
	 */
	public void setMatrix(Matrix mat);
	
	/**
	 * Sets the value of a single position in this matrix
	 * 
	 * @param col Column to change
	 * @param row Row of to change
	 * @param value Sets Matrix[col][row] to value
	 */
	public void setValueAt(int col, int row, float value);
	
	/**
	 * Retrieves the underlying matrix that stores this matrix
	 * 
	 * @return Vector array used to store this matrix
	 */
	public Vector[] getMatrix();

	/**
	 * Stores this matrix into a buffer 
	 * 
	 * @param storage Float buffer to store into 
	 */
	public void store(FloatBuffer storage);
	
	/**
	 * Stores this matrix into a buffer 
	 * 
	 * @param storage Byte buffer to store into 
	 */
	public void store(ByteBuffer storage);
	
	/**
	 * Returns this matrix as a column major ordered FloatBuffer
	 * 
	 * @return This matrix as a column major ordered FloatBuffer
	 */
	public FloatBuffer asFloatBuffer();
	
	/**
	 * Returns this matrix as a column major ordered ByteBuffer
	 * 
	 * @return This matrix as a column major ordered ByteBuffer
	 */
	public ByteBuffer asByteBuffer();
	
	/**
	 * Prints the matrix in a readable format to the terminal
	 */
	public void print();
	
	/**
	 * Computes the trace of the matrix
	 * 
	 * @return The trace of this matrix
	 */
	public float trace();
	
	/**
	 * Rounds near zero values in this matrix to zero
	 */
	public void trunc();
	
	/**
	 * Orthonormalize this matrix using the first column of the matrix as the base vector
	 */
	public void orthonormalize();
}
