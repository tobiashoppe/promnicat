package de.uni_potsdam.hpi.bpt.promnicat.bpa;

import java.util.List;

public class BusinessProcess {
	
	List<Event> events;
	
	
	
	public BusinessProcess(List<Event> events) {
		super();
		this.events = events;
	}


	public List<Event> getEvents() {
		return events;
	}

}
