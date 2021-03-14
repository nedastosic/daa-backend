package com.fon.neda.da.algorithms;

import com.fon.neda.da.util.EvaluationDetails;
import weka.classifiers.Evaluation;

public interface IAlgorithm {
    EvaluationDetails evaluate() throws Exception;
}
