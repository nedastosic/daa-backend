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

public class LogisticRegression {


    /**
     * This method is used to process the input and return the statistics.
     *
     * @throws Exception
     */
    public static Evaluation process(String fileName) throws Exception {

        Instances data = (new ConverterUtils.DataSource("src/main/resources/files/"+ fileName + ".arff")).getDataSet();
        data.setClassIndex(data.numAttributes() - 1);
        data.randomize(new java.util.Random());	// randomize instance order before splitting dataset
        Instances trainingDataSet = data.trainCV(2, 0);
        Instances testingDataSet = data.testCV(2, 0);
        /** Classifier here is Linear Regression */
        Classifier classifier = new weka.classifiers.functions.Logistic();
        /** */
        classifier.buildClassifier(trainingDataSet);
        /**
         * train the alogorithm with the training data and evaluate the
         * algorithm with testing data
         */
        Evaluation eval = new Evaluation(trainingDataSet);
        eval.evaluateModel(classifier, testingDataSet);
        /** Print the algorithm summary */
        System.out.println("** Linear Regression Evaluation with Datasets **");
        System.out.println(eval.toSummaryString());
        System.out.print(" the expression for the input data as per alogorithm is ");
        System.out.println(classifier);

        /*Instance predicationDataSet = getDataSet(PREDICTION_DATA_SET_FILENAME).lastInstance();
        double value = classifier.classifyInstance(predicationDataSet);
        *//** Prediction Output *//*
        System.out.println(value);*/

        return eval;
    }

    public Evaluation logisticRegression(String fileName, String className) throws Exception {
        CSVToArffConverter.convert(fileName);
        ArffConverter.convert(fileName, className);

        return process(fileName);
    }

    public static void main(String[] args) throws Exception {
        Evaluation e = new LogisticRegression().logisticRegression("heart", "target");


    }

}