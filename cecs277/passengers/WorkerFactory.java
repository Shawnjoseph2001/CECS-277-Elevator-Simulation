package cecs277.passengers;

import cecs277.Simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorkerFactory implements PassengerFactory {
    private int mWeight;
    /**
     * Gets the name of this factory, that is, a brief description of a passenger that it creates.
     */
    @Override
    public String factoryName() {
        return "WorkerFactory";
    }
    public WorkerFactory(int weight) {
        mWeight = weight;
    }

    /**
     * Gets a short (1 or 2 letter) abbreviation for the name of this factory.
     */
    @Override
    public String shortName() {
        return "WF";
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
        return new ThresholdBoarding(3);
    }

    /**
     * Creates a TravelStrategy used by passengers represented by this factory.
     *
     * @param simulation a reference to the simulation, in case the strategy needs a source of random numbers
     *                   or other information about the simulation.
     */
    @Override
    public TravelStrategy createTravelStrategy(Simulation simulation) {
        Random r = simulation.getRandom();
        int x = 2 + r.nextInt(4);
        List<Integer> floors = new ArrayList<>();
        List<Long> durations = new ArrayList<>();
        floors.add(2 + r.nextInt(simulation.getBuilding().getFloorCount() - 1));
        for(int i = 1; i < x; i++) {
            int nextAdd = 2 + r.nextInt(simulation.getBuilding().getFloorCount() - 1);
            if(nextAdd != floors.get(i - 1)) {
                floors.add(nextAdd);
            }
            else {
                i--;
            }
        }
        for(int i = 0; i < x; i++) {
            durations.add((long)(600 + 300 * r.nextGaussian()));
        }
        return new MultipleDestinationTravel(floors, durations);
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
        return new AttentiveDebarking();
    }
}
