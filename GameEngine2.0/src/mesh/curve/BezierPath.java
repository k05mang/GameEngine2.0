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
		start = new BezierNode(null);
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
//			end = new BezierNode(null, start, null);//create a new node for end
//			start.next = end;//set the next node to be the end node
//		}else if(curves.size() == 1 && !isPoint){
//			end.curve = new BezierCurve(curve);
//			curves.add(end.curve);
		}else if(!isClosed && !isPoint){
			ArrayList<Vec3> points = curve.getPoints();
			//determine if the curve should be added to the front, end, or neither
			Vec3 startPoint = points.get(0), endPoint = points.get(points.size()-1);
			PathJoint joint = null;
			if(end.curve.getBezierPoint(1).equals(startPoint)){//check if the start point matches this end
				//in this case we only need to add the curve and make it the new end
				BezierNode curEnd = end;
				end = new BezierNode(new BezierCurve(curve));
				joint = new PathJoint(curEnd, end);
				curves.add(end.curve);
				//associate the joint with the nodes
				curEnd.forwardJoint = joint;
				end.backJoint = joint;
			}else if(end.curve.getBezierPoint(1).equals(endPoint)){//check if the end point matches
				//in this case we need to add the curve but reverse, this way values read consecutively along the path
				//are order in the same direction making reading the path much easier
				BezierNode curEnd = end;
				end = new BezierNode(new BezierCurve(curve, true));
				joint = new PathJoint(curEnd, end);
				curves.add(end.curve);
				//associate the joint with the nodes
				curEnd.forwardJoint = joint;
				end.backJoint = joint;
			}
			//compare with the start curves start point
			else if(start.curve.getBezierPoint(0).equals(endPoint)){//check if the end point matches this end
				//in this case we only need to add the curve and make it the new start
				BezierNode curStart = start;
				start = new BezierNode(new BezierCurve(curve));
				joint = new PathJoint(start, curStart);
				curves.add(start.curve);
				//associate the joint with the nodes
				curStart.backJoint = joint;
				start.forwardJoint = joint;
			}else if(start.curve.getBezierPoint(0).equals(startPoint)){//check if the start point matches
				//in this case we need to add the curve but reverse, this way values read consecutively along the path
				//are order in the same direction making reading the path much easier
				BezierNode curStart = start;
				start = new BezierNode(new BezierCurve(curve, true));
				joint = new PathJoint(start, curStart);
				curves.add(start.curve);
				//associate the joint with the nodes
				curStart.backJoint = joint;
				start.forwardJoint = joint;
			}else{//else they dont match
				return false;
			}
			//smooth the joint
			joint.smooth(smoothness);
			//check if the start and end curves match tips, in which case the loop is closed
			if(end.curve.getBezierPoint(1).equals(start.curve.getBezierPoint(0))){
				joint = new PathJoint(end, start);
				//if they are connect the list and mark it closed
				start.backJoint = joint;
				end.forwardJoint = joint;
				isClosed = true;
				//smooth the joint
				joint.smooth(smoothness);
			}
		}else{//the path is closed and isn't accepting new curves
			return false;
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
	 * Gets the current starting Bezier Curve for this Bezier Path
	 * 
	 * @return Curve that is at the start of this BezierPath
	 */
	public BezierCurve start(){
		return start.curve;
	}
	
	/**
	 * Gets the current ending Bezier Curve for this Bezier Path
	 * 
	 * @return Curve that is at the end of this BezierPath
	 */
	public BezierCurve end(){
		return end.curve;
	}
	
	/**
	 * Gets the Bezier Curves that define this Bezier path object
	 * 
	 * @return ArrayList containing the Bezier curves that make up this Bezier Path
	 */
	public ArrayList<BezierCurve> getCurves(){
		return curves;
	}
	
	/**
	 * Maintains the flow and connection between Bezier Curves along the path, and maintains the joints between these curve nodes
	 * 
	 * @author Kevin Mango
	 *
	 */
	private class BezierNode{
		public PathJoint forwardJoint, backJoint;
		public BezierCurve curve;
		
		public BezierNode(BezierCurve data){
			forwardJoint = null;
			backJoint = null;
			curve = data;
		}
	}
	
	/**
	 * Represents the joint between two bezier curves in the path. This class can handle the smoothing and propagation of that smoothing.
	 * @author Kevin Mango
	 *
	 */
	private class PathJoint{
		private BezierNode prev, next;
		private Vec3 incoming, incomingPrev, outgoing, outgoingNext, joint;
		private Continuity curSmoothing;
		
		/**
		 * Constructs a Bezier path joint based on the nodes provided. 
		 * 
		 * @param prev Previous node to take data from and update
		 * @param next Next node to take data from and update 
		 */
		public PathJoint(BezierNode prev, BezierNode next){
			this.prev = prev;
			this.next = next;
			curSmoothing = null;
			
			joint = null;
			ArrayList<Vec3> points = null;
			
			//set the data taken from the previous node
			points = prev.curve.getPoints();
			incoming = points.get(points.size()-2);
			//check to make sure we can actually get a secondary point from the curve
			if(points.size() > 2){
				incomingPrev = points.get(points.size()-3);
			}
			
			//set the data taken from the next node
			points = next.curve.getPoints();
			outgoing = points.get(1);
			//set the joint
			joint = points.get(0);
			
			if(points.size() > 2){
				outgoingNext = points.get(2);
			}
		}
		
		public void smooth(Continuity smoothness){
			smooth(smoothness, false, false);
		}
		
		protected void smooth(Continuity smoothness, boolean constrainIn, boolean constrainOut){
			if(isClosed){
				if(start.backJoint == this){
					return;
				}
			}
			
//			if(VecUtil.distance(incoming, outgoing, joint) == 0){
//				return;
//			}
			
			//check to make sure that the curves this joint merges are both order 2 or higher, if one is a line ignore the smoothing
			if(prev.curve.getOrder() < 2 || next.curve.getOrder() < 2){
				return;
			}
			
			curSmoothing = smoothness;
//			if(curSmoothing == null){
//				System.out.println("Cursmoothing");
//			}
			switch(smoothness){
				case C0:
					return;
				case G1:
					if(VecUtil.distance(incoming, outgoing, joint) == 0){
						return;
					}
					linearize(constrainIn, constrainOut);
					break;
				case C1:
					if(VecUtil.distance(incoming, outgoing, joint) == 0){
						midShift(constrainIn, constrainOut);
						return;
					}else 
						if(linearize(constrainIn, constrainOut)){
						midShift(constrainIn, constrainOut);
					}
					break;
				case C2:
					if(linearize(constrainIn, constrainOut)){
						midShift(constrainIn, constrainOut);
						neighborShift();
					}
					break;
			}
			//propagate these changes to adjacent joints if necessary
			
			//determine if the adjacent curves are 3 point curves, these are the only curves that need to be updated on the other joint
			//3 points curves share the single off curve control with both joints and lines are end points for propagation
			if(prev.curve.getOrder() == 2 &&  //the previous curve has 3 points
					prev.backJoint != null && //has a joint with another curve going backward
					!constrainIn){//and we aren't constraining the incoming control
				prev.backJoint.smooth(smoothness, false, true);
			}
			
			if(next.curve.getOrder() == 2 && //the adjacent next curve has 3 points
					next.forwardJoint != null && //has a joint with another curve going forward
					!constrainOut){//and we aren't constraining the outgoing control
				next.forwardJoint.smooth(smoothness, true, false);
			}
		}
		
		/**
		 * Takes three points from a curve and linearizes them for c1 smoothness conditions. Both control points
		 * are adjusted and share in the shift.
		 * 
		 * @param constrainIn Determines if the incoming control should not be modified and only the outgoing or both
		 * @param constrainOut Determines if the outgoing control should not be modified and only the incoming or both
		 * 
		 * @return True if the joint can be linearized and therefore smoothed, false if it cannot. This is decided based on the angle
		 * between the controls being greater than 90 degrees acute angles will not be linearized
		 */
		private boolean linearize(boolean constrainIn, boolean constrainOut){
			//if we are to constrain movement on both controls simply do nothing and return
			if(constrainIn && constrainOut){
				return false;
			}
			//get the angle between the lines formed between the joint and the control points
			Vec3 relaPoint1 = VecUtil.subtract(outgoing, joint).normalize();//line from the joint to the start curve control
			Vec3 relaPoint2 = VecUtil.subtract(incoming, joint).normalize();//line from the joint to the second curve control
			double angle = Math.toDegrees(Math.acos(relaPoint1.dot(relaPoint2)));//angle between the lines
			//check if they are already colinear
			if(angle <= 90){
//				//this means they are already co-linear and that the control points lie on the same side of the joint
//				//TODO update this to be contextual and adjust the control that is on the wrong side of the joint
//				Vec3 adjust = VecUtil.subtract(joint, outgoing);//from target control to joint
//				adjust.scale(2);//translate once to reach the joint again to mirror it = adjust+adjust = 2*adjust
//				//apply the translation
//				outgoing.add(adjust);
				return false;
			}else{//they are not co-linear
				//get the axis of rotation
				Vec3 axis = relaPoint1.cross(relaPoint2);
				Vec3 outVec = VecUtil.subtract(outgoing, joint);//original vector from the joint to the end control
				Vec3 inVec = VecUtil.subtract(incoming, joint);
				float adjustAngle = (float)(180-angle);
				
				//determine if we are only modifying one control
				if(!constrainIn && !constrainOut){
					adjustAngle /= 2.0f;
				}
				//get the rotation
				if(!constrainOut){
					Quaternion outRot = Quaternion.fromAxisAngle(axis, -adjustAngle);//we want angle to be negative to rotate it to be 180 to the body control
					outgoing.set(outRot.multVec(outVec).add(joint));//perform the rotation and set the control point
				}
				
				if(!constrainIn){
					Quaternion inRot = Quaternion.fromAxisAngle(axis, adjustAngle);//we want angle to be negative to rotate it to be 180 to the body control
					incoming.set(inRot.multVec(inVec).add(joint));//perform the rotation and set the control point
				}
				
//				Quaternion outRot = Quaternion.fromAxisAngle(axis, -adjustAngle);//we want angle to be negative to rotate it to be 180 to the body control
//				outgoing.set(outRot.multVec(outVec).add(joint));//perform the rotation and set the control point
				return true;
			}
		}
		
		/**
		 * Given three linearized control points that define a joint between two bezier curves in the path this method will adjust both control points such 
		 * that the joint point is the mid point between both control points.
		 * 
		 * @param constrainIn Determines if the incoming control should not be modified and only the outgoing or both
		 * @param constrainOut Determines if the outgoing control should not be modified and only the incoming or both
		 */
		private void midShift(boolean constrainIn, boolean constrainOut){
			//if we are to constrain movement on both controls simply do nothing and return
			if(constrainIn && constrainOut){
				return;
			}
			//get the vectors relative to the joint point
			Vec3 inVec = VecUtil.subtract(incoming, joint);
			Vec3 outVec = VecUtil.subtract(outgoing, joint);
			
			//get the vector we will use to adjust the control points
			Vec3 adjustVec = VecUtil.add(inVec, outVec);//since they are already opposite of each other then addition is the same as if we were subtracting
			
			//determine whether the adjust vector needs to be halved to distribute the change based on the constraints
			if(!constrainIn && !constrainOut){
				adjustVec.scale(.5f);
			}
			//adjust the control points
			if(!constrainIn){
				incoming.subtract(adjustVec);
			}
			
			if(!constrainOut){
				outgoing.subtract(adjustVec);
			}
//			outgoing.subtract(adjustVec);
		}
		
		private void neighborShift(){
			//determine if there are neighboring controls to manipulate
			if(incomingPrev == null || outgoingNext == null){
				return;
			}
			//get the vector to make the adjustments with on the neighboring vectors
			Vec3 adjustDir = VecUtil.subtract(outgoingNext, incomingPrev).normalize();
			//get the length of the joint vectors
			float jointLen = VecUtil.subtract(incoming, joint).length();
			
			outgoingNext.set(adjustDir.scale(4*jointLen).add(incomingPrev));
		}
	}
}
