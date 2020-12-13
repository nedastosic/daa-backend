package com.fon.neda.da.algorithms;

import com.fon.neda.da.util.ArffConverter;
import com.fon.neda.da.util.CSVToArffConverter;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;

public class NaiveBayes {

    /**
     * This method is used to process the input and return the statistics.
     *
     * @throws Exception
     */
    public static Evaluation process(String fileName) throws Exception {

        //Instances trainingDataSet = getDataSet(TRAINING_DATA_SET_FILENAME);
        //Instances testingDataSet = getDataSet(TESTING_DATA_SET_FILENAME);
        //Instances predictingDataSet = getDataSet(PREDICTION_DATA_SET_FILENAME);

        //String dataFileName = "iris.arff";	// use appropriate path
        Instances data = (new ConverterUtils.DataSource("src/main/resources/files/"+ fileName + ".arff")).getDataSet();
        data.setClassIndex(data.numAttributes() - 1);
        data.randomize(new java.util.Random());	// randomize instance order before splitting dataset
        Instances trainData = data.trainCV(2, 0);
        Instances testData = data.testCV(2, 0);



        /** Classifier here is Linear Regression */
        Classifier classifier = new NaiveBayesMultinomial();
        /** */
        classifier.buildClassifier(trainData);
        /**
         * train the alogorithm with the training data and evaluate the
         * algorithm with testing data
         */
        Evaluation eval = new Evaluation(trainData);
        eval.evaluateModel(classifier, testData);
        /** Print the algorithm summary */
        System.out.println("** Naive Bayes Evaluation with Datasets **");
        System.out.println(eval.toSummaryString());
        System.out.print(" the expression for the input data as per alogorithm is ");
        System.out.println(classifier);
        /*for (int i = 0; i < predictingDataSet.numInstances(); i++) {
            System.out.println(predictingDataSet.instance(i));
            double index = classifier.classifyInstance(predictingDataSet.instance(i));
            String className = trainingDataSet.attribute(0).value((int) index);
            System.out.println(className);
        }*/
        return eval;
    }

    public Evaluation naiveBayes(String fileName, String className) throws Exception {
        CSVToArffConverter.convert(fileName);
        ArffConverter.convert(fileName, className);

        return process(fileName);
    }


    public static void main(String[] args) throws Exception {
        new NaiveBayes().naiveBayes("heart", "target");
    }
}

