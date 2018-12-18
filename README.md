# Human-Activity-Recognition
Classify different physical activities from motion sensors data (accelerometers and gyroscope) colleted in smartphones and smartwatches by using a multilayer perceptron (implemented from scratch). 
Activities : ‘Biking’, ‘Sitting’, ‘Standing’, ‘Walking’, ‘Stair Up’ and ‘Stair down’.
Average_error% = 18% (k-fold cross-validation)

# Dataset
Link: https://archive.ics.uci.edu/ml/datasets/Heterogeneity+Activity+Recognition

561 columns
7352 lines

There two main files : one contains the data without classes and one contains classes only.

# Code structure
"Main.java" : Show the error% using k-fold cross-validation. Data are divided in parts of length 1000.
"MultiLayerPerceptron.java"
"Layer.java" : Linked list of layer makes forward and backward propagation processes easier to manage.
"OutputLayer.java"
"Neuron.java"
"Batch.java" : Reading and carring data.
