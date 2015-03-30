# Model-Revision-Representation #

The underlying database schema uses models, revisions, and representations. For each process model parsed by an importer (see [Importer](Importer.md)) the following **schema** will be created: one model may have multiple revisions (i.e. versions) which again have multiple representations.

![http://wiki.promnicat.googlecode.com/git/images/databaseSchema.png](http://wiki.promnicat.googlecode.com/git/images/databaseSchema.png)

A model object is stored within the database containing the origin of the model (e.g. BPMAI, NPB), as well as the title and a group of revisions, from which one is the latestRevision. For each revision, a huge set of meta data is stored and possibly several representations. Each representation consists of: the data file containing the model code as byte string, the format of this data (usually file ending, e.g. json) and the notation used within this data file (e.g. bpmn).

**Note:** The model data stored within the database is read only, because each time a model is updated a new revision is stored.

Further, all data objects stored in the database need to inherit from `AbstractPojo` to assure a database id.



## Meta data ##


During import, all metadata is kept and some of them are additionally mapped to a global metadata schema, e.g.:

  * per model: title
  * per revision: versionNumber, author, metadata.
  * per representation: modeling language, (natural) language.
The API of revisions assumes metadata is key/values but internally it is stored as key/value only by using designated separator signs.