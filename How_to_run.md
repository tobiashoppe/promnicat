# Import Models #

Execute the following command:
```
java -jar PromniCAT-0.2.jar <path_to_config> <collection> <path1> [<path2> [<path3> ...]]
```


<path\_to\_config>

 is the path to promniCAT's configuration file. Enter an empty string to use the default configuration.


&lt;collection&gt;

 the name of the process model collection to import. Currently "BPMAI", "NPB", "SAP\_RM", and "AOK"are supported.


&lt;path&gt;

 path to the folder containing the process models to import. To import the process models of many folders, just enter the path of each folder seperated by spaces.

# Run Analysis Module #

### From Command Line ###
Execute the following command:
```
java -cp PromniCAT-0.2.jar de.uni_potsdam.hpi.bpt.promnicat.launch.AnalysisModuleLauncher <name_of_analysis_module> <parameter_for_analysis_module>
```


<name\_of\_analysis\_module>

 The name of the analysis module that should be executed. The name must be given case sensitive and if the analysis module is located in any sub-package of PromniCAT's analysisModule-package the full path must be entered as name.
e.g for the ProcessClassification analysis module the name is "classification.ProcessClassification"


<parameter\_for\_analysis\_module>


All parameters following the analysis module's name are passed to the analysis module's main-method.

### Using programmatic API ###
You can use the same command as for command line or you can use one of the provided execute-methods of the AnalysisModuleLauncher.
```
AnalysisModuleLauncher.execute( String <name_of_analysis_module>)
```
or
```
AnalysisModuleLauncher.execute( String <name_of_analysis_module>, String[] <parameter_for_analysis_module>)
```