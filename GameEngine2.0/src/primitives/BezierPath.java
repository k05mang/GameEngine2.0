package primitives;
import java.util.ArrayList;

import glMath.VecUtil;
import glMath.vectors.Vec3;

public class BezierPath {
	private ArrayList<Vec3> points;
	
	/**
	 * Constructs a Bezier path or curve using the given starting points
	 * 
	 * @param p0 Start point of the curve
	 * @param p1 Mid control point of the curve
	 * @param p2 End point of the curve
	 * @param initPoints Additional points to be added to the curve during initial construction
	 */
	public BezierPath(Vec3 p0, Vec3 p1, Vec3 p2, Vec3... initPoints){
		points = new ArrayList<Vec3>(initPoints.length+3);
		points.add(p0);
		points.add(p1);
		points.add(p2);
		for(Vec3 curPoint : initPoints){
			points.add(curPoint);
		}
	}
	
	/**
	 * Adds a point to the curve, this new point will become the new end point for the curve,
	 * and the previous end point will become a control point along the curve
	 * 
	 * @param point New point to add
	 */
	public void add(Vec3 point){
		points.add(point);
	}
	
	/**
	 * Gets a point on this curve at the given point t along the curve. 
	 * If the value of t is greater than 1 then the end point of the curve will be returned.
	 * If the value of t is less than 0 then the start point will be returned.
	 * 
	 * @param t Value between 0 and 1 that defines a point along the curve
	 * @return A point at t along the curve
	 */
	public Vec3 getBezierPoint(float t){
		if(t >= 1){
			return points.get(points.size()-1);
		}else if(t <= 0){
			return points.get(0);
		}else{
			return getPoint(t, points);
		}
	}
	
	/**
	 * Uses de Casteljau's algorithm to calculate a point on the curve at t using a subdividing recursion
	 * 
	 * @param t Point along the curve to find
	 * @param subPoints
	 * @return
	 */
	private Vec3 getPoint(float t, ArrayList<Vec3> subPoints){
		if(subPoints.size() == 1){//indicates we have calculated the point on the curve at t
			return subPoints.get(0);
		}else{
			//else continue following de Casteljau's algorithm and subdivide
			ArrayList<Vec3> newPoints = new ArrayList<Vec3>(subPoints.size()-1);
			for(int curPoint = 0; curPoint < subPoints.size()-1; curPoint++){
				newPoints.add(
						VecUtil.scale(subPoints.get(curPoint), 1-t).add( VecUtil.scale(subPoints.get(curPoint+1), t))
						);
			}
			return getPoint(t, newPoints);
		}
	}
	
	/**
	 * Gets the points that define the curve, these points are the start and end points, and the control points of the curve
	 * 
	 * @return All defining points of the curve
	 */
	public ArrayList<Vec3> getPoints(){
		return points;
	}
	
	/**
	 * Gets the number of points that define this curve including the start and end points
	 * 
	 * @return Number of points that define the curve
	 */
	public int numPoints(){
		return points.size();
	}
}
