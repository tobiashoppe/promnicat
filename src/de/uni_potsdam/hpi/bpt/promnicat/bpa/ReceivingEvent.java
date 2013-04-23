/**
 * 
 */
package de.uni_potsdam.hpi.bpt.promnicat.bpa;

import java.util.List;

/**
 * @author rami.eidsabbagh
 *
 */
public class ReceivingEvent extends Event {

	/**
	 * @param eventid
	 * @param bpid
	 * @param label
	 * @param mult
	 */
	private List<SendingEvent> preset;
	
	public ReceivingEvent(int eventid, int bpid, String label, int[] mult) {
		super(eventid, bpid, label, mult);
		// TODO Auto-generated constructor stub
	}
	
	public List<SendingEvent> getPreset(){
		return preset;
	}
    
	public void setPreset(List<SendingEvent> predecessors){
		preset = predecessors;
	}
}
