package carwash;


import sim.SimEvent;


public class CWEvent implements SimEvent {
	
	CWState state;
	private double time = 0;
	private int carId = 0;
	private int action = 1; //Default ARRIVE

	private int START = 0;
	private int ARRIVE = 1;
	private int LEAVE = 2;
	private int STOP = 3;
	//int successArrives = 0;
	
	private boolean stopping = false;
	private boolean removing = false;
	
	//Anv�nds i LEAVE() kollar om bilen �kte fr�n snabb eller l�ngsam tvät

	private boolean fast = false;
	private boolean slow = false;

	public CWEvent(double time, int carId, CWState state){
		this.time = time;
		state.setPreviousEventTime(time);
		this.state = state;
		this.carId = carId;
	}
	
	public void execute(){
		if(time == 0){
			start();
		}
		else if(time >= state.getMaxTime()){
			stop();
		}
		else if(action == ARRIVE){
			Arrival();
		}else if(action == LEAVE){
			Leave();
		}
	}
	
	private void start(){
		idle();
		action = START;
		state.setEvent(action);//Uppdaterar i carwashview
		removing = true;
	}
	
	private void stop(){
		idle();
		state.setQueueTime(state.getMaxTime());
		action = STOP;
		state.setSimulationTime(state.getMaxTime());
		state.setEvent(action);
		stopping = true;
	}

	private void Arrival(){
		idle(); //R�knar samman maskinernas idle time
		state.setQueueTime(time); //R�knar samman k�tiderna enligt pdf exempel... fast den r�knar �ven rejected cars..
		if(state.getFastWashers() > 0){
			state.setSimulationTime(time);
			state.setCarId(carId);
			state.setEvent(action);
			state.changeFastWashers(-1);
			time += state.getFastRandom();
			//TODO add as a list
			// [time, 1.0]
			state.carWashQueue.add(time); //Tiden f�r hhhhhhhleave
			state.carWashQueue.add(1.0);
			action = LEAVE;
			fast = true;
			state.meantimeCalc = state.meantimeCalc + 1;
			
		}

		else if(state.getSlowWashers() > 0){
			state.setSimulationTime(time);
			state.setCarId(carId);
			state.setEvent(action);
			state.changeSlowWashers(-1);
			time += state.getSlowRandom();
			state.carWashQueue.add(time);
			state.carWashQueue.add(2.0);
			action = LEAVE;
			slow = true;
			state.meantimeCalc = state.meantimeCalc + 1;
			
		}
		else if(state.getQueueSize() < state.getMaxQueueSize()){
			double t = time; //Spara ARRIVE tiden
			double wash = state.carWashQueue.get(1); //spara tv�tten
			state.setSimulationTime(time);
			state.setCarId(carId);
			state.setEvent(action); //S�tter event arrival (Updaterar observer i view)
			
			if(state.carWashQueue.get(1) == 1){
				time += state.getFastRandom();	//tiden f�r att tv�ttas l�ggs till
				time += (state.carWashQueue.get(0) - t);
		
				state.carWashQueue.remove(0);
				state.carWashQueue.remove(0);
				state.carWashQueue.add(time);
				state.carWashQueue.add(1.0);
			}
			else if(state.carWashQueue.get(1) == 2){
				time += state.getSlowRandom();
				time += (state.carWashQueue.get(0) - t); // v�ntetiden f�r n�sta maskin l�ggs till
				state.carWashQueue.remove(0);
				state.carWashQueue.remove(0);
				
				state.carWashQueue.add(time);
				state.carWashQueue.add(2.0);
			}			
			state.setQueueSize(1);
			action = LEAVE;
			
			if(wash == 1){
				fast = true;
			}
			else{
				slow = true;
			}
		}
		else{
			state.setSimulationTime(time);
			state.setCarId(carId);
			state.setEvent(action);
			
			state.setRejected(1);
			removing = true;
		}
		state.sort();
	}
	
	private void idle(){
		double diff = state.getTime();
		diff = time - diff;
		state.setIdle(diff * (state.getFastWashers() + state.getSlowWashers()));
	}
	
	private void Leave(){		
		idle();
		state.setQueueTime(time);
		state.setCarId(carId);
		state.setSimulationTime(time);
		state.setEvent(action);

		
		if(state.getQueueSize() == 0){ //Tar bort den senaste k�andes tid och tv�tt om k�n �r tom
			while(state.carWashQueue.size() > 0){
				state.carWashQueue.remove(0);
			}
		}
		
		if(fast){
			if(state.getQueueSize() == 0){ 	//Om k�n till tv�tten �r tom s� blir tv�ttmaskinen ledig
				state.changeFastWashers(1);
			}
			else{						//Annars s� minskas k�n med 1;
				state.setQueueSize(-1);
			}
		}
		else if (slow){
			if(state.getQueueSize() == 0){	//Om k�n till tv�tten �r tom s� blir tv�ttmaskinen ledig
				state.changeSlowWashers(1);
			}
			else{						//Annars s� minskas k�n med 1;
				state.setQueueSize(-1);
			}
		}
		removing = true;
	}
	
	public double getTime(){
		return time;
	}
	
	public int getAction(){
		return action;
	}
	
	public boolean getSTOP(){
		return stopping;
	}
	
	public boolean getRemove(){
		return removing;
	}
}
