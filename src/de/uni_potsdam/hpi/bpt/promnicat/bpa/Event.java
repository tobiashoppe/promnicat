package de.uni_potsdam.hpi.bpt.promnicat.bpa;

public abstract class Event {
private int id;
private String label;
private int[] multiplicity;
private EventType type;
public int owner; 

public enum EventType{
	STARTEVENT,
	THROWINGINTEVENT,
	CATCHINGINTEVENT,
	ENDEVENT;
	
}

//private void setID(int newid){
//	this.id = newid;
//}

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

public Event(int eventid,int bpid, String label, int[] mult){
	this.id = eventid;
	this.owner = bpid;
	this.label = label;
	this.multiplicity = mult;
}


}
