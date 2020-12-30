package cecs277.passengers;

import cecs277.elevators.Elevator;

import java.util.HashSet;

public class ClumsyEmbarking implements EmbarkingStrategy{
    /**
     * Called when the passenger entered the given elevator, giving a chance to request floors, etc.
     *
     * @param passenger
     * @param elevator
     */
    @Override
    public void enteredElevator(Passenger passenger, Elevator elevator) {
        int [] destinations = new int [2];
        destinations[0] = passenger.getDestination();
        if(passenger.getDestination() > elevator.getCurrentFloor().getNumber()) {
            destinations[1] = passenger.getDestination() - 1;
        }
        else {
           destinations[1] = passenger.getDestination() + 1;
        }
        elevator.addDestination(destinations[0]);
        elevator.addDestination(destinations[1]);
        System.out.println(elevator.getBuilding().getSimulation().currentTime() + ": " +  passenger + " clumsily requests floors " + destinations[0] + " and " + destinations[1] + "on Elevator " + elevator.getNumber()
        );
    }
}
