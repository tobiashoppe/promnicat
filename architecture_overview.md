# Architecture Overview #

The framework architecture is modeled in FMC notation. It consists of different layers:

  * Importer - for collecting and importing process models from different sources
  * Persistence API - to capture and query our process model database
  * Unit Chain of Utility Units - utilities for filtering, parsing and extract process model information
  * Analysis Modules - use the utility units to extract information from process models and analyze the results

The underlying model database will store all business process models imported from BPMAI, NPB, ... . It is the document-based OrientDB, which has a graph-database on top.


![http://wiki.promnicat.googlecode.com/git/images/architecture_overview.png](http://wiki.promnicat.googlecode.com/git/images/architecture_overview.png)


The underlying storage format is Model-Revision-Representation, meaning one Model has several Revisions which have several Representations, e.g. in different notations(EPC, BPMN) or formats (JSON, XML). The importer needs to create this structure and stores it in the database. The utility units and analysis modules usually load and work with this structure. According to their notation and format, some representations can be parsed into a jBPT process model for further analysis.

## Component Details ##
  * [Importer](Importer.md)
  * [Model-Revision-Representation](database_schema.md)
  * [Persistence API](persistence_api.md)
  * [jBPT](http://code.google.com/p/jbpt/) [Mapping Idea](jbpt_mappings.md) (General process, BPMN, EPC)
    * [jBPT](http://code.google.com/p/jbpt/) [Parser Implementation](parser.md)
    * BPMAI Parsing Problems
  * [Unit Chain and Utility Units](utility_units.md)
    * [UnitData](unit_data.md)