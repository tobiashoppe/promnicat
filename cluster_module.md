# Hierarchically Clustering Process Model Collections #

This analysis module has been implemented to provide the possibility to cluster specific process models by customized criteria, which can be of both numeric (i.e., process metrics) and string (for instance, activity label names) nature. The clustering process is divided into 3 steps:

  1. [Preprocessing](Preprocessing.md): Providing functionality to select the desired criteria and process models to cluster and to create a feature vector for each process model.
  1. [Clustering](Clustering.md): The hierarchical clustering of the selected process models by their feature vectors.
  1. [Labeling](Labeling.md): Providing suitable and expressive names to the clusters.