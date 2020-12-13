package com.fon.neda.da.algorithms;

import com.fon.neda.da.util.ArffConverter;
import com.fon.neda.da.util.CSVToArffConverter;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils;
import weka.core.stopwords.Null;

import java.io.File;
import java.io.IOException;

public class KNN {
    /**
     * @param
     * @throws Exception
     */
    public static Evaluation process(String fileName, int k) throws Exception {

        /*CSVToArffConverter.convert(fileName.split("\\.")[0]);
        ArffConverter.convert(fileName, "target");*/


        //Instances data = getDataSet("/files/" + fileName + ".arff");
        Instances data = (new ConverterUtils.DataSource("src/main/resources/files/"+ fileName + ".arff")).getDataSet();
        data.setClassIndex(data.numAttributes() - 1);

        //k - the number of nearest neighbors to use for prediction
        Classifier ibk = new IBk(k);
        ibk.buildClassifier(data);

        System.out.println(ibk);

        Evaluation eval = new Evaluation(data);
        eval.evaluateModel(ibk, data);
        /** Print the algorithm summary */
        System.out.println("** KNN Demo  **");
        System.out.println(eval.toSummaryString());
        System.out.println(eval.toClassDetailsString());
        System.out.println(eval.toMatrixString());

        return eval;

    }

    public Evaluation knn(String fileName, int k, String className) throws Exception {
        CSVToArffConverter.convert(fileName);
        ArffConverter.convert(fileName, className);

        return process(fileName, k);
    }


    public static void main(String[] args) throws Exception {
        Evaluation e = new KNN().knn("winequality-white", 2, "quality");
        System.out.println(e.numTruePositives(1));
        System.out.println(e.numFalseNegatives(1));
        System.out.println(e.numFalseNegatives(1));
        System.out.println(e.numFalsePositives(1));


    }

}
