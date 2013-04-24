package de.uni_potsdam.hpi.bpt.promnicat.bpa;

import java.util.HashMap;
import java.util.Map;

public abstract class Event {
private int id;
private String label;
private int[] multiplicity;
private EventType type;
public int owner; 

private static Map<Event, Integer> ids = new HashMap<Event, Integer>();
private static Integer maxId = 0;

public enum EventType{
	STARTEVENT,
	THROWINGINTEVENT,
	CATCHINGINTEVENT,
	ENDEVENT;
	
}

public Event(int eventid,int bpid, String label, int[] mult){
	this.id = eventid;
	this.owner = bpid;
	this.label = label;
	this.multiplicity = mult;
}


//private void setID(int newid){
//	this.id = newid;
//}


/**
 * Constructor which automatically creates id.
 * @param bpid
 * @param label
 * @param mult
 */
public Event(int bpid, String label, int[] mult) {
	this(0,bpid,label, mult);
	this.id = identify(this); 
}


/**
 * Constructor with trivial multiplicity.
 * @param bpid
 * @param label
 */
public Event (int bpid, String label) {
	this(bpid,label, new int[]{1});
}

/**
 * Assigns (Integer) IDs to a event if it has none,
 * returns existing ID otherwise. 
 */
private static Integer identify(Event e) {
	if (ids.containsKey(e)) {
		return ids.get(e);
	} else {
		ids.put(e, maxId++);
		return maxId; 
	}
}

public int getID(){
	return this.id;
}

public void setLabel(String newlabel){
	this.label = newlabel;
}


public String getLabel(){
	
	return this.label;
	
}
public void setMultiplicity(int[] mult){
	this.multiplicity = mult;
}


public int[] getMultiplicity(){
	return this.multiplicity;
}

public int getOwner(){ 
	return this.owner;
}

//private void setOwner(int bpid){
//	this.owner = bpid;
//}

public EventType getType(){
	
	return this.type;
}

public boolean hasTrivialMultiplicity() {
	return (multiplicity.length == 1 && multiplicity[0] == 1);
}


}
