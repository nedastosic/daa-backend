package com.fon.neda.da.algorithms;

import weka.classifiers.Evaluation;

public interface IAlgorithm {
    Evaluation evaluate() throws Exception;
}
