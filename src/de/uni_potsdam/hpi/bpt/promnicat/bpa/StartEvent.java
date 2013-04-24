/**
 * 
 */
package de.uni_potsdam.hpi.bpt.promnicat.bpa;

/**
 * @author rami.eidsabbagh
 *
 */
public class StartEvent extends ReceivingEvent {

	private EventType type;
	/**
	 * @param eventid
	 * @param bpid
	 * @param label
	 * @param mult
	 */
	public StartEvent(int eventid, int bpid, String label, int[] mult) {
		
		super(eventid, bpid, label, mult);
		type = EventType.STARTEVENT;
		// TODO Auto-generated constructor stub
	}
	public EventType getType() {
		return type;
	}
	public void setType(EventType type) {
		this.type = type;
	}
	

	
}
