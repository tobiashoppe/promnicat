# UnitData #

_IUnitData_ is the base interface for classes that can be used as _IUnit_ input and output.

![http://wiki.promnicat.googlecode.com/git/images/unit_data_classes.png](http://wiki.promnicat.googlecode.com/git/images/unit_data_classes.png)

## Unit Data ##
The _UnitData_ class implements the _IUnitData_-Interface. In this class the id of the used _Representation_ as well as the result value of the last _IUnit_ of the _IUnitChain_ is stored. Thus, the generic parameter of this class can be set to the class type of the result value of the last unit in the chain(e.g. for the Diagram to jBPT unit it can be set to ProcessModel).

## UnitDataJbpt ##
The _IUnitDataJbpt_ interface extends the _IUnitData_ interface by two methods for setting and getting the [jBPT](http://code.google.com/p/jbpt/) process model.

The _UnitDataJbpt_ class extends the _UnitData_ class and implements the _IUnitDataJbpt_ interface. The _UnitDataJbpt_ stores the information from _UnitData_ and a parsed [jBPT](http://code.google.com/p/jbpt/) process model.

## UnitDataLabelFilter ##
The _IUnitDataLabelFilter_ interface extends the _IUnitDataJbpt_ interface by some methods for setting and getting labels extracted from a process model as well as only a sub-set of this labels.

The _UnitDataLabelFilter_ class extends the _UnitDataJbpt_ class and implements the _IUnitDataLabelFilter_ interface. The _UnitDataLabelFilter_ stores the information from _UnitDataJbpt_ and all labels extracted from the [jBPT](http://code.google.com/p/jbpt/) process model by the _[ProcessmodelLabelExtractorUnit](utility_units.md)_ as well as the labels filtered by the _[LabelFilterUnit](utility_units.md)_. Moreover, the model elements extracted by the _[ElementExtractorUnit](utility_units.md)_ are stored. If one this units is not used in the chain, the corresponding result is null.

## UnitDataMetaData ##
The _IUnitDataMetaData_ interface extends the _IUnitData_ interace by some methods for metadata storring and retrieving.

The _UnitDataMetaData_ class extends the _UnitData_ class and implements the _IUnitDataMetaData_ interface. The _UnitDataMetaData_ stores the information from _UnitData_ and the extracted metadata.

## UnitDataProcessMetrics ##
The _IUnitDataProcessMetrics_ interface extends the _IUnitData_ interface by methods for setting and retrieving process metrics calculated with the _ProcessMetricsCalculator_.

The _UnitDataProcessMetrics_ class extends the _UnitData_ class and implements the _IUnitDataProcessMetrics_ interface. The _UnitDataProcessMetrics_ stores the information from _UnitData_ and claculated process metrics.

## UnitDataFeatureVector ##
The _IUnitDataFeatureVector_ interface extends the _IUnitData_ interface by methods for setting and retrieving a feature vector calculated from the process metrics.

The _UnitDataFeatureVector_ class extends the _UnitData_ class and implements the _IUnitDataFeatureVector_ interface. The _UnitDataFeatureVector_ stores the information from _UnitData_ and the calculated feature vector.