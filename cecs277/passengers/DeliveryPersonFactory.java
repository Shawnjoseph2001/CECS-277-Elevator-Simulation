package cecs277.passengers;

import cecs277.Simulation;

import java.util.ArrayList;

import java.util.List;

public class DeliveryPersonFactory implements PassengerFactory {
    private int mWeight;
    /**
     * Gets the name of this factory, that is, a brief description of a passenger that it creates.
     */
    @Override
    public String factoryName() {
        return "DeliveryPersonFactory";
    }
    public DeliveryPersonFactory(int weight) {
        mWeight = weight;
    }

    /**
     * Gets a short (1 or 2 letter) abbreviation for the name of this factory.
     */
    @Override
    public String shortName() {
        return "DF";
    }

    /**
     * Gets the weight with which this factory should be selected in a uniform random selection of known factories.
     */
    @Override
    public int factoryWeight() {
        return mWeight;
    }

    /**
     * Creates a BoardingStrategy used by passengers represented by this factory.
     *
     * @param simulation a reference to the simulation, in case the strategy needs a source of random numbers
     *                   or other information about the simulation.
     */
    @Override
    public BoardingStrategy createBoardingStrategy(Simulation simulation) {
        return new ThresholdBoarding(5);
    }

    /**
     * Creates a TravelStrategy used by passengers represented by this factory.
     *
     * @param simulation a reference to the simulation, in case the strategy needs a source of random numbers
     *                   or other information about the simulation.
     */
    @Override
    public TravelStrategy createTravelStrategy(Simulation simulation) {
        List<Integer> destinations = new ArrayList<>();
        List<Long> durations = new ArrayList<>();
        int numFloors = 1 + simulation.getRandom().nextInt(simulation.getBuilding().getFloorCount());
        int j = 0;
        while(j < numFloors) {
            int rNum = simulation.getRandom().nextInt(simulation.getBuilding().getFloorCount() - 1) + 1;
            if(!destinations.contains(rNum)) {
                destinations.add(rNum);
                j++;
            }
        }
        for(int i = 0; i < numFloors; i++) {
              durations.add((long) (300 + simulation.getRandom().nextGaussian() * 10));
        }
        return new MultipleDestinationTravel(destinations, durations);
    }

    /**
     * Creates a EmbarkingStrategy used by passengers represented by this factory.
     *
     * @param simulation a reference to the simulation, in case the strategy needs a source of random numbers
     *                   or other information about the simulation.
     */
    @Override
    public EmbarkingStrategy createEmbarkingStrategy(Simulation simulation) {
        return new ResponsibleEmbarking();
    }

    /**
     * Creates a DebarkingStrategy used by passengers represented by this factory.
     *
     * @param simulation a reference to the simulation, in case the strategy needs a source of random numbers
     *                   or other information about the simulation.
     */
    @Override
    public DebarkingStrategy createDebarkingStrategy(Simulation simulation) {
        return new DistractedDebarking();
    }
}
