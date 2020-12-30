package cecs277.buildings;

import cecs277.elevators.ElevatorObserver;
import cecs277.passengers.Passenger;
import cecs277.elevators.Elevator;

import java.util.*;

public class Floor implements ElevatorObserver {
	private Building mBuilding;
	private List<Passenger> mPassengers = new ArrayList<>();
	private ArrayList<FloorObserver> mObservers = new ArrayList<>();
	private int mNumber;
	private HashSet<Elevator.Direction> mDirections = new HashSet<>();
	
	// TODO: declare a field(s) to help keep track of which direction buttons are currently pressed.
	// You can assume that every floor has both up and down buttons, even the ground and top floors.
	
	public Floor(int number, Building building) {
		mNumber = number;
		mBuilding = building;
	}
	public Building getBuilding() {
		return mBuilding;
	}
	
	/**
	 * Sets a flag that the given direction has been requested by a passenger on this floor. If the direction
	 * had NOT already been requested, then all observers of the floor are notified that directionRequested.
	 * @param direction
	 */
	public void requestDirection(Elevator.Direction direction) {
		// TODO: implement this method as described in the comment.
		if(!mDirections.contains(direction)) {
			mDirections.add(direction);
			List<FloorObserver> f = new ArrayList<>(mObservers);
			for(FloorObserver o:f) {
				o.directionRequested(this, direction);
			}
		}
	}
	
	/**
	 * Returns true if the given direction button has been pressed.
	 */
	public boolean directionIsPressed(Elevator.Direction direction) {
		// TODO: complete this method.
		return mDirections.contains(direction);

	}
	public Set<Elevator.Direction> getDirections() {
		return mDirections;
	}
	
	/**
	 * Clears the given direction button so it is no longer pressed.
	 */
	public void clearDirection(Elevator.Direction direction) {
		// TODO: complete this method.
		mDirections.remove(direction);
	}
	
	/**
	 * Adds a given Passenger as a waiting passenger on this floor, and presses the passenger's direction button.
	 */
	public void addWaitingPassenger(Passenger p) {
		mPassengers.add(p);
		addObserver(p);
		p.setState(Passenger.PassengerState.WAITING_ON_FLOOR);
		Elevator.Direction dirCall;
		if(p.getDestination() > mNumber) {
			dirCall = Elevator.Direction.MOVING_UP;
		}
		else {
			dirCall = Elevator.Direction.MOVING_DOWN;
		}
		// TODO: call requestDirection with the appropriate direction for this passenger's destination.
		requestDirection(dirCall);
		p.directionRequested(this, dirCall);
	}
	
	/**
	 * Removes the given Passenger from the floor's waiting passengers.
	 */
	public void removeWaitingPassenger(Passenger p) {
		mPassengers.remove(p);
	}
	
	
	// Simple accessors.
	public int getNumber() {
		return mNumber;
	}
	
	public List<Passenger> getWaitingPassengers() {
		return mPassengers;
	}
	
	@Override
	public String toString() {
		return "Floor " + mNumber;
	}
	
	// Observer methods.
	public void removeObserver(FloorObserver observer) {
		mObservers.remove(observer);
	}
	
	public void addObserver(FloorObserver observer) {
		if(!mObservers.contains(observer)) {
			mObservers.add(observer);
		}
	}
	
	// Observer methods.
	@Override
	public void elevatorDecelerating(Elevator elevator) {
		// TODO: if the elevator is arriving at THIS FLOOR, alert all the floor's observers that elevatorArriving.
		// TODO:    then clear the elevator's current direction from this floor's requested direction buttons.
		if(elevator.getCurrentFloor() == this) {
			List<FloorObserver> obsList = new ArrayList<>(mObservers);
			for(FloorObserver o:obsList) {
				o.elevatorArriving(this, elevator);
			}
			mDirections.remove(elevator.getCurrentDirection());
		}
	}
	
	@Override
	public void elevatorDoorsOpened(Elevator elevator) {
		// Not needed.
	}
	
	@Override
	public void elevatorWentIdle(Elevator elevator) {
		if(mDirections.size() > 0) {
			elevator.dispatchTo(this, mDirections.iterator().next());
		}
	}
}
