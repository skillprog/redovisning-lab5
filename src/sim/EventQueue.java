package sim;

import java.util.ArrayList;

/**
 * EventQueue declares an ArrayList that holds the events.
 */
public class EventQueue {
	public ArrayList<SimEvent> eventList;
	SimState state;

	/**
	 * The constructor initializes the variables state and startEvent and adds eventList to ArrayList.
	 * @param state
	 * @param startEvent
     */
	public EventQueue(SimState state, SimEvent startEvent){
		this.state = state;
		this.eventList = new ArrayList<>();
		eventList.add(startEvent);
	}

	/**
	 * loop adds eventList to a sequence and then calls sort(), execute() and shift().
	 */
	public void loop(){
		eventList = state.addInSequence(eventList);
		sort();
		eventList.get(0).execute();
		shift();
	}

	/**
	 * sort is sorting the eventList by time.
	 */
	private void sort(){
		eventList.sort((e1, e2) -> Double.compare(e1.getTime(), e2.getTime()));
	}

	/**
	 *
	 */
	private void shift(){
		if(eventList.get(0).getRemove()){
			eventList.remove(0);
		}
	}
}
