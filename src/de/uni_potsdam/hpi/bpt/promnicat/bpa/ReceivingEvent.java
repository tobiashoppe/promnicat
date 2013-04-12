/**
 * 
 */
package de.uni_potsdam.hpi.bpt.promnicat.bpa;

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
	private int[] preset;
	
	public ReceivingEvent(int eventid, int bpid, String label, int[] mult) {
		super(eventid, bpid, label, mult);
		// TODO Auto-generated constructor stub
	}
	
	public int[] getPreSet(){
	return this.preset;
	
	}
    
	public void setPreset(int[] preset){
		
	}
}
