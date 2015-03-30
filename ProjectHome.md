# PromniCAT #
PromniCAT is a framework to import various business process models of many business process model repositories into one generic database (therefore the "Pro" for process and "omni" in the name). This enables research on the entire collection across the boundaries of business process modeling notations.

PromniCAT is completely written in JAVA. The database used for business process model persisting is [Orient DB](http://www.orientechnologies.com/orient-db.htm). For business process model analysis we use [jBPT](http://code.google.com/p/jbpt/), a graph analysis framework.

You can get a large set of process models for evaluating your algorithms from the BPM Academic Initiative ([BPMAI](http://bpmai.org/download/index.html)) homepage. The [BPMAI](http://bpmai.org/download/index.html) homepage povides filters for choosing process models with desired process characteristics for your evaluation.
PromniCAT offers a direct importer for the BPMAI process models to jump start your research.

## Documentation ##
  1. [Architecture overview](architecture_overview.md)
  1. [Database schema](database_schema.md)
  1. [Importer](Importer.md)
  1. [Persistence API](persistence_api.md)
  1. [Unit Data and Utility Units](utility_units.md)
  1. [Analysis Modules/Examples](analysis_examples.md)
  1. [Process Model Parser](parser.md)

> Further information about PromniCAT can be obtained from our [paper](http://wiki.promnicat.googlecode.com/git/promnicat.pdf).

> A research paper "A Platform for Research on Process Model Collections" describing the platform from a research perspective has been published
> [here](http://link.springer.com/chapter/10.1007%2F978-3-642-33155-8_2) or the pre-print version [here](http://bpt.hpi.uni-potsdam.de/pub/Public/AndreasMeyer/A_Platform_for_Research_on_Process_Model_Collections.pdf).



## Usage ##
  * [How to run PromniCAT](How_to_run.md)
  * [Installation Details](install.md)
  * [How to build PromniCAT](build.md)
  * [Configuration File](config.md)
  * [Coding Conventions](coding_conventions.md)

If you need more help, don't hesitate to ask on the [forum/mailing list](https://groups.google.com/group/promnicat).

## Bugs, patches, suggestions ##

If you're interested in discussing something PromniCAT related, consider posting to the friendly gang on the [forum/mailing list](https://groups.google.com/group/promnicat).

Bugs in PromniCAT can be reported on the [bug tracker](http://code.google.com/p/promnicat/issues/list) - but please be polite and remember that the [bug tracker](http://code.google.com/p/promnicat/issues/list) is for tracking bugs in PromniCAT, not for debugging your code. Unless you spot an obvious mistake in PromniCAT or can produce a small self-contained test case reproducing the problem, you are probably better off asking on the [forum/mailing list](https://groups.google.com/group/promnicat) which is watched by lots of knowledgeable people.

As for new features, you can open an issue on the [issue tracker](http://code.google.com/p/promnicat/issues/list) and attach a patch or start a discussion on the [forum/mailing list](https://groups.google.com/group/promnicat).