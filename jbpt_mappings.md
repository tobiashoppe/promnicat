# jBPT mapping #

In the following, we provide a mapping from the different notation elements (as first step, only for BPMN and EPC) to classes in [jBPT](http://code.google.com/p/jbpt/). The mapping is given in the way "notation element --> class name". You can look up the classes in the corresponding class diagrams. Since we have to integrate our class structure in [jBPT](http://code.google.com/p/jbpt/), we marked new classes **added** by us with **green**, whereas we marked those classes that have to be **removed** from the existing class structure with **red**.

## Process - In General ##

In [jBPT](http://code.google.com/p/jbpt/), there did not exist a "general" class structure up to now. There only existed different packages for the notations (EPC, Petri Nets, and a simple mapping to BPMN), which had their own structure. Since we wanted to create a more generic view, we introduced a new "abstract" layer of a process model. In the diagram below, you can see its architecture. There exists a class **ProcessModel** (a container for Edges and Vertexes), from which the corresponding **EPC or BPMN** container inherit.

The Vertexes (we deleted the Node class, that originally existed) are separated into two types: **FlowNode** and **NonFlowNode**. FlowNodes are all objects that take part in the control flow of a process - this means Activities, Events, and Gateways (think of the description of a process by Prof. Weske). NonFlowNodes thereby are those that do not actively take part in the control flow - this includes Data Objects and things like Roles, Lanes, Pools, etc.

The Edges of a process model are only modeled as **ControlFlow**. We do not map the data flow aspect here explicitely.

This class structure is the basis for the following mapping of BPMN's and EPC's notation elements. Their concrete classes inherit from their corresponding super class.

![http://wiki.promnicat.googlecode.com/git/images/jbpt_process_model.png](http://wiki.promnicat.googlecode.com/git/images/jbpt_process_model.png)

## BPMN ##

![http://wiki.promnicat.googlecode.com/git/images/jbpt_bpmn_model.png](http://wiki.promnicat.googlecode.com/git/images/jbpt_bpmn_model.png)

### Activity ###
  * Task --> Task
  * Transaction --> not mapped (not mapped in BPMAI Json either)
  * Event-Subprocess --> Subprocess (has attribute to mark it as Event-Subprocess)
  * Invoking Activities --> not mapped (not mapped in BPMAI Json either)
  * Loops, Sequential/Parallel Multiple Instance Execution, Compensation --> attributes of Activity class
  * Subprocess --> Subprocess
  * Adhoc Process --> SubProcess

### Gateway ###
  * Exclusive Gateway --> XorGateway
  * Parallel Gateway --> AndGateway
  * Inclusive Gateway --> OrGateway
  * Complex Gateway --> AlternativeGateway
  * Event-based Gateway --> EventBasedGateway
  * Exclusive Event-based Gateway --> AlternativeGateway
  * Parallel Event-based Gateway --> AlternativeGateway

### Container ###
  * Pool --> BpmnResource (with attribute: pool/lane and collapsed yes/no)
  * Lane --> BpmnResource
  * Group --> not mapped, since it has no semantic meaning for the process model

### Data ###
  * Data Object --> Document
  * Data List --> not mapped (not mapped in BPMAI Json either)
  * Input --> not mapped (not mapped in BPMAI Json either)
  * Output --> not mapped (not mapped in BPMAI Json either)
  * Storage --> not mapped

### Event ###
Are divided into four classes: Start, End, Catching, Throwing.

They all (this means: their super class BpmnEvent) have a class/type attribute that contain the event types: Blank, Message, Timer, Condition, Escalation, Link, Error, Exit, Compensation, Signal, Multiple, Multiple/Parallel, Termination

They also have two more attributes: interrupting and attached. If both are true, it is an attached intermediate interrupting event. If only attached is true, it is an attached intermediate non-interrupting event. If only interrupted is true, it is an event-subprocess interrupting event. If both are false, it is an event-subprocess non-interrupting event.

This way, we map alle the different event types and classes to our four jBPT classes.

### Edges ###
In BPMN, there exists a notation element called Attached Intermediate Event, where an event is directly connected to an Activity. This violates jBPTs condition that all vertexes have to be connected by edges with each other. To avoid this violation, we simply add the attached event to the ControlFlow object.

  * Sequence Flow --> BpmnControlFlow
  * Conditional Flow --> BpmnControlFlow (has attribute with condition)
  * Standard Flow --> BpmnControlFlow (has attribute with default)
  * Message Flow --> BpmnMessageFlow
  * Data Flow --> not mapped

## EPC ##
[jBPT](http://code.google.com/p/jbpt/) already has a class structure for EPC models. Since the data flow is not modeled explicitely there, we only adapted the existing class structure a little bit (**red** classes are those to be **removed**, **green** classes those to be **added**).

![http://wiki.promnicat.googlecode.com/git/images/jbpt_epc_model.png](http://wiki.promnicat.googlecode.com/git/images/jbpt_epc_model.png)

### Activity ###
  * Function --> Function
  * Process Interface --> ProcessInterface
  * Phone/Faximile/Letter/Mail --> not mapped - it has no semantic meaning to the model

### Gateway ###
Was modeled in jBPT as Connector --> This is removed by us.
  * AND --> AndConnector
  * OR --> OrConnector
  * XOR --> XorConnector

### Static ###
  * Organization --> EpcResource
  * Organization Type --> EpcResource
  * Role --> EpcResource

### Data ###
  * Entity --> not mapped
  * Document --> Document
  * IT System --> not mapped

### Event ###
  * Event --> Event

### Edges ###
  * Control Flow --> ControlFlow
  * Relation --> (data) objects are directly annotated as attribute to events/functions/etc.