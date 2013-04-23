package de.uni_potsdam.hpi.bpt.promnicat.bpa;

import java.util.ArrayList;
import java.util.List;


public class BPA {

String name = "";
String organisation = "";
List<BusinessProcess> processlist;
List<Relation> relation;
private List<Event> events = new ArrayList<Event>();

public void addProcess(BusinessProcess BP){
	BusinessProcess process = BP;
	this.processlist.add(process);
	
}

public void addRelation(){
	
}


public void removeProcess(){

}

public void removeRelation(){
	
}



public List<Event> getEvents() {
	return events;
}

public List<BusinessProcess> getAllProcesses() {
	return processlist;
}

public void setProcesslist(List<BusinessProcess> processlist) {
	this.processlist = processlist;
	for (BusinessProcess bp : processlist) {
		events.addAll(bp.getEvents());
	}
}

}

