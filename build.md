# How to build PromniCAT #
The PromniCAT project is build using the build.xml Ant script. The script has been tested using Ant-1.8.2. If you expect problems please update Ant to Version 1.8.2

The following targets can be used:
  * build: Builds the whole project
  * full: execute the build target and aftwards all tests are executed
  * doc: generates the JavaDoc for the PromniCAT project.
  * clean: removes all generated files and folders
  * help: prints the usage message


### Within the Eclipse IDE: ###
Use the provided launch file (in the build folder)PromniCAT\_build.launch to run the configured build script. The full target is executed. This means, that the project is build and all tests will be executed.

### From command line: ###
Switch into code/PromniCAT/build
  * run the build script by typing:
```
ant -lib ../lib/ecj-3.7.jar <target>
```


&lt;target&gt;

 means one of the targets listed above

**or**

  * copy the lib code/PromniCAT/lib/ecj-3.7.jar into the ANT\_HOME/lib directory. Then run the build script by typing
```
ant <target>
```


&lt;target&gt;

 means one of the targets listed above