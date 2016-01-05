package primitives;
import java.util.ArrayList;

import glMath.VecUtil;
import glMath.vectors.Vec3;

public class BezierPath {
	private ArrayList<Vec3> points;
	BezierCurve curve;
	private BezierPath derivative;
	private final int MIN_SEGMENTS = 50;
	private final float T_NORMAL_STEP = .001f;
	private static ArrayList<int[]> binomialLUT = new ArrayList<int[]>();
	private int n;
	private boolean isModified;

	/**
	 * Constructs a Bezier path or curve using the given starting points
	 * 
	 * @param initPoints Points used in the initial construction of the curve
	 */
	public BezierPath(Vec3... initPoints){
		derivative = null;
		points = new ArrayList<Vec3>(initPoints.length);
		for(Vec3 curPoint : initPoints){
			points.add(new Vec3(curPoint));
		}
		curve = null;
		n = initPoints.length-1;
		if(n > binomialLUT.size()){
			extendLUT();
		}
		isModified = false;
	}
	
	/**
	 * Constructs a Bezier path or curve using the given starting points
	 * 
	 * @param initPoints Points used in the initial construction of the curve
	 */
	public BezierPath(ArrayList<Vec3> initPoints){
		derivative = null;
		points = new ArrayList<Vec3>(initPoints.size());
		for(Vec3 curPoint : initPoints){
			points.add(new Vec3(curPoint));
		}
		curve = null;
		n = initPoints.size()-1;
		if(n > binomialLUT.size()){
			extendLUT();
		}
		isModified = false;
	}
	
	/**
	 * Adds a point to the curve, this new point will become the new end point for the curve,
	 * and the previous end point will become a control point along the curve
	 * 
	 * @param point New point to add
	 */
	public void add(Vec3 point){
		points.add(point);
		n++;
		if(n > binomialLUT.size()){
			extendLUT();
		}
		if(curve != null){
			curve.updateCurve();
		}
		isModified = true;
	}
	
	/**
	 * Adds a set of points to the bezier path
	 * 
	 * @param points Points to add to the bezier curve
	 */
	public void add(Vec3... points){
		for(Vec3 point : points){
			this.points.add(point);
		}
		n += points.length;
		if(n > binomialLUT.size()){
			extendLUT();
		}
		if(curve != null){
			curve.updateCurve();
		}
		isModified = true;
	}
	
	/**
	 * Adds a set of points to the bezier path
	 * 
	 * @param points Points to add to the bezier curve
	 */
	public void add(ArrayList<Vec3> points){
		for(Vec3 point : points){
			this.points.add(point);
		}
		n += points.size();
		if(n > binomialLUT.size()){
			extendLUT();
		}
		if(curve != null){
			curve.updateCurve();
		}
		isModified = true;
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
	
	public Vec3 getTangent(float t){
//		Vec3 tangent = new Vec3();
//		float factor = 0;
//		for(int curPoint = 0; curPoint < points.size()-1; curPoint++){
//			factor = (float)(binomialLUT.get(n-1)[curPoint]*Math.pow(1-t, n-1-curPoint)*Math.pow(t,curPoint)*n);
//			tangent.add(
//					factor*(points.get(curPoint+1).x-points.get(curPoint).x),
//					factor*(points.get(curPoint+1).y-points.get(curPoint).y),
//					factor*(points.get(curPoint+1).z-points.get(curPoint).z)
//					);
//		}
//		return tangent;
		if(isModified || derivative == null){
			computeDerivative();
		}
		return derivative.getBezierPoint(t);
	}
	
	public Vec3 getNormal(float t){
		if(isModified || derivative == null){
			computeDerivative();
		}
		if(t >= 1){
			return getBezierPoint(t-T_NORMAL_STEP).cross(getTangent(t));
		}else{
			return getBezierPoint(t+T_NORMAL_STEP).cross(getTangent(t));
		}
//		if(isModified || derivative == null){
//			computeDerivative();
//		}
//		Vec3 normal = new Vec3();
//		float factor = 0;
//		for(int curPoint = 0; curPoint < points.size()-2; curPoint++){
//			factor = (float)(binomialLUT.get(n-2)[curPoint]*Math.pow(1-t, n-2-curPoint)*Math.pow(t,curPoint)*(n-1));
//			normal.add(
//					factor*(points.get(curPoint+1).x-points.get(curPoint).x),
//					factor*(points.get(curPoint+1).y-points.get(curPoint).y),
//					factor*(points.get(curPoint+1).z-points.get(curPoint).z)
//					);
//		}
//		return normal;
//		return derivative.getTangent(t);
	}
	
	/**
	 * Uses de Casteljau's algorithm to calculate a point on the curve at t using a subdividing recursion
	 * 
	 * @param t Point along the curve to find
	 * @param subPoints Previously computed points used in de Casteljau's algorithm
	 * @return Final computed point
	 */
	private Vec3 getPoint(float t, ArrayList<Vec3> subPoints){
		if(subPoints.isEmpty()){
			return new Vec3();
		}else if(subPoints.size() == 1){//indicates we have calculated the point on the curve at t
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
	
	private void extendLUT(){
		//add the inital value to the LUT if it is empty
		if(binomialLUT.isEmpty()){
			binomialLUT.add(new int[]{1});
			binomialLUT.add(new int[]{1,1});
			binomialLUT.add(new int[]{1,2,1});
		}
		int prevLUTSize = 0;
		//determine how many new LUT we need based on the new order of the curve
		for(int prevLUT = binomialLUT.size()-1; prevLUT < n; prevLUT++){
			prevLUTSize = binomialLUT.get(prevLUT).length;
			int[] newLut = new int[prevLUTSize+1];
			//the end values will always be 1
			newLut[0] = 1;
			newLut[newLut.length-1] = 1;
			
			//compute the new values
			for(int curValue = 1; curValue < prevLUTSize; curValue++){
				newLut[curValue] = binomialLUT.get(prevLUT)[curValue-1]+binomialLUT.get(prevLUT)[curValue];
			}
			//add the new LUT
			binomialLUT.add(newLut);
		}
	}
	
	private void computeDerivative(){
		if(derivative == null){
			derivative = new BezierPath();
		}
		//compute the new points for what we already have stored in the derivative
		for(int curPoint = 0; curPoint < derivative.numPoints(); curPoint++){
			derivative.points.set(curPoint, 
					VecUtil.subtract(points.get(curPoint+1), points.get(curPoint)).scale(n)
					);
		}
		//compute new points for added points to this curve if any were added
		for(int newPoint = derivative.numPoints(); newPoint < points.size()-1; newPoint++){
			derivative.points.add(
					VecUtil.subtract(points.get(newPoint+1), points.get(newPoint)).scale(n)
					);
			derivative.n++;
		}
		
		if(derivative.curve != null){
			derivative.curve.updateCurve();
		}
	}
	
	public void setpoint(int index, Vec3 value){
		setPoint(index, value.x, value.y, value.z);
	}
	
	public void setPoint(int index, float x, float y, float z){
		//check that the index passed is in bounds 
		if(index > -1 && index < points.size()){
			points.get(index).set(x,y,z);
			//if there is a curve to update then update it
			if(curve != null){
				curve.updateCurve();
			}
			isModified = true;
		}
	}
	
	/**
	 * Increases the order of the curve by 1 while preserving the curve
	 */
	public void increaseOrder(){
		ArrayList<Vec3> newPoints = new ArrayList<Vec3>(points.size()+1);
		newPoints.add(points.get(0));
		//loop through the current control points and modify them for the new point
		for(int curPoint = 0; curPoint < n; curPoint++){
			float factor = (curPoint+1)/(float)(n+1);
			newPoints.add(
					VecUtil.scale(points.get(curPoint), factor).add( 
							points.get(curPoint+1).x*(1-factor), 
							points.get(curPoint+1).y*(1-factor), 
							points.get(curPoint+1).z*(1-factor)
							)
					);
		}
		//add original end point
		newPoints.add(points.get(points.size()-1));
		points = newPoints;
		//update the curve that renders this path if it is available
		if(curve != null){
			curve.constructCurve(MIN_SEGMENTS+(n+1)*20);
		}
		n++;
		extendLUT();
		isModified = true;
	}
	
	/**
	 * Decreases the order of the curve by 1 while attempting to preserve the curve
	 */
	public void decreaseOrder(){
		if(points.size()-1 > 2){
			
		}
	}
	
	/**
	 * Moves the control point of this curve at the given {@code pIndex}, by the given translation vector
	 * 
	 * @param pIndex Index of the control point in this curve to modify
	 * @param translation Amount to move the control point at {@code pIndex} by
	 */
	public void translatePoint(int pIndex, Vec3 translation){
		translatePoint(pIndex, translation.x, translation.y, translation.z);
	}
	
	/**
	 * Moves the control point of this curve at the given {@code pIndex}, by the given translation amounts
	 * 
	 * @param pIndex Index of the control point in this curve to modify
	 * @param x X amount to translate by
	 * @param y Y amount to translate by
	 * @param z Z amount to translate by
	 */
	public void translatePoint(int pIndex, float x, float y, float z){
		//do a bounds check
		if(pIndex < points.size() && pIndex > 0){
			points.get(pIndex).add(x, y, z);
			//if there is a renderable curve associated with this path update it
			if(curve != null){
				curve.updateCurve();
			}
			isModified = true;
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
	
	public int getOrder(){
		return n;
	}
}
