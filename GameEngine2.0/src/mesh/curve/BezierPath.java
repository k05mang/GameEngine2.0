package mesh.curve;

import java.util.ArrayList;

import glMath.Quaternion;
import glMath.VecUtil;
import glMath.vectors.Vec3;

/**
 * Represents a composite Bezier curve object. Internally this is represented by a LinkedList
 * @author Kevin Mango
 *
 */
public class BezierPath {

	private ArrayList<BezierCurve> curves;
	private BezierNode start, end;
	private Continuity smoothness;
	private boolean isClosed;
	
	/**
	 * Constructs a BezierPath with a default smoothness of C0
	 */
	public BezierPath() {
		this(Continuity.C0);
	}
	
	/**
	 * Constructs a BezierPath with the given continuity
	 * 
	 * @param c Smoothness this Path will maintain between its curves
	 */
	public BezierPath(Continuity c){
		curves = new ArrayList<BezierCurve>();
		start = new BezierNode();
		end = start;//since there is no data both start and end are the same
		smoothness = c;
		isClosed = false;
	}
	
	/**
	 * Attempts to add the given curve to the path object, if the given {@code curve} is does not have a connection with
	 * the pre-existing curves then the add operation will fail and this function will return false.
	 * <br>
	 * In order for a curve to have a connection it must share an end point with one of the end curves in the path object.
	 * If the curve shares a point then it will be added to this path, and will be transformed to integrate with the existing
	 * curves to maintain the specified smoothness of the path.
	 * 
	 * @param curve Curve to be added to the path
	 * 
	 * @return True if the curve was successfully added, false if it could not be added to the path
	 *///TODO: add field to decide if the user wants to merge the curves when they dont match, if we want to allow that
	public boolean add(BezierCurve curve){
		//check to make sure the curve isn't just a point
		boolean isPoint = curve.getOrder() == 0;
		//check if there is a previous curve to relate them to
		if(curves.size() == 0 && !isPoint){
			start.curve = new BezierCurve(curve);//since there is no current data this is the first new curve
			curves.add(start.curve);//store the new curve object for reference when rendering
			end = new BezierNode(null, start, null);//create a new node for end
			start.next = end;//set the next node to be the end node
		}else if(curves.size() == 1 && !isPoint){
			end.curve = new BezierCurve(curve);
			curves.add(end.curve);
		}else if(!isClosed && !isPoint){
			//determine if the curve should be added to the front, end, or neither
			Vec3 startPoint = curve.getBezierPoint(0), endPoint = curve.getBezierPoint(1);
			
			//compare with the start curves start point
			if(start.curve.getBezierPoint(0).equals(endPoint)){//check if the end point matches this end
				//in this case we only need to add the curve and make it the new start
				BezierNode curStart = start;
				start = new BezierNode(curStart, null, new BezierCurve(curve));
				curves.add(start.curve);
				curStart.prev = start;
			}else if(start.curve.getBezierPoint(0).equals(startPoint)){//check if the start point matches
				//in this case we need to add the curve but reverse, this way values read consecutively along the path
				//are order in the same direction making reading the path much easier
				BezierNode curStart = start;
				start = new BezierNode(curStart, null, new BezierCurve(curve, true));
				curves.add(start.curve);
				curStart.prev = start;
			}//the start doesn't match anywhere, so try the end curve
			
			else if(end.curve.getBezierPoint(1).equals(startPoint)){//check if the start point matches this end
				//in this case we only need to add the curve and make it the new end
				BezierNode curEnd = end;
				end = new BezierNode(null, curEnd, new BezierCurve(curve));
				curves.add(end.curve);
				curEnd.prev = end;
			}else if(end.curve.getBezierPoint(1).equals(endPoint)){//check if the end point matches
				//in this case we need to add the curve but reverse, this way values read consecutively along the path
				//are order in the same direction making reading the path much easier
				BezierNode curEnd = end;
				end = new BezierNode(null, curEnd, new BezierCurve(curve, true));
				curves.add(end.curve);
				curEnd.prev = end;
			}else{//else they dont match
				return false;
			}
			
			//check if the start and end curves match tips, in which case the loop is closed
			if(end.curve.getBezierPoint(1).equals(start.curve.getBezierPoint(0))){
				//if they are connect the list and mark it closed
				start.prev = end;
				end.next = start;
				isClosed = true;
			}
		}else{//the path is closed and isn't accepting new curves
			return false;
		}
		
		//smooth the path only if there is another curve to smooth it with
		if(curves.size() > 1){
			smooth(curves.get(curves.size()-1));
		}
		return true;
	}
	
	/**
	 * Sets the given smoothness of the BezierPath, new additions to the path object will reflect this change in smoothness.
	 * Existing curves will not change smoothness however.
	 * 
	 * @param c New smoothness for the curve
	 */
	public void setSmoothness(Continuity c){
		smoothness = c;
	}
	
	/**
	 * Determines if the given path object is closed by all the curves that define the path. If
	 * this returns true then this means that the path will no longer accept new curve objects.
	 * 
	 * @return True if the path is closed and connected by all its current curves, false otherwise
	 */
	public boolean isClosed(){
		return isClosed;
	}
	
	/**
	 * Smooth's the curve at the joint points of the end curves
	 * 
	 * @param newCurve Pointer to the new curve that was added
	 */
	private void smooth(BezierCurve newCurve){
		switch(smoothness){
//			case C0: if it is c0 do nothing since this just means the curves are joint
//				break;
			case C1:
				smoothC1(newCurve);
				break;
			case C2:
				smoothC2(newCurve);
				break;
		}
	}
	
	/**
	 * Smooths out the curves to create a path with c1 smoothness
	 * 
	 * @param curve New curve that was added, this will be used to determine where we are on the path and how to handle data
	 */
	private void smoothC1(BezierCurve curve){
		ArrayList<Vec3> edgeCurve = null;
		ArrayList<Vec3> bodyCurve = null;
		Vec3 joint = null, body = null, change = null;
		boolean shouldProp = false;//tracks whether we need to perform a propagation to reflect a change in the path smoothness
		//determine if the new curve added was added to the start or end of the path, this way we know what the flow of the data is
		if(curve == start.curve){
			edgeCurve = start.curve.getPoints();
			bodyCurve = start.next.curve.getPoints();
			joint = bodyCurve.get(0);
			//check which curve can be updated
			if(edgeCurve.size() < 3){//check if the new curve is just a line
				//in which case we want to make changes to the body curve not the edge
				body = edgeCurve.get(0);
				change = bodyCurve.get(1);
				//if the body curve we just adjusted was part of a 3 point curve then we need to propagate changes to the rest of the path
				shouldProp = bodyCurve.size() == 3;//4 point curves and higher order curves don't need updating
			}else{
				//otherwise we want to adjust the edge curve
				body = bodyCurve.get(1);
				change = edgeCurve.get(edgeCurve.size()-2);
			}
		}else{//otherwise it is the end curve
			edgeCurve = end.curve.getPoints();
			bodyCurve = end.prev.curve.getPoints();
			joint = edgeCurve.get(0);
			//check which curve can be updated
			if(edgeCurve.size() < 3){//check if the new curve is just a line
				//in which case we want to make changes to the body curve not the edge
				body = edgeCurve.get(1);
				change = bodyCurve.get(bodyCurve.size()-2);
				//if the body curve we just adjusted was part of a 3 point curve then we need to propagate changes to the rest of the path
				shouldProp = bodyCurve.size() == 3;//4 point curves and higher order curves don't need updating
			}else{
				//otherwise we want to adjust the edge curve
				body = bodyCurve.get(bodyCurve.size()-2);
				change = edgeCurve.get(1);
			}
		}
		
		//check to see if both adjacent curves are just lines
		if(edgeCurve.size() > 2 || bodyCurve.size() > 2){
			//since at least one of them is not a line we can linearize them
			linearize(joint, body, change);
		}
		
		//check if the edge curves are jointed and the path is closed
		if(isClosed){
			//then we also need to linearize these
			edgeCurve = end.curve.getPoints();
			bodyCurve = start.curve.getPoints();
			joint = bodyCurve.get(0);
			
			//determine which curve is more easily updated
			//not a line            and    body curve isn't more easily updated
			if(edgeCurve.size() > 2 && bodyCurve.size() <= edgeCurve.size()){
				//this means it isn't and can be updated normally
				body = bodyCurve.get(1);
				change = edgeCurve.get(edgeCurve.size()-2);
				//however we will need to propagate changes to the path only if the curve was a 3 point curve
				shouldProp = edgeCurve.size() == 3;
				linearize(joint, body, change);
			}else if(bodyCurve.size() > 2){//check if the body curve is a line
				body = edgeCurve.get(edgeCurve.size()-2);
				change = bodyCurve.get(1);
				//however we will need to propagate changes to the path
				shouldProp = true;
				linearize(joint, body, change);
			}//otherwise they are both lines and nothing can be done
		}
		
		//check if we need to propagate changes to the rest of the path
		if(shouldProp){
			//TODO right propagation function
		}
	}
	
	private void smoothC2(BezierCurve curve){
		ArrayList<Vec3> edgeCurve = null;
		ArrayList<Vec3> bodyCurve = null;
		Vec3 joint = null, body = null, change = null;
		boolean shouldProp = false;//tracks whether we need to perform a propagation to reflect a change in the path smoothness
		//determine if the new curve added was added to the start or end of the path, this way we know what the flow of the data is
		if(curve == start.curve){
			edgeCurve = start.curve.getPoints();
			bodyCurve = start.next.curve.getPoints();
			joint = bodyCurve.get(0);
			//check which curve can be updated
			if(edgeCurve.size() < 3){//check if the new curve is just a line
				//in which case we want to make changes to the body curve not the edge
				body = edgeCurve.get(0);
				change = bodyCurve.get(1);
				//if the body curve we just adjusted was part of a 3 point curve then we need to propagate changes to the rest of the path
				shouldProp = bodyCurve.size() == 3;//4 point curves and higher order curves don't need updating
			}else{
				//otherwise we want to adjust the edge curve
				body = bodyCurve.get(1);
				change = edgeCurve.get(edgeCurve.size()-2);
			}
		}else{//otherwise it is the end curve
			edgeCurve = end.curve.getPoints();
			bodyCurve = end.prev.curve.getPoints();
			joint = edgeCurve.get(0);
			//check which curve can be updated
			if(edgeCurve.size() < 3){//check if the new curve is just a line
				//in which case we want to make changes to the body curve not the edge
				body = edgeCurve.get(1);
				change = bodyCurve.get(bodyCurve.size()-2);
				//if the body curve we just adjusted was part of a 3 point curve then we need to propagate changes to the rest of the path
				shouldProp = bodyCurve.size() == 3;//4 point curves and higher order curves don't need updating
			}else{
				//otherwise we want to adjust the edge curve
				body = bodyCurve.get(bodyCurve.size()-2);
				change = edgeCurve.get(1);
			}
		}
		
		//check to see if both adjacent curves are just lines
		if(edgeCurve.size() > 2 || bodyCurve.size() > 2){
			//since at least one of them is not a line we can linearize them
			Vec3 adjustVec = VecUtil.subtract(joint, body).scale(2).add(body);//(joint-body)*2+body = 2*joint-2*body+body = 2*joint-3*body
			change.set(adjustVec);
		}
		
		//check if the edge curves are jointed and the path is closed
//		if(isClosed){
//			//then we also need to linearize these
//			edgeCurve = end.curve.getPoints();
//			bodyCurve = start.curve.getPoints();
//			joint = bodyCurve.get(0);
//			
//			//determine which curve is more easily updated
//			//not a line            and    body curve isn't more easily updated
//			if(edgeCurve.size() > 2 && bodyCurve.size() <= edgeCurve.size()){
//				//this means it isn't and can be updated normally
//				body = bodyCurve.get(1);
//				change = edgeCurve.get(edgeCurve.size()-2);
//				//however we will need to propagate changes to the path only if the curve was a 3 point curve
//				shouldProp = edgeCurve.size() == 3;Vec3 adjustVec = VecUtil.subtract(joint, body).scale(2).add(joint);//(joint-body)*2+joint = 2*joint-2*body+joint = 3*joint-2*body
//				change.set(adjustVec);
//			}else if(bodyCurve.size() > 2){//check if the body curve is a line
//				body = edgeCurve.get(edgeCurve.size()-2);
//				change = bodyCurve.get(1);
//				//however we will need to propagate changes to the path
//				shouldProp = true;Vec3 adjustVec = VecUtil.subtract(joint, body).scale(2).add(joint);//(joint-body)*2+joint = 2*joint-2*body+joint = 3*joint-2*body
//				change.set(adjustVec);
//			}//otherwise they are both lines and nothing can be done
//		}
		
		//check if we need to propagate changes to the rest of the path
		if(shouldProp){
			//TODO right propagation function
		}
	}
	
	/**
	 * Takes 3 points from a curve and linearizes them for c1 smoothness conditions. Only the end control will
	 * have its value updated, this is to reduce the need to update the entire curve 
	 * 
	 * @param joint Point that is shared between the curves
	 * @param bodyControl Control point coming from the main body of the path
	 * @param endControl Control point coming from the end curve.
	 */
	private void linearize(Vec3 joint, Vec3 bodyControl, Vec3 endControl){
		//get the angle between the lines formed between the joint and the control points
		Vec3 relaPoint1 = VecUtil.subtract(endControl, joint).normalize();//line from the joint to the start curve control
		Vec3 relaPoint2 = VecUtil.subtract(bodyControl, joint).normalize();//line from the joint to the second curve control
		double angle = Math.toDegrees(Math.acos(relaPoint1.dot(relaPoint2)));//angle bewtween the lines
		//check if they are already colinear
		if(VecUtil.distance(joint, bodyControl, endControl) == 0){
			//this means they are already co-linear
			//but test to see how the control points line up to guarantee they aren't on the same side of the joint
			//which in turn makes sharp edges
			
			//check if the angle is 180 or 0
			if(angle == 0){
				//if it is 0 then that means one of the control points is over extending the joint and needs to be adjusted
				//move the control point on the end curve, this preserves the rest of the curve smoothness
				//move the control point opposite the joint point the same distance it currently is from the joint
				//get a translation vector
				Vec3 adjust = VecUtil.subtract(joint, endControl);//from target control to joint
				adjust.scale(2);//translate once to reach the joint again to mirror it = adjust+adjust = 2*adjust
				//apply the translation
				endControl.add(adjust);
			}
		}else{//they are not co-linear
			//get the axis of rotation
			Vec3 axis = relaPoint1.cross(relaPoint2);
			Vec3 origVec = VecUtil.subtract(endControl, joint);//original vector from the join to the end control
			//get the rotation
			Quaternion rot = Quaternion.fromAxisAngle(axis, (float)angle-180);//we want angle to be negative to rotate it to be 180 to the body control
			endControl.add(rot.multVec(origVec).subtract(origVec));//perform the rotation and set the control point
		}
	}
	
	/**
	 * Gets the Bezier Curves that define this Bezier path object
	 * 
	 * @return ArrayList containing the Bezier curves taht make up this Bezier Path
	 */
	public ArrayList<BezierCurve> getCurves(){
		return curves;
	}
	
	private class BezierNode{
		public BezierNode next, prev;
		public BezierCurve curve;
		
		public BezierNode(){
			this(null, null, null);
		}
		
		public BezierNode(BezierNode next, BezierNode prev, BezierCurve data){
			this.next = next;
			this.prev = prev;
			curve = data;
		}
	}
}
