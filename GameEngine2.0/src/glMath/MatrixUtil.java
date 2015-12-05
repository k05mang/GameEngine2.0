package glMath;

import glMath.matrices.Mat2;
import glMath.matrices.Mat3;
import glMath.matrices.Mat4;
import glMath.vectors.Vec4;

public abstract class MatrixUtil {
	
	/**
	 * Gets the transpose of the given matrix as a separate matrix,
	 * the given matrix is unaltered
	 * 
	 * @param toTranspose Matrix whose transpose is requested
	 * @return A matrix that is the transpose of the given matrix
	 */
	public static Mat2 transpose(Mat2 toTranspose){
		return new Mat2(toTranspose).transpose();
	}

	/**
	 * Gets the transpose of the given matrix as a separate matrix,
	 * the given matrix is unaltered
	 * 
	 * @param toTranspose Matrix whose transpose is requested
	 * @return A matrix that is the transpose of the given matrix
	 */
	public static Mat3 transpose(Mat3 toTranspose){
		return new Mat3(toTranspose).transpose();
	}

	/**
	 * Gets the transpose of the given matrix as a separate matrix,
	 * the given matrix is unaltered
	 * 
	 * @param toTranspose Matrix whose transpose is requested
	 * @return A matrix that is the transpose of the given matrix
	 */
	public static Mat4 transpose(Mat4 toTranspose){
		return new Mat4(toTranspose).transpose();
	}
	
	/**
	 * Multiplies all the given matrices together and returns the result in a new matrix, the given
	 * matrices are multiplied from the farthest right matrix and are concatenated together moving
	 * left through the matrices
	 * 
	 * @param matrices Matrices to be multiplied together
	
	 * @return A new matrix of the same type as the ones given that contains the multiplication of 
	 * the given matrices
	 */
	public static Mat2 multiply(Mat2... matrices){
		//check if there are enough values in the array to multiply with
		if(matrices.length > 1){
			Mat2 result = new Mat2(1);
			for (int curMatrix = matrices.length - 1; curMatrix > -1; curMatrix--){
				result.leftMult(matrices[curMatrix]);
			}
			return result;
		}else{
			return matrices.length == 1 ? matrices[0] : null;
		}
	}
	
	/**
	 * Multiplies all the given matrices together and returns the result in a new matrix, the given
	 * matrices are multiplied from the farthest right matrix and are concatenated together moving
	 * left through the matrices
	 * 
	 * @param matrices Matrices to be multiplied together
	
	 * @return A new matrix of the same type as the ones given that contains the multiplication of 
	 * the given matrices
	 */
	public static Mat3 multiply(Mat3... matrices){
		//check if there are enough values in the array to multiply with
		if(matrices.length > 1){
			Mat3 result = new Mat3(1);
			for (int curMatrix = matrices.length - 1; curMatrix > -1; curMatrix--){
				result.leftMult(matrices[curMatrix]);
			}
			return result;
		}else{
			return matrices.length == 1 ? matrices[0] : null;
		}
	}
	
	/**
	 * Multiplies all the given matrices together and returns the result in a new matrix, the given
	 * matrices are multiplied from the farthest right matrix and are concatenated together moving
	 * left through the matrices
	 * 
	 * @param matrices Matrices to be multiplied together
	
	 * @return A new matrix of the same type as the ones given that contains the multiplication of 
	 * the given matrices
	 */
	public static Mat4 multiply(Mat4... matrices){
		//check if there are enough values in the array to multiply with
		if(matrices.length > 1){
			Mat4 result = new Mat4(1);
			for (int curMatrix = matrices.length - 1; curMatrix > -1; curMatrix--){
				result.leftMult(matrices[curMatrix]);
			}
			return result;
		}else{
			return matrices.length == 1 ? matrices[0] : null;
		}
	}

	/**
	 * Adds a list of matrices together and returns a new matrix that is the sum of the matrices
	 * the given matrices are read from left to right and added together
	 * 
	 * @param matrices Matrices to add together
	 * @return A new matrix containing the sum of the given matrices
	 */
	public static Mat2 add(Mat2... matrices){
		if(matrices.length > 1){
			Mat2 result = new Mat2(0);
			for (int curMatrix = 0; curMatrix < matrices.length; curMatrix++){
				result.add(matrices[curMatrix]);
			}
			return result;
		}else{
			return matrices.length == 1 ? matrices[0] : null;
		}
	}
	
	/**
	 * Adds a list of matrices together and returns a new matrix that is the sum of the matrices
	 * the given matrices are read from left to right and added together
	 * 
	 * @param matrices Matrices to add together
	 * @return A new matrix containing the sum of the given matrices
	 */
	public static Mat3 add(Mat3... matrices){
		if(matrices.length > 1){
			Mat3 result = new Mat3(0);
			for (int curMatrix = 0; curMatrix < matrices.length; curMatrix++){
				result.add(matrices[curMatrix]);
			}
			return result;
		}else{
			return matrices.length == 1 ? matrices[0] : null;
		}
	}
	
	/**
	 * Adds a list of matrices together and returns a new matrix that is the sum of the matrices
	 * the given matrices are read from left to right and added together
	 * 
	 * @param matrices Matrices to add together
	 * @return A new matrix containing the sum of the given matrices
	 */
	public static Mat4 add(Mat4... matrices){
		if(matrices.length > 1){
			Mat4 result = new Mat4(0);
			for (int curMatrix = 0; curMatrix < matrices.length; curMatrix++){
				result.add(matrices[curMatrix]);
			}
			return result;
		}else{
			return matrices.length == 1 ? matrices[0] : null;
		}
	}
	
	/**
	 * Subtracts a list of matrices and returns the result in a new matrix, values are read from left to right
	 * and subtracted
	 * 
	 * @param matrices Matrices to subtract
	 * @return A new matrix containing the difference between the given matrices
	 */
	public static Mat2 subtract(Mat2... matrices){
		if(matrices.length > 1){
			Mat2 result = new Mat2((Mat2)matrices[0]);
			for (int curMatrix = 1; curMatrix < matrices.length; curMatrix++){
				result.subtract(matrices[curMatrix]);
			}
			return result;
		}else{
			return matrices.length == 1 ? matrices[0] : null;
		}
	}
	
	/**
	 * Subtracts a list of matrices and returns the result in a new matrix, values are read from left to right
	 * and subtracted
	 * 
	 * @param matrices Matrices to subtract
	 * @return A new matrix containing the difference between the given matrices
	 */
	public static Mat3 subtract(Mat3... matrices){
		if(matrices.length > 1){
			Mat3 result = new Mat3((Mat3)matrices[0]);
			for (int curMatrix = 1; curMatrix < matrices.length; curMatrix++){
				result.subtract(matrices[curMatrix]);
			}
			return result;
		}else{
			return matrices.length == 1 ? matrices[0] : null;
		}
	}
	
	/**
	 * Subtracts a list of matrices and returns the result in a new matrix, values are read from left to right
	 * and subtracted
	 * 
	 * @param matrices Matrices to subtract
	 * @return A new matrix containing the difference between the given matrices
	 */
	public static Mat4 subtract(Mat4... matrices){
		if(matrices.length > 1){
			Mat4 result = new Mat4((Mat4)matrices[0]);
			for (int curMatrix = 1; curMatrix < matrices.length; curMatrix++){
				result.subtract(matrices[curMatrix]);
			}
			return result;
		}else{
			return matrices.length == 1 ? matrices[0] : null;
		}
	}
	
	/**
	 * Generates a perspective projection matrix as a 1-dimensional array that can be passed 
	 * into OpenGL shader uniforms, after being stored in a buffer, with calls to glUniformMatrix4* 
	 * 
	 * @param fovy Field of View angle for the y direction 
	 * @param aspect Aspect ratio of the area being rendered to
	 * @param zNear The nearest z value to the view before being clipped
	 * @param zFar The farthest z value from the view before being clipped
	 * @return Mat4 that is the perspective projection matrix formed using the given data
	 */
	public static Mat4 getPerspective(float fovy, float aspect, float zNear, float zFar){
		float f = 1/(float)Math.tan(Math.toRadians(fovy/2f));
		 return new Mat4(
				new Vec4(f/aspect,0,0,0),
				new Vec4(0,f,0,0),
				new Vec4(0,0,(zFar+zNear)/(zNear-zFar),-1),
				new Vec4(0,0,(2*zFar*zNear)/(zNear-zFar),0)
		);
	}
	
	/**
	 * Generates an orthographic projection matrix as a 1-dimensional array that can be passed 
	 * into OpenGL shader uniforms, after being stored in a buffer, with calls to glUniformMatrix4* 
	 * 
	 * @param left The minimum x clipping value
	 * @param right The maximum x clipping value
	 * @param bottom The minimum y clipping value
	 * @param top The maximum y clipping value
	 * @param zNear The nearest z value to the view before being clipped
	 * @param zFar The farthest z value from the view before being clipped
	 * @return Mat4 that is the Orthographic projection matrix formed using the given values
	 */
	public static Mat4 getOrtho(float left, float right, float bottom, float top, float zNear, float zFar){
		
		return new Mat4(
				new Vec4(2/(right-left),0,0,0),
				new Vec4(0,2/(top-bottom),0,0),
				new Vec4(0,0,1/(zFar-zNear),0),
				new Vec4(-((right+left)/(right-left)), -((top+bottom)/(top-bottom)), -(zNear/(zFar-zNear)), 1)
				);
	}
}
