package physics.collision;

import glMath.VecUtil;
import glMath.vectors.Vec3;

public class Simplex {
	private Vec3 a, b, c, d;
	
	public Simplex(Vec3 startA, Vec3 startB){
		a = new Vec3(startA);
		b = new Vec3(startB);
	}
	
	public void add(Vec3 point){
		d = c;
		c = b;
		b = a;
		a = point;
	}
	
	/**
	 * Computes the new direction vector to search on the Minkowski "difference". Additionally updates
	 * the simplex, removing points from the simplex that are no longer needed.
	 * 
	 * @param direction Direction vector to set as the newly computed direction
	 * 
	 * @return True only if the origin has been contained in the simplex thus indicating a collision.
	 * It is expected that external users of this function will have an early out implementation when looping. 
	 */
	public boolean getDirection(Vec3 direction){
		//check what dimensionality the simplex has based on what variables are null
		if(c == null && d == null){
			//vector from the recently added point to the simplex to the origin
			Vec3 ao = a.inverse();
			//vector from the recently added point in the simplex to the previous point
			Vec3 ab = (Vec3)VecUtil.subtract(b, a);
			/*we know that the origin can't be behind B, since we just came from that
			direction, and we know it can't be in front of A, since A is that farthest point 
			on the sum and it would have failed the early exit test, we know then that the
			origin is only in the direction perpendicular to the edge AB
			*/				
			direction.set(VecUtil.cross(ab,ao,ab));
			return false;
		}else if(d == null){
			Vec3 ab = (Vec3)VecUtil.subtract( b, a );
			Vec3 ac = (Vec3)VecUtil.subtract( c, a );
			Vec3 ao = a.inverse();
			Vec3 abc = ab.cross(ac);//triangle normal
			
			//check which edge is closest to the origin and modify values to reflect the shift
			if(abc.cross(ac).dot(ao) > 0){
				//remove B and move C to B
				b = c;
				c = null;
				direction.set(VecUtil.cross(ac,ao,ac));
			}else if(ab.cross(abc).dot(ao) > 0){
				//remove C
				c = null;
				direction.set(VecUtil.cross(ab,ao,ab));
			}else{
				//check which direction, above or below the triangle, the origin is in
				//check the triangle normal
				if(abc.dot(ao) > 0){
					direction.set(abc);
				}else{//check the inverted normal
					direction.set(abc.inverse());
					//change the order of the points to maintain the winding order
					//swap B and C
					Vec3 tempB = b;
					b = c;
					c = tempB;
				}
			}
			return false;
		}else{
			Vec3 ab = (Vec3)VecUtil.subtract( b, a );
			Vec3 ac = (Vec3)VecUtil.subtract( c, a );
			Vec3 ad = (Vec3)VecUtil.subtract( d, a );
			Vec3 abc = ab.cross(ac);
			Vec3 acd = ac.cross(ad);
			Vec3 adb = ad.cross(ab);
			Vec3 ao = a.inverse();
			
			//test what face the origin might be located, we don't test the "bottom" triangle since that was 
			//the triangle used to point towards A meaning that checking the opposite is meaningless as
			//the previous test already checked that and ruled it out
			if(abc.dot(ao) > 0){
				//remove D
				d = null;
				direction.set(abc);
			}else if(acd.dot(ao) > 0){
				//remove B and shift up
				b = c;
				c = d;
				d = null;
				direction.set(acd);
			}else if(adb.dot(ao) > 0){
				//remove C
				//shift and maintain winding order,
				c = b;//remove C, D would become C but to maintain winding order B and D need to swap
				b = d;
				d = null;
				direction.set(adb);
			}else{
				return true;
			}
			//check the new triangle for edge cases, only updating the direction vector
			//after, this was where I was messing up last time, passing a new direction
			//and letting the loop run again
//			ab = (Vec3)VecUtil.subtract( b, a );
//			ac = (Vec3)VecUtil.subtract( c, a );
//			abc = ab.cross(ac);
//			if(abc.cross(ac).dot(ao) > 0){
//				//remove b
//				b = c;
//				c = null;
//				direction.set(VecUtil.cross(ac,ao,ac));
//			}else if(ab.cross(abc).dot(ao) > 0){
//				//remove c
//				c = null;
//				direction.set(VecUtil.cross(ab,ao,ab));
//			}else{
//				direction.set(abc);
//			}
			return false;
		}
	}
}
