# Unit Chain and Utility Units #

The integration within the whole framework can be seen on the [Architecture page](architecture_overview.md).

![http://wiki.promnicat.googlecode.com/git/images/architecture_utility.png](http://wiki.promnicat.googlecode.com/git/images/architecture_utility.png)

The _Utility Units_ component provides some utilities for filtering and extraction of process model information.

It consists of two parts. On the one hand are the _Units_ and on the other hand are the _UnitChains_.

A _Unit_ encapsulates one single step in process model analysis and a _UnitChain_ is a container for _Units_.

Thus, to create a complex process model analysis one _UnitChain_ with an ordered set of _Units_ is needed.

The creation of a _UnitChain_ is described in detail in the next section.


The concrete architecture is structured as follows (only selection of _UtilityUnits_ is modeled):
![http://wiki.promnicat.googlecode.com/git/images/utility_units_classes.png](http://wiki.promnicat.googlecode.com/git/images/utility_units_classes.png)

### How to use the utility units? ###
To create a new _UnitChain_ create a new _UnitChainBuilder_ instance. Afterwards, a new _DBFilterConfig_ instance can be used to add several filters being considered in the database query. Add the filter needed to fulfill your task. You can specifying an origin, notation and/or format filter. If no _DBFilterConfig_ is provided the complete database content is used for analysis. Add this database filter to the builder(`UnitChainBuilder#addDbFilterConfig(DBFilterConfig)`).

Afterwards, specify the units needed to fulfill your task by adding them to the unit chain. Therefore, just call `UnitChainBuilder#create"<FilterName>"()`. **Attention**: Order is important here.

For unit compatibilty (means that the output type of the last unit is compatible with the input type of the unit to add) see documentation of the concerning units.

This check is performed by the builder each time a new unit should be added.

Finally, a unit implementing the _ICollectorUnit-interface_ must be added to the chain. If none is given, the builder implicitly adds a _SimpleCollectorUnit_. This collector unit is needed to sum up the results of each result coming from the database.

To execute a specified chain call `getChain()` on the _UnitChainBuilder-instance_ at hand. On the returned _UnitChain_ call `execute()` which will return the result of the last unit in the chain. Thus, the result is always a collection of classes that implements `IUnitData<Object>`. For further information see [IUnitData](unit_data.md).

It is recommended to execute the steps above in an _Analysis Module_.

### How does the execution works? ###
When the execute-method of a unit chain is called, the first unit, namely the _DatabaseFilterUnit_, is executed. Therefore, the database query is constructed and executed. For each matching database result the unit chain's update-method is called. Within this method a thread from the unit chian's thread pool is used to execute the chain, apart of the database query, for the given database result. These steps are repeated for each database result. The intermediate results are collected in a collector unit. Thus, a unit implementing _ICollectorUnit_ have to be the last unit of the chain.

After collecting all intermediate results, the database filter unit returns from execution and the unit chain get the chain's execution result from the collector unit. This result is returned to the analysis module and can be used for further investigations.

## Units - What they calculate and expected [input and output formats](unit_data.md) ##

#### Database Filter Unit ####
This unit is used to query data from the database. Thus, it is always the first unit of a chain and added implicitly by the unit chain builder.

Currently, the config used by the database filter unit is build by the !IUnitChainBuilder, because it collects all added notation filters, origin filters and format filters and constructs the appropriate database configuration.

When executed, this unit executes a query with the help of the [PersistenceAPI](persistence_api.md) and for each result matching the given criteria the update()-method of the executed unit chain is called.

The expected input type is `IUnitData<IUnitChain>` (see [UnitData](unit_data.md)). The output type is the same as the input type.

#### Process Model Filter Unit ####
This unit searches for a given element instance or element class in the given process model's elements.

If a matching element was found the input is returned. Otherwise, the return value is set to null.

The expected input type is `IUnitData<ProcessModel>` (see [UnitData](unit_data.md)). The output type is the same as the input type.

#### Label Filter Unit ####
This unit takes a list of labels extracted from a process model and returns a subset of them containing a given regular expression.

e.g. 'model' matches: 'model', 'processmodel' or 'models', ....

The lockup is case insensitive.

The expected input type is `IUnitData<Map<String, Collection<String> > >` (see [UnitData](unit_data.md)). The output type is the same as the input type.

#### Connectedness Filter Unit ####
This unit takes a process model as [jBPT](http://code.google.com/p/jbpt/) object and returns **null** if the process model is **not connected**, otherwise the given process modell is set as return value.

The expected input type is `IUnitData<ProcessModel>` (see [UnitData](unit_data.md)). The output type is the same as the input type

#### Meta Data Filter Unit ####
This unit filters out metadata from a Revision. Therefore, this unit takes a Representation and extracs all metadata from it. Afterwards, the metadata is scaned for a given key and/or value and only matching items are added to the result. If all keys or values shoud be added to the result, just provide null as search criterion.
The expected input type is `IUnitData<Representation>` (see [UnitData](unit_data.md)). The output type is `IUnitData<Map<String, Collection<String>>>` (see [UnitData](unit_data.md)).

#### BPM AI JSON to Diagram Unit ####
This unit converts a process model given in Signavio JSON-file format into a BPM AI Diagram Object.

The expected input type is `IUnitData<String>` (see [UnitData](unit_data.md)). The output type is `IUnitData<Diagram>` (see [UnitData](unit_data.md)).

#### Diagram to jBPT Unit ####
This unit converts a BPM AI Diagram object into a [jBPT](http://code.google.com/p/jbpt/) object.

The expected input type is `IUnitData<Diagram>` (see [UnitData](unit_data.md)). The output type is `IUnitData<ProcessModel>` (see [UnitData](unit_data.md)).

#### Element Extractor Unit ####
This unit extracts all elements from a specified type of a process model given as [jBPT](http://code.google.com/p/jbpt/) object.

e.g. all functions from an EPC model or all complex-gateways from a BPMN model.

The expected input type is `IUnitData<ProcessModel>` (see [UnitData](unit_data.md)). The output type is `IUnitData<Collection<IVertex> >` (see [UnitData](unit_data.md)).

#### Process Model Label Extractor Unit ####
This unit returns a list of all labels from the given process model as [jBPT](http://code.google.com/p/jbpt/) object.

The expected input type is `IUnitData<ProcessModel>` (see [UnitData](unit_data.md)). The output type is `IUnitData<Map<String, Collection<String> > >` (see [UnitData](unit_data.md)).

#### Simple Collector Unit ####
This class collects all incoming results. The collected elements can be received by SimpleCollectorUnit.getResult().

The expected input type is `IUnitData<Object>` (see [UnitData](unit_data.md)). The output type is the same as the input type.