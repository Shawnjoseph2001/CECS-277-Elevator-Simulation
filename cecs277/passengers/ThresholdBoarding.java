package cecs277.passengers;

import cecs277.elevators.Elevator;

public class ThresholdBoarding implements BoardingStrategy {
    private int mThreshold;
    public ThresholdBoarding(int threshold) {
        mThreshold = threshold;
    }

    /**
     * Returns true if the given passenger will board the given elevator.
     *
     * @param passenger
     * @param elevator
     */
    @Override
    public boolean willBoardElevator(Passenger passenger, Elevator elevator) {
        return elevator.getPassengerCount() <= mThreshold && elevator.getPassengerCount() <= elevator.getCapacity();
    }
}
