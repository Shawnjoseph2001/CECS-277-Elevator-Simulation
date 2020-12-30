package cecs277.events;

import cecs277.Simulation;
import cecs277.buildings.Floor;
import cecs277.passengers.Passenger;
import cecs277.passengers.TravelStrategy;

public class SingleDestinationTravel implements TravelStrategy {
    private int mDestination;
    private long mDuration;


    public SingleDestinationTravel(int destination, long duration){
        mDestination = destination;
        mDuration = duration;
    }


    @Override
    public int getDestination() {
        return mDestination;
    }

    @Override
    public void scheduleNextDestination(Passenger passenger, Floor currentFloor) {
        Simulation s = currentFloor.getBuilding().getSimulation();
        if (currentFloor.getNumber() != 1){
            PassengerNextDestinationEvent ev = new PassengerNextDestinationEvent(s.currentTime() + mDuration, passenger, currentFloor);
            s.scheduleEvent(ev);
        }
        else{
            System.out.println("Passenger has left the building");
        }
    }

}
