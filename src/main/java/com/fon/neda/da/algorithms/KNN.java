package com.fon.neda.da.algorithms;

import com.fon.neda.da.util.ArffConverter;
import com.fon.neda.da.util.CSVToArffConverter;
import com.fon.neda.da.util.EvaluationDetails;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.CSVSaver;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.NominalToBinary;

import java.io.File;
import java.io.IOException;

public class KNN implements IAlgorithm {
    private String fileName;
    private String className;
    private int k;


    KNN(String fileName, int k, String className) {
        this.fileName = fileName;
        this.k = k;
        this.className = className;
    }


    public EvaluationDetails evaluate() throws Exception{

        CSVToArffConverter.convert(fileName);
        ArffConverter.convert(fileName, className);
        Instances data = (new ConverterUtils.DataSource("src/main/resources/files/" + fileName + ".arff")).getDataSet();
        data.setClassIndex(data.numAttributes() - 1);
        Instances trainData = data.trainCV(2, 0);
        Instances testData = data.testCV(2, 0);

        Classifier cls = new IBk(k);
        cls.buildClassifier(trainData);

        Evaluation wekaEvaluation = new Evaluation(trainData);
        wekaEvaluation.evaluateModel(cls, testData);

        com.fon.neda.da.entity.Evaluation evaluation = new com.fon.neda.da.entity.Evaluation();
        evaluation.setPrecision(wekaEvaluation.precision(1));
        evaluation.setAccuracy(wekaEvaluation.pctCorrect() / 100);
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
