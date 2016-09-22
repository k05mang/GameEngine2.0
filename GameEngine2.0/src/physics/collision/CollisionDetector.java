package physics.collision;

import glMath.Quaternion;
import glMath.Transform;
import glMath.VecUtil;
import glMath.vectors.Vec2;
import glMath.vectors.Vec3;

public abstract class CollisionDetector {
	
	protected static final float MAX_THRESHOLD = .001f;
	
	public static boolean intersects(CollisionMesh objA, CollisionMesh objB){
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
	
	public static boolean intersects(Ray ray, CollisionMesh mesh){
		if(mesh instanceof ConvexHull2D){
			return intersects(ray, (ConvexHull2D)mesh);
		}else if(mesh instanceof ConvexHull3D){
			return intersects(ray, (ConvexHull3D)mesh);
		}else if(mesh instanceof CollisionPlane){
			return intersects(ray, (CollisionPlane)mesh);
		}else if(mesh instanceof AABB){
			return intersects(ray, (AABB)mesh);
		}/*else if(mesh instanceof OBB){
			return intersects(ray, (OBB)mesh);
		}else if(mesh instanceof CollisionSphere){
			return intersects(ray, (CollisionSphere)mesh);
		}else if(mesh instanceof CollisionCone){
			return intersects(ray, (CollisionCone)mesh);
		}else if(mesh instanceof CollisionCylinder){
			return intersects(ray, (CollisionCylinder)mesh);
		}else if(mesh instanceof CollisionCapsule){
			return intersects(ray, (CollisionCapsule)mesh);
		}
		
		*/
		
		return false;
	}
	
	public static boolean intersects(Ray ray, ConvexHull2D hull){
		//determine if the ray runs parallel to the plane the convex hull is on
		if(ray.getDirection().dot(hull.planeNormal) == 0){
			//if it does, determine if the ray potentially runs through the plane
			if(VecUtil.subtract(hull.getPos(), ray.getPos()).dot(ray.getDirection()) == 0){
				//if it does determine if the ray passes through the convex hull
			}else{
				//otherwise we know the ray cannot intersect the convex hull
				return false;
			}
		}else{
			//if it doesn't then calculate the intersection of the ray with the plane
			
			//compute the depth along the line for the point on the line that intersects the plane
			//d = ((p0-L0)·n)/(L·n), where n is the plane normal, L0 ray pos, L ray direction, p0 plane pos
			float depth = VecUtil.subtract(hull.getPos(), ray.getPos()).dot(hull.planeNormal)/ray.getDirection().dot(hull.planeNormal);
			Vec3 point = VecUtil.add(ray.getPos(), VecUtil.scale(ray.getDirection(), depth*ray.getLength()));
			
			//then determine if this point is contained inside the convex hull
			
			//first construct an initial triangle that may contain the point, 
			//the triangle is composed of vertices from the hull and is known to be within the hull
			Vec3 a, b, c;//these are the points that make up the triangle
			//the first point is farthest in the direction from the mesh center to the point
			Vec3 relaPoint = VecUtil.subtract(point, hull.getPos());//point relative to the hull center
			a = hull.support(relaPoint);
			
			Vec3 relativeA = VecUtil.subtract(a, hull.getPos());
			//the next point is found by searching in the normal direction of the edge from the center to a in the direction of the point
			//first get the direction vector
			Vec3 direction = VecUtil.cross(relaPoint, relativeA, relativeA).normalize();
			//check if the direction we found is the zero vector then this means the point lies on the edge from the center to a
			if(direction.isZero()){
				//if it does then check if the point lies within the center and a
				return relaPoint.length() <= relativeA.length();
			}
			b = hull.support(direction);
			
			//the last point is from the edge in the direction of the point
			relaPoint = VecUtil.subtract(point, a);//point relative to a
			Vec3 ab = VecUtil.subtract(b, a);
			direction = VecUtil.cross(relaPoint, ab, ab).normalize();
			
			//check if the direction we found is the zero vector then this means the point lies on the edge from the a to b
			if(direction.isZero()){
				//if it does then check if the point lies within the a and b
				return relaPoint.length() <= ab.length();
			}
			c = hull.support(direction);
		}
	}
	
	public static boolean intersects(Ray ray, ConvexHull3D hull){
		return false;
	}
	
	public static boolean intersects(Ray ray, CollisionPlane plane){
		Vec2 planeHalfDim = plane.getHalfDimensions();
		//compute the point intersection for the 3 planes defined by the normals above
			//x face of the bounding box
				float lDotn = plane.getNormal().dot(ray.getDirection());
				//first check if the ray could intersect the plane at all
				if(lDotn <= 0){
					//compute the depth along the line for the point on the line that intersects the plane
					//d = ((p0-L0)·n)/(L·n), where n is the plane normal, L0 ray pos, L ray direction, p0 plane pos
					float d = VecUtil.subtract(plane.getPos(), ray.getPos()).dot(plane.getNormal())/lDotn;
					//check if the point computed along the line is within the range of the ray
					if(ray.getLength() >= 0 && d > ray.getLength()){
						return false;
					}
					Quaternion inverseOrient = plane.getTransform().getOrientation().conjugate();
					//get the point on the plane by getting the point along the line in the direction of the ray using d
					//translating that point by the ray position to get the point on the plane in world space
					//translate that point so that it is relative to the plane origin
					//orient that final point to make it relative to the plane before it is oriented
					Vec3 planePoint = inverseOrient.multVec(VecUtil.scale(ray.getDirection(), d).add(ray.getPos()).subtract(plane.getPos()));
					if(Math.abs(planePoint.x) <= planeHalfDim.x && Math.abs(planePoint.z) <= planeHalfDim.y){
						return true;
					}
				}
		return false;
	}
	
	public static boolean intersects(Ray ray, AABB bbox){
		//check if the ray was emitted from the box center which means the ray collides with the box
		if(ray.getPos().equals(bbox.getPos())){
			return true;
		}
		CollisionPlane plane = new CollisionPlane(1,1);//collision plane that will be modified for use with the faces
		Transform trans = new Transform();//transform used to modify the plane
		//this is to reduce memory usage
		//compute the planes of the faces of the bounding box that are relevant for the ray
		Vec3 rayRelBox = VecUtil.subtract(ray.getPos(), bbox.getPos());//ray position relative to the box
		Vec3 boxHalfDim = bbox.getHalfDimensions();
		
		//compute x face
		//scale the plane
		trans.scale(boxHalfDim.y*2, 1, boxHalfDim.z*2);
		//orient
		trans.rotate(0,0,-1,Math.signum(rayRelBox.x)*90);
		//translate
		trans.translate(bbox.getPos());
		trans.translate(Math.signum(rayRelBox.x)*boxHalfDim.x,0,0);
		
		//set the collision plane to the computed transform
		plane.setTransform(trans);
		//perform the computation for the x face
		if(!intersects(ray, plane)){
			//if that failed try the y face
			//scale the plane
			trans.setScale(boxHalfDim.x*2, 1, boxHalfDim.z*2);
			//orient
			trans.setOrientation(Quaternion.fromAxisAngle(0,0,-1,90-Math.signum(rayRelBox.y)*90));//rotate 180 if the y is negative 0 if not
			//translate
			trans.setTranslation(bbox.getPos());
			trans.translate(0,Math.signum(rayRelBox.y)*boxHalfDim.y,0);
			
			//set the collision plane to the computed transform
			plane.setTransform(trans);
			
			//perform the computation for the y face
			if(!intersects(ray, plane)){
				//if that failed try the z face
				//scale the plane
				trans.setScale(boxHalfDim.x*2, 1, boxHalfDim.y*2);
				//orient
				trans.setOrientation(Quaternion.fromAxisAngle(1,0,0,Math.signum(rayRelBox.z)*90));
				//translate
				trans.setTranslation(bbox.getPos());
				trans.translate(0,0,Math.signum(rayRelBox.z)*boxHalfDim.z);
				
				//set the collision plane to the computed transform
				plane.setTransform(trans);
				
				//perform the computation for the z face
				return intersects(ray, plane);//since this is the final one it will decide true or false
			}else{
				return true;
			}
		}else{
			return true;
		}
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
