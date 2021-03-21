package com.fon.neda.da.algorithms;


import com.fon.neda.da.util.ArffConverter;
import com.fon.neda.da.util.CSVToArffConverter;
import com.fon.neda.da.util.EvaluationDetails;
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

    public EvaluationDetails evaluate() throws Exception {

        Instances data = (new ConverterUtils.DataSource("src/main/resources/files/"+ fileName + ".arff")).getDataSet();
        data.setClassIndex(data.numAttributes() - 1);
        data.randomize(new java.util.Random());	// randomize instance order before splitting dataset
        Instances trainingDataSet = data.trainCV(2, 0);
        Instances testingDataSet = data.testCV(2, 0);
        Classifier classifier = new weka.classifiers.functions.Logistic();
        classifier.buildClassifier(trainingDataSet);
        Evaluation wekaEvaluation = new Evaluation(trainingDataSet);
        wekaEvaluation.evaluateModel(classifier, testingDataSet);

        com.fon.neda.da.entity.Evaluation evaluation = new com.fon.neda.da.entity.Evaluation();
        evaluation.setPrecision(wekaEvaluation.precision(1));
        evaluation.setAccuracy(wekaEvaluation.areaUnderROC(1));
        evaluation.setRecall(wekaEvaluation.recall(1));
        evaluation.setF1(wekaEvaluation.fMeasure(1));

        EvaluationDetails evaluationDetails = new EvaluationDetails();
        evaluationDetails.setEvaluation(evaluation);
        evaluationDetails.setCorrectlyClassifiedInstances((int) wekaEvaluation.correct());
        evaluationDetails.setIncorrectlyClassifiedInstances((int) wekaEvaluation.incorrect());
        evaluationDetails.setTruePositives((int)(wekaEvaluation.numTruePositives(1)));
        evaluationDetails.setFalseNegatives((int)(wekaEvaluation.numFalseNegatives(1)));
        evaluationDetails.setFalsePositives((int)(wekaEvaluation.numFalsePositives(1)));
        evaluationDetails.setTrueNegatives((int)(wekaEvaluation.numTrueNegatives(1)));

        return evaluationDetails;
    }

}