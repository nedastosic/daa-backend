package com.fon.neda.da.algorithms;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.MultiIdentifierLoadAccess;

import java.io.IOException;
import java.lang.reflect.Field;

public class AlgorithmFactory {
    public static IAlgorithm generate(int algorithmId, String params, String fileName, String className) {
        try {
            switch (algorithmId) {
                case 1:
                    ObjectMapper knnMapper = new ObjectMapper();
                    KNNParams knnParams = knnMapper.readValue(params.getBytes(), KNNParams.class);
                    return new KNN(fileName, knnParams.k, className);
                case 2:
                    return new NaiveBayes(fileName, className);
                case 3:
                    return new LogisticRegression(fileName, className);

                case 4:
                    ObjectMapper multiLayerPerceptronMapper = new ObjectMapper();
                    MultiLayerPerceptronParams multiLayerPerceptronParams = multiLayerPerceptronMapper.readValue(params.getBytes(), MultiLayerPerceptronParams.class);
                    return new MultiLayerPerceprtron(fileName, multiLayerPerceptronParams.inputLayer, multiLayerPerceptronParams.outputLayer, multiLayerPerceptronParams.hiddenLayer, multiLayerPerceptronParams.numberOfHiddenLayers, multiLayerPerceptronParams.learningRate, multiLayerPerceptronParams.maxError,
                            multiLayerPerceptronParams.maxIterations);
                default:
                    return null;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String getParams(int algorithmId, String params, String parameterName) {
        try {
            switch (algorithmId) {
                case 1:
                    ObjectMapper mapper = new ObjectMapper();
                    KNNParams knnParams = mapper.readValue(params.getBytes(), KNNParams.class);
                    Field field = knnParams.getClass().getDeclaredField(parameterName);
                    return field.get(knnParams).toString();
                default:
                    return null;
            }
        } catch (IOException | NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
