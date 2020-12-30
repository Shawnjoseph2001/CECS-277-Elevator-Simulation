package cecs277.events;

import cecs277.buildings.Building;
import cecs277.passengers.Passenger;
import cecs277.Simulation;
import cecs277.passengers.PassengerFactory;
import java.util.List;

/**
 * A simulation event that adds a new random passenger on floor 1, and then schedules the next spawn event.
 */
public class SpawnPassengerEvent extends SimulationEvent {
	private static long SPAWN_MEAN_DURATION = 10_800;
	private static long SPAWN_STDEV_DURATION = 3_600;

	// After executing, will reference the Passenger object that was spawned.
	private Passenger mPassenger;
	private Building mBuilding;
	
	public SpawnPassengerEvent(long scheduledTime, Building building) {
		super(scheduledTime);
		mBuilding = building;
	}
	
	@Override
	public String toString() {
		System.out.print("");
		return super.toString() + "Adding " + mPassenger.toString() + "[->" +  mPassenger.getDestination() + "] to floor 1.";
	}
	
	@Override
	public void execute(Simulation sim) {
		List<PassengerFactory> possibilities = sim.getFactories();
		int total = 0;
		for(PassengerFactory p:possibilities) {
			total += p.factoryWeight();
		}
		int r = sim.getRandom().nextInt(total);
		int totalC =  0;
		int i = 0;
		 do {
			if(possibilities.size() > i) {
				totalC += possibilities.get(i).factoryWeight();
				i++;
				if (i > possibilities.size() - 1) {
					i = possibilities.size();
					totalC = r;
				}
			}
			else {
				break;
			}
		} while(totalC <= r);
		PassengerFactory cst;
		if(i > 0) {
			cst = possibilities.get(i - 1);
		}
		else {
			cst = possibilities.get(0);
		}
		Passenger p = new Passenger(cst.createBoardingStrategy(sim), cst.createDebarkingStrategy(sim), cst.createEmbarkingStrategy(sim), cst.createTravelStrategy(sim), cst.shortName());
		mPassenger = p;
		mBuilding.getFloor(1).addWaitingPassenger(p);

/*		// 75% of all passengers are normal Visitors.
		if (r.nextInt(4) <= 2) {
			mPassenger = getVisitor();
		}
		else {
			mPassenger = getWorker();
		}
		mBuilding.getFloor(1).addWaitingPassenger(mPassenger);

		/*
		 TODO: schedule the new SpawnPassengerEvent with the simulation. Construct a new SpawnPassengerEvent
		 with a scheduled time that is X seconds in the future, where X is a uniform random integer from
		 1 to 30 inclusive.
		*/

		sim.scheduleEvent(new SpawnPassengerEvent(sim.currentTime() + sim.getRandom().nextInt(30) + 1, mBuilding));
	}
	
	/*
	private Passenger getVisitor() {
		/*
		 TODO: construct a VisitorPassenger and return it.
		 The visitor should have a random destination floor that is not floor 1 (generate a random int from 2 to N).
		 The visitor's visit duration should follow a NORMAL (GAUSSIAN) DISTRIBUTION with a mean of 1 hour
		 and a standard deviation of 20 minutes.
		 *
		Random r = mBuilding.getSimulation().getRandom();
		int destinationFloor = 2 + r.nextInt(mBuilding.getFloorCount() - 1);
		int visitDuration = 3600 + (int)(r.nextGaussian() * 1201);
		// Look up the documentation for the .nextGaussian() method of the Random class.
		return new VisitorPassenger(destinationFloor, visitDuration);
	}
	*/
	/*
	private Passenger getWorker() {
		/*
		TODO: construct and return a WorkerPassenger. A Worker requires a list of destinations and a list of durations.
		To generate the list of destinations, first generate a random number from 2 to 5 inclusive. Call this "X",
		how many floors the worker will visit before returning to floor 1.
		X times, generate an integer from 2 to N (number of floors) that is NOT THE SAME as the previously-generated floor.
		Add those X integers to a list.
		To generate the list of durations, generate X integers using a NORMAL DISTRIBUTION with a mean of 10 minutes
		and a standard deviation of 3 minutes.
		 *
		Random r = mBuilding.getSimulation().getRandom();
		int x = 2 + r.nextInt(4);
		List<Integer> floors = new ArrayList<>();
		List<Long> durations = new ArrayList<>();
		floors.add(2 + r.nextInt(mBuilding.getFloorCount() - 1));
		for(int i = 1; i < x; i++) {
			int nextAdd = 2 + r.nextInt(mBuilding.getFloorCount() - 1);
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
		return new WorkerPassenger(floors, durations);
	}

	 */
}
