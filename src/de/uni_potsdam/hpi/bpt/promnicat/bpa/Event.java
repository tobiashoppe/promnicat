package de.uni_potsdam.hpi.bpt.promnicat.bpa;

public abstract class Event {
private int id;
private String label;
private int[] multiplicity;

private enum EVENTTYPE{
	STARTEVENT,
	THROWINGINTEVENT,
	CATCHINGINTEVENT,
	ENDEVENT;
	
}

private void setID(int newid){
	this.id = newid;
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

public void getOwner(){ 
	
}

private void setOwner(){
	
}

public Event(int id, String label, int[] mult){
	this.id = id;
	this.label = label;
	this.multiplicity = mult;
}

private void setEventType(){
	EVENTTYPE;
}
}
