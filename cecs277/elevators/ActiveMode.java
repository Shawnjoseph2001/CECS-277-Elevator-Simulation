package cecs277.elevators;

import cecs277.Simulation;
import cecs277.buildings.Building;
import cecs277.buildings.Floor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An ActiveMode elevator is handling at least one floor request.
 */
public class ActiveMode implements OperationMode {
	
	// TODO: implement this class.
	// An active elevator cannot be dispatched, and will ignore direction requests from its current floor. (Only idle
	//    mode elevators observe floors, so an ActiveMode elevator will never observe directionRequested.)
	// The bulk of your Project 2 tick() logic goes here, except that you will never be in IDLE_STATE when active.
	// If you used to schedule a transition to IDLE_STATE, you should instead schedule an operation change to
	//    IdleMode in IDLE_STATE.
	// Otherwise your code should be almost identical, except you are no longer in the Elevator class, so you need
	//    to use accessors and mutators instead of directly addressing the fields of Elevator.


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
		Simulation s = elevator.getBuilding().getSimulation();
		Elevator.ElevatorState mCurrentState = elevator.getCurrentState();
		switch(mCurrentState) {
			case DOORS_OPENING:
				elevator.scheduleStateChange(Elevator.ElevatorState.DOORS_OPEN, 2);
				break;
			case DOORS_OPEN:
				int beforeFloor = elevator.getCurrentFloor().getWaitingPassengers().size();
				int beforeElevator = elevator.getPassengerCount();
				Set<ElevatorObserver> elements = new HashSet<>(elevator.getObservers());
				for(ElevatorObserver e:elements) {
					e.elevatorDoorsOpened(elevator);
				}
				elevator.mFloorRequests.remove(elevator.getCurrentFloor());
				int afterFloor = elevator.getCurrentFloor().getWaitingPassengers().size();
				int afterElevator = elevator.getPassengerCount();
				int boarded = beforeFloor - afterFloor;
				int left = boarded + beforeElevator - afterElevator;
				elevator.scheduleStateChange(Elevator.ElevatorState.DOORS_CLOSING, 1 + ((boarded + left) / 2));
				break;
			case DOORS_CLOSING:
				if(elevator.greaterFloorRequest()) {
					elevator.scheduleStateChange(Elevator.ElevatorState.ACCELERATING, 2);
				}
				else if(elevator.getFloorRequests().size() > 0) {
					Floor mNextRequest = elevator.mFloorRequests.iterator().next();
					if(mNextRequest.getNumber() > elevator.getCurrentFloor().getNumber()) {
						elevator.setCurrentDirection(Elevator.Direction.MOVING_UP);
					}
					else {
						elevator.setCurrentDirection(Elevator.Direction.MOVING_DOWN);
					}
					elevator.scheduleStateChange(Elevator.ElevatorState.DOORS_OPENING, 2);
				}
				else {
					elevator.setCurrentDirection(Elevator.Direction.NOT_MOVING);
					elevator.scheduleModeChange(new IdleMode(), Elevator.ElevatorState.IDLE_STATE, 2);
				}
				break;
			case ACCELERATING:
				elevator.getCurrentFloor().removeObserver(elevator);
				elevator.scheduleStateChange(Elevator.ElevatorState.MOVING, 3);
				break;
			case MOVING:
				if(elevator.getFloorRequests().contains(elevator.getBuilding().getFloor(elevator.getCurrentFloor().getNumber() + 1)) || elevator.getBuilding().getFloor(elevator.getCurrentFloor().getNumber() + 1).directionIsPressed(elevator.getCurrentDirection())) {
					elevator.scheduleStateChange(Elevator.ElevatorState.DECELERATING, 2);
				}
				else {
					elevator.scheduleStateChange(Elevator.ElevatorState.MOVING, 2);
				}
					if (elevator.getCurrentDirection() == Elevator.Direction.MOVING_UP && elevator.getCurrentFloor().getNumber() < elevator.getBuilding().getFloorCount()) {
						elevator.setCurrentFloor(elevator.getBuilding().getFloor(elevator.getCurrentFloor().getNumber() + 1));
					} else if (elevator.getCurrentDirection() == Elevator.Direction.MOVING_DOWN && elevator.getCurrentFloor().getNumber() > 1) {
						elevator.setCurrentFloor(elevator.getBuilding().getFloor(elevator.getCurrentFloor().getNumber() - 1));
					} else {
						elevator.scheduleModeChange(new IdleMode(), Elevator.ElevatorState.IDLE_STATE, 0);
						elevator.setCurrentDirection(Elevator.Direction.NOT_MOVING);
						System.out.println("Uhh..");
					}

				break;
			case DECELERATING:
				boolean b = false;
				for(Floor f:elevator.mFloorRequests) {
					if (f.getNumber() < elevator.getCurrentFloor().getNumber() && elevator.getCurrentDirection() == Elevator.Direction.MOVING_DOWN
							|| f.getNumber() > elevator.getCurrentFloor().getNumber() && elevator.getCurrentDirection() == Elevator.Direction.MOVING_UP) {
						b = true;
						break;
					}
				}
				if(b || elevator.getCurrentFloor().directionIsPressed(elevator.getCurrentDirection())) {

				}
				else if(elevator.getCurrentFloor().getWaitingPassengers().size() > 0) {
					if(elevator.getCurrentFloor().directionIsPressed(Elevator.Direction.MOVING_UP)) {
						elevator.setCurrentDirection(Elevator.Direction.MOVING_UP);
					}
					else {
						elevator.setCurrentDirection(Elevator.Direction.MOVING_DOWN);
					}

				}
				else {
					elevator.setCurrentDirection(Elevator.Direction.NOT_MOVING);
				}
				List<ElevatorObserver> el = new ArrayList<>(elevator.getObservers());
				for(ElevatorObserver e:el) {
					e.elevatorDecelerating(elevator);
				}
				for(Floor f:new ArrayList<Floor>(elevator.getFloorRequests())) {
					if(f == elevator.getCurrentFloor())
					elevator.mFloorRequests.remove(f);
				}
				elevator.scheduleStateChange(Elevator.ElevatorState.DOORS_OPENING, 3);
		}
	}

	@Override
	public String toString() {
		return "Active";
	}
}
