package physics.collision;

import java.util.ArrayList;

import glMath.VecUtil;
import glMath.vectors.Vec3;

public abstract class CollisionDetector {
	
	protected static final float MAX_THRESHOLD = .001f;
	
	public static boolean checkCollision(CollisionMesh objA, CollisionMesh objB){
		Vec3 direction = new Vec3(1,1,1);
		Simplex simplex = new Simplex(
				objB.support(direction.inverse()).subtract(objA.support(direction)), 
				objB.support(direction).subtract(objA.support(direction.inverse()))
				);
		while(!simplex.getDirection(direction)){
			Vec3 newPoint = objB.support(direction).subtract(objA.support(direction.inverse()));
//			direction.print();
//			newPoint.print();
			//
			if(newPoint.dot(direction) < 0){
				return false;
			}
			simplex.add(newPoint);
		}
		return true;
	}
	
	//this seems to be producing the wrong results FIX IT
//	private static CollisionData computeDistance(ArrayList<Polytope.PolytopePoint> points){
////		System.out.println(points.size());
//		switch(points.size()){
//			case 1:
//				Polytope.PolytopePoint point = points.get(0);
//				Vec3 normal = new Vec3(point.supportA);
//				float distance = normal.length();
//				normal.normalize();
//				return new CollisionData(normal, point.supportA, point.supportA, distance, false);
//			case 2:
//				Polytope.PolytopePoint start = points.get(1);
//				Polytope.PolytopePoint end = points.get(0);
//				Vec3 edge = (Vec3)VecUtil.subtract(start.supportFinal, end.supportFinal );
//				Vec3 pointA = null, pointB = null;
//				edge.trunc();
//			if(!edge.isZero()){;
//				float lambda1 = edge.dot(start.supportFinal)/edge.dot(edge);
//				float lambda2 = 1 - lambda1;
//				
//				if(lambda1 < 0){
//					pointA = end.supportA;
//					pointB = end.supportB;
//				}else if(lambda2 < 0){
//					pointA = start.supportA;
//					pointB = start.supportB;
//				}else{
//					pointA = start.supportA.scale(lambda1).add(start.supportB.scale(lambda2));
//					pointB = end.supportA.scale(lambda1).add(end.supportB.scale(lambda2));
//				}
//			}else{
//				pointA = start.supportA;
//				pointB = start.supportB;
//			}
//			normal = (Vec3)VecUtil.subtract(pointB, pointA);
//			distance = normal.length();
//			normal.normalize();
//			return new CollisionData(normal, pointA, pointB, distance, false);
//		case 3:
//			Polytope.PolytopeTriangle polytopeFace = new Polytope.PolytopeTriangle(points.get(2), points.get(1), points.get(0));
//			 Vec3 coords = computeBarycentric(
//						polytopeFace.s1.start.supportFinal,
//						polytopeFace.s2.start.supportFinal,
//						polytopeFace.s3.start.supportFinal,
//						new Vec3(polytopeFace.normal).scale(polytopeFace.getDistance()));
//			    
//			    //compute the point on A using the barycentric coordinates
//			    Vec3 aPoint = (Vec3)VecUtil.add(
//			    		polytopeFace.s1.start.supportA.scale(coords.x),
//			    		polytopeFace.s2.start.supportA.scale(coords.y),
//			    		polytopeFace.s3.start.supportA.scale(coords.z));
//			    
//			    //compute the point on B using the barycentric coordinates
//			    Vec3 bPoint = (Vec3)VecUtil.add(
//			    		polytopeFace.s1.start.supportB.scale(coords.x),
//			    		polytopeFace.s2.start.supportB.scale(coords.y),
//			    		polytopeFace.s3.start.supportB.scale(coords.z));
//			    
//	    		//construct the collision data object 
//			    return new CollisionData(
//			    			polytopeFace.normal.inverse(), aPoint, bPoint, polytopeFace.getDistance(), false
//			    		);
//			default:
//				return new CollisionData();
//		}
//	}
//	
//	private static CollisionData expandingPolytope(Polytope simplex, CollisionMesh objA, CollisionMesh objB){
//		//get the closest face on the polytope which is the face on the GJK termination simplex
//		Polytope.PolytopeTriangle polytopeFace = simplex.findClosest();
//		//get the distance this face is from the origin
//		float prevDist = polytopeFace.getDistance();
//		//get a new support point to add to the polytope in the direction of the closest face normal
//		Polytope.PolytopePoint newSupport = new Polytope.PolytopePoint(
//				objA.support(polytopeFace.normal),
//				objB.support(polytopeFace.normal.inverse())
//				);
//		for(int curIter = 0; curIter < 50; curIter++){
//			if(polytopeFace.normal.dot(newSupport.supportFinal)-prevDist < MAX_THRESHOLD){
//				//compute the barycentric coordinates of the collision
//			    
//			    Vec3 coords = computeBarycentric(
//						polytopeFace.s1.start.supportFinal,
//						polytopeFace.s2.start.supportFinal,
//						polytopeFace.s3.start.supportFinal,
//						new Vec3(polytopeFace.normal).scale(prevDist));
//			    
//			    //compute the point on A using the barycentric coordinates
//			    Vec3 aPoint = (Vec3)VecUtil.add(
//			    		polytopeFace.s1.start.supportA.scale(coords.x),
//			    		polytopeFace.s2.start.supportA.scale(coords.y),
//			    		polytopeFace.s3.start.supportA.scale(coords.z));
//			    
//			    //compute the point on B using the barycentric coordinates
//			    Vec3 bPoint = (Vec3)VecUtil.add(
//			    		polytopeFace.s1.start.supportB.scale(coords.x),
//			    		polytopeFace.s2.start.supportB.scale(coords.y),
//			    		polytopeFace.s3.start.supportB.scale(coords.z));
//			    
//	    		//construct the collision data object 
//			    return new CollisionData(
//			    			polytopeFace.normal.inverse(), aPoint, bPoint, prevDist, true
//			    		);
//			}else{
//				prevDist = polytopeFace.getDistance();
//				//add the new Support point to the simplex and have it recompute the triangles
//				//for the polytope
//				simplex.add(newSupport);
//				//get the new closest triangle on the new simplex 
//				polytopeFace = simplex.findClosest();
//				//compute a new support point in the direction of the new polytopeFace normal
//				newSupport = new Polytope.PolytopePoint(
//						objA.support(polytopeFace.normal),
//						objB.support(polytopeFace.normal.inverse())
//						);
//			}
//		}
//		//compute the barycentric coordinates of the collision
//	    
//	    Vec3 coords = computeBarycentric(
//				polytopeFace.s1.start.supportFinal,
//				polytopeFace.s2.start.supportFinal,
//				polytopeFace.s3.start.supportFinal,
//				new Vec3(polytopeFace.normal).scale(prevDist));
//	    
//	    //compute the point on A using the barycentric coordinates
//	    Vec3 aPoint = (Vec3)VecUtil.add(
//	    		polytopeFace.s1.start.supportA.scale(coords.x),
//	    		polytopeFace.s2.start.supportA.scale(coords.y),
//	    		polytopeFace.s3.start.supportA.scale(coords.z));
//	    
//	    //compute the point on B using the barycentric coordinates
//	    Vec3 bPoint = (Vec3)VecUtil.add(
//	    		polytopeFace.s1.start.supportB.scale(coords.x),
//	    		polytopeFace.s2.start.supportB.scale(coords.y),
//	    		polytopeFace.s3.start.supportB.scale(coords.z));
//	    
//		//construct the collision data object 
//	    return new CollisionData(
//	    			polytopeFace.normal.inverse(), aPoint, bPoint, prevDist, true
//	    		);
//	}
//	
//	private static Vec3 computeBarycentric(Vec3 a, Vec3 b, Vec3 c, Vec3 p){
//		// code from Christer Erickson's Real-Time Collision Detection
//		
//		Vec3 v0 = (Vec3)VecUtil.subtract(b,a);//b - a;
//		Vec3 v1 = (Vec3)VecUtil.subtract(c,a);//c - a; 
//		Vec3 v2 = (Vec3)VecUtil.subtract(p,a);//p - a;
//		
//	    float d00 = v0.dot(v0);
//	    float d01 = v0.dot(v1);
//	    float d11 = v1.dot(v1);
//	    float d20 = v2.dot(v0);
//	    float d21 = v2.dot(v1);
//	    float denom = d00 * d11 - d01 * d01;
//	     
//	    Vec3 coords = new Vec3();
//	    coords.y = (d11 * d20 - d01 * d21) / denom;
//	    coords.z = (d00 * d21 - d01 * d20) / denom;
//	    coords.x = 1.0f - coords.y - coords.z;
//	    
//	    return coords;
//	}
}
