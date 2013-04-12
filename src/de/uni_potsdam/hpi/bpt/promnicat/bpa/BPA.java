package de.uni_potsdam.hpi.bpt.promnicat.bpa;

import java.io.IOException;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.logging.Logger;
import de.uni_potsdam.hpi.bpt.promnicat.bpa.BusinessProcess;;

public class BPA {

String name = "";
String organisation = "";
ArrayList<BusinessProcess> processlist;
ArrayList<Relation> relation;

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

}