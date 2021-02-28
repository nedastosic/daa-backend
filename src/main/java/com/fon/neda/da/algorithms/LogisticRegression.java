package com.fon.neda.da.algorithms;


import com.fon.neda.da.util.ArffConverter;
import com.fon.neda.da.util.CSVToArffConverter;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ListOptions;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils;

import java.io.IOException;

public class LogisticRegression implements IAlgorithm{

    private String fileName;
    private String className;

    LogisticRegression(String fileName, String className) {
        this.fileName = fileName;
        this.className = className;
    }

    public Evaluation evaluate() throws Exception {

        Instances data = (new ConverterUtils.DataSource("src/main/resources/files/"+ fileName + ".arff")).getDataSet();
        data.setClassIndex(data.numAttributes() - 1);
        data.randomize(new java.util.Random());	// randomize instance order before splitting dataset
        Instances trainingDataSet = data.trainCV(2, 0);
        Instances testingDataSet = data.testCV(2, 0);
        Classifier classifier = new weka.classifiers.functions.Logistic();
        classifier.buildClassifier(trainingDataSet);
        Evaluation eval = new Evaluation(trainingDataSet);
        eval.evaluateModel(classifier, testingDataSet);

        return eval;
    }

}