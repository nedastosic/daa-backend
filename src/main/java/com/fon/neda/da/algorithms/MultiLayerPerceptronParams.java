package com.fon.neda.da.algorithms;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MultiLayerPerceptronParams {
    @JsonProperty("inputLayer")
    int inputLayer;

    @JsonProperty("outputLayer")
    int outputLayer;

    @JsonProperty("hiddenLayer")
    int hiddenLayer;

    @JsonProperty("numberOfHiddenLayers")
    int numberOfHiddenLayers;

    @JsonProperty("learningRate")
    double learningRate;

    @JsonProperty("maxError")
    double maxError;

    @JsonProperty("maxIterations")
    int maxIterations;
}
