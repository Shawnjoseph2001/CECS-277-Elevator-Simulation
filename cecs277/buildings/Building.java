package cecs277.buildings;

import cecs277.passengers.Passenger;
import cecs277.Simulation;
import cecs277.elevators.Elevator;
import cecs277.elevators.ElevatorObserver;

import java.util.*;

public class Building implements ElevatorObserver, FloorObserver {
	private List<Elevator> mElevators = new ArrayList<>();
	private List<Floor> mFloors = new ArrayList<>();
	private Simulation mSimulation;
	private Queue<Integer> mWaitingFloors = new ArrayDeque<>();
	
	public Building(int floors, int elevatorCount, Simulation sim) {
		mSimulation = sim;
		
		// Construct the floors, and observe each one.
		for (int i = 0; i < floors; i++) {
			Floor f = new Floor(i + 1, this);
			f.addObserver(this);
			mFloors.add(f);
		}
		
		// Construct the elevators, and observe each one.
		for (int i = 0; i < elevatorCount; i++) {
			Elevator elevator = new Elevator(i + 1, this);
			elevator.addObserver(this);
			for (Floor f : mFloors) {
				elevator.addObserver(f);
			}
			mElevators.add(elevator);
		}
	}
	

	// TODO: recreate your toString() here.
	public String toString() {
		StringBuilder ret = new StringBuilder();
		for (int i = mFloors.size() - 1; i > 0; i--) {
			ret.append(getFloor(i).getNumber()).append(": | ");
			//boolean hasE = false;
			for(Elevator e:mElevators) {
				if (e.getCurrentFloor() == getFloor(i)) {
					ret.append("X");
					break;
				}
				else {
					ret.append(" ");
				}
				ret.append(" | ");
			}
			for(Passenger p:getFloor(i).getWaitingPassengers()) {
				ret.append(" ").append(p.getDestination());
			}
			ret.append("\n");
		}
		return ret.toString();
	}
	private String rightPad(int s) {
		//time to break millions of JS programs
		if ((s + "").length() == 1) {
			return " " + s;
		} else if(("" + s).length() == 2){
			return "" + s;
		}
		else {
			return s + "ERROR";
		}
		//That wasn't that hard, was it?
	}
	
	public int getFloorCount() {
		return mFloors.size();
	}
	
	public Floor getFloor(int floor) {
			return mFloors.get(floor - 1);
	}
	
	public Simulation getSimulation() {
		return mSimulation;
	}
	
	
	@Override
	public void elevatorDecelerating(Elevator elevator) {
		// Have to implement all interface methods even if we don't use them.
	}
	
	@Override
	public void elevatorDoorsOpened(Elevator elevator) {
		// Don't care.
		mWaitingFloors.remove(elevator.getCurrentFloor().getNumber());
	}
	
	@Override
	public void elevatorWentIdle(Elevator elevator) {
		if(!mWaitingFloors.isEmpty()) {
		// TODO: if mWaitingFloors is not empty, remove the first entry from the queue and dispatch the elevator to that floor.
			Floor f = getFloor(mWaitingFloors.remove());
			if(f.getDirections().isEmpty()) {
				elevator.dispatchTo(f, Elevator.Direction.NOT_MOVING);
			}
			else {
				elevator.dispatchTo(f, f.getDirections().iterator().next());
			}
		}
	}
	
	@Override
	public void elevatorArriving(Floor sender, Elevator elevator) {
		// TODO: add the floor mWaitingFloors if it is not already in the queue.
		if(mWaitingFloors.contains(sender.getNumber())) {
			mWaitingFloors.remove(sender.getNumber());
		}
	}
	
	@Override
	public void directionRequested(Floor floor, Elevator.Direction direction) {
		// TODO: go through each elevator. If an elevator is idle, dispatch it to the given floor.
		// TODO: if no elevators are idle, then add the floor number to the mWaitingFloors queue.
		boolean b = true;
		for(Elevator e:mElevators) {
			if(e.canBeDispatchedToFloor(floor)) {
				e.dispatchTo(floor, direction);
				b = false;
			}
		}
		if(b) {
			if(!mWaitingFloors.contains(floor.getNumber())) {
				mWaitingFloors.add(floor.getNumber());
			}
		}
	}
}
