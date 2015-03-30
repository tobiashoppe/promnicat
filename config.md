# Configuration File #

In the configuration file the path to the database, the database access properties and the maximum number of threads to use for Unit Chain execution are specified.

It is a simple java property file. The default configuration file is "PromniCAT/configuration.properties". The file content is shown below:

```
 #----------------Utility Units properties--------------------   
 #max number of threads used for unit chain processing  
 #to use the default number of threads (4 * number of available cores) change value to <=0 
 maxNumberOfThreads = 8                                           
 #----------------database properties--------------------------         
 #database path
 db.dbPath = local:resources/orientDBsmall/
 #user name      
 db.user = admin
 #password   
 db.password = admin
```

The attribute 'maxNumberOfThreads' sets the maximum number of threads to use for Unit Chain execution. If a value smaller than one is given,

the number is set to four times the available cores (received with Runtime.getRuntime()availableProcessors()).



The location of the database to use can be set by the db.dbPath attribute.

The user name and the password to access the database can be set with db.user and db.password.