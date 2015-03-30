# jBPT Parser Implementation #

![http://wiki.promnicat.googlecode.com/git/images/architecture_utility.png](http://wiki.promnicat.googlecode.com/git/images/architecture_utility.png)

The integration within the whole framework can be seen on the [Architecture page](architecture_overview.md).

The parser is the essential component of the [DiagramToJbptUnit](utility_units.md). This unit transforms Diagram objects to [jBpt process models](jbpt_mappings.md) by invoking the transformProcess-method of an instance of ModelParser. The ModelParser class is the central class for invoking the parsing process. As the other parser classes, it implements the IParser interface. It delegates the actual parsing task to the "real" Parsers for BPMN, EPC, and any other notation desired.

At the moment, there only exist parsers for **EPC** and **BPMN** (1.1 and 2.0) process models. They parse Diagrams that have been transformed with the [JsonToDiagramUnit](utility_units.md) from **JSON strings conforming BPM AI format**. The actual parser classes have **constant classes** containing the BPM AI JSON entity, property, and property value names that are necessary to construct a consistent process model. See our [mapping documentation](jbpt_mappings.md) to have a look at what entities are mapped on what jBpt classes.

All parsers also own a flag you can set to only parse those process models that are modelled correctly (for example, edges without incoming/outgoing nodes). This flag you set in the constructor of the ModelParser class.

## EPC Parser ##

#### Parsing Strategy ####
Since we do not have any childshape-relations in EPC process models, we can just parse the entities the way they come along. The parsing itself is much simpler than for BPMN process models (for the differences, see our [jBPT-mapping](jbpt_mappings.md)).

**The parsing order is considered as follows:**
  1. All nodes (FlowNodes - Activities, Gateways; NonFlowNodes - Resources, Documents)
  1. All flow elements (control flow, message flow - since we need to have the nodes created first to assign a connection between them)

## BPMN Parser ##
**Differences between BPMN 1.1 and BPMN 2.0**
| **Attribute/Entity Name** | **BPMN 1.1** | **BPMN 2.0** |
|:--------------------------|:-------------|:-------------|
|Normal Loop | looptype = Standard/None | looptype = Standard/None |
|Multiple Instances (Sequential/Parallel) | mi\_ordering = Sequential/Parallel | looptype =  Sequential/Parallel (mi\_ordering does not exist anymore) |
| Compensation | isforcompensation = true/false | iscompensation = true/false |
| Blank Events | StartEvent | StartNoneEvent |


#### Parsing Strategy ####
The parser parses the JSON entities by **bottom-up strategy**. Since the childshape-hierarchy can have an arbitrary depth, this has to be done recursively. The parser therefore starts parsing the innermost childshapes to then add them afterwards to subprocesses, or annotate top-elements such as lanes or pools to the already parsed nodes. If parsed top-down, the parser e.g. would not know where to annotate the parsed Lanes and Pools as attributes since their childnodes are not existing yet. In general, **only nodes are childnodes**. Flow elements are not considered to be a childshape anyway. For subprocesses, the parser first parses all the child nodes to add them afterwards as process model to the subprocess itself.

All the entities of a contained subprocess of a process model will only exist as reference in the subprocess entity, not in the overall process model. **Think of that fact when implementing and running your analyses**.

**The parsing order is considered as follows:**
  1. All nodes (FlowNodes - Activities, Events, Gateways; NonFlowNodes - Resources, Documents)
  1. All flow elements (control flow, message flow - since we need to have the nodes created first to assign a connection between them)
  1. All associations (for data annotation)
  1. All attached intermediate events (see how we modeled that in our [jBPT-mapping](jbpt_mappings.md) to understand why)