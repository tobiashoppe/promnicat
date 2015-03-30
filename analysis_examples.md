# Analysis Modules #

The integration of these components within the whole framework can be seen on the [main page](architecture_overview.md). Next to the already implemented analysis modules representing different scenarios, we will work on different topics to implement more functionality. Each AnalysisModule that should be accessible from command line must implement the IAnalysisModule-interface.

![http://wiki.promnicat.googlecode.com/git/images/architecture_analysis.png](http://wiki.promnicat.googlecode.com/git/images/architecture_analysis.png)


---


# Example Scenarios #

## Connectedness ##
Test all EPC JSON diagrams from BPM AI for connectedness. This scenario is implemented as illustrated at the image, the corresponding analysis module class is named ConnectedEPC.

![http://wiki.promnicat.googlecode.com/git/images/scenario_connectedness.png](http://wiki.promnicat.googlecode.com/git/images/scenario_connectedness.png)

## Activity Filtering ##
Find all the newest versions of BPMN1.1 and BPMN2.0 diagrams from BPM AI that have an activity containing "customer" in their title. This scenario is implemented as illustrated at the image, the corresponding analysis module class is named CalcAndSaveNodeNames which saves the analysis results in the database.

![http://wiki.promnicat.googlecode.com/git/images/scenario_labelfilter_and_result.png](http://wiki.promnicat.googlecode.com/git/images/scenario_labelfilter_and_result.png)

### Save Analysis Results in the Database ###
It must be possible to save the analysis results in the database. For that, see class CalcAndSaveNodeName in our project implementation. For each result from the CollectorUnit an instance of LabelStorage is created. All L!abelStorages are collected in an AnalysisRun which is then saved in the database. For later retrieval of this AnalysisRun, its database id  is printed to the command line.

To load the results, see RetrieveNodeNames and set the new database id. All connected LabelStorages are loaded as well. Connected Representations can be loaded with their database id.


---


## Realization of Process Metrics ##
  * Realize different process metrics in PromniCAT, such as  number of events/functions/..., connectedness, cycling, ...
  * The implementation is based on the metric definitions of [Jan Mendling - Metrics for Process Models, 2009 - Springer](http://www.springerlink.com/content/vj505800l4376717/)

## Index Generation from Dynamic Requests ##
  * Create indexes on analysis results to reuse them for other purposes later
  * Indices are key/value pairs. Keys can be any string or numerical, the value is a pointer to some database object. Indices can be combined with Intersections.

## [Hierarchically Clustering Process Model Collections](cluster_module.md) ##
  * Cluster process model collections by different aspects (e.g. label similarity)

## Classification of Process Models ##
  * Classify process models according to several criteria like:
    * soundness(classical, relaxed, weak)
    * BPMN Modeling Conformance level
    * workflow net
    * S-net
    * T-net
    * free choice
    * extended free choice