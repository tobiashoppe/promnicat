# ClusterPreprocessing #

### Creating Feature Vectors from Process Models ###

To determine the similarity of two process models, they are compared regarding the values of their specific **features**. These features can be selected by the user and alltogether form a so-called **feature vector**, which is created in this preprocessing step. In PromniCAT, retrieving a feature vector for each process model involves the creation of a new unit chain that provides a possibility to select appropriate features as well as creating a representation of the actual feature vector.

The execution flow of the unit chain is the following:

  1. Select process models by their origin, format, or notation
  1. Transform the process models into jBPT
  1. Calculate desired process metrics (The string features currently taken into account do not require a complex calculation as for the process metrics and can be directly retrieved from the process models later on)
  1. Create the feature vector for each process model

![http://wiki.promnicat.googlecode.com/git/images/clustering_preprocessing_workflow.png](http://wiki.promnicat.googlecode.com/git/images/clustering_preprocessing_workflow.png)

The new utility unit takes the output of the former unit as **input**, i.e., the **process models and the calculated values for the selected process metrics**. The **output** of the utility unit
contains the **current process model and its feature vector**.

The underlying class structure is the following:

![http://wiki.promnicat.googlecode.com/git/images/clustering_class_features.png](http://wiki.promnicat.googlecode.com/git/images/clustering_class_features.png)

Highlighted classes indicate the additions made to the already existing class structure. The new utility unit is called **ModelToFeatureVectorUnit**, and owns a config attribute of the type **FeatureConfig**, containing the names of all selected features. These values are set in advance at unit chain creation.


During the execution of the new utility unit, it evaluates the given conguration and collects the corresponding feature values. The actual feature value retrieval is encapsulated in the constants class **ProcessFeatureConstants**, since it maintains the selectable features in enumerations which also return the corresponding values.


The feature values are represented by instances of the class **Feature**, whereas the feature vector is modeled by the class **FeatureVector**. It has an attribute features holding a list of instances of Feature. The resulting instance of FeatureVector is then annotated to the output of the utility unit: the UnitDataFeatureVector.