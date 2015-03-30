# Persistence API #

The PersistenceAPI encapsulates the framework from the used database system. This concept has the advantage, that only the PersistenceAPI must be rewritten when changing the database system and not the whole framework.

![http://wiki.promnicat.googlecode.com/git/images/architecture_papi.png](http://wiki.promnicat.googlecode.com/git/images/architecture_papi.png)

The integration within the whole framework can be seen on the [Architecture page](architecture_overview.md).


---

Some functions should be of notice apart from the mechanism below:

closeDB(): don't forget to call it after your work

dropDB(): just deletes the database folder from disk

openDB(): also functions as creation of new db

clearCache(): call it once in a while to allow GarbageCollector to renew it.

delete(...): according to one database id, a collection of ids, or an entire class. If one id in the list is not found in the database, no records will be removed.

### Saving ###
  * Save(pojo) also assigns a database id to this pojo. All pojos to be saved need to inherit from AbstractPojo.
  * Saving any pojo is possible with a variety of attributes such as Numeric, String, boolean, and collections of these types (see http://code.google.com/p/orient/wiki/Types. References to other Pojos are possible, when all referenced classes are registered in the Persistence API. Registering a package is also possible.
  * During the saving of a pojo, it will be provided with a database id.


### Loading ###
  * how to load: either all results at once (default) or only one element at a time (asynchronously, intended to load large collections)
  * what to load:
    * any subclass of AbstractPojo based on the database ids, class name, or plain OrientDB sql.
    * Representations based on database ids or a DbFilterConfig (to search for values in attributes). Representations are loaded as lightweight Representations, which means, only its Revision and Model are loaded, but no sibling Revisions or any further Representations (these missing links can be added on demand by loading the complete model)
    * Models are always loaded completely, based on their database id or their imported id. The imported id is taken from the original data source.

### Managing analysis results ###
  * As this persistence API allows to store nearly arbitrary pojos, storing of some analysis results can be achieved via indices. For example, an instance of a NumberIndex might contain multiple values of one metric and pointers to the specific database objects, such as representations.
  * Using multiple search criteria when loading analysis results can accomplished via index intersection.


### OrientDB ###
[OrientDB](http://www.orientechnologies.com/orient-db.htm) is a mixture of a document store (entities are stored in JSON format) and a graph database (queries allow easy traversal among entities). There are 3 levels of access to OrientDB: byte level, document/json level, object level. The object level provides a object wrapper for most pojos.

[OrientDB](http://www.orientechnologies.com/orient-db.htm) demands special NoSQL commands to use the connections of the graph, such as
`SELECT FROM Representation WHERE revision.model.title like '%create%'`
to find all Representations with the substring create in their title. Details on [OrientDB](http://www.orientechnologies.com/orient-db.htm) are described in their [google code page](http://code.google.com/p/orient/).