package com.fon.neda.da.algorithms;

import com.fon.neda.da.util.ArffConverter;
import com.fon.neda.da.util.CSVToArffConverter;
import com.fon.neda.da.util.EvaluationDetails;
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

public class NaiveBayes implements IAlgorithm{
    private String fileName;
    private String className;


    NaiveBayes(String fileName, String className) {
        this.fileName = fileName;
        this.className = className;
    }

    public EvaluationDetails evaluate() throws Exception {


        Instances data = (new ConverterUtils.DataSource("src/main/resources/files/"+ fileName + ".arff")).getDataSet();
        data.setClassIndex(data.numAttributes() - 1);
        data.randomize(new java.util.Random());	// randomize instance order before splitting dataset
        Instances trainData = data.trainCV(2, 0);
        Instances testData = data.testCV(2, 0);

        Classifier classifier = new NaiveBayesMultinomial();
        classifier.buildClassifier(trainData);

        Evaluation wekaEvaluation = new Evaluation(trainData);
        wekaEvaluation.evaluateModel(classifier, testData);

        com.fon.neda.da.entity.Evaluation evaluation = new com.fon.neda.da.entity.Evaluation();
        evaluation.setPrecision(wekaEvaluation.precision(1));
        evaluation.setAccuracy(wekaEvaluation.areaUnderROC(1));
        evaluation.setRecall(wekaEvaluation.recall(1));
        evaluation.setF1(wekaEvaluation.fMeasure(1));

        EvaluationDetails evaluationDetails = new EvaluationDetails();
        evaluationDetails.setEvaluation(evaluation);
        evaluationDetails.setCorrectlyClassifiedInstances((int) wekaEvaluation.correct());
        evaluationDetails.setIncorrectlyClassifiedInstances((int) wekaEvaluation.incorrect());

        return evaluationDetails;
    }

}

