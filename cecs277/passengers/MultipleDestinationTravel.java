package cecs277.passengers;

import cecs277.Simulation;
import cecs277.buildings.Floor;
import cecs277.events.PassengerNextDestinationEvent;


import java.util.List;

public class MultipleDestinationTravel implements TravelStrategy {
    private List<Integer> mDestinations;
    private List<Long> mDurations;

    public MultipleDestinationTravel(List<Integer> destinations, List<Long> durations){
        mDestinations = destinations;
        mDurations = durations;
    }



    @Override
    public int getDestination() {
        if (mDestinations.isEmpty()){
            return 1;
        }
        else{
            return mDestinations.get(0);
        }
    }

    @Override
    public void scheduleNextDestination(Passenger passenger, Floor currentFloor) {
        if (currentFloor.getNumber() != 1){
            mDestinations.remove((Number)(currentFloor.getNumber()));
            Simulation s = currentFloor.getBuilding().getSimulation();
            if(mDestinations.size() > 1 && mDurations.size() > 1) {
                int nextDest = mDestinations.remove(0);
                long nextDur = mDurations.remove(0);
                PassengerNextDestinationEvent ev = new PassengerNextDestinationEvent(s.currentTime() + nextDur, passenger, currentFloor);
                s.scheduleEvent(ev);
            }
        }
        else{
            System.out.println("Passenger has left the building");
        }
    }
}
//        if(elevator.getCurrentFloor().getNumber() == 1) {
//            System.out.println("Worker has left the building");
//        }
//        else {
//            mDestinations.remove((Number)(elevator.getCurrentFloor().getNumber()));
//            Simulation s = elevator.getBuilding().getSimulation();
//            int nextDest = mDestinations.remove(0);
//            long nextDur = mDurations.remove(0);
//            s.scheduleEvent(new PassengerNextDestinationEvent(s.currentTime() + nextDur, this, elevator.getBuilding().getFloor(nextDest)));
//
//        }
//    }