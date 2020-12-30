package cecs277.elevators;

import cecs277.buildings.Floor;

import javax.print.attribute.standard.DialogOwner;

import java.util.ArrayList;

import static cecs277.elevators.Elevator.ElevatorState.*;

/**
 * A DispatchMode elevator is in the midst of a dispatch to a target floor in order to handle a request in a target
 * direction. The elevator will not stop on any floor that is not its destination, and will not respond to any other
 * request until it arrives at the destination.
 */
public class DispatchMode implements OperationMode {
	// The destination floor of the dispatch.
	private Floor mDestination;
	// The direction requested by the destination floor; NOT the direction the elevator must move to get to that floor.
	private Elevator.Direction mDesiredDirection;
	
	public DispatchMode(Floor destination, Elevator.Direction desiredDirection) {
		mDestination = destination;
		mDesiredDirection = desiredDirection;
	}
	
	// TODO: implement the other methods of the OperationMode interface.
	// Only Idle elevators can be dispatched.
	// A dispatching elevator ignores all other requests.
	// It does not check to see if it should stop of floors that are not the destination.
	// Its flow of ticks should go: IDLE_STATE -> ACCELERATING -> MOVING -> ... -> MOVING -> DECELERATING.
	//    When decelerating to the destination floor, change the elevator's direction to the desired direction,
	//    announce that it is decelerating, and then schedule an operation change in 3 seconds to
	//    ActiveOperation in the DOORS_OPENING state.
	// A DispatchOperation elevator should never be in the DOORS_OPENING, DOORS_OPEN, or DOORS_CLOSING states.


	/**
	 * Returns true if the given elevator is currently able to accept a dispatch request to the given floor.
	 *
	 * @param elevator
	 * @param floor
	 */
	public boolean canBeDispatchedToFloor(Elevator elevator, Floor floor) {
		return false;
	}

	/**
	 * Asks the given elevator to dispatch to the given floor and direction. If this is a legal operation in the given mode,
	 * this schedules a transition to DispatchOperation.
	 *
	 * @param elevator
	 * @param targetFloor
	 * @param targetDirection
	 */
	public void dispatchToFloor(Elevator elevator, Floor targetFloor, Elevator.Direction targetDirection) {

	}

	/**
	 * Informs the given elevator that the floor it is currently on (also given) has requested an elevator going in
	 * the given direction.
	 *
	 * @param elevator
	 * @param floor
	 * @param direction
	 */
	public void directionRequested(Elevator elevator, Floor floor, Elevator.Direction direction) {

	}

	/**
	 * Ticks the elevator to progress its physical state in the simulation.
	 *
	 * @param elevator
	 */
	public void tick(Elevator elevator) {
		switch(elevator.getCurrentState()) {
			case IDLE_STATE:
				if(mDestination.getNumber() > elevator.getCurrentFloor().getNumber()) {
					elevator.setCurrentDirection(Elevator.Direction.MOVING_UP);
					elevator.scheduleStateChange(ACCELERATING, 0);
				}
				else if(mDestination.getNumber() < elevator.getCurrentFloor().getNumber()) {
					elevator.setCurrentDirection(Elevator.Direction.MOVING_DOWN);
					elevator.scheduleStateChange(ACCELERATING, 0);
				}
				else if(mDestination == elevator.getCurrentFloor()) {
					elevator.scheduleModeChange(new ActiveMode(), DOORS_OPENING, 2);
		}
				break;
			case ACCELERATING:
				elevator.scheduleStateChange(MOVING, 3);
				break;
			case MOVING:
				if(elevator.getCurrentDirection() == Elevator.Direction.MOVING_DOWN && elevator.getBuilding().getFloor(elevator.getCurrentFloor().getNumber() - 1) == mDestination
				|| elevator.getCurrentDirection() == Elevator.Direction.MOVING_UP && elevator.getBuilding().getFloor(elevator.getCurrentFloor().getNumber() + 1) == mDestination){
					elevator.scheduleStateChange(DECELERATING, 2);
				}
				else {
					elevator.scheduleStateChange(MOVING, 2);
				}
					Floor nextFloor = null;
					if(elevator.getCurrentDirection() == Elevator.Direction.MOVING_UP) {
						nextFloor = elevator.getBuilding().getFloor(elevator.getCurrentFloor().getNumber() + 1);
					}
					else {
						if(elevator.getCurrentFloor().getNumber() > 1) {
							nextFloor = elevator.getBuilding().getFloor(elevator.getCurrentFloor().getNumber() - 1);
						}
					}
					elevator.removeObserver(elevator.getCurrentFloor());
					elevator.setCurrentFloor(nextFloor);
				break;
			case DECELERATING:
				elevator.scheduleStateChange(DOORS_OPENING, 3);
				elevator.setCurrentDirection(mDesiredDirection);
				for(ElevatorObserver e:new ArrayList<ElevatorObserver>(elevator.getObservers())) {
					e.elevatorDecelerating(elevator);
				}
				break;
			case DOORS_OPENING:
				elevator.scheduleModeChange(new ActiveMode(), DOORS_OPEN, 2);
				break;
			default:
				System.out.println("Error");
		}

	}

	@Override
	public String toString() {
		return "Dispatching to " + mDestination.getNumber() + " " + mDesiredDirection;
	}
}
