package physics.collision;

import glMath.Quaternion;
import glMath.Transform;
import glMath.VecUtil;
import glMath.matrices.Mat4;
import glMath.vectors.Vec2;
import glMath.vectors.Vec3;
import glMath.vectors.Vec4;

import java.util.Set;

import mesh.primitives.HalfEdge;
import mesh.primitives.Triangle;
import core.Entity;

public abstract class CollisionDetector {
	
	protected static final float MAX_THRESHOLD = .001f;
	
	public static boolean intersects(CollisionMesh objA, Entity objB){
		return intersects(objA, objB.getCollider());
	}
	
	public static boolean intersects(Entity objA, CollisionMesh objB){
		return intersects(objA.getCollider(), objB);
	}
	
	public static boolean intersects(Entity objA, Entity objB){
		return intersects(objA.getCollider(), objB.getCollider());
	}
	
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
	
	public static boolean intersects(Entity obj, Ray ray){
		return intersects(ray, obj.getCollider());
	}
	
	public static boolean intersects(Entity obj, Vec3 point){
		return intersects(point, obj.getCollider());
	}
	
	public static boolean intersects(Ray ray, Entity obj){
		return intersects(ray, obj.getCollider());
	}
	
	public static boolean intersects(Vec3 point, Entity obj){
		return intersects(point, obj.getCollider());
	}

	public static boolean intersects(Ray ray, CollisionMesh mesh){
		if(ray.getPos().equals(ray.getPoint(1))){//test if the ray is actually a point
			return intersects(ray.getPos(), mesh);
		}else if(mesh instanceof ConvexHull2D){
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
	
	public static boolean intersects(CollisionMesh mesh, Ray ray){
		return intersects(ray, mesh);
	}
	
	public static boolean intersects(Vec3 point, CollisionMesh mesh){
		if(mesh instanceof ConvexHull2D){
			return intersects(point, (ConvexHull2D)mesh);
		}else if(mesh instanceof ConvexHull3D){
			return intersects(point, (ConvexHull3D)mesh);
		}/*else if(mesh instanceof CollisionPlane){
			return intersects(point, (CollisionPlane)mesh);
		}else if(mesh instanceof AABB){
			return intersects(point, (AABB)mesh);
		}else if(mesh instanceof OBB){
			return intersects(point, (OBB)mesh);
		}else if(mesh instanceof CollisionSphere){
			return intersects(point, (CollisionSphere)mesh);
		}else if(mesh instanceof CollisionCone){
			return intersects(point, (CollisionCone)mesh);
		}else if(mesh instanceof CollisionCylinder){
			return intersects(point, (CollisionCylinder)mesh);
		}else if(mesh instanceof CollisionCapsule){
			return intersects(point, (CollisionCapsule)mesh);
		}
		
		*/
		
		return false;
	}
	
	public static boolean intersects(CollisionMesh mesh, Vec3 point){
		return intersects(point, mesh);
	}
	
	private static boolean intersects(Vec3 point, ConvexHull2D hull){
		//check for null input
		if(point == null || hull == null){
			return false;
		}
		//first construct an initial triangle that may contain the point, 
		//the triangle is composed of vertices from the hull and is known to be within the hull
		Vec3 a, b, c,//points that make up the triangle
		ab, bc, ca;//edges for the current triangle
		//the first point is farthest in the direction from the mesh center to the point
		Vec3 relaPoint = VecUtil.subtract(point, hull.getPos());//point relative to the hull center
		a = hull.support(relaPoint);
		
		Vec3 relativeA = VecUtil.subtract(a, hull.getPos());
		//the next point is found by searching in the normal direction of the edge from the center to a in the direction of the point
		//first get the direction vector
		Vec3 direction = VecUtil.cross(relativeA, relaPoint, relativeA);
		//check if the direction we found is the zero vector then this means the point lies on the edge from the center to a
		if(direction.isZero()){
			//if it does then check if the point lies within the center and a
			return relaPoint.length() <= relativeA.length();
		}
		b = hull.support(direction);
		
		//the last point is from the edge in the direction of the point
		relaPoint = VecUtil.subtract(point, a);//point relative to a
		ab = VecUtil.subtract(b, a);
		direction = VecUtil.cross(ab, relaPoint, ab);
		
		//check if the direction we found is the zero vector then this means the point lies on the edge from the a to b
		if(direction.isZero()){
			//if it does then check if the point lies within the a and b
			return relaPoint.length() <= ab.length();
		}
		c = hull.support(direction);
		
		bc = VecUtil.subtract(c, b);
		ca = VecUtil.subtract(a, c);
		
		//to determine if the point lies within the triangle we can simply test if the dot product between the point
		//relative to a triangle point and the edge normal pointing in towards the triangle center is greater than or equal to 0 
		//if the dot product is the same for each edge then the point is contained in the triangle and therefore inside the hull
		
		return VecUtil.dot(relaPoint, VecUtil.cross(ab, bc, ab)) > 0 &&						//check for edge ab
				VecUtil.dot(VecUtil.subtract(point, b), VecUtil.cross(bc, ca, bc)) > 0 &&	//check for edge bc
				VecUtil.dot(VecUtil.subtract(point, c), VecUtil.cross(ca, ab, ca)) > 0;		//check for edge ca
	}
	
	private static boolean intersects(Vec3 point, ConvexHull3D hull){
		//check for null input
		if(point == null || hull == null){
			return false;
		}
		return false;
	}
	
	private static boolean intersects(Ray ray, ConvexHull2D hull){
		//check for null input
		if(ray == null || hull == null){
			return false;
		}
		Vec3 planeNormal = hull.getPlaneNormal();//this guarantees a plane normal for the hull that represents the current state of the hull
		//post transformations
		
		//find a point that may lie in the convex hull
		//determine if the ray runs parallel to the plane the convex hull is on
		if(ray.getDirection().dot(planeNormal) == 0){
			//if it does, determine if the ray potentially runs through the plane
			//if the dot product of the vector between the hull center and ray start is perpendicular to the plane normal then the ray lies on the plane
			if(VecUtil.subtract(hull.getPos(), ray.getPos()).dot(planeNormal) == 0){
				//if it does determine if the ray passes through the convex hull
				//algorithm based on one found here http://geomalgorithms.com/a13-_intersect-4.html
				//the algorithm is based on the idea that to get the intersection point of the ray and edge you need (P(t)-Vi)·ni = 0
				//so we need to find t, which can be found using this formula t = ((Vi-P0)·ni)/((P1-P0)·ni), where ni is the edge normal
				
				//first we need to transform the ray to be in the hulls model space, this will reduce the need to transform the hull
				//vertices into world space each iteration below. 
				
				Transform hullTrans = hull.getTransform();
				Vec3 p0 = hullTrans.inverseTransform(ray.getPos());
				Vec3 p1 = hullTrans.inverseTransform(ray.getPoint(1));
				//get new ray direction
				Vec3 rayDir = VecUtil.subtract(p1,p0);
				
				float tE = 0.0f;//maximum t for entering the hull from the ray pos
				float tL = 1.0f;//minimum value the ray can leave the hull from
				HalfEdge curEdge = hull.baseEdge;//tracks the current edge
				do{
					Vec3 vi = hull.mesh.getVertex(curEdge.sourceVert).getPos();
					Vec3 viPlus1 = hull.mesh.getVertex(curEdge.next.sourceVert).getPos();
					Vec3 hullEdge = VecUtil.subtract(viPlus1, vi);
					Vec3 edgeNormal = hullEdge.cross(hull.planeNormal).normalize();//calculate the outward normal of the edge
			        float n = VecUtil.subtract(vi, p0).dot(edgeNormal);//(Vi-P0)·ni
			        float d = rayDir.dot(edgeNormal);//(P1-P0)·ni
			        //test if the ray is parallel to the edge
			        if (d == 0){
			        	//if it is we need to test if the ray is outside the hull at this point
			        	//this will determine if we should even continue to search for intersection points
			            if (n < 0){
			                 return false;
			            }
			        }

			        //get the depth the ray intersects the hull on this edge
			        float t = n/d;//the case for when d would be 0 is handled above
			        //determine whether the ray is entering the edge or leaving the edge
			        if (d < 0){//entering the edge case
			            tE = Math.max(tE, t);
			            //check if the depth that the ray enters the hull is greater than the depth the ray leaves the hull
			            if (tE > tL){
			            	//if the entry depth is greater than the exit depth then the ray is not intersecting
			                return false;
			            }
			        }else{//leaving the edge
			            tL = Math.min(tL, t);
			        	//check if the depth that the ray leaves the hull is less than the depth the ray enters the hull
			            if (tL < tE){
			            	//if the exit depth is less than the entry depth then the ray is not intersecting
			                return false;
			            }
			        }
					curEdge = curEdge.next;//advance the pointer
				}
				while(!curEdge.equals(hull.baseEdge));
				
			    return tE <= 1.0f && tL >= 0.0f;
			}else{
				//otherwise we know the ray cannot intersect the convex hull
				return false;
			}
		}else{
			//if it doesn't then calculate the intersection of the ray with the plane
			
			//compute the depth along the line for the point on the line that intersects the plane
			//d = ((p0-L0)·n)/(L·n), where n is the plane normal, L0 ray pos, L ray direction, p0 plane pos
			float depth = VecUtil.subtract(hull.getPos(), ray.getPos()).dot(planeNormal)/ray.getDirection().dot(planeNormal);
			//check if the depth is negative, in this case the arrow is pointing away from the plane but could still mathematically intersect
			//if the ray is considered an infinite line, additionally check for cases where the depth is longer than the rays length
			if(depth < 0 || depth > ray.getLength()){
				return false;
			}

			Vec3 point = VecUtil.add(ray.getPos(), VecUtil.scale(ray.getDirection(), depth));
			//then determine if this point is contained inside the convex hull
			
			return intersects(point, hull);
		}

		
	}
	
	private static boolean intersects(Ray ray, ConvexHull3D hull){
		//check for null input
		if(ray == null || hull == null){
			return false;
		}
		//check if the ray is actually a point
		if(ray.getLength() == 0){
			return intersects(ray.getPos(), hull);
		}
		Set<Triangle> faces = hull.normals.keySet();
		//first we need to transform the ray to be in the hulls model space, this will reduce the need to transform the hull
		//vertices into world space each iteration below. 
		
		Transform hullTrans = hull.getTransform();
		Vec3 p0 = hullTrans.inverseTransform(ray.getPos());
		Vec3 p1 = hullTrans.inverseTransform(ray.getPoint(1));
		//get new ray direction
		Vec3 rayDir = VecUtil.subtract(p1,p0);
		
		float tE = 0.0f;//maximum t for entering the hull from the ray pos
		float tL = 1.0f;//minimum value the ray can leave the hull from
		for(Triangle curFace : faces){
			Vec3 vi = hull.mesh.getVertex(curFace.he1.sourceVert).getPos();
			Vec3 faceNormal = hull.normals.get(curFace);//get the face normal
	        float n = VecUtil.subtract(vi, p0).dot(faceNormal);//(Vi-P0)·ni
	        float d = rayDir.dot(faceNormal);//(P1-P0)·ni
	        //test if the ray is parallel to the edge
	        if (d == 0){
	        	//if it is we need to test if the ray is outside the hull at this point
	        	//this will determine if we should even continue to search for intersection points
	            if (n < 0){
	                 return false;
	            }
	        }

	        //get the depth the ray intersects the hull on this edge
	        float t = n/d;//the case for when d would be 0 is handled above
	        //determine whether the ray is entering the edge or leaving the edge
	        if (d < 0){//entering the edge case
	            tE = Math.max(tE, t);
	            //check if the depth that the ray enters the hull is greater than the depth the ray leaves the hull
	            if (tE > tL){
	            	//if the entry depth is greater than the exit depth then the ray is not intersecting
	                return false;
	            }
	        }else{//leaving the edge
	            tL = Math.min(tL, t);
	        	//check if the depth that the ray leaves the hull is less than the depth the ray enters the hull
	            if (tL < tE){
	            	//if the exit depth is less than the entry depth then the ray is not intersecting
	                return false;
	            }
	        }
		}
		
	    return tE <= 1.0f && tL >= 0.0f;
	}
	
	/**
	 * Gets the depth that the {@code ray} intersects the plane represented by the {@code planeNormal, and planePoint}
	 * 
	 * @param ray Ray to calculate the intersection depth of with the plane
	 * @param planeNormal Plane normal, this is used in calculating the plane
	 * @param planePoint Point on the plane
	 * 
	 * @return Depth of intersection the {@code ray} has with the plane, given as a floating point value in world units
	 */
	public static float depth(Ray ray, Vec3 planeNormal, Vec3 planePoint){
		//compute the depth along the line for the point on the line that intersects the plane
		//d = ((p0-L0)·n)/(L·n), where n is the plane normal, L0 ray pos, L ray direction, p0 plane pos
		return VecUtil.subtract(planePoint, ray.getPos()).dot(planeNormal)/
				planeNormal.dot(ray.getDirection());
	}
	
	private static boolean intersects(Ray ray, CollisionPlane plane){
		Vec2 planeHalfDim = plane.getHalfDimensions();
		//compute the point intersection for the 3 planes defined by the normals above
			//x face of the bounding box
				float lDotn = plane.getNormal().dot(ray.getDirection());
				//first check if the ray could intersect the plane at all
				if(lDotn <= 0){
					//compute the depth along the line for the point on the line that intersects the plane
					float d = depth(ray, plane.getNormal(), plane.getPos());
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
	
	private static boolean intersects(Ray ray, AABB bbox){
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
