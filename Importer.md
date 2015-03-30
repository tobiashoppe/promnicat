# Importer #

The importer component is used to fetch business process models from a given file system path or even a set of paths.

Each importer is used once to import the business process model collection it is associated with. Afterwards, the importers can be used to update the framework's database with the latest changes of the external model collection. Therefore, the method called for the initial import is used again. The framework itself decides whether an update should be performed or an initial import.


![http://wiki.promnicat.googlecode.com/git/images/architecture_importer.png](http://wiki.promnicat.googlecode.com/git/images/architecture_importer.png)


The _BPMAI Importer_ is used to import models from the _BPM Academic Initiative_. The _NPB Importer_ is used to import models from the so called _'Nationale Prozessbibliothek'_. The _SAP Importer_ is used to import the reference model from the _SAP AG_ and the _AOK Importer_ is used to import process models from the _AOK_ process model repository.


![http://wiki.promnicat.googlecode.com/git/images/importer_classes.png](http://wiki.promnicat.googlecode.com/git/images/importer_classes.png)


Importing models from one of the supported process modell collections is easily done by using:

**`ModelImporter.main(<Path to configuration file> <Collection> <URI> [<URI2> [URI3 ...]]])`**

The first parameter should point to a [configuration file](config.md). It could be empty, than the default file _"PromniCAT/configuration.properties"_ is used.

Valid inputs for `<Collection>` are 'BPMAI', 'NPB', 'SAP\_RM' and 'AOK'.

(e.g. _ModelImporter configuration.properties BPMAI C:/models_)

or by passing a collection of uris pointing to the models that should be imported.

(e.g. _ModelImporter configuration.properties NPB C:/model1 C:/model2 C:/model3_)

If one of these methods is called, the given directory structure is analyzed to provide a [Model-Revision-Representation schema](database_schema.md) specified on the storage schema side. Therefore, each model is identified and stored within the internal database if it is not already present. During the import, models can be given an importedId which will be used for a later re-import, during which only new revisions are added to the already present model. Note, that all models also have a dBid once they are stored in the database.



Furthermore, all given revisions of each model are parsed from the directory structure and for each of these revisions all representations are stored within the database. If the model is only updated, a new revision is created containing the updated representations.

During model and revision parsing the associated meta data is parsed from the imported model file. It is stored within the database. If the meta data is revision specific it is associated with revision otherwise it is stored directly with the model. Further information about the database format are provided at the [OrientDB page](database_schema.md).

## Implemented Imports: ##
#### BPMAI: ####
  * use models, revisions and representations
  * models don't have metadata (only name, id, date)
  * revisions have a number, a Diagram with properties and two Files: Json and Svg
  * Diagram properties are e.g. {monitoring=, modificationdate=, namespaces=, creationdate=, version=, author=, targetnamespace=http://www.signavio.com/bpmn20, name=, documentation=, language=English, auditing=, expressionlanguage=http://www.w3.org/1999/XPath, typelanguage=http://www.w3.org/2001/XMLSchema}
  * [BPMAI project page](http://bpt.hpi.uni-potsdam.de/BPMAcademicInitiative)
  * [Json-parser](http://code.google.com/p/bpmai/)
  * models: https://svn.bpt.hpi.uni-potsdam.de/svn/bpm_ai_pcm/models/2011-04-19_signavio_academic_processes.zip

#### NPB: ####
  * a lot of metadata: keep all metadata for each revision, might include redundancy but is saver
  * some metadata is fixed, others is flexible. name of metadata attribute is key itself, it has German description and content
  * files can have arbitrary format, e.g. Word, Visio, Power Point, Json
  * Project: http://www.prozessbibliothek.de/
  * Wiki: http://developernpb.pbworks.com

#### Possible Future Imports: ####
  * SAP RM
  * Insurer Collection

## Automatic indexes during import: ##

  * labels of nodes (and edges?)
  * language (human)
  * number of nodes
  * connectivity
  * seperability
  * heterogenity