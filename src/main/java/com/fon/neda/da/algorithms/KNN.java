package com.fon.neda.da.algorithms;

import com.fon.neda.da.util.ArffConverter;
import com.fon.neda.da.util.CSVToArffConverter;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class KNN implements IAlgorithm {
    private String fileName;
    private String className;
    private int k;


    KNN(String fileName, int k, String className) {
        this.fileName = fileName;
        this.k = k;
        this.className = className;
    }


    public Evaluation evaluate() throws Exception{

        CSVToArffConverter.convert(fileName);
        ArffConverter.convert(fileName, className);
        Instances data = (new ConverterUtils.DataSource("src/main/resources/files/" + fileName + ".arff")).getDataSet();
        data.setClassIndex(data.numAttributes() - 1);

        Classifier ibk = new IBk(k);
        ibk.buildClassifier(data);

        Evaluation eval = new Evaluation(data);
        eval.evaluateModel(ibk, data);

        return eval;

    }

}
