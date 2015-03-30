# To use PromniCAT #
  * download project as described  [here](http://code.google.com/p/promnicat/source/checkout)(e.g. use [TortoiseGit](http://code.google.com/p/tortoisegit/) or [eGit](http://www.eclipse.org/egit/) or [SmartGit](http://www.syntevo.com/smartgit/index.html))
  * use JDK 1.6
  * tested on Windows 7 and Mac OS X
  * OrientDB comes with a jar and does not need to be installed
  * suggested Eclipse (tested on Indigo)
  * Have a look on how to run PromniCAT via command line or programmatic API [here](How_to_run.md).
# To develop PromniCAT #
  * Visual Paradigm
  * Additional plugins for Eclipse (not necessary, only optional)
    * JUnit 3.8.1 or 4.x (normally comes with Eclipse already)
    * Json Plugin:  http://eclipsejsonedit.svn.sourceforge.net/viewvc/eclipsejsonedit/trunk/Json%20Editor%20Plugin/
  * [JSON](http://json.org/) format (with [browser editor](http://www.thomasfrank.se/downloadableJS/JSONeditor_example.html) for tree view)
  * http://www.w3schools.com can be used as online editor for js, json, html, etc.
  * in case OrientDb needs to be investigated:
    * download and unzip orientDb from http://code.google.com/p/orient/wiki/Download
    * code was developed on version 1.0rc6
    * usually not needed, but in case someone wants to investigate the OrientDB server: start it with ./bin/server.sh for Unix-like operating system, or execute server.bat for Windows. Check execute permission on the script file.